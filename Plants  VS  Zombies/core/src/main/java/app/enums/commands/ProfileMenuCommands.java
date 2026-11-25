package app.enums.commands;

public enum ProfileMenuCommands implements Commands{
    CHANGE_USERNAME("\\s*menu\\s+profile\\s+change-username\\s+-u\\s*(?<username>.*?)\\s*"),
    CHANGE_NICKNAME("\\s*menu\\s+profile\\s+change-nickname\\s+-u\\s*(?<nickname>.*?)\\s*"),
    CHANGE_EMAIL("\\s*menu\\s+profile\\s+change-email\\s+-e\\s*(?<email>.*?)\\s*"),
    CHANGE_PASSWORD("\\s*menu\\s+profile\\s+change-password\\s+-p\\s+(?<new>.*?)\\s*-o\\s*(?<old>.*?)\\s*"),
    SHOW_INFO("\\s*menu\\s+profile\\s+show-info\\s*")
    ;
    private final String pattern;

    ProfileMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
