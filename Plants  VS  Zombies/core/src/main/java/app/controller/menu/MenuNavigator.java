package app.controller.menu;

import app.enums.Menu;
import app.model.App;
import common.Result;

public class MenuNavigator {
    public static Result goTo(Menu menu) {
        if (possibilityToChangeMenu(menu , App.getCurrentMenu()))
        {
            App.setCurrentMenu(menu);
            return new Result(true , "Current Menu :" + App.getCurrentMenu());
        }

        String massage = "It is not possible to change the menu.\n" +
                "\n" +
                "The user will be able to go to specific menus in any menu:\n" +
                "Registration menu: Login menu.\n" +
                "Login menu: Only by logging in to the account will he be able to go to the main menu.\n" +
                "Main menu: Game menu, Options menu, Network menu, News menu, Profile menu\n" +
                "Game menu: Collection menu";

        return new Result(false , massage);
    }

    private static boolean possibilityToChangeMenu(Menu menu , Menu currentMnue) {
        if (menu == null) return false;
        switch (currentMnue) {
            case SignupMenu: {
                return menu == Menu.LoginMenu;
            }
            case LoginMenu: {
                return false;
            }
            case MainMenu: {
                return menu == Menu.GameMenu ||
                        menu == Menu.SettingsMenu ||
                        menu == Menu.NetworkMenu ||
                        menu == Menu.NewsMenu ||
                        menu == Menu.ProfileMenu;
            }
            case GameMenu: {
                return menu == Menu.CollectionMenu ||
                        menu == Menu.GreenhousMenu ||
                        menu == Menu.TravelLogMenu ||
                        menu == Menu.LeaderBoardMenu ||
                        menu == Menu.PlantSelectionMenu;
            }
            case GreenhousMenu:
            {
                return menu == Menu.Shop;
            }
            case PlantSelectionMenu:
            {
                return menu == Menu.PlayMenu ||
                        menu == Menu.VaseBreakerMenu ||
                        menu == Menu.IZombieMenu ||
                        menu == Menu.GameMenu;
            }
            case PlayMenu, Shop, IZombieMenu, VaseBreakerMenu: {
                return menu == Menu.GameMenu;
            }
            case TravelLogMenu: {
                return menu == Menu.PlantSelectionMenu;
            }
            default:
                return false;
        }
    }

    public static Result successfulLogin(boolean loginResult)
    {
        if (App.getCurrentMenu() != Menu.LoginMenu) return new Result(false ,
                "ERROR: This command is executed at login time.");
        App.setCurrentMenu(Menu.MainMenu);
        return new Result(true , "Current Menu :" + App.getCurrentMenu());
    }

    public static Result logout()
    {
        if (App.getCurrentMenu() != Menu.LoginMenu) return new Result(false ,
                "ERROR: To log out of your account, go to the main menu.");
        // TODO: Mohammad Reza Ajdari -> Use this section to delete the information of the person who has logged in.
        App.setCurrentMenu(Menu.SignupMenu);
        return new Result(true , "Current Menu :" + App.getCurrentMenu());
    }
}
