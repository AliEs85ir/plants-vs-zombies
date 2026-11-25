package app.controller.menu;

import app.model.App;
import app.model.user.ProfilePictures;
import app.model.user.User;
import app.service.AppService;
import app.service.SecurityQuestionCatalog;
import app.validator.AppValidator;
import app.validator.AuthErrorCodes;
import app.validator.PasswordRules;
import app.validator.ValidationMessages;
import common.Result;
import server.network.Packets;

import java.util.function.Consumer;

public class ProfileMenuController {

    public void manageChangeUsername(
        String username,
        Consumer<Result> callback
    ) {
        manageChangeUsername(username, null, callback);
    }

    public void manageChangeUsername(
        String username,
        String securityAnswer,
        Consumer<Result> callback
    ) {
        User currentUser = App.getCurrentUser();

        if (currentUser == null) {
            callback.accept(Result.failure("You are not logged in."));
            return;
        }

        username = username == null ? "" : username.trim();

        if (username.equalsIgnoreCase(currentUser.getUsername())) {
            callback.accept(new Result(false, "You already have this username."));
            return;
        }

        if (!AppValidator.isUsernameCorrect(username)) {
            callback.accept(Result.failure(
                AuthErrorCodes.USERNAME_INVALID,
                ValidationMessages.active().invalidUsername
            ));
            return;
        }

        Packets.ProfileUpdateRequest request = new Packets.ProfileUpdateRequest();
        request.newUsername = username;
        if (securityAnswer != null) {
            request.securityAnswer = securityAnswer;
        }

        AppService.updateProfile(request, callback);
    }

    public void manageChangeNickname(
        String nickname,
        Consumer<Result> callback
    ) {
        User currentUser = App.getCurrentUser();

        if (currentUser == null) {
            callback.accept(Result.failure("You are not logged in."));
            return;
        }

        nickname = nickname == null ? "" : nickname.trim();

        if (nickname.equals(currentUser.getNickname())) {
            callback.accept(new Result(false, "You already have this nickname."));
            return;
        }

        if (!AppValidator.isNicknameCorrect(nickname, currentUser.getUsername())) {
            callback.accept(Result.failure(
                AuthErrorCodes.INVALID_NICKNAME,
                ValidationMessages.active().invalidNickname
            ));
            return;
        }

        Packets.ProfileUpdateRequest request = new Packets.ProfileUpdateRequest();
        request.newNickname = nickname;

        AppService.updateProfile(request, callback);
    }

    public void manageChangeEmail(
        String email,
        Consumer<Result> callback
    ) {
        manageChangeEmail(email, null, callback);
    }

    public void manageChangeEmail(
        String email,
        String securityAnswer,
        Consumer<Result> callback
    ) {
        User currentUser = App.getCurrentUser();

        if (currentUser == null) {
            callback.accept(Result.failure("You are not logged in."));
            return;
        }

        email = email == null ? "" : email.trim();

        if (email.equalsIgnoreCase(currentUser.getEmail())) {
            callback.accept(new Result(false, "You already have this email."));
            return;
        }

        if (!AppValidator.isEmailCorrect(email)) {
            callback.accept(Result.failure(
                AuthErrorCodes.EMAIL_INVALID,
                ValidationMessages.active().invalidEmail
            ));
            return;
        }

        Packets.ProfileUpdateRequest request = new Packets.ProfileUpdateRequest();
        request.newEmail = email;
        if (securityAnswer != null) {
            request.securityAnswer = securityAnswer;
        }
        AppService.updateProfile(request, callback);
    }

    public Result validateUsernameChange(String username) {
        User currentUser = App.getCurrentUser();
        if (currentUser == null) {
            return Result.failure("You are not logged in.");
        }
        username = username == null ? "" : username.trim();
        if (username.equalsIgnoreCase(currentUser.getUsername())) {
            return new Result(false, "You already have this username.");
        }
        if (!AppValidator.isUsernameCorrect(username)) {
            return Result.failure(
                AuthErrorCodes.USERNAME_INVALID,
                ValidationMessages.active().invalidUsername
            );
        }
        return Result.success("ok");
    }

    public Result validateEmailChange(String email) {
        User currentUser = App.getCurrentUser();
        if (currentUser == null) {
            return Result.failure("You are not logged in.");
        }
        email = email == null ? "" : email.trim();
        if (email.equalsIgnoreCase(currentUser.getEmail())) {
            return new Result(false, "You already have this email.");
        }
        if (!AppValidator.isEmailCorrect(email)) {
            return Result.failure(
                AuthErrorCodes.EMAIL_INVALID,
                ValidationMessages.active().invalidEmail
            );
        }
        return Result.success("ok");
    }

    public String securityQuestionText() {
        User currentUser = App.getCurrentUser();
        if (currentUser == null) {
            return "";
        }
        int number = currentUser.getSecurityQuestionNumber();
        String text = SecurityQuestionCatalog.textForNumber(number);
        if (text == null || text.isBlank()) {
            return ValidationMessages.active().missingSecurityQuestion;
        }
        return text;
    }

    public void manageConfirmNewPassword(
        String newPassword,
        String passwordConfirm,
        Consumer<Result> callback
    ) {
        newPassword = newPassword == null ? "" : newPassword;
        passwordConfirm = passwordConfirm == null ? "" : passwordConfirm;

        if (!newPassword.equals(passwordConfirm)) {
            callback.accept(Result.failure(
                AuthErrorCodes.PASSWORD_MISMATCH,
                ValidationMessages.active().passwordMismatch
            ));
            return;
        }

        var passwordFailures = PasswordRules.failures(newPassword);
        if (!passwordFailures.isEmpty()) {
            callback.accept(Result.failure(
                AuthErrorCodes.INVALID_PASSWORD,
                String.join("\n", passwordFailures)
            ));
            return;
        }

        Packets.ProfileUpdateRequest request = new Packets.ProfileUpdateRequest();
        request.newPassword = newPassword;

        AppService.updateProfile(request, result -> {
            if (result.success()) {
                callback.accept(Result.success(ValidationMessages.active().passwordChanged));
            } else {
                callback.accept(result);
            }
        });
    }

    public Result manageChangeProfilePicture(String pictureId) {
        Result[] box = new Result[1];
        manageChangeProfilePicture(pictureId, result -> box[0] = result);
        return box[0] == null
            ? Result.failure("Profile picture update did not complete.")
            : box[0];
    }

    public void manageChangeProfilePicture(String pictureId, Consumer<Result> callback) {
        User currentUser = App.getCurrentUser();
        if (currentUser == null) {
            callback.accept(Result.failure("You are not logged in."));
            return;
        }

        String id = pictureId == null ? "" : pictureId.trim();
        if (id.isEmpty() || !ProfilePictures.isKnown(id)) {
            callback.accept(Result.failure("Please choose a profile picture."));
            return;
        }

        if (id.equals(currentUser.getProfilePictureId())) {
            callback.accept(Result.success("Profile picture updated."));
            return;
        }

        Packets.ProfileUpdateRequest request = new Packets.ProfileUpdateRequest();
        request.newProfilePictureId = id;
        AppService.updateProfile(request, callback);
    }

    public Result manageChangeCoins(String amount) {
        amount = amount == null ? "" : amount.trim();

        if (!AppValidator.isNonNegativeInteger(amount)) {
            return new Result(false, "Value must be a non-negative integer.");
        }

        User currentUser = App.getCurrentUser();

        if (currentUser == null) {
            return Result.failure("You are not logged in.");
        }

        currentUser.setCoins(Integer.parseInt(amount));
        AppService.syncGameData();

        return new Result(true, "Coins changed successfully.");
    }

    public Result manageChangeGems(String amount) {
        amount = amount == null ? "" : amount.trim();

        if (!AppValidator.isNonNegativeInteger(amount)) {
            return new Result(false, "Value must be a non-negative integer.");
        }

        User currentUser = App.getCurrentUser();

        if (currentUser == null) {
            return Result.failure("You are not logged in.");
        }

        currentUser.setGems(Integer.parseInt(amount));
        AppService.syncGameData();

        return new Result(true, "Gems changed successfully.");
    }
}
