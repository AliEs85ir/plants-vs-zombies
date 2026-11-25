package app.enums.commands;

public enum MainMenuCommands implements Commands{
    Logout("^\\s*menu\\s+logout\\s*$");
    private final String pattern;

    MainMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
