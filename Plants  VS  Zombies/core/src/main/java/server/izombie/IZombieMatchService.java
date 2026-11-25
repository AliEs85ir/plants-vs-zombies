package server.izombie;

import com.esotericsoftware.kryonet.Connection;
import server.GameServer;
import server.RemoteClient;
import server.network.ClientState;
import server.network.Packets;

import game.world.minigame.izombie.IZombieSide;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class IZombieMatchService {
    private static final long INVITE_TTL_MS = 45_000L;
    private static final int TEXT_MAX_LENGTH = 80;
    private static final int REACTION_ID_MAX_LENGTH = 40;

    private static final String EMOJI_ID_PREFIX = "emoji_";
    private static final String STICKER_ID_PREFIX = "sticker_";

    private final GameServer gameServer;
    private final Map<Connection, IZombieQueueEntry> queue = new ConcurrentHashMap<>();
    private final Map<String, IZombiePendingInvite> invites = new ConcurrentHashMap<>();
    private final Map<String, IZombieMatch> matches = new ConcurrentHashMap<>();
    private final Map<Connection, IZombieMatch> matchByConnection = new ConcurrentHashMap<>();

    public IZombieMatchService(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void joinQueue(Connection connection, Packets.IZombieQueueJoinRequest request) {
        RemoteClient client = requireAuthenticated(connection);
        if (client == null) {
            connection.sendTCP(Packets.IZombieQueueStatus.stopped("You must be logged in."));
            return;
        }
        String chapterName = normalize(request == null ? null : request.chapterName);
        if (chapterName == null) {
            connection.sendTCP(Packets.IZombieQueueStatus.stopped("Missing chapter."));
            return;
        }
        if (matchByConnection.containsKey(connection)) {
            connection.sendTCP(Packets.IZombieQueueStatus.stopped("You are already in a match."));
            return;
        }

        cancelInvitesInvolving(connection, "Left to join the queue.");
        queue.remove(connection);

        IZombieQueueEntry waiting = findQueuedPartner(connection, chapterName);
        if (waiting != null) {
            queue.remove(waiting.connection());
            createMatch(
                waiting.connection(),
                waiting.username(),
                connection,
                client.getUsername(),
                chapterName
            );
            return;
        }

        queue.put(connection, new IZombieQueueEntry(connection, client.getUsername(), chapterName));
        client.setClientState(ClientState.IN_QUEUE);
        connection.sendTCP(Packets.IZombieQueueStatus.waiting(
            "Waiting for another player on this I, Zombie stage..."
        ));
    }

    public void leaveQueue(Connection connection) {
        IZombieQueueEntry removed = queue.remove(connection);
        RemoteClient client = gameServer.clientOf(connection);
        if (client != null && client.getClientState() == ClientState.IN_QUEUE) {
            client.setClientState(ClientState.REGISTERED);
        }
        if (removed != null && connection.isConnected()) {
            connection.sendTCP(Packets.IZombieQueueStatus.stopped("Left the queue."));
        }
    }

    public void listOnlinePlayers(Connection connection) {
        Packets.IZombieOnlinePlayersResponse response = new Packets.IZombieOnlinePlayersResponse();
        RemoteClient self = gameServer.clientOf(connection);
        String selfName = self == null ? null : self.getUsername();
        for (RemoteClient client : gameServer.authenticatedClients()) {
            if (client.getUsername() == null) {
                continue;
            }
            if (selfName != null && selfName.equalsIgnoreCase(client.getUsername())) {
                continue;
            }
            response.usernames.add(client.getUsername());
        }
        connection.sendTCP(response);
    }

    public void invite(Connection connection, Packets.IZombieInviteRequest request) {
        RemoteClient from = requireAuthenticated(connection);
        if (from == null) {
            connection.sendTCP(Packets.IZombieInviteResult.fail("You must be logged in."));
            return;
        }
        String chapterName = normalize(request == null ? null : request.chapterName);
        String targetName = normalize(request == null ? null : request.targetUsername);
        if (chapterName == null || targetName == null) {
            connection.sendTCP(Packets.IZombieInviteResult.fail("Choose a player and a stage."));
            return;
        }
        if (targetName.equalsIgnoreCase(from.getUsername())) {
            connection.sendTCP(Packets.IZombieInviteResult.fail("You cannot invite yourself."));
            return;
        }
        if (matchByConnection.containsKey(connection)) {
            connection.sendTCP(Packets.IZombieInviteResult.fail("You are already in a match."));
            return;
        }

        RemoteClient target = gameServer.findByUsername(targetName);
        if (target == null || target.getConnection() == null) {
            connection.sendTCP(Packets.IZombieInviteResult.fail("That player is not online."));
            return;
        }
        if (matchByConnection.containsKey(target.getConnection())) {
            connection.sendTCP(Packets.IZombieInviteResult.fail("That player is already in a match."));
            return;
        }

        cancelInvitesInvolving(connection, "Replaced by a new invite.");
        queue.remove(connection);
        queue.remove(target.getConnection());

        String inviteId = UUID.randomUUID().toString();
        IZombiePendingInvite invite = new IZombiePendingInvite(
            inviteId,
            connection,
            from.getUsername(),
            target.getConnection(),
            target.getUsername(),
            chapterName
        );
        invites.put(inviteId, invite);

        Packets.IZombieInviteIncoming incoming = new Packets.IZombieInviteIncoming();
        incoming.inviteId = inviteId;
        incoming.fromUsername = from.getUsername();
        incoming.chapterName = chapterName;
        target.getConnection().sendTCP(incoming);
        connection.sendTCP(Packets.IZombieInviteResult.ok(
            "Invite sent to " + target.getUsername() + "."
        ));
    }

    public void replyToInvite(Connection connection, Packets.IZombieInviteReply reply) {
        if (reply == null || reply.inviteId == null) {
            return;
        }
        IZombiePendingInvite invite = invites.remove(reply.inviteId);
        if (invite == null) {
            connection.sendTCP(Packets.IZombieInviteResult.fail("That invite is no longer valid."));
            return;
        }
        if (invite.to() != connection) {
            invites.put(invite.inviteId(), invite);
            connection.sendTCP(Packets.IZombieInviteResult.fail("This invite is not for you."));
            return;
        }

        if (!reply.accepted) {
            invite.from().sendTCP(Packets.IZombieInviteResult.fail(
                invite.toUsername() + " declined the invite."
            ));
            return;
        }
        if (matchByConnection.containsKey(invite.from()) || matchByConnection.containsKey(invite.to())) {
            connection.sendTCP(Packets.IZombieInviteResult.fail("A player is already in a match."));
            invite.from().sendTCP(Packets.IZombieInviteResult.fail("Match could not start."));
            return;
        }

        queue.remove(invite.from());
        queue.remove(invite.to());
        createMatch(
            invite.from(),
            invite.fromUsername(),
            invite.to(),
            invite.toUsername(),
            invite.chapterName()
        );
    }

    public void leaveMatch(Connection connection, Packets.IZombieLeaveMatchRequest request) {
        IZombieMatch match = matchByConnection.get(connection);
        if (match == null && request != null && request.matchId != null) {
            match = matches.get(request.matchId);
        }
        if (match != null && match.contains(connection)) {
            endMatch(match, "left", "Your opponent left the match.");
        }
    }

    public void placeZombie(Connection connection, Packets.IZombiePlaceZombieRequest request) {
        if (request == null || request.matchId == null || request.alias == null) {
            return;
        }
        IZombieMatch match = matches.get(request.matchId);
        if (match == null || match.isEnded() || !match.contains(connection)) {
            return;
        }
        if (match.sideOf(connection) != IZombieSide.ZOMBIES) {
            return;
        }
        if (!match.tryOccupy(request.row, request.col)) {
            return;
        }

        Packets.IZombiePlaceZombieEvent event = new Packets.IZombiePlaceZombieEvent();
        event.matchId = match.matchId();
        event.alias = request.alias;
        event.row = request.row;
        event.col = request.col;
        event.byUsername = match.usernameOf(connection);
        event.tick = System.currentTimeMillis();
        match.enqueue(event);
    }

    public void placePlant(Connection connection, Packets.IZombiePlacePlantRequest request) {
        if (request == null || request.matchId == null || request.plantName == null) {
            return;
        }
        IZombieMatch match = matches.get(request.matchId);
        if (match == null || match.isEnded() || !match.contains(connection)) {
            return;
        }
        if (match.sideOf(connection) != IZombieSide.PLANTS) {
            return;
        }
        if (!match.tryOccupy(request.row, request.col)) {
            return;
        }

        Packets.IZombiePlacePlantEvent event = new Packets.IZombiePlacePlantEvent();
        event.matchId = match.matchId();
        event.plantName = request.plantName;
        event.row = request.row;
        event.col = request.col;
        event.byUsername = match.usernameOf(connection);
        event.tick = System.currentTimeMillis();
        match.enqueue(event);
    }

    public void sendChat(Connection connection, Packets.IZombieChatRequest request) {
        if (connection == null || request == null) {
            return;
        }

        if (request.matchId == null || request.matchId.isBlank()) {
            return;
        }

        String kind = normalizeReactionKind(request.kind);

        /*
         * Only TEXT is allowed to carry arbitrary text.
         *
         * EMOJI/STICKER carry an ID only.
         */
        if (kind.equals("TEXT")) {
            String text = request.text == null
                ? ""
                : request.text.trim();

            if (text.isEmpty() || text.length() > TEXT_MAX_LENGTH) {
                return;
            }

            request.text = text;
        } else {
            /*
             * Reaction IDs are deliberately short and namespaced.
             *
             * Examples:
             *   emoji_laugh
             *   emoji_thumbsup
             *   sticker_laugh
             *
             * This prevents arbitrary text from being used as reaction
             * content while still allowing us to add new IDs later
             * without changing this server code.
             */
            if (!isValidReactionId(request.messageId, kind)) {
                return;
            }

            /*
             * Never forward client-provided display text for reactions.
             * The receiver resolves the ID from its own catalog.
             */
            request.text = "";
        }

        IZombieMatch match = matches.get(request.matchId);

        if (match == null
            || match.isEnded()
            || !match.contains(connection)) {
            return;
        }

        Connection opponent = match.opponentOf(connection);

        if (opponent == null || !opponent.isConnected()) {
            return;
        }

        Packets.IZombieChatEvent event = new Packets.IZombieChatEvent();

        event.matchId = match.matchId();
        event.messageId = request.messageId;
        event.text = request.text;
        event.byUsername = match.usernameOf(connection);
        event.kind = kind;

        opponent.sendTCP(event);
    }

    private static String normalizeReactionKind(String kind) {
        if (kind == null || kind.isBlank()) {
            return "TEXT";
        }

        String normalized = kind.trim().toUpperCase(java.util.Locale.ROOT);

        if (normalized.equals("TEXT")
            || normalized.equals("EMOJI")
            || normalized.equals("STICKER")) {
            return normalized;
        }

        /*
         * Unknown kinds are NOT converted to TEXT.
         *
         * This is important because otherwise:
         *
         * kind = "WHATEVER"
         * text = "arbitrary malicious text"
         *
         * would become a valid TEXT message.
         */
        return "INVALID";
    }

    private static boolean isValidReactionId(String id, String kind) {
        if (id == null || id.isBlank()) {
            return false;
        }

        String normalized = id.trim();

        if (normalized.length() > REACTION_ID_MAX_LENGTH) {
            return false;
        }

        /*
         * Keep the protocol extensible.
         *
         * We don't require the server to know every future sticker/emoji
         * asset. It only verifies that the ID belongs to the correct
         * namespace. The receiving client performs the actual catalog
         * lookup.
         */
        if (kind.equals("EMOJI")) {
            return normalized.startsWith(EMOJI_ID_PREFIX)
                && isSafeReactionId(normalized);
        }

        if (kind.equals("STICKER")) {
            return normalized.startsWith(STICKER_ID_PREFIX)
                && isSafeReactionId(normalized);
        }

        return false;
    }

    private static boolean isSafeReactionId(String id) {
        /*
         * IDs intentionally contain only:
         *
         * a-z
         * A-Z
         * 0-9
         * _
         * -
         *
         * No spaces, paths, JSON fragments, etc.
         */
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);

            if ((c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9')
                || c == '_'
                || c == '-') {
                continue;
            }

            return false;
        }

        return true;
    }

    public void onDisconnect(Connection connection) {
        leaveQueue(connection);
        cancelInvitesInvolving(connection, "Player disconnected.");
        IZombieMatch match = matchByConnection.get(connection);
        if (match != null) {
            endMatch(match, "disconnected", "Your opponent disconnected.");
        }
    }

    public void tick() {
        expireInvites();
        flushPlacements();
    }

    private void expireInvites() {
        long now = System.currentTimeMillis();
        Iterator<IZombiePendingInvite> iterator = invites.values().iterator();
        while (iterator.hasNext()) {
            IZombiePendingInvite invite = iterator.next();
            if (invite.isExpired(now, INVITE_TTL_MS)) {
                iterator.remove();
                invite.from().sendTCP(Packets.IZombieInviteResult.fail("Invite expired."));
            }
        }
    }

    private void flushPlacements() {
        for (IZombieMatch match : matches.values()) {
            if (match.isEnded()) {
                continue;
            }
            Object event;
            while ((event = match.pollOutbound()) != null) {
                match.host().sendTCP(event);
                match.guest().sendTCP(event);
            }
        }
    }

    private void createMatch(
        Connection host,
        String hostUsername,
        Connection guest,
        String guestUsername,
        String chapterName
    ) {
        String matchId = UUID.randomUUID().toString();
        long seed = java.util.concurrent.ThreadLocalRandom.current().nextLong();
        IZombieMatch match = new IZombieMatch(
            matchId,
            chapterName,
            seed,
            host,
            hostUsername,
            guest,
            guestUsername,
            IZombieSide.random()
        );
        matches.put(matchId, match);
        matchByConnection.put(host, match);
        matchByConnection.put(guest, match);

        RemoteClient hostClient = gameServer.clientOf(host);
        RemoteClient guestClient = gameServer.clientOf(guest);
        if (hostClient != null) {
            hostClient.setClientState(ClientState.IN_GAME);
        }
        if (guestClient != null) {
            guestClient.setClientState(ClientState.IN_GAME);
        }

        host.sendTCP(matchFound(match, guestUsername, true));
        guest.sendTCP(matchFound(match, hostUsername, false));
    }

    private Packets.IZombieMatchFound matchFound(IZombieMatch match, String opponent, boolean host) {
        Packets.IZombieMatchFound found = new Packets.IZombieMatchFound();
        found.matchId = match.matchId();
        found.chapterName = match.chapterName();
        found.opponentUsername = opponent;
        found.layoutSeed = match.layoutSeed();
        found.host = host;
        found.localSide = host ? match.hostSide().name() : match.hostSide().opponent().name();
        return found;
    }

    private void endMatch(IZombieMatch match, String reason, String message) {
        if (match == null || match.isEnded()) {
            return;
        }
        match.markEnded();
        matches.remove(match.matchId());
        matchByConnection.remove(match.host());
        matchByConnection.remove(match.guest());

        Packets.IZombieMatchEnded ended = Packets.IZombieMatchEnded.of(match.matchId(), reason, message);
        sendQuietly(match.host(), ended);
        sendQuietly(match.guest(), ended);

        resetState(match.host());
        resetState(match.guest());
    }

    private void resetState(Connection connection) {
        RemoteClient client = gameServer.clientOf(connection);
        if (client != null && client.isAuthenticated()) {
            client.setClientState(ClientState.REGISTERED);
        }
    }

    private void sendQuietly(Connection connection, Object packet) {
        try {
            if (connection != null && connection.isConnected()) {
                connection.sendTCP(packet);
            }
        } catch (Exception ignored) {
        }
    }

    private void cancelInvitesInvolving(Connection connection, String reason) {
        Iterator<Map.Entry<String, IZombiePendingInvite>> iterator = invites.entrySet().iterator();
        while (iterator.hasNext()) {
            IZombiePendingInvite invite = iterator.next().getValue();
            if (invite.from() != connection && invite.to() != connection) {
                continue;
            }
            iterator.remove();
            Connection other = invite.from() == connection ? invite.to() : invite.from();
            sendQuietly(other, Packets.IZombieInviteResult.fail(reason));
        }
    }

    private IZombieQueueEntry findQueuedPartner(Connection self, String chapterName) {
        for (IZombieQueueEntry entry : queue.values()) {
            if (entry.connection() == self) {
                continue;
            }
            if (chapterName.equals(entry.chapterName())) {
                return entry;
            }
        }
        return null;
    }

    private RemoteClient requireAuthenticated(Connection connection) {
        RemoteClient client = gameServer.clientOf(connection);
        if (client == null || !client.isAuthenticated()) {
            return null;
        }
        return client;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
