package server.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;
import java.util.HashMap;

public class NetworkHandler {
    static public final int port = 54555;

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(HashMap.class);

        kryo.register(Packets.MatchmakeRequest.class);
        kryo.register(Packets.StickerRequest.class);

        kryo.register(Packets.RegisterRequest.class);
        kryo.register(Packets.RegisterResponse.class);

        kryo.register(Packets.LoginRequest.class);
        kryo.register(Packets.LoginResponse.class);

        kryo.register(Packets.ProfileData.class);
        kryo.register(Packets.ProfileRequest.class);
        kryo.register(Packets.ProfileResponse.class);

        kryo.register(Packets.SessionRestoreRequest.class);

        kryo.register(Packets.ForgotPasswordRequest.class);
        kryo.register(Packets.ForgotPasswordResponse.class);
        kryo.register(Packets.ResetPasswordRequest.class);
        kryo.register(Packets.ResetPasswordResponse.class);

        kryo.register(Packets.ProfileUpdateRequest.class);
        kryo.register(Packets.GameDataSyncRequest.class);

        kryo.register(Packets.LogoutRequest.class);
        kryo.register(Packets.LogoutResponse.class);

        kryo.register(Packets.FullGameDataRequest.class);
        kryo.register(Packets.FullGameDataResponse.class);
        kryo.register(Packets.FullGameDataLoadRequest.class);

        kryo.register(Packets.LeaderboardRequest.class);
        kryo.register(Packets.LeaderboardEntry.class);
        kryo.register(Packets.LeaderboardResponse.class);

        kryo.register(ArrayList.class);
        kryo.register(Packets.IZombieQueueJoinRequest.class);
        kryo.register(Packets.IZombieQueueLeaveRequest.class);
        kryo.register(Packets.IZombieQueueStatus.class);
        kryo.register(Packets.IZombieOnlinePlayersRequest.class);
        kryo.register(Packets.IZombieOnlinePlayersResponse.class);
        kryo.register(Packets.IZombieInviteRequest.class);
        kryo.register(Packets.IZombieInviteIncoming.class);
        kryo.register(Packets.IZombieInviteReply.class);
        kryo.register(Packets.IZombieInviteResult.class);
        kryo.register(Packets.IZombieMatchFound.class);
        kryo.register(Packets.IZombiePlaceZombieRequest.class);
        kryo.register(Packets.IZombiePlaceZombieEvent.class);
        kryo.register(Packets.IZombiePlacePlantRequest.class);
        kryo.register(Packets.IZombiePlacePlantEvent.class);
        kryo.register(Packets.IZombieLeaveMatchRequest.class);
        kryo.register(Packets.IZombieChatRequest.class);
        kryo.register(Packets.IZombieChatEvent.class);
        kryo.register(Packets.IZombieMatchEnded.class);
    }
}
