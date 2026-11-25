package app.enums.commands;

public enum VaseBreakerMenuCommands implements Commands{
    BREAK_VASE("\\s*break\\s+vase\\s+-l\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*"),
    PLANT_PLANT("\\s*plant\\s+plant\\s+-t\\s*(?<type>.*?)\\s+-l\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*"),
    SHOW_MAP("\\s*show\\s+map\\s*"),
    BREAK_ALL("\\s*break\\s+all\\s*");
    private final String pattern;

    VaseBreakerMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
