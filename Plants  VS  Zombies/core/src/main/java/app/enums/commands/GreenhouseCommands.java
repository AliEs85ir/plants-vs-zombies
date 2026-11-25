package app.enums.commands;

public enum GreenhouseCommands implements Commands {
    SHOW_GREENHOUSE("^\\s*show\\s+greenhouse\\s*$"),
    UNLOCK_POT("^\\s*unlock\\s+pot\\s+at\\s+\\(\\s*(?<x>.*)\\s*,\\s*(?<y>.*)\\s*\\)\\s*$"),
    PLANT_POT("^\\s*plant\\s+pot\\s+at\\s+\\(\\s*(?<x>.*)\\s*,\\s*(?<y>.*)\\s*\\)\\s*$"),
    COLLECT("^\\s*collect\\s+\\(\\s*(?<x>.*)\\s*,\\s*(?<y>.*)\\s*\\)\\s*$"),
    Full_GROW("^\\s*grow\\s+\\(\\s*(?<x>.*)\\s*,\\s*(?<y>.*)\\s*\\)\\s*$") ,
    GROW("^\\s*grow\\s+\\(\\s*(?<x>.*)\\s*,\\s*(?<y>.*)\\s*\\)\\s+-h\\s+(?<hours>.*)\\s*$");

    private final String pattern;

    GreenhouseCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String getPattern() {
        return pattern;
    }
}
