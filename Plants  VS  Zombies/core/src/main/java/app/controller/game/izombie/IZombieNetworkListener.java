package app.controller.game.izombie;

import server.network.Packets;

public interface IZombieNetworkListener {
    void onOnlinePlayers(Packets.IZombieOnlinePlayersResponse response);

    void onInviteResult(Packets.IZombieInviteResult result);

    void onInviteIncoming(Packets.IZombieInviteIncoming incoming);

    void onQueueStatus(Packets.IZombieQueueStatus status);

    void onMatchFound(Packets.IZombieMatchFound found);

    void onPlaceZombie(Packets.IZombiePlaceZombieEvent event);

    void onPlacePlant(Packets.IZombiePlacePlantEvent event);

    void onChat(Packets.IZombieChatEvent event);

    void onMatchEnded(Packets.IZombieMatchEnded ended);
}
