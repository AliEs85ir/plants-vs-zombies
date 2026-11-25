package app.enums.commands;

public enum ConveyorBeltCommands implements Commands{
    SHOW_BELT("\\s*show\\s+belt\\s+plants\\s*"),
    SET_BELT_TIME("\\s*cheat\\s+set\\s+belt\\s+time\\s+-t\\s+(?<time>.*?)\\s*seconds\\s*"),
    ROLL_BELT("\\s*roll\\s+belt\\s*")
    ;
    private final String pattern;

    ConveyorBeltCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
