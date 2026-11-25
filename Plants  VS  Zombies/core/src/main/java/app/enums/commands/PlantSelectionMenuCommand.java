package app.enums.commands;

public enum PlantSelectionMenuCommand implements Commands
{
    SHOW_ALL("^\\s*show\\s+all\\s+plants\\s*$"),
    SHOW_AVAILABLE("^\\s*show\\s+available\\s+plants\\s*$"),
    ADD_PLANT("^\\s*add\\s+plant\\s+-t\\s+(?<type>.*)\\s*$") ,
    REMOVE_PLANT("^\\s*remove\\s+plant\\s+-t\\s+(?<type>.*)\\s*$"),
    BOOST_PLANT("^\\s*boost\\s+plant\\s+-t\\s+(?<type>.*)\\s*$") ,
    START_GAME("^\\s*start\\s+game\\s*$"),
    EXIT_GAME("^\\s*exit\\s+game\\s*$")
    ;
    private final String pattern;

    PlantSelectionMenuCommand(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
