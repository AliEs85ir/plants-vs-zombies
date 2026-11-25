package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import server.izombie.IZombieMatchService;
import server.network.ClientState;
import server.network.NetworkConfig;
import server.network.NetworkHandler;
import server.network.Packets;
import server.auth.ServerAuthService;
import server.user.ServerUserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private final Server server = new Server(
        NetworkConfig.WRITE_BUFFER_SIZE,
        NetworkConfig.OBJECT_BUFFER_SIZE
    );
    private final ServerUserRepository repository           = new ServerUserRepository();
    private final ServerAuthService authService             = new ServerAuthService(repository);
    private final Map<Connection, RemoteClient> onlineUsers = new ConcurrentHashMap<>();
    private final IZombieMatchService izombieMatchService   = new IZombieMatchService(this);
    private Thread updateThread;
    private volatile boolean ready;

    public void start() throws IOException {
        NetworkHandler.register(server);
        server.addListener(new ServerListener(this));
        register(NetworkConfig.PORT, NetworkConfig.PORT);
        UpdateThread ticker = new UpdateThread(this);
        updateThread = new Thread(ticker, "izombie-update");
        updateThread.setDaemon(true);
        updateThread.start();
        ready = true;
    }

    public boolean isReady() {
        return ready;
    }

    public void stop() {
        ready = false;
        try {
            server.stop();
        } catch (RuntimeException ignored) {
        }
        if (updateThread != null) {
            updateThread.interrupt();
        }
    }

    private void register(int tcpPort, int udpPort) throws IOException {
        try {
            server.bind(tcpPort, udpPort);
        } catch (IOException e) {
            throw new IOException("Could not bind server on port " + tcpPort, e);
        }
        server.start();
    }

    void addClient(Connection connection) {
        onlineUsers.put(connection, new RemoteClient(connection, ClientState.NAMELESS));
    }

    void removeClient(Connection connection) {
        izombieMatchService.onDisconnect(connection);
        onlineUsers.remove(connection);
    }

    public RemoteClient clientOf(Connection connection) {
        return onlineUsers.get(connection);
    }

    public Collection<RemoteClient> authenticatedClients() {
        List<RemoteClient> authenticated = new ArrayList<>();
        for (RemoteClient client : onlineUsers.values()) {
            if (client != null && client.isAuthenticated()) {
                authenticated.add(client);
            }
        }
        return authenticated;
    }

    public RemoteClient findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        for (RemoteClient client : onlineUsers.values()) {
            if (client != null
                && client.getUsername() != null
                && client.getUsername().equalsIgnoreCase(username)) {
                return client;
            }
        }
        return null;
    }

    public void tickMatches() {
        izombieMatchService.tick();
    }

    public void handle(Connection connection, Object packet) {
        if (packet instanceof Packets.RegisterRequest request) {
            Packets.RegisterResponse response = authService.register(request);

            if (response.success) {
                applyAuth(
                    connection,
                    response.profileData.username,
                    response.sessionToken
                );
            }

            connection.sendTCP(response);
            return;
        }

        if (packet instanceof Packets.LoginRequest request) {
            Packets.LoginResponse response = authService.login(request);

            if (response.success) {
                applyAuth(
                    connection,
                    response.profileData.username,
                    response.sessionToken
                );
            }

            connection.sendTCP(response);
            return;
        }

        if (packet instanceof Packets.SessionRestoreRequest request) {
            Packets.LoginResponse response = authService.restoreSession(request);

            if (response.success) {
                applyAuth(
                    connection,
                    response.profileData.username,
                    response.sessionToken
                );
            }

            connection.sendTCP(response);
            return;
        }

        if (packet instanceof Packets.LogoutRequest) {
            izombieMatchService.onDisconnect(connection);
            RemoteClient remoteClient = onlineUsers.get(connection);

            if (remoteClient != null && remoteClient.getSessionToken() != null) {
                authService.logout(remoteClient.getSessionToken());
            }

            onlineUsers.put(connection, new RemoteClient(connection, ClientState.NAMELESS));
            connection.sendTCP(Packets.LogoutResponse.ok());
            return;
        }

        if (packet instanceof Packets.ProfileRequest request) {
            connection.sendTCP(authService.loadProfile(request));
            return;
        }

        if (packet instanceof Packets.ForgotPasswordRequest request) {
            connection.sendTCP(authService.forgotPassword(request));
            return;
        }

        if (packet instanceof Packets.ResetPasswordRequest request) {
            connection.sendTCP(authService.resetPassword(request));
            return;
        }
        if (packet instanceof Packets.FullGameDataRequest request) {
            connection.sendTCP(
                authService.saveFullGameData(
                    request,
                    authenticatedUsername(connection)
                )
            );
            return;
        }

        if (packet instanceof Packets.FullGameDataLoadRequest) {
            connection.sendTCP(
                authService.loadFullGameData(
                    authenticatedUsername(connection)
                )
            );
            return;
        }

        if (packet instanceof Packets.ProfileUpdateRequest request) {
            String username = authenticatedUsername(connection);
            if ((username == null || username.isBlank())
                && request.sessionToken != null
                && !request.sessionToken.isBlank()) {
                Packets.SessionRestoreRequest restore = new Packets.SessionRestoreRequest();
                restore.sessionToken = request.sessionToken;
                Packets.LoginResponse restored = authService.restoreSession(restore);
                if (restored.success
                    && restored.profileData != null
                    && restored.profileData.username != null
                    && !restored.profileData.username.isBlank()) {
                    applyAuth(connection, restored.profileData.username, restored.sessionToken);
                    username = restored.profileData.username;
                }
            }
            Packets.ProfileResponse response = authService.updateProfile(
                request,
                username
            );
            // Keep connection session identity in sync after username rename.
            if (response.success
                && response.profileData != null
                && response.profileData.username != null
                && !response.profileData.username.isBlank()) {
                RemoteClient remoteClient = onlineUsers.get(connection);
                if (remoteClient != null) {
                    remoteClient.setUsername(response.profileData.username);
                    if (remoteClient.getSessionToken() == null
                        && request.sessionToken != null
                        && !request.sessionToken.isBlank()) {
                        remoteClient.setSessionToken(request.sessionToken);
                    }
                }
            }
            connection.sendTCP(response);
            return;
        }

        if (packet instanceof Packets.GameDataSyncRequest request) {
            connection.sendTCP(
                authService.syncGameData(
                    request,
                    authenticatedUsername(connection)
                )
            );
            return;
        }

        if (packet instanceof Packets.LeaderboardRequest) {
            connection.sendTCP(authService.leaderboard());
            return;
        }

        if (packet instanceof Packets.IZombieQueueJoinRequest request) {
            izombieMatchService.joinQueue(connection, request);
            return;
        }
        if (packet instanceof Packets.IZombieQueueLeaveRequest) {
            izombieMatchService.leaveQueue(connection);
            return;
        }
        if (packet instanceof Packets.IZombieOnlinePlayersRequest) {
            izombieMatchService.listOnlinePlayers(connection);
            return;
        }
        if (packet instanceof Packets.IZombieInviteRequest request) {
            izombieMatchService.invite(connection, request);
            return;
        }
        if (packet instanceof Packets.IZombieInviteReply reply) {
            izombieMatchService.replyToInvite(connection, reply);
            return;
        }
        if (packet instanceof Packets.IZombiePlaceZombieRequest request) {
            izombieMatchService.placeZombie(connection, request);
            return;
        }
        if (packet instanceof Packets.IZombiePlacePlantRequest request) {
            izombieMatchService.placePlant(connection, request);
            return;
        }
        if (packet instanceof Packets.IZombieChatRequest request) {
            izombieMatchService.sendChat(connection, request);
            return;
        }
        if (packet instanceof Packets.IZombieLeaveMatchRequest request) {
            izombieMatchService.leaveMatch(connection, request);
        }
    }

    private String authenticatedUsername(Connection connection) {
        RemoteClient rc = onlineUsers.get(connection);
        return rc == null ? null : rc.getUsername();
    }

    private void applyAuth(Connection connection, String username, String token) {
        RemoteClient rc = onlineUsers.get(connection);
        if (rc != null) {
            rc.setUsername(username);
            rc.setSessionToken(token);
            rc.setClientState(ClientState.REGISTERED);
        }
    }

}
