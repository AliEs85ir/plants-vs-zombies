package server.auth;

import app.validator.AuthErrorCodes;
import common.utils.HashUtil;
import server.network.GameStateSerializer;
import server.network.Packets;
import server.quest.QuestProgressStore;
import server.user.ScoringLeaderboard;
import server.user.ServerUserRepository;
import server.user.UserAccount;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerAuthService {
    private static final int STARTING_GEMS = 150;
    private static final int DEFAULT_DIFFICULTY = 3;

    private final ServerUserRepository repository;
    private final QuestProgressStore questProgressStore;

    // token -> username
    private final ConcurrentHashMap<String, String> tokenToUsername = new ConcurrentHashMap<>();

    public ServerAuthService(ServerUserRepository repository) {
        this(repository, new QuestProgressStore());
    }

    public ServerAuthService(ServerUserRepository repository, QuestProgressStore questProgressStore) {
        this.repository = repository;
        this.questProgressStore = questProgressStore;
        rebuildSessionsFromAccounts();
    }

    private void rebuildSessionsFromAccounts() {
        for (UserAccount account : repository.findAll()) {
            String token = account.getSessionToken();
            if (token != null && !token.isBlank()) {
                tokenToUsername.put(token.trim(), account.getUsername());
            }
        }
    }

    public Packets.RegisterResponse register(Packets.RegisterRequest req) {
        if (req == null) {
            return Packets.RegisterResponse.fail("Invalid request.");
        }

        String username = trim(req.username);
        String password = req.password;
        String nickname = trim(req.nickname);
        String email = trim(req.email);
        String gender = trim(req.gender);
        String securityAnswer = trim(req.securityAnswer);

        int securityQuestionNumber = ServerSecurityQuestions.parse(req.securityQuestion);
        int age = parseAge(req.age);

        String error;

        if ((error = ServerValidator.validateUsername(username)) != null) {
            return Packets.RegisterResponse.fail(error);
        }

        if ((error = ServerValidator.validatePassword(password)) != null) {
            return Packets.RegisterResponse.fail(error);
        }

        if ((error = ServerValidator.validateNickname(nickname, username)) != null) {
            return Packets.RegisterResponse.fail(error);
        }

        if ((error = ServerValidator.validateEmail(email)) != null) {
            return Packets.RegisterResponse.fail(error);
        }

        if ((error = ServerValidator.validateGender(gender)) != null) {
            return Packets.RegisterResponse.fail(error);
        }

        if (age < -1) {
            return Packets.RegisterResponse.fail("Age must be a non-negative integer.");
        }

        if (!ServerSecurityQuestions.isValidNumber(securityQuestionNumber)) {
            return Packets.RegisterResponse.fail("Invalid security question.");
        }

        if (securityAnswer.isEmpty()) {
            return Packets.RegisterResponse.fail("Security answer is required.");
        }

        if (repository.findByUsername(username).isPresent()) {
            return Packets.RegisterResponse.fail(
                AuthErrorCodes.USERNAME_ALREADY_EXISTS,
                AuthErrorCodes.localize(AuthErrorCodes.USERNAME_ALREADY_EXISTS, "Username already taken.")
            );
        }

        if (repository.findByEmail(email).isPresent()) {
            return Packets.RegisterResponse.fail(
                AuthErrorCodes.EMAIL_ALREADY_EXISTS,
                AuthErrorCodes.localize(AuthErrorCodes.EMAIL_ALREADY_EXISTS, "Email already registered.")
            );
        }

        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPasswordHash(HashUtil.hash(password));
        account.setNickname(nickname);
        account.setEmail(email);
        account.setGender(gender);
        account.setAge(age);
        account.setSecurityQuestionNumber(securityQuestionNumber);
        account.setSecurityAnswerHash(HashUtil.hash(securityAnswer));

        account.setGamesPlayed(0);
        account.setCoins(0);
        account.setGems(STARTING_GEMS);
        account.setMioPoint(0);
        account.setDifficulty(DEFAULT_DIFFICULTY);
        account.setRegisteredAtEpoch(System.currentTimeMillis());
        questProgressStore.initializeNewAccount(account);

        if (!repository.add(account)) {
            return Packets.RegisterResponse.fail("Could not create account.");
        }

        String token = createSession(account.getUsername());

        return Packets.RegisterResponse.ok(
            profileWithQuests(account),
            token,
            "Account created successfully."
        );
    }

    public Packets.LoginResponse login(Packets.LoginRequest req) {
        if (req == null) {
            return Packets.LoginResponse.fail("Invalid request.");
        }

        String username = trim(req.username);
        String password = req.password;

        if (username.isEmpty() || password == null || password.isEmpty()) {
            return Packets.LoginResponse.fail("Username and password are required.");
        }

        UserAccount account = repository.findByUsername(username).orElse(null);

        if (account == null || !HashUtil.verify(password, account.getPasswordHash())) {
            return Packets.LoginResponse.fail("Invalid username or password.");
        }

        String token = createSession(account.getUsername());

        return Packets.LoginResponse.ok(
            profileWithQuests(account),
            token,
            "Welcome back!"
        );
    }

    public Packets.LoginResponse restoreSession(Packets.SessionRestoreRequest req) {
        if (req == null || req.sessionToken == null || req.sessionToken.isBlank()) {
            return Packets.LoginResponse.fail("No saved session.");
        }

        String token = req.sessionToken.trim();
        String username = tokenToUsername.get(token);

        if (username == null) {
            UserAccount byToken = repository.findBySessionToken(token).orElse(null);
            if (byToken != null) {
                username = byToken.getUsername();
                tokenToUsername.put(token, username);
            }
        }

        if (username == null) {
            return Packets.LoginResponse.fail("Session expired. Please log in again.");
        }

        UserAccount account = repository.findByUsername(username).orElse(null);

        if (account == null) {
            tokenToUsername.remove(token);
            return Packets.LoginResponse.fail("Account no longer exists.");
        }

        if (account.getSessionToken() == null
            || !token.equals(account.getSessionToken())) {
            tokenToUsername.remove(token);
            return Packets.LoginResponse.fail("Session expired. Please log in again.");
        }

        return Packets.LoginResponse.ok(
            profileWithQuests(account),
            token,
            "Session restored."
        );
    }

    public Packets.ProfileResponse loadProfile(Packets.ProfileRequest req) {
        if (req == null) {
            return Packets.ProfileResponse.fail("Invalid request.");
        }

        UserAccount account = repository.findByUsername(trim(req.username)).orElse(null);

        if (account == null) {
            return Packets.ProfileResponse.fail(
                AuthErrorCodes.ACCOUNT_NOT_FOUND,
                AuthErrorCodes.localize(AuthErrorCodes.ACCOUNT_NOT_FOUND, "Account not found.")
            );
        }

        return Packets.ProfileResponse.ok(profileWithQuests(account));
    }

    public Packets.ForgotPasswordResponse forgotPassword(Packets.ForgotPasswordRequest req) {
        if (req == null) {
            return Packets.ForgotPasswordResponse.fail("Invalid request.");
        }

        UserAccount account = repository.findByEmail(trim(req.email)).orElse(null);

        if (account == null) {
            return Packets.ForgotPasswordResponse.fail("No account found for this email.");
        }

        String question = ServerSecurityQuestions.questionText(account.getSecurityQuestionNumber());

        return Packets.ForgotPasswordResponse.ok(question == null ? "" : question);
    }

    public Packets.ResetPasswordResponse resetPassword(Packets.ResetPasswordRequest req) {
        if (req == null) {
            return Packets.ResetPasswordResponse.fail("Invalid request.");
        }

        UserAccount account = repository.findByEmail(trim(req.email)).orElse(null);

        if (account == null) {
            return Packets.ResetPasswordResponse.fail("No account found for this email.");
        }

        String answer = trim(req.securityAnswer);

        if (answer.isEmpty() ||
            !HashUtil.verify(answer, account.getSecurityAnswerHash())) {
            return Packets.ResetPasswordResponse.fail("Incorrect security answer.");
        }

        String error = ServerValidator.validatePassword(req.newPassword);

        if (error != null) {
            return Packets.ResetPasswordResponse.fail(error);
        }

        String oldUsername = account.getUsername();
        account.setPasswordHash(HashUtil.hash(req.newPassword));

        if (!repository.update(oldUsername, account)) {
            return Packets.ResetPasswordResponse.fail("Could not update password.");
        }

        return Packets.ResetPasswordResponse.ok();
    }

    public Packets.ProfileResponse updateProfile(
        Packets.ProfileUpdateRequest req,
        String authenticatedUsername
    ) {
        if (req == null) {
            return Packets.ProfileResponse.fail("Invalid request.");
        }

        if (authenticatedUsername == null || authenticatedUsername.isBlank()) {
            return Packets.ProfileResponse.fail(
                AuthErrorCodes.NOT_AUTHENTICATED,
                AuthErrorCodes.localize(AuthErrorCodes.NOT_AUTHENTICATED, "Not authenticated.")
            );
        }

        UserAccount account = repository.findByUsername(authenticatedUsername).orElse(null);

        if (account == null) {
            return Packets.ProfileResponse.fail(
                AuthErrorCodes.ACCOUNT_NOT_FOUND,
                AuthErrorCodes.localize(AuthErrorCodes.ACCOUNT_NOT_FOUND, "Account not found.")
            );
        }

        String oldUsername = account.getUsername();

        boolean changingUsername =
            req.newUsername != null &&
                !req.newUsername.isBlank() &&
                !req.newUsername.trim().equalsIgnoreCase(account.getUsername());

        boolean changingEmail =
            req.newEmail != null &&
                !req.newEmail.isBlank() &&
                !req.newEmail.trim().equalsIgnoreCase(account.getEmail());

        if (changingUsername || changingEmail) {
            String answer = trim(req.securityAnswer);

            if (answer.isEmpty()) {
                return Packets.ProfileResponse.fail(
                    AuthErrorCodes.SECURITY_ANSWER_REQUIRED,
                    AuthErrorCodes.localize(
                        AuthErrorCodes.SECURITY_ANSWER_REQUIRED,
                        "Security answer is required to change username or email."
                    )
                );
            }

            if (!HashUtil.verify(answer, account.getSecurityAnswerHash())) {
                return Packets.ProfileResponse.fail(
                    AuthErrorCodes.SECURITY_ANSWER_INCORRECT,
                    AuthErrorCodes.localize(
                        AuthErrorCodes.SECURITY_ANSWER_INCORRECT,
                        "Incorrect security answer."
                    )
                );
            }
        }

        if (changingUsername) {
            String newUsername = req.newUsername.trim();
            String error = ServerValidator.validateUsername(newUsername);

            if (error != null) {
                return Packets.ProfileResponse.fail(
                    AuthErrorCodes.USERNAME_INVALID,
                    AuthErrorCodes.localize(AuthErrorCodes.USERNAME_INVALID, error)
                );
            }

            if (repository.findByUsername(newUsername).isPresent()) {
                return Packets.ProfileResponse.fail(
                    AuthErrorCodes.USERNAME_ALREADY_EXISTS,
                    AuthErrorCodes.localize(AuthErrorCodes.USERNAME_ALREADY_EXISTS, "Username already taken.")
                );
            }

            account.setUsername(newUsername);
        }

        if (req.newNickname != null && !req.newNickname.isBlank()) {
            String newNickname = req.newNickname.trim();
            String error = ServerValidator.validateNickname(newNickname, account.getUsername());

            if (error != null) {
                return Packets.ProfileResponse.fail(error);
            }

            account.setNickname(newNickname);
        }

        if (changingEmail) {
            String newEmail = req.newEmail.trim();
            String error = ServerValidator.validateEmail(newEmail);

            if (error != null) {
                return Packets.ProfileResponse.fail(
                    AuthErrorCodes.EMAIL_INVALID,
                    AuthErrorCodes.localize(AuthErrorCodes.EMAIL_INVALID, error)
                );
            }

            if (repository.findByEmail(newEmail).isPresent()) {
                return Packets.ProfileResponse.fail(
                    AuthErrorCodes.EMAIL_ALREADY_EXISTS,
                    AuthErrorCodes.localize(AuthErrorCodes.EMAIL_ALREADY_EXISTS, "Email already registered.")
                );
            }

            account.setEmail(newEmail);
        }

        if (req.newProfilePictureId != null && !req.newProfilePictureId.isBlank()) {
            String pictureId = req.newProfilePictureId.trim();
            if (!app.model.user.ProfilePictures.isKnown(pictureId)) {
                return Packets.ProfileResponse.fail("Please choose a profile picture.");
            }
            account.setProfilePictureId(pictureId);
        }

        if (req.newPassword != null && !req.newPassword.isEmpty()) {
            String error = ServerValidator.validatePassword(req.newPassword);

            if (error != null) {
                return Packets.ProfileResponse.fail(error);
            }

            account.setPasswordHash(HashUtil.hash(req.newPassword));
        }

        if (!oldUsername.equals(account.getUsername())) {
            questProgressStore.relocate(oldUsername, account);
        }

        if (!repository.update(oldUsername, account)) {
            return Packets.ProfileResponse.fail("Could not update profile.");
        }

        // Username changed: all active sessions must point to the new username.
        if (!oldUsername.equals(account.getUsername())) {
            tokenToUsername.replaceAll((token, username) ->
                username.equalsIgnoreCase(oldUsername)
                    ? account.getUsername()
                    : username
            );
        }

        return Packets.ProfileResponse.ok(profileWithQuests(account));
    }

    public Packets.ProfileResponse syncGameData(
        Packets.GameDataSyncRequest request,
        String authenticatedUsername
    ) {
        if (request == null) {
            return Packets.ProfileResponse.fail("Invalid request.");
        }

        if (authenticatedUsername == null ||
            authenticatedUsername.isBlank()) {

            return Packets.ProfileResponse.fail(
                AuthErrorCodes.NOT_AUTHENTICATED,
                AuthErrorCodes.localize(AuthErrorCodes.NOT_AUTHENTICATED, "Not authenticated.")
            );
        }

        UserAccount account = repository
            .findByUsername(authenticatedUsername)
            .orElse(null);

        if (account == null) {
            return Packets.ProfileResponse.fail(
                AuthErrorCodes.ACCOUNT_NOT_FOUND,
                AuthErrorCodes.localize(AuthErrorCodes.ACCOUNT_NOT_FOUND, "Account not found.")
            );
        }

        if (request.coins < 0 ||
            request.gems < 0 ||
            request.mioPoint < 0 ||
            request.gamesPlayed < 0 ||
            request.difficulty < 1) {

            return Packets.ProfileResponse.fail("Invalid game data.");
        }

        String oldUsername = account.getUsername();

        account.setCoins(request.coins);
        account.setGems(request.gems);
        account.setMioPoint(Math.max(account.getMioPoint(), request.mioPoint));
        account.setGamesPlayed(request.gamesPlayed);
        account.setDifficulty(request.difficulty);

        if (request.gameStateJson != null) {
            questProgressStore.saveFromGameState(account, request.gameStateJson);
        }

        if (!repository.update(oldUsername, account)) {
            return Packets.ProfileResponse.fail("Could not save game data.");
        }

        return Packets.ProfileResponse.ok(profileWithQuests(account));
    }

    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        String username = tokenToUsername.remove(token.trim());
        if (username == null) {
            UserAccount byToken = repository.findBySessionToken(token.trim()).orElse(null);
            if (byToken != null) {
                username = byToken.getUsername();
            }
        }

        if (username == null) {
            return;
        }

        UserAccount account = repository.findByUsername(username).orElse(null);
        if (account == null) {
            return;
        }

        if (token.trim().equals(account.getSessionToken())) {
            account.setSessionToken(null);
            repository.update(account.getUsername(), account);
        }
    }

    private String createSession(String username) {
        tokenToUsername.entrySet().removeIf(entry -> entry.getValue().equalsIgnoreCase(username));
        String token = UUID.randomUUID().toString();
        tokenToUsername.put(token, username);

        UserAccount account = repository.findByUsername(username).orElse(null);
        if (account != null) {
            account.setSessionToken(token);
            repository.update(account.getUsername(), account);
        }

        return token;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static int parseAge(String rawAge) {
        if (rawAge == null || rawAge.isBlank()) {
            return -1;
        }

        try {
            int age = Integer.parseInt(rawAge.trim());
            return age >= 0 ? age : -2;
        } catch (NumberFormatException e) {
            return -2;
        }
    }

    public Packets.FullGameDataResponse saveFullGameData(
        Packets.FullGameDataRequest request,
        String authenticatedUsername
    ) {
        if (authenticatedUsername == null || authenticatedUsername.isBlank()) {
            return Packets.FullGameDataResponse.fail("Not authenticated.");
        }

        if (request == null || request.gameStateJson == null) {
            return Packets.FullGameDataResponse.fail("Invalid game state.");
        }

        UserAccount account = repository.findByUsername(authenticatedUsername).orElse(null);

        if (account == null) {
            return Packets.FullGameDataResponse.fail("Account not found.");
        }

        String oldUsername = account.getUsername();

        account.setGameStateJson(request.gameStateJson);
        questProgressStore.saveFromGameState(account, request.gameStateJson);

        if (!repository.update(oldUsername, account)) {
            return Packets.FullGameDataResponse.fail("Could not save game state.");
        }

        return Packets.FullGameDataResponse.ok(gameStateWithQuests(account));
    }

    public Packets.FullGameDataResponse loadFullGameData(String authenticatedUsername) {
        if (authenticatedUsername == null || authenticatedUsername.isBlank()) {
            return Packets.FullGameDataResponse.fail("Not authenticated.");
        }

        UserAccount account = repository.findByUsername(authenticatedUsername).orElse(null);

        if (account == null) {
            return Packets.FullGameDataResponse.fail("Account not found.");
        }

        persistQuestPathIfNeeded(account);
        return Packets.FullGameDataResponse.ok(gameStateWithQuests(account));
    }

    public Packets.LeaderboardResponse leaderboard() {
        return Packets.LeaderboardResponse.ok(ScoringLeaderboard.fromAccounts(repository.findAll()));
    }

    private Packets.ProfileData profileWithQuests(UserAccount account) {
        persistQuestPathIfNeeded(account);
        Packets.ProfileData profile = account.toProfileData();
        profile.gameStateJson = gameStateWithQuests(account);
        return profile;
    }

    private String gameStateWithQuests(UserAccount account) {
        return GameStateSerializer.replaceQuestManager(
            account.getGameStateJson(),
            questProgressStore.loadOrCreate(account)
        );
    }

    private void persistQuestPathIfNeeded(UserAccount account) {
        if (questProgressStore.ensureAssigned(account)) {
            repository.update(account.getUsername(), account);
        }
    }
}
