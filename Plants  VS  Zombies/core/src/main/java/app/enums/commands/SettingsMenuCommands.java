package app.enums.commands;

public enum SettingsMenuCommands implements Commands{
    ChangeDifficulty("^\\s*menu\\s+settings\\s+change-difficulty\\s+-l\\s+(?<level>[1-5])\\s*$"),
    Exit("^\\s*menu\\s+exit\\s*$");
    private final String pattern;

    SettingsMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
