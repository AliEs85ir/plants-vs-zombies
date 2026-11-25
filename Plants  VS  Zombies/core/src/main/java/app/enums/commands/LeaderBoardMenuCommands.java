package app.enums.commands;

public enum LeaderBoardMenuCommands implements Commands{
    SORT("\\s*sort\\s+-t\\s*(?<type>.*?)\\s*")
    ;
    private final String pattern;

    LeaderBoardMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
