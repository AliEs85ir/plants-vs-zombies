package server.user;

import server.network.Packets;

public class UserAccount {
    private String username;
    private String passwordHash;
    private int securityQuestionNumber;
    private String securityAnswerHash;
    private String nickname;
    private String email;
    private String gender;
    private int age = -1;
    private long registeredAtEpoch;
    private int gamesPlayed;
    private int coins;
    private int gems;
    private int mioPoint;
    private int difficulty = 3;
    private String gameStateJson;
    private String sessionToken;
    private String questProgressPath;
    private String profilePictureId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getSecurityQuestionNumber() {
        return securityQuestionNumber;
    }

    public void setSecurityQuestionNumber(int securityQuestionNumber) {
        this.securityQuestionNumber = securityQuestionNumber;
    }

    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }

    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getRegisteredAtEpoch() {
        return registeredAtEpoch;
    }

    public void setRegisteredAtEpoch(long registeredAtEpoch) {
        this.registeredAtEpoch = registeredAtEpoch;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = Math.max(0, gamesPlayed);
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = Math.max(0, gems);
    }

    public int getMioPoint() {
        return mioPoint;
    }

    public void setMioPoint(int mioPoint) {
        this.mioPoint = Math.max(0, mioPoint);
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    public String getGameStateJson() {
        return gameStateJson;
    }

    public void setGameStateJson(String gameStateJson) {
        this.gameStateJson = gameStateJson;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getQuestProgressPath() {
        return questProgressPath;
    }

    public void setQuestProgressPath(String questProgressPath) {
        this.questProgressPath = questProgressPath;
    }

    public String getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(String profilePictureId) {
        this.profilePictureId = profilePictureId == null || profilePictureId.isBlank()
            ? null
            : profilePictureId.trim();
    }

    public Packets.ProfileData toProfileData() {
        Packets.ProfileData profile = new Packets.ProfileData();

        profile.username = username;
        profile.nickname = nickname;
        profile.email = email;
        profile.gender = gender;
        profile.age = age;
        profile.securityQuestionNumber = securityQuestionNumber;

        profile.gamesPlayed = gamesPlayed;
        profile.coins = coins;
        profile.gems = gems;
        profile.mioPoint = mioPoint;
        profile.difficulty = difficulty;

        profile.registeredAtEpoch = registeredAtEpoch;
        profile.profilePictureId = profilePictureId;
        profile.gameStateJson = gameStateJson;

        return profile;
    }
}
