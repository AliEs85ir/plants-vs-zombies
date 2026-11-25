package app.validator;

import app.enums.commands.ValidatingCommands;

public class AppValidator {

    public static boolean isUsernameCorrect(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return ValidatingCommands.USERNAME_FORMAT.matches(username);
    }
    public static boolean isPasswordCorrect(String password){
        return PasswordRules.hasOnlyAllowedChars(password);
    }
    public static boolean isNicknameCorrect(String nickName) {
        return isNicknameCorrect(nickName, null);
    }

    public static boolean isNicknameCorrect(String nickName, String username) {
        return NicknameRules.isValid(nickName, username);
    }
    public static boolean isEmailCorrect(String email) {
        return EmailValidator.isValid(email);
    }
    public static boolean isGenderValid(String gender) {
        if (gender == null) {
            return false;
        }
        gender = gender.toLowerCase();
        return ValidatingCommands.GENDER_FORMAT.matches(gender);
    }
    public static boolean isNumberValid(String number) {
        if (number == null) {
            return false;
        }
        number = number.trim();
        try {
            Integer.parseInt(number);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean isNonNegativeInteger(String number) {
        if (number == null || !isNumberValid(number)) {
            return false;
        }
        return Integer.parseInt(number.trim()) >= 0;
    }

    public static boolean isSecurityAnswerValid(String answer) {
        if (answer == null) {
            return false;
        }
        String value = answer.trim();
        if (value.isEmpty() || value.length() > 64) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (character < 32 || character == 127) {
                return false;
            }
        }
        return true;
    }
    public static boolean isPasswordStrong(String password) {
        return PasswordRules.isStrong(password);
    }
    public static boolean isCoordinateValid(String posX, String posY) {
        posX = posX.trim();
        posY = posY.trim();
        float x,y;
        try {
            x = Float.parseFloat(posX);
            y = Float.parseFloat(posY);
        }
        catch (Exception e) {
            return false;
        }
        return !(x < 0) && !(y < 0);


    }
}
