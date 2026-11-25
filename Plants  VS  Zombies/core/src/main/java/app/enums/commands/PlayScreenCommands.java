package app.enums.commands;

public enum PlayScreenCommands implements Commands{
    ADVANCE_TIME("\\s*advance\\s+time\\s+-t\\s+(?<tick>.*?)\\s*ticks\\s*"),
    COLLECT_SUN("\\s*collect\\s+sun\\s+-l\\s*\\(\\s*(?<x>.*?)\\s*,\\s*(?<y>.*?)\\s*\\)\\s*"),
    SUN_AMOUNT("\\s*show\\s+sun\\s+amount\\s*"),
    ADD_SUN("\\s*cheat\\s+add\\s+-n\\s*(?<count>.*?)\\s*suns\\s*"),
    NUKE("\\s*release\\s+the\\s+nuke\\s*"),
    PLANT_PLANT("\\s*plant\\s+plant\\s+-t\\s*(?<type>.*?)\\s*-l\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*"),
    REMOVE_COOLDOWN("\\s*cheat\\s+remove-cooldown\\s*"),
    PLUCK_PLANT("\\s*pluck\\s+plant\\s+-l\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*"),
    FEED_PLANT("\\s*feed\\s+plant\\s+-l\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*"),
    ADD_FOOD("\\s*cheat\\s+add-plant-food"),
    SHOW_MAP("\\s*show\\s+map\\s*"),
    SHOW_PLANT_STATUS("\\s*show\\s+plants\\s+status\\s*"),
    SHOW_TILE_STATUS("\\s*show\\s+tile\\s+status\\s+-l\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*"),
    SHOW_ZOMBIES_INFO("\\s*zombies\\s+info\\s*"),
    SPAWN_ZOMBIE("\\s*cheat\\s+spawn\\s+zombie\\s+-n\\s+(?<alias>.*?)\\s*-l\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*"),
    PLANT_FOOD_COLLECT("\\s*collec\\s+plan\\s+food\\s+-l\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*"),
    APPLY_PLANT_FOOD("\\s*apply\\s+plant\\s+food\\s+-t\\s+(?<type>.*?)\\s*\\(\\s*(?<x>.*?),\\s*(?<y>.*?)\\s*\\)\\s*")

    ;
    private final String pattern;

    PlayScreenCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
