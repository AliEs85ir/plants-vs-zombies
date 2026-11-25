package app.enums.commands;

public enum IZombieCommands implements Commands{
    SPAWN_ZOMBIE("\\s*spawn\\s+zombie\\s+-n\\s+(?<alias>.*?)\\s+-l\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*"),
    SHOW_ZOMBIES("\\s*show\\s+available\\s+zombies\\s*")
    ;
    private final String pattern;

    IZombieCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
