package app.enums.commands;

public enum GlobalCommand implements Commands{
    CHANGE_MENU("^\\s*menu\\s+enter\\s+(?<menuName>.+)\\s*$"),
    CHANGE_MENU_IN_GAME_MENU("^\\s*menu\\s+(?!enter\\b|exit\\b|show\\b)(?<menuName>\\S+)\\s*$"),
    SHOW_MENU("^\\s*menu\\s+show\\s+current\\s*$") ,
    EXIT_MENU("^\\s*menu\\s+exit\\s*$")
    ;
    private final String pattern;

    GlobalCommand(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }

}
