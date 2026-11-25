package app.model.user;

import server.network.Packets;

public class UserFactory {
    private UserFactory() {
    }

    public static User fromProfile(Packets.ProfileData profile) {
        if (profile == null) {
            return null;
        }

        User user = new User();

        user.setUsername(profile.username);
        user.setNickname(profile.nickname);
        user.setEmail(profile.email);
        user.setGender(profile.gender);

        user.setAge(profile.age);
        if (profile.securityQuestionNumber > 0) {
            user.setSecurityQuestionNumber(profile.securityQuestionNumber);
        }
        user.setGamesPlayed(profile.gamesPlayed);
        user.setCoins(profile.coins);
        user.setGems(profile.gems);
        user.setMioPoint(profile.mioPoint);
        user.setDifficultyLevel(profile.difficulty);
        if (profile.profilePictureId != null && !profile.profilePictureId.isBlank()) {
            user.setProfilePictureId(profile.profilePictureId);
        }

        return user;
    }
}
