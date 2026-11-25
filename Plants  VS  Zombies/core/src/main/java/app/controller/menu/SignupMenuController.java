package app.controller.menu;

import app.enums.SecurityQuestions;
import app.service.AppService;
import app.validator.AppValidator;
import app.validator.ValidationMessages;
import common.Result;
import server.network.Packets;

import java.util.function.Consumer;

public class SignupMenuController {

    public void validateIdentity(
        String username,
        String nickName,
        String email,
        String gender,
        String age,
        Consumer<Result> callback
    ) {
        validateUsername(username, usernameResult -> {
            if (!usernameResult.success()) {
                callback.accept(usernameResult);
                return;
            }
            callback.accept(validateProfileFields(nickName, email, gender, age));
        });
    }

    public Result validatePasswordPair(String password, String passwordConfirm) {
        ValidationMessages messages = ValidationMessages.active();
        password = password == null ? "" : password;
        passwordConfirm = passwordConfirm == null ? "" : passwordConfirm;
        if(!AppValidator.isPasswordCorrect(password)) {
            return new Result(false, messages.invalidPassword);
        }
        if(!password.equals(passwordConfirm)) {
            return new Result(false, messages.passwordMismatch);
        }
        if(!AppValidator.isPasswordStrong(password)) {
            return new Result(false, messages.weakPassword);
        }
        return new Result(true, "");
    }

    public Result validateSecurity(
            String qNumber,
            String answer,
            String answerConfirm
    ) {
        ValidationMessages messages = ValidationMessages.active();
        answer = answer == null ? "" : answer;
        answerConfirm = answerConfirm == null ? "" : answerConfirm;
        if(!AppValidator.isNumberValid(qNumber)){
            return new Result(false, messages.invalidQuestionNumber);
        }
        int questionNumber = Integer.parseInt(qNumber.trim());
        if(SecurityQuestions.getQuestionByNumber(questionNumber) == null) {
            return new Result(false, messages.unknownQuestion);
        }
        if(!AppValidator.isSecurityAnswerValid(answer) || !AppValidator.isSecurityAnswerValid(answerConfirm)) {
            return new Result(false, messages.invalidAnswer);
        }
        if(!answer.equals(answerConfirm)) {
            return new Result(false, messages.answerMismatch);
        }
        return new Result(true, "");
    }


    public void finalizeRegister(
        String username,
        String password,
        String passwordConfirm,
        String nickname,
        String email,
        String gender,
        String questionNumber,
        String answer,
        String answerConfirm,
        String age,
        Consumer<Result> callback
    ) {
        validateIdentity(username, nickname, email, gender, age, identityResult -> {
            if (!identityResult.success()) {
                callback.accept(identityResult);
                return;
            }
            continueRegister(username, password, passwordConfirm, nickname, email,
                gender, questionNumber, answer, answerConfirm, age, callback);
        });
    }

    private void validateUsername(String username, Consumer<Result> callback) {
        ValidationMessages messages = ValidationMessages.active();
        username = username == null ? "" : username;

        if (!AppValidator.isUsernameCorrect(username)) {
            callback.accept(new Result(false, messages.invalidUsername));
            return;
        }

        checkUsernameAvailable(username, result -> {
            if (!result.success()) {
                callback.accept(Result.failure("username is used by another player. please try another one."));
            } else {
                callback.accept(new Result(true, ""));
            }
        });
    }

    private Result validateProfileFields(String nickName, String email, String gender, String age) {
        ValidationMessages messages = ValidationMessages.active();
        nickName = nickName == null ? "" : nickName;
        email = email == null ? "" : email;
        gender = gender == null ? "" : gender;
        if(!AppValidator.isNicknameCorrect(nickName)) {
            return new Result(false, messages.invalidNickname);
        }
        if(!AppValidator.isEmailCorrect(email)) {
            return new Result(false, messages.invalidEmail);
        }
        if(!AppValidator.isGenderValid(gender)) {
            return new Result(false, messages.invalidGender);
        }
        if (age != null && !AppValidator.isNonNegativeInteger(age)) {
            return new Result(false, messages.invalidAge);
        }
        return new Result(true, "");
    }

    public void checkUsernameAvailable(String username, Consumer<Result> callback) {
        if (AppService.client() == null) {
            callback.accept(Result.failure("Server unavailable."));
            return;
        }

        Packets.ProfileRequest request = new Packets.ProfileRequest();
        request.username = username.trim();

        AppService.client().sendProfile(request, response -> {
            if (response.success) {
                callback.accept(Result.failure("Username already taken."));
            } else {
                callback.accept(Result.success("Username available."));
            }
        });
    }

    private void continueRegister(
        String username,
        String password,
        String passwordConfirm,
        String nickname,
        String email,
        String gender,
        String questionNumber,
        String answer,
        String answerConfirm,
        String age,
        Consumer<Result> callback
    ) {
        Result passwordResult = validatePasswordPair(password, passwordConfirm);

        if (!passwordResult.success()) {
            callback.accept(passwordResult);
            return;
        }

        Result securityResult = validateSecurity(questionNumber, answer, answerConfirm);

        if (!securityResult.success()) {
            callback.accept(securityResult);
            return;
        }

        Packets.RegisterRequest request = new Packets.RegisterRequest();
        request.username = username.trim();
        request.password = password;
        request.nickname = nickname.trim();
        request.email = email.trim();
        request.gender = gender.trim();
        request.age = age == null ? "" : age.trim();
        request.securityQuestion = questionNumber.trim();
        request.securityAnswer = answer.trim();

        AppService.register(request, true, callback);
    }
}
