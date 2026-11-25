package app.service;

import app.enums.Menu;
import app.validator.AuthErrorCodes;
import app.model.App;
import app.model.user.User;
import app.service.news.NewsPublisher;
import app.model.user.UserFactory;
import app.repository.Repository;
import common.Result;
import game.service.quest.QuestManager;
import server.network.GameStateSerializer;
import server.network.Packets;
import server.network.client.GameClient;
import server.user.ScoringLeaderboard;

import java.util.List;
import java.util.function.Consumer;


public class AppService {
    private static GameClient gameClient;
    private static volatile boolean gameDataDirty = false;
    private static volatile boolean syncInProgress = false;
    private static volatile boolean syncRequestedAgain = false;

    public static void init(GameClient client) { gameClient = client; }
    public static GameClient client() { return gameClient; }

    public static void register(
        Packets.RegisterRequest req,
        boolean rememberMe,
        Consumer<Result> callback
    ) {
        if (gameClient == null) {
            callback.accept(Result.failure(
                AuthErrorCodes.SERVER_UNAVAILABLE,
                AuthErrorCodes.localize(AuthErrorCodes.SERVER_UNAVAILABLE, "Server connection is unavailable.")
            ));
            return;
        }

        gameClient.sendRegister(req, response -> {
            if (!response.success) {
                callback.accept(Result.failure(
                    response.errorCode,
                    AuthErrorCodes.localize(response.errorCode, response.message)
                ));
                return;
            }

            applyProfile(response.profileData);
            rememberLogin(rememberMe, response.sessionToken, rememberedUsername(response.profileData, req.username));
            NewsPublisher.onUserRegistered(App.getCurrentUser());
            callback.accept(Result.success(response.message));
        });
    }
    public static void login(
        String username,
        String password,
        boolean rememberMe,
        Consumer<Result> callback
    ) {
        if (gameClient == null) {
            callback.accept(Result.failure(
                AuthErrorCodes.SERVER_UNAVAILABLE,
                AuthErrorCodes.localize(AuthErrorCodes.SERVER_UNAVAILABLE, "Server connection is unavailable.")
            ));
            return;
        }

        Packets.LoginRequest request = new Packets.LoginRequest();
        request.username = username;
        request.password = password;

        gameClient.sendLogin(request, response -> {
            if (!response.success) {
                callback.accept(Result.failure(
                    response.errorCode,
                    AuthErrorCodes.localize(response.errorCode, response.message)
                ));
                return;
            }

            applyProfile(response.profileData);
            rememberLogin(rememberMe, response.sessionToken, rememberedUsername(response.profileData, username));
            callback.accept(Result.success(response.message));
        });
    }

    public static void restoreSession(Consumer<Result> callback) {
        String token = Repository.loadLoginToken();
        String username = Repository.loadRememberedUsername();

        if ((token == null || token.isBlank()) && (username == null || username.isBlank())) {
            callback.accept(Result.failure("No saved session."));
            return;
        }

        if (gameClient == null || token == null || token.isBlank()) {
            finishRestoreFromLocal(username, callback);
            return;
        }

        Packets.SessionRestoreRequest request = new Packets.SessionRestoreRequest();
        request.sessionToken = token;

        gameClient.sendRestore(request, response -> {
            if (response.success) {
                applyProfile(response.profileData);
                rememberLogin(true, response.sessionToken, rememberedUsername(response.profileData, username));
                callback.accept(Result.success(response.message));
                return;
            }

            if (isAccountGone(response.message)) {
                Repository.clearLoginToken();
                callback.accept(Result.failure(response.message));
                return;
            }

            finishRestoreFromLocal(username, callback);
        });
    }

    private static void rememberLogin(boolean rememberMe, String token, String username) {
        if (!rememberMe) {
            Repository.clearLoginToken();
            return;
        }
        Repository.saveRememberedLogin(token, username);
    }

    private static String rememberedUsername(Packets.ProfileData profileData, String fallback) {
        if (profileData != null && profileData.username != null && !profileData.username.isBlank()) {
            return profileData.username;
        }
        return fallback;
    }

    private static void finishRestoreFromLocal(String username, Consumer<Result> callback) {
        if (applyLocalRememberedUser(username)) {
            callback.accept(Result.success("Local session restored."));
            return;
        }
        callback.accept(Result.failure("No saved session."));
    }

    private static boolean applyLocalRememberedUser(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        ProgressService.loadUserProgress(user);
        App.setCurrentUser(user);
        App.setCurrentProgress(user.getGameProgress());
        return true;
    }

    private static boolean isAccountGone(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }
        return message.toLowerCase().contains("no longer exists");
    }

    public static void updateProfile(
        Packets.ProfileUpdateRequest request,
        Consumer<Result> callback
    ) {
        if (gameClient == null) {
            callback.accept(Result.failure(
                AuthErrorCodes.SERVER_UNAVAILABLE,
                AuthErrorCodes.localize(AuthErrorCodes.SERVER_UNAVAILABLE, "Server connection is unavailable.")
            ));
            return;
        }

        User currentUser = App.getCurrentUser();
        if (currentUser == null) {
            callback.accept(Result.failure("You are not logged in."));
            return;
        }

        String oldUsername = currentUser.getUsername();
        String oldNickname = currentUser.getNickname();
        String oldEmail = currentUser.getEmail();
        ProgressService.saveUserProgress(currentUser);

        if (request.sessionToken == null || request.sessionToken.isBlank()) {
            request.sessionToken = Repository.loadLoginToken();
        }

        gameClient.sendProfileUpdate(request, response -> {
            if (!response.success) {
                callback.accept(Result.failure(
                    response.errorCode,
                    AuthErrorCodes.localize(response.errorCode, response.message)
                ));
                return;
            }

            if (response.profileData != null
                && oldUsername != null
                && response.profileData.username != null
                && !oldUsername.equalsIgnoreCase(response.profileData.username)) {
                ProgressService.renameUserProgress(
                    oldUsername,
                    response.profileData.username
                );
            }

            applyProfile(response.profileData);

            String token = Repository.loadLoginToken();
            if (token != null && !token.isBlank() && response.profileData != null) {
                rememberLogin(true, token, rememberedUsername(response.profileData, oldUsername));
            }

            NewsPublisher.onProfileUpdated(
                App.getCurrentUser(),
                request,
                oldUsername,
                oldNickname,
                oldEmail
            );
            callback.accept(Result.success("Profile updated successfully."));
        });
    }

    public static void loadProfile(String username, Consumer<Packets.ProfileData> callback) {
        Packets.ProfileRequest req = new Packets.ProfileRequest();
        req.username = username;
        if (gameClient == null) {
            callback.accept(null);
            return;
        }
        gameClient.sendProfile(req, res -> callback.accept(res.success ? res.profileData : null));
    }

    public static void syncIdentityFromServer(Runnable after) {
        User current = App.getCurrentUser();
        if (current == null || current.getUsername() == null || gameClient == null) {
            if (after != null) {
                after.run();
            }
            return;
        }
        loadProfile(current.getUsername(), data -> {
            if (data != null) {
                if (data.username != null && !data.username.isBlank()) {
                    current.setUsername(data.username);
                }
                if (data.nickname != null && !data.nickname.isBlank()) {
                    current.setNickname(data.nickname);
                }
                if (data.email != null && !data.email.isBlank()) {
                    current.setEmail(data.email);
                }
                if (data.gender != null && !data.gender.isBlank()) {
                    current.setGender(data.gender);
                }
                if (data.age >= 0) {
                    current.setAge(data.age);
                }
                if (data.profilePictureId != null && !data.profilePictureId.isBlank()) {
                    current.setProfilePictureId(data.profilePictureId);
                }
                if (data.securityQuestionNumber > 0) {
                    current.setSecurityQuestionNumber(data.securityQuestionNumber);
                }
            }
            if (after != null) {
                after.run();
            }
        });
    }

    public static void loadLeaderboard(Consumer<List<Packets.LeaderboardEntry>> callback) {
        if (callback == null) {
            return;
        }
        if (gameClient == null) {
            callback.accept(ScoringLeaderboard.fromUsersFile());
            return;
        }
        gameClient.sendLeaderboard(response -> {
            if (response != null && response.success && response.entries != null) {
                callback.accept(response.entries);
                return;
            }
            callback.accept(ScoringLeaderboard.fromUsersFile());
        });
    }

    public static boolean isLoggedIn() {
        return App.getCurrentUser() != null;
    }

    public static void logout() {
        User currentUser = App.getCurrentUser();

        if (currentUser == null) {
            Repository.clearLoginToken();
            App.setCurrentProgress(null);
            App.setCurrentMenu(Menu.SignupMenu);
            return;
        }

        syncGameData(result -> {
            if (gameClient != null) {
                gameClient.sendLogout(response -> {
                });
            }

            App.setCurrentUser(null);
            App.setCurrentProgress(null);
            App.setCurrentMenu(Menu.SignupMenu);
            gameDataDirty = false;

            Repository.clearLoginToken();
        });
    }

    public static void applyProfile(Packets.ProfileData profileData) {
        if (profileData == null) {
            return;
        }

        User user = UserFactory.fromProfile(profileData);

        boolean loadedLocal = ProgressService.loadUserProgress(user);

        if (!loadedLocal) {
            if (profileData.gameStateJson != null
                && !profileData.gameStateJson.isBlank()) {
                GameStateSerializer.applyToUser(user, profileData.gameStateJson);
            }
            ProgressService.saveUserProgress(user);
        } else {
            applyServerQuests(user, profileData.gameStateJson);
        }

        if (user != null) {
            if (profileData.username != null && !profileData.username.isBlank()) {
                user.setUsername(profileData.username);
            }
            if (profileData.nickname != null && !profileData.nickname.isBlank()) {
                user.setNickname(profileData.nickname);
            }
            if (profileData.email != null && !profileData.email.isBlank()) {
                user.setEmail(profileData.email);
            }
            if (profileData.gender != null && !profileData.gender.isBlank()) {
                user.setGender(profileData.gender);
            }
            if (profileData.age >= 0) {
                user.setAge(profileData.age);
            }
            if (profileData.profilePictureId != null && !profileData.profilePictureId.isBlank()) {
                user.setProfilePictureId(profileData.profilePictureId);
            }
            if (profileData.securityQuestionNumber > 0) {
                user.setSecurityQuestionNumber(profileData.securityQuestionNumber);
            }
        }

        App.setCurrentUser(user);

        if (user != null) {
            App.setCurrentProgress(user.getGameProgress());
            NewsService.ensureReady(user);
            app.service.news.NewsPublisher.refreshGreenhouseReadyNews(user);
        }

        gameDataDirty = false;
    }

    public static void markGameDataDirty() {
        gameDataDirty = true;
    }

    public static boolean hasUnsavedGameData() {
        return gameDataDirty;
    }

    public static void syncGameData() {
        syncGameData(result -> {
        });
    }

    public static void updateAllUsers() {
        syncGameData();
    }

    public static void syncGameData(Consumer<Result> callback) {
        User user = App.getCurrentUser();

        if (user == null) {
            callback.accept(Result.failure("No logged-in user."));
            return;
        }

        boolean localSaved = ProgressService.saveUserProgress(user);
        if (!localSaved) {
            gameDataDirty = true;
            callback.accept(Result.failure("Could not save local progress."));
            return;
        }

        if (gameClient == null) {
            gameDataDirty = false;
            callback.accept(Result.success("Local game data saved."));
            return;
        }
        if (syncInProgress) {
            syncRequestedAgain = true;
            callback.accept(Result.success("Save is already in progress."));
            return;
        }
        syncInProgress = true;

        Packets.GameDataSyncRequest request =
            new Packets.GameDataSyncRequest();

        request.coins = user.getCoins();
        request.gems = user.getGems();
        request.mioPoint = user.getMioPoint();
        request.gamesPlayed = user.getGamesPlayed();
        request.difficulty = user.getDifficultyLevel();
        request.gameStateJson = GameStateSerializer.replaceQuestManager(null, user.getQuestManager());

        gameClient.sendSync(request, response -> {
            syncInProgress = false;

            if (!response.success) {
                gameDataDirty = true;
                callback.accept(Result.failure(response.message));
                return;
            }
            gameDataDirty = false;

            callback.accept(Result.success("Game data saved."));

            if (syncRequestedAgain) {
                syncRequestedAgain = false;
                gameDataDirty = true;
                syncGameData();
            }
        });
    }

    public static void syncGameDataIfDirty() {
        if (gameDataDirty && !syncInProgress) {
            syncGameData();
        }
    }

    private static void applyServerQuests(User user, String gameStateJson) {
        if (user == null || gameStateJson == null || gameStateJson.isBlank()) {
            return;
        }
        var serverQuests = GameStateSerializer.readQuestManager(gameStateJson);
        if (serverQuests == null) {
            return;
        }
        QuestManager local = user.getQuestManager();
        if (local == null
            || (local.getActiveQuests().isEmpty() && local.getCompletedQuests().isEmpty())) {
            user.setQuestManager(serverQuests);
            return;
        }
        user.setQuestManager(QuestManager.merged(local, serverQuests));
    }
}
