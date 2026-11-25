package app.enums;

public enum Menu {
    SignupMenu,
    LoginMenu,
    MainMenu,
    GameMenu,
    EnterMenu,
    SettingsMenu,
    ProfileMenu,
    NewsMenu,
    TravelLogMenu,
    CollectionMenu,
    LeaderBoardMenu,
    GreenhousMenu,
    NetworkMenu,
    MiniGameMenu,
    PlantSelectionMenu,
    Shop,
    PlayMenu,
    VaseBreakerMenu,
    IZombieMenu,
    ;

    @Override
    public String toString() {
        return this.name();
    }

    public static Menu parseMenu(String input) {
        if (input == null)
            return null;

        input = input.trim();

        for (Menu menu : Menu.values()) {
            if (menu.toString().equalsIgnoreCase(input))
                return menu;
        }

        return null;
    }
}
