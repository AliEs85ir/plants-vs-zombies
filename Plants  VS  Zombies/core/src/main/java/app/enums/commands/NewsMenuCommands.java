package app.enums.commands;

public enum NewsMenuCommands implements Commands{
    ShowUnreadNews("^\\s*menu\\s+news\\s+show-unread\\s*$"),
    ShowAllNews("^\\s*menu\\s+news\\s+show-all\\s*$")
    ;
    private final String pattern;

    NewsMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
