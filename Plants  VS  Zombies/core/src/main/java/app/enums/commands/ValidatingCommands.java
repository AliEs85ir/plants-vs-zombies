package app.enums.commands;

public enum ValidatingCommands implements Commands{
    USERNAME_FORMAT("[a-zA-Z0-9\\-]+"),
    NICKNAME_FORMAT(".{3,30}"),
    PASSWORD_FORMAT("^[a-zA-Z0-9!#$%^&*)(=+{}\\[\\]\\|\\\\/:;'\"<>,?]+$"),
    EMAIL_FORMAT("[a-zA-Z0-9]+([._-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-][a-zA-Z0-9]+)*(\\.[a-zA-Z0-9]+([-][a-zA-Z0-9]+)*)*\\.[a-zA-Z0-9]{2,}$"),
    GENDER_FORMAT("\\s*man|woman|male|female\\s*"),
    PASSWORD_STRENGTH("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!#$%^&*()=+{}\\[\\]|/\\\\:;'\",<>?]).{8,}$")
    ;
    private final String pattern;

    ValidatingCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
