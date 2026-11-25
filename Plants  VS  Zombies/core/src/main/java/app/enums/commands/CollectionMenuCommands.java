package app.enums.commands;

public enum CollectionMenuCommands implements Commands{
    ShowPlants("^\\s*menu\\s+collection\\s+show-plants\\s*$"),
    ShowAllPlants("^\\s*menu\\s+collection\\s+show-all-plants\\s*$"),
    ShowZombies("^\\s*menu\\s+collection\\s+show-zombies\\s*$"),
    ShowAllZombies("^\\s*menu\\s+collection\\s+show-all-zombies\\s*$"),
    ShowPlant("^\\s*menu\\s+collection\\s+show-plant\\s+-p\\s+(?<plantName>.*?)\\s*$"),
    ShowZombie("^\\s*menu\\s+collection\\s+show-zombie\\s+-z\\s+(?<zombieName>.*?)\\s*$"),
    UpgradePlant("^\\s*menu\\s+collection\\s+upgrade-plant\\s+-p\\s+(?<plantName>.*?)\\s*$"),
    PurchasePlant("^\\s*menu\\s+collection\\s+purchase-plant\\s+-p\\s+(?<plantName>.*?)\\s*$"),
    ;
    private final String pattern;

    CollectionMenuCommands(String pattern) {
        this.pattern = pattern;
    }

    @Override
    // >>> getting pattern to match it with regex
    public String getPattern() {
        return pattern;
    }
}
