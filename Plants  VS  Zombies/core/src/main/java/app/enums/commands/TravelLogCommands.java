package app.enums.commands;

public enum TravelLogCommands implements Commands{
    EnterMinigame("\\s*enter\\s+minigame\\s+-n\\s*(?<name>.*?)\\s*"),
    SHOW_QUESTS("\\s*show\\s+all\\s+quests\\s*"),
    ;
    private final String pattern;

    TravelLogCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
