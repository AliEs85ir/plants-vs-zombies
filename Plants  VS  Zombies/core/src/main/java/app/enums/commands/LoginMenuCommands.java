package app.enums.commands;

public enum LoginMenuCommands implements Commands{
    FORGOT_PASSWORD("\\s*forget\\s+password\\s+-u\\s+(?<username>.*?)\\s*-e\\s*(?<email>.*?)\\s*"),
    LOGIN("\\s*login\\s+-u\\s+(?<username>.*?)\\s*-p\\s+(?<password>.*?)\\s*(?<stay>-stay-logged-in)?\\s*"),
    ANSWER("\\s*answer\\s+-a\\s+(?<answer>.*?)\\s*"),

    ;
    private final String pattern;

    LoginMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
