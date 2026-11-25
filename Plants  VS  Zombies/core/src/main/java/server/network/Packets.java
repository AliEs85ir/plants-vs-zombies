package server.network;

import java.util.ArrayList;

/**
 * Holds classes that will be transferred over the network
 */
public class Packets {
    private Packets() {}

    public static class RegisterRequest {
        public String username;
        public String password;
        public String nickname;
        public String email;
        public String gender;
        public String securityQuestion;
        public String age;
        public String securityAnswer;
    }

    public static class RegisterResponse {
        public boolean success;
        public String message;
        public String errorCode;
        public String sessionToken;
        public ProfileData profileData;

        public static RegisterResponse ok(ProfileData profileData, String sessionToken, String message) {
            RegisterResponse r = new RegisterResponse();
            r.success = true;
            r.message = message;
            r.profileData = profileData;
            r.sessionToken = sessionToken;
            return r;
        }

        public static RegisterResponse fail(String message) {
            return fail(null, message);
        }

        public static RegisterResponse fail(String errorCode, String message) {
            RegisterResponse r = new RegisterResponse();
            r.success = false;
            r.message = message;
            r.errorCode = errorCode;
            return r;
        }
    }

    public static class LoginRequest {
        public String username;
        public String password;
    }

    public static class LoginResponse {
        public boolean success;
        public String message;
        public String errorCode;
        public String sessionToken;
        public ProfileData profileData;

        public static LoginResponse ok(ProfileData profile, String token, String message) {
            LoginResponse r = new LoginResponse();
            r.success = true;
            r.profileData = profile;
            r.sessionToken = token;
            r.message = message;
            return r;
        }

        public static LoginResponse fail(String message) {
            LoginResponse r = new LoginResponse();
            r.success = false;
            r.message = message;
            return r;
        }
    }

    public static class ProfileData {
        public String username;
        public String nickname;
        public String email;
        public String gender;
        public int age = -1;
        public int securityQuestionNumber;
        public String profilePictureId;
        public String gameStateJson;

        public int gamesPlayed;
        public int coins;
        public int gems;
        public int mioPoint;
        public int difficulty;

        public long registeredAtEpoch;
    }

    public static class ProfileRequest {
        public String username;
    }

    public static class ProfileResponse {
        public boolean success;
        public String message;
        public String errorCode;
        public ProfileData profileData;

        public static ProfileResponse ok(ProfileData profileData) {
            ProfileResponse r = new ProfileResponse();
            r.success = true;
            r.profileData = profileData;
            return r;
        }

        public static ProfileResponse fail(String message) {
            return fail(null, message);
        }

        public static ProfileResponse fail(String errorCode, String message) {
            ProfileResponse r = new ProfileResponse();
            r.success = false;
            r.message = message;
            r.errorCode = errorCode;
            return r;
        }
    }

    public static class SessionRestoreRequest {
        public String sessionToken;
    }

    public static class ForgotPasswordRequest {
        public String email;
    }

    public static class ForgotPasswordResponse {
        public boolean success;
        public String message;
        public String securityQuestionText;

        public static ForgotPasswordResponse ok(String questionText) {
            ForgotPasswordResponse r = new ForgotPasswordResponse();
            r.success = true;
            r.securityQuestionText = questionText;
            return r;
        }

        public static ForgotPasswordResponse fail(String message) {
            ForgotPasswordResponse r = new ForgotPasswordResponse();
            r.success = false;
            r.message = message;
            return r;
        }
    }

    public static class ResetPasswordRequest {
        public String email;
        public String securityAnswer;
        public String newPassword;
    }

    public static class ResetPasswordResponse {
        public boolean success;
        public String message;

        public static ResetPasswordResponse ok() {
            ResetPasswordResponse r = new ResetPasswordResponse();
            r.success = true;
            r.message = "Password changed successfully.";
            return r;
        }

        public static ResetPasswordResponse fail(String message) {
            ResetPasswordResponse r = new ResetPasswordResponse();
            r.success = false;
            r.message = message;
            return r;
        }
    }
    public static class ProfileUpdateRequest {
        public String sessionToken;
        public String newUsername;
        public String newNickname;
        public String newEmail;
        public String newPassword;
        public String newProfilePictureId;
        public String securityAnswer;
    }

    public static class GameDataSyncRequest {
        public int coins;
        public int gems;
        public int mioPoint;
        public int gamesPlayed;
        public int difficulty;
        public String gameStateJson;
    }

    public static class LogoutRequest {
    }

    public static class LogoutResponse {
        public boolean success;
        public String message;

        public static LogoutResponse ok() {
            LogoutResponse response = new LogoutResponse();
            response.success = true;
            response.message = "Logged out.";
            return response;
        }
    }

    public static class FullGameDataRequest {
        public String gameStateJson;
    }

    public static class FullGameDataResponse {
        public boolean success;
        public String message;
        public String gameStateJson;

        public static FullGameDataResponse ok(String gameStateJson) {
            FullGameDataResponse response = new FullGameDataResponse();
            response.success = true;
            response.gameStateJson = gameStateJson;
            return response;
        }

        public static FullGameDataResponse fail(String message) {
            FullGameDataResponse response = new FullGameDataResponse();
            response.success = false;
            response.message = message;
            return response;
        }
    }

    public static class FullGameDataLoadRequest {
    }

    public static class LeaderboardRequest {
    }

    public static class LeaderboardEntry {
        public String username;
        public String nickname;
        public int mioPoint;
        public String pictureId;
        public int gems;
        public int coins;
        public int gamesPlayed;
    }

    public static class LeaderboardResponse {
        public boolean success;
        public String message;
        public ArrayList<LeaderboardEntry> entries = new ArrayList<>();

        public static LeaderboardResponse ok(ArrayList<LeaderboardEntry> entries) {
            LeaderboardResponse response = new LeaderboardResponse();
            response.success = true;
            response.entries = entries == null ? new ArrayList<>() : entries;
            return response;
        }

        public static LeaderboardResponse fail(String message) {
            LeaderboardResponse response = new LeaderboardResponse();
            response.success = false;
            response.message = message;
            response.entries = new ArrayList<>();
            return response;
        }
    }

    public static class MatchmakeRequest {}
    public static class StickerRequest {}

    public static class IZombieQueueJoinRequest {
        public String chapterName;
    }

    public static class IZombieQueueLeaveRequest {
        public String chapterName;
    }

    public static class IZombieQueueStatus {
        public boolean waiting;
        public String message;

        public static IZombieQueueStatus waiting(String message) {
            IZombieQueueStatus status = new IZombieQueueStatus();
            status.waiting = true;
            status.message = message;
            return status;
        }

        public static IZombieQueueStatus stopped(String message) {
            IZombieQueueStatus status = new IZombieQueueStatus();
            status.waiting = false;
            status.message = message;
            return status;
        }
    }

    public static class IZombieOnlinePlayersRequest {}

    public static class IZombieOnlinePlayersResponse {
        public ArrayList<String> usernames = new ArrayList<>();
    }

    public static class IZombieInviteRequest {
        public String chapterName;
        public String targetUsername;
    }

    public static class IZombieInviteIncoming {
        public String inviteId;
        public String fromUsername;
        public String chapterName;
    }

    public static class IZombieInviteReply {
        public String inviteId;
        public boolean accepted;
    }

    public static class IZombieInviteResult {
        public boolean success;
        public String message;

        public static IZombieInviteResult ok(String message) {
            IZombieInviteResult result = new IZombieInviteResult();
            result.success = true;
            result.message = message;
            return result;
        }

        public static IZombieInviteResult fail(String message) {
            IZombieInviteResult result = new IZombieInviteResult();
            result.success = false;
            result.message = message;
            return result;
        }
    }

    public static class IZombieMatchFound {
        public String matchId;
        public String chapterName;
        public String opponentUsername;
        public long layoutSeed;
        public boolean host;
        public String localSide;
    }

    public static class IZombiePlaceZombieRequest {
        public String matchId;
        public String alias;
        public int row;
        public int col;
    }

    public static class IZombiePlaceZombieEvent {
        public String matchId;
        public String alias;
        public int row;
        public int col;
        public String byUsername;
        public long tick;
    }

    public static class IZombiePlacePlantRequest {
        public String matchId;
        public String plantName;
        public int row;
        public int col;
    }

    public static class IZombiePlacePlantEvent {
        public String matchId;
        public String plantName;
        public int row;
        public int col;
        public String byUsername;
        public long tick;
    }

    public static class IZombieLeaveMatchRequest {
        public String matchId;
    }

    public static class IZombieChatRequest {
        public String matchId;
        public String messageId;
        public String text;
        /** "TEXT", "EMOJI", or "STICKER". Null is treated as "TEXT" for backward compatibility. */
        public String kind;
    }

    public static class IZombieChatEvent {
        public String matchId;
        public String messageId;
        public String text;
        public String byUsername;
        public String kind;
    }

    public static class IZombieMatchEnded {
        public String matchId;
        public String reason;
        public String message;

        public static IZombieMatchEnded of(String matchId, String reason, String message) {
            IZombieMatchEnded ended = new IZombieMatchEnded();
            ended.matchId = matchId;
            ended.reason = reason;
            ended.message = message;
            return ended;
        }
    }
}
