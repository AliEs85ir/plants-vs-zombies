package app.controller.menu;

import app.service.AppService;
import app.validator.AppValidator;
import app.validator.ValidationMessages;
import common.Result;
import server.network.Packets;

import java.util.function.Consumer;

public class LoginMenuController {

    public void manageLogin(
        String username,
        String password,
        boolean stayLoggedIn,
        Consumer<Result> callback
    ) {
        AppService.login(username, password, stayLoggedIn, callback);
    }

    public void manageForgotPasswordByEmail(
        String email,
        Consumer<Result> callback
    ) {
        ValidationMessages messages = ValidationMessages.active();

        email = email == null ? "" : email.trim();

        if (!AppValidator.isEmailCorrect(email)) {
            callback.accept(new Result(false, messages.invalidEmail));
            return;
        }

        Packets.ForgotPasswordRequest request = new Packets.ForgotPasswordRequest();
        request.email = email;

        if (AppService.client() == null) {
            callback.accept(Result.failure("Server connection is unavailable."));
            return;
        }

        AppService.client().sendForgot(request, response -> {
            if (!response.success) {
                callback.accept(Result.failure(response.message));
                return;
            }

            String question = response.securityQuestionText == null
                ? ""
                : response.securityQuestionText;

            callback.accept(new Result(
                true,
                messages.securityQuestionPrefix + question
            ));
        });
    }

    public void resetPasswordByEmail(
        String email,
        String securityAnswer,
        String newPassword,
        String passwordConfirm,
        Consumer<Result> callback
    ) {
        ValidationMessages messages = ValidationMessages.active();

        email = email == null ? "" : email.trim();
        securityAnswer = securityAnswer == null ? "" : securityAnswer.trim();

        if (!AppValidator.isEmailCorrect(email)) {
            callback.accept(new Result(false, messages.invalidEmail));
            return;
        }

        if (securityAnswer.isEmpty()) {
            callback.accept(new Result(false, messages.missingSecurityAnswer));
            return;
        }

        Result passwordResult = validatePasswordPair(newPassword, passwordConfirm);

        if (!passwordResult.success()) {
            callback.accept(passwordResult);
            return;
        }

        Packets.ResetPasswordRequest request = new Packets.ResetPasswordRequest();
        request.email = email;
        request.securityAnswer = securityAnswer;
        request.newPassword = newPassword;

        if (AppService.client() == null) {
            callback.accept(Result.failure("Server connection is unavailable."));
            return;
        }

        AppService.client().sendReset(request, response -> {
            if (response.success) {
                callback.accept(new Result(true, messages.passwordChanged));
            } else {
                callback.accept(Result.failure(response.message));
            }
        });
    }

    private Result validatePasswordPair(String password, String passwordConfirm) {
        ValidationMessages messages = ValidationMessages.active();

        password = password == null ? "" : password;
        passwordConfirm = passwordConfirm == null ? "" : passwordConfirm;

        if (!AppValidator.isPasswordCorrect(password)) {
            return new Result(false, messages.invalidPasswordReturning);
        }

        if (!password.equals(passwordConfirm)) {
            return new Result(false, messages.passwordMismatch);
        }

        if (!AppValidator.isPasswordStrong(password)) {
            return new Result(false, messages.weakPassword);
        }

        return new Result(true, "");
    }
}
