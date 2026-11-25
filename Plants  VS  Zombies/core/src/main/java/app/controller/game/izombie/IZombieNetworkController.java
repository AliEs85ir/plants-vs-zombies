package app.controller.game.izombie;

import app.controller.game.IZombieController;
import app.enums.plant.PlantName;
import app.view.game.PlayScreen;
import app.view.hud.notification.GameplayNotifications;
import app.view.izombie.IZombieInvitePopup;
import app.view.izombie.IZombieMatchPopup;
import app.view.izombie.IZombieQuickEmoji;
import app.view.izombie.IZombieQuickEmojiCatalog;
import app.view.izombie.IZombieQuickMessage;
import app.view.izombie.IZombieQuickMessageCatalog;
import app.view.izombie.IZombieReaction;
import app.view.izombie.IZombieSticker;
import app.view.izombie.IZombieStickerCatalog;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import common.Result;
import game.world.chapter.Chapter;
import game.world.minigame.izombie.IZombieRules;
import game.world.minigame.izombie.IZombieSide;
import plants_vs_zombies.PlantsVSZombies;
import server.network.Packets;
import server.network.client.GameClient;

import java.util.ArrayList;

public final class IZombieNetworkController implements IZombieNetworkListener {
    private static IZombieNetworkController instance;

    private final GameClient client;
    private IZombieMatchPopup matchPopup;
    private IZombieInvitePopup invitePopup;
    private String pendingChapterName;
    private Runnable onCancelled;
    private String pendingInviteId;

    private IZombieNetworkController(GameClient client) {
        this.client = client;
        client.setIZombieListener(this);
    }

    public static void init(GameClient client) {
        if (client == null) {
            return;
        }
        instance = new IZombieNetworkController(client);
    }

    public static IZombieNetworkController get() {
        return instance;
    }

    public void openMatchPopup(Stage stage, String chapterName, Runnable onCancelled) {
        if (stage == null || chapterName == null) {
            if (onCancelled != null) {
                onCancelled.run();
            }
            return;
        }
        closeMatchPopup();
        this.pendingChapterName = chapterName;
        this.onCancelled = onCancelled;
        matchPopup = new IZombieMatchPopup(new IZombieMatchPopup.Callback() {
            @Override
            public void onPlayWithFriend() {
                requestOnlinePlayers();
            }

            @Override
            public void onRandomQueue() {
                joinQueue();
            }

            @Override
            public void onInvite(String username) {
                invite(username);
            }

            @Override
            public void onRefreshPlayers() {
                requestOnlinePlayers();
            }

            @Override
            public void onCancel() {
                cancelMatchmaking();
            }
        });
        stage.addActor(matchPopup);
        matchPopup.toFront();
    }

    public static void publishPlacement(String alias, int row, int col) {
        IZombieNetworkController controller = instance;
        IZombieMatchContext context = IZombieMatchContext.get();
        if (controller == null || context == null || alias == null) {
            return;
        }
        Packets.IZombiePlaceZombieRequest request = new Packets.IZombiePlaceZombieRequest();
        request.matchId = context.matchId();
        request.alias = alias;
        request.row = row;
        request.col = col;
        controller.client.sendIZombie(request);
    }

    public static void publishPlantPlacement(PlantName plantName, int row, int col) {
        IZombieNetworkController controller = instance;
        IZombieMatchContext context = IZombieMatchContext.get();
        if (controller == null || context == null || plantName == null) {
            return;
        }
        Packets.IZombiePlacePlantRequest request = new Packets.IZombiePlacePlantRequest();
        request.matchId = context.matchId();
        request.plantName = plantName.name();
        request.row = row;
        request.col = col;
        controller.client.sendIZombie(request);
    }

    public static void publishChat(String messageId, String text) {
        publishReaction(IZombieReaction.Kind.TEXT, messageId, text);
    }

    public static void publishReaction(IZombieReaction reaction) {
        if (reaction == null) {
            return;
        }
        publishReaction(reaction.kind(), reaction.id(), reaction.content());
    }

    private static void publishReaction(IZombieReaction.Kind kind, String id, String content) {
        IZombieNetworkController controller = instance;
        IZombieMatchContext context = IZombieMatchContext.get();

        if (controller == null || context == null || kind == null) {
            return;
        }

        if (id == null || id.isBlank()) {
            return;
        }

        Packets.IZombieChatRequest request = new Packets.IZombieChatRequest();
        request.matchId = context.matchId();
        request.messageId = id.trim();
        request.kind = kind.name();


        if (kind == IZombieReaction.Kind.TEXT) {
            request.text = content == null ? "" : content.trim();
        } else {
            request.text = "";
        }

        controller.client.sendIZombie(request);
    }

    @Override
    public void onOnlinePlayers(Packets.IZombieOnlinePlayersResponse response) {
        if (matchPopup == null) {
            return;
        }
        matchPopup.showFriendList(response == null ? new ArrayList<>() : response.usernames);
    }

    @Override
    public void onInviteResult(Packets.IZombieInviteResult result) {
        if (matchPopup == null) {
            return;
        }
        if (result != null && result.success) {
            matchPopup.showWaiting(result.message);
            return;
        }
        matchPopup.showError(result == null ? "Invite failed." : result.message);
    }

    @Override
    public void onInviteIncoming(Packets.IZombieInviteIncoming incoming) {
        if (incoming == null || incoming.inviteId == null) {
            return;
        }
        Stage stage = IZombieUiStages.current();
        if (stage == null) {
            declineInvite(incoming.inviteId);
            return;
        }
        closeInvitePopup();
        pendingInviteId = incoming.inviteId;
        invitePopup = new IZombieInvitePopup(incoming.fromUsername, new IZombieInvitePopup.Callback() {
            @Override
            public void onAccept() {
                replyToInvite(incoming.inviteId, true);
            }

            @Override
            public void onDecline() {
                replyToInvite(incoming.inviteId, false);
            }
        });
        stage.addActor(invitePopup);
        invitePopup.toFront();
    }

    @Override
    public void onQueueStatus(Packets.IZombieQueueStatus status) {
        if (matchPopup == null) {
            return;
        }
        if (status != null && status.waiting) {
            matchPopup.showWaiting(status.message);
            return;
        }
        if (status != null && status.message != null && !status.message.isBlank()) {
            matchPopup.showChoice();
            matchPopup.setStatus(status.message);
        }
    }

    @Override
    public void onMatchFound(Packets.IZombieMatchFound found) {
        if (found == null || found.chapterName == null) {
            return;
        }
        IZombieMatchContext.activate(
            found.matchId,
            found.chapterName,
            found.opponentUsername,
            found.layoutSeed,
            found.host,
            IZombieSide.parse(found.localSide)
        );
        closePopups();
        Result started = ScreenManager.startChapter(found.chapterName);
        if (!started.success()) {
            leaveActiveMatch();
        }
    }

    @Override
    public void onPlaceZombie(Packets.IZombiePlaceZombieEvent event) {
        if (event == null || !IZombieMatchContext.isActive()) {
            return;
        }
        IZombieMatchContext context = IZombieMatchContext.get();
        if (context == null || !context.matchId().equals(event.matchId)) {
            return;
        }
        IZombieController controller = activeController();
        if (controller == null) {
            return;
        }
        controller.applyNetworkZombiePlacement(event.alias, event.row, event.col);
    }

    @Override
    public void onPlacePlant(Packets.IZombiePlacePlantEvent event) {
        if (event == null || !IZombieMatchContext.isActive()) {
            return;
        }
        IZombieMatchContext context = IZombieMatchContext.get();
        if (context == null || !context.matchId().equals(event.matchId)) {
            return;
        }
        IZombieController controller = activeController();
        if (controller == null) {
            return;
        }
        controller.applyNetworkPlantPlacement(
            IZombieRules.plantNameFromPacket(event.plantName),
            event.row,
            event.col
        );
    }

    @Override
    public void onChat(Packets.IZombieChatEvent event) {
        if (event == null || !IZombieMatchContext.isActive()) {
            return;
        }

        IZombieMatchContext context = IZombieMatchContext.get();
        if (context == null || event.matchId == null
            || !context.matchId().equals(event.matchId)) {
            return;
        }

        Screen screen = PlantsVSZombies.getPvz() == null
            ? null
            : PlantsVSZombies.getPvz().getScreen();

        if (!(screen instanceof PlayScreen playScreen)
            || playScreen.getHud() == null) {
            return;
        }

        String kind = event.kind == null
            ? "TEXT"
            : event.kind.trim().toUpperCase(java.util.Locale.ROOT);

        switch (kind) {
            case "EMOJI" -> {

                IZombieQuickEmoji emoji =
                    IZombieQuickEmojiCatalog.find(event.messageId);

                if (emoji == null) {
                    return;
                }

                String glyph = emoji.glyph();
                String drawableName = emoji.drawableName();

                if ((glyph == null || glyph.isBlank())
                    && (drawableName == null || drawableName.isBlank())) {
                    return;
                }

                playScreen.getHud().showNotification(
                    GameplayNotifications.iZombieEmoji(
                        drawableName,
                        glyph
                    )
                );
            }

            case "STICKER" -> {

                IZombieSticker sticker =
                    IZombieStickerCatalog.find(event.messageId);

                if (sticker == null) {
                    return;
                }

                String label = sticker.label();
                String drawableName = sticker.drawableName();

                if ((label == null || label.isBlank())
                    && (drawableName == null || drawableName.isBlank())) {
                    return;
                }

                Gdx.app.postRunnable(() -> {
                    if (playScreen.getHud() != null) {
                        playScreen.getHud().showNotification(
                            GameplayNotifications.iZombieSticker(
                                drawableName,
                                label
                            )
                        );
                    }
                });
            }

            case "TEXT" -> {
                IZombieQuickMessage message =
                    IZombieQuickMessageCatalog.find(
                        event.messageId,
                        event.text
                    );

                String text = message != null
                    ? message.text()
                    : event.text;

                if (text == null || text.isBlank()) {
                    return;
                }

                playScreen.getHud().showNotification(
                    GameplayNotifications.iZombieMessage(text)
                );
            }

            default -> {}
        }
    }

    @Override
    public void onMatchEnded(Packets.IZombieMatchEnded ended) {
        boolean alreadyLeaving = !IZombieMatchContext.isActive();
        IZombieMatchContext.clear();
        closePopups();
        if (alreadyLeaving) {
            return;
        }
        Screen screen = PlantsVSZombies.getPvz() == null ? null : PlantsVSZombies.getPvz().getScreen();
        if (screen instanceof PlayScreen playScreen) {
            Chapter chapter = playScreen.getChapter();
            if (playScreen.getHud() != null && ended != null && ended.message != null) {
                playScreen.getHud().showErrorPopup(ended.message);
            }
            Gdx.app.postRunnable(() -> ScreenManager.showLevelSelectFor(PlantsVSZombies.getPvz(), chapter));
            return;
        }
        if (onCancelled != null) {
            onCancelled.run();
            onCancelled = null;
        }
    }

    private IZombieController activeController() {
        Screen screen = PlantsVSZombies.getPvz() == null ? null : PlantsVSZombies.getPvz().getScreen();
        if (!(screen instanceof PlayScreen playScreen)) {
            return null;
        }
        if (!(playScreen.getController() instanceof IZombieController controller)) {
            return null;
        }
        return controller;
    }

    private void requestOnlinePlayers() {
        client.sendIZombie(
            new Packets.IZombieOnlinePlayersRequest(),
            () -> {
                if (matchPopup != null) {
                    matchPopup.showError("Could not reach the server.");
                }
            }
        );
    }

    private void joinQueue() {
        if (pendingChapterName == null) {
            return;
        }
        Packets.IZombieQueueJoinRequest request = new Packets.IZombieQueueJoinRequest();
        request.chapterName = pendingChapterName;
        client.sendIZombie(request, () -> {
            if (matchPopup != null) {
                matchPopup.showError("Could not reach the server.");
            }
        });
        if (matchPopup != null) {
            matchPopup.showWaiting("Joining the queue...");
        }
    }

    private void invite(String username) {
        Packets.IZombieInviteRequest request = new Packets.IZombieInviteRequest();
        request.chapterName = pendingChapterName;
        request.targetUsername = username;
        client.sendIZombie(request, () -> {
            if (matchPopup != null) {
                matchPopup.showError("Could not reach the server.");
            }
        });
    }

    private void cancelMatchmaking() {
        Packets.IZombieQueueLeaveRequest leave = new Packets.IZombieQueueLeaveRequest();
        leave.chapterName = pendingChapterName;
        client.sendIZombie(leave);
        IZombieMatchContext.clear();
        matchPopup = null;
        Runnable cancelled = onCancelled;
        onCancelled = null;
        pendingChapterName = null;
        if (cancelled != null) {
            cancelled.run();
        }
    }

    private void replyToInvite(String inviteId, boolean accepted) {
        Packets.IZombieInviteReply reply = new Packets.IZombieInviteReply();
        reply.inviteId = inviteId;
        reply.accepted = accepted;
        client.sendIZombie(reply);
        pendingInviteId = null;
        invitePopup = null;
    }

    private void declineInvite(String inviteId) {
        replyToInvite(inviteId, false);
    }

    private void closeMatchPopup() {
        if (matchPopup != null) {
            matchPopup.remove();
            matchPopup = null;
        }
    }

    private void closeInvitePopup() {
        if (invitePopup != null) {
            invitePopup.remove();
            invitePopup = null;
        }
    }

    private void closePopups() {
        closeMatchPopup();
        closeInvitePopup();
        onCancelled = null;
        pendingChapterName = null;
        pendingInviteId = null;
    }

    public void leaveActiveMatch() {
        IZombieMatchContext context = IZombieMatchContext.get();
        if (context == null) {
            return;
        }
        Packets.IZombieLeaveMatchRequest request = new Packets.IZombieLeaveMatchRequest();
        request.matchId = context.matchId();
        client.sendIZombie(request);
        IZombieMatchContext.clear();
    }
}
