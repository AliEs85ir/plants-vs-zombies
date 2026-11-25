package app.validator;

public class ValidationMessages {
    private static ValidationMessages active;

    public String invalidUsername = "Invalid username format, Please try again.";
    public String duplicateUsername = "این نام کاربری قبلاً استفاده شده است";
    public String passwordTooShort = "Password must be at least 8 characters.";
    public String passwordNeedLower = "Password must contain a lowercase letter.";
    public String passwordNeedUpper = "Password must contain an uppercase letter.";
    public String passwordNeedDigit = "Password must contain a digit.";
    public String passwordNeedSpecial = "Password must contain a special character.";
    public String invalidPassword = "Invalid password format, Please try again.";
    public String passwordMismatch = "Password and its confirmation are not equal.\nPlease enter password again.";
    public String weakPassword = "Password is not strong, Please try again.";
    public String invalidNickname = "Invalid nickname format, Please try again.";
    public String invalidEmail = "Invalid email format, Please try again.";
    public String duplicateEmail = "Suggested email already exist.";
    public String invalidGender = "Invalid gender format, Please try again.";
    public String invalidAge = "Value must be a non-negative integer.";
    public String invalidQuestionNumber = "Question number must be integer, Please try again";
    public String unknownQuestion = "Invalid security question number, Please try again";
    public String answerMismatch = "Answers do not match, Please try again";
    public String invalidAnswer = "Invalid security answer, Please try again.";
    public String registerSuccessPrefix = "Registering finalized!, welcome ";
    public String usernameNotFound = "Username doesn't exist.";
    public String wrongPassword = "Password is not correct.";
    public String welcomePrefix = "Welcome ";
    public String incorrectEmail = "Incorrect email.";
    public String emailNotFound = "Email doesn't exist.";
    public String securityQuestionPrefix = "Please answer security question :\n";
    public String securityAnswerCorrect = "Answer is correct. Please enter your new password:";
    public String incorrectSecurityAnswer = "Incorrect security answer. Please try again.";
    public String invalidPasswordReturning = "Invalid password format, returning back to menu.";
    public String passwordChanged = "Password changed successfully.";
    public String missingSecurityQuestion = "Security question is missing for this gardener.";
    public String missingSecurityAnswer = "Security answer is missing for this gardener.";
    public String missingUser = "User not found.";
    public String missingConfiguration = "Authentication configuration is missing.";

    public ValidationMessages sanitize() {
        invalidUsername = text(invalidUsername, "Invalid username format, Please try again.");
        duplicateUsername = text(duplicateUsername, "این نام کاربری قبلاً استفاده شده است");
        passwordTooShort = text(passwordTooShort, "Password must be at least 8 characters.");
        passwordNeedLower = text(passwordNeedLower, "Password must contain a lowercase letter.");
        passwordNeedUpper = text(passwordNeedUpper, "Password must contain an uppercase letter.");
        passwordNeedDigit = text(passwordNeedDigit, "Password must contain a digit.");
        passwordNeedSpecial = text(passwordNeedSpecial, "Password must contain a special character.");
        invalidPassword = text(invalidPassword, "Invalid password format, Please try again.");
        passwordMismatch = text(
            passwordMismatch,
            "Password and its confirmation are not equal.\nPlease enter password again."
        );
        weakPassword = text(weakPassword, "Password is not strong, Please try again.");
        invalidNickname = text(invalidNickname, "Invalid nickname format, Please try again.");
        invalidEmail = text(invalidEmail, "Invalid email format, Please try again.");
        duplicateEmail = text(duplicateEmail, "Suggested email already exist.");
        invalidGender = text(invalidGender, "Invalid gender format, Please try again.");
        invalidAge = text(invalidAge, "Value must be a non-negative integer.");
        invalidQuestionNumber = text(invalidQuestionNumber, "Question number must be integer, Please try again");
        unknownQuestion = text(unknownQuestion, "Invalid security question number, Please try again");
        answerMismatch = text(answerMismatch, "Answers do not match, Please try again");
        invalidAnswer = text(invalidAnswer, "Invalid security answer, Please try again.");
        registerSuccessPrefix = text(registerSuccessPrefix, "Registering finalized!, welcome ");
        usernameNotFound = text(usernameNotFound, "Username doesn't exist.");
        wrongPassword = text(wrongPassword, "Password is not correct.");
        welcomePrefix = text(welcomePrefix, "Welcome ");
        incorrectEmail = text(incorrectEmail, "Incorrect email.");
        emailNotFound = text(emailNotFound, "Email doesn't exist.");
        securityQuestionPrefix = text(securityQuestionPrefix, "Please answer security question :\n");
        securityAnswerCorrect = text(securityAnswerCorrect, "Answer is correct. Please enter your new password:");
        incorrectSecurityAnswer = text(incorrectSecurityAnswer, "Incorrect security answer. Please try again.");
        invalidPasswordReturning = text(invalidPasswordReturning, "Invalid password format, returning back to menu.");
        passwordChanged = text(passwordChanged, "Password changed successfully.");
        missingSecurityQuestion = text(missingSecurityQuestion, "Security question is missing for this gardener.");
        missingSecurityAnswer = text(missingSecurityAnswer, "Security answer is missing for this gardener.");
        missingUser = text(missingUser, "User not found.");
        missingConfiguration = text(missingConfiguration, "Authentication configuration is missing.");
        return this;
    }

    public static ValidationMessages defaults() {
        return new ValidationMessages().sanitize();
    }

    public static ValidationMessages active() {
        if (active == null) {
            active = ValidationMessagesLoader.load();
        }
        return active;
    }

    public static void setActive(ValidationMessages messages) {
        active = messages == null ? defaults() : messages.sanitize();
    }

    public String fieldIdFor(String message) {
        if (message == null) {
            return "";
        }
        if (message.equals(invalidUsername) || message.equals(duplicateUsername)
            || "USERNAME_ALREADY_EXISTS".equals(message)) {
            return "username";
        }
        if (message.equals(invalidNickname)) {
            return "nickname";
        }
        if (message.equals(invalidEmail) || message.equals(duplicateEmail) || message.equals(emailNotFound)
            || message.equals(incorrectEmail)) {
            return "email";
        }
        if (message.equals(invalidGender)) {
            return "gender";
        }
        if (message.equals(invalidAge)) {
            return "age";
        }
        if (message.equals(invalidPassword) || message.equals(weakPassword) || message.equals(passwordMismatch)
            || message.equals(wrongPassword) || message.equals(invalidPasswordReturning)) {
            return "password";
        }
        if (message.equals(invalidQuestionNumber) || message.equals(unknownQuestion) || message.equals(missingSecurityQuestion)) {
            return "question";
        }
        if (message.equals(answerMismatch) || message.equals(incorrectSecurityAnswer)
            || message.equals(missingSecurityAnswer) || message.equals(invalidAnswer)) {
            return "answer";
        }
        if (message.equals(usernameNotFound) || message.equals(missingUser)) {
            return "username";
        }
        return "";
    }

    private static String text(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
