package app.controller.menu;

import app.enums.Menu;
import app.model.App;
import app.view.*;
import app.view.AppMenu;
import app.view.game.PlantSelectionMenu;
import common.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class MenuController {

    private static final Map<Menu, AppMenu> menus = new HashMap<>();

    static {
        menus.put(Menu.MainMenu, new MainMenu());
        menus.put(Menu.GameMenu, new GameMenu());
        menus.put(Menu.SettingsMenu, new SettingsMenu());
        menus.put(Menu.NewsMenu, new NewsMenu());
        menus.put(Menu.CollectionMenu, new CollectionMenu());
        menus.put(Menu.NetworkMenu , new NetworkMenu());
        menus.put(Menu.GreenhousMenu , new Greenhouse());
        menus.put(Menu.PlantSelectionMenu , new PlantSelectionMenu());
        menus.put(Menu.PlayMenu, () -> {});
        menus.put(Menu.TravelLogMenu, new TravelLogMenu());
        menus.put(Menu.Shop, new ShopMenu());
    }

    public static AppMenu getMenu(Menu menu) {
        return menus.get(menu);
    }

    public static Result menuEnter(Matcher matcher)
    {
        String menuName = matcher.group("menuName");
        Menu menu = Menu.parseMenu(menuName.trim());
        if (menuName.trim().isEmpty() || menu == null)
        {
            System.out.println();
            return new Result(false , "Please enter a valid menu name.");
        }
        return MenuNavigator.goTo(menu);
    }

    public static Result showMenu()
    {
        return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
    }

    public static Result menuExit(Matcher matcher)
    {
        try{App.getCurrentUser().getQuestManager().saveQuests();} catch (Exception e){}

        switch (App.getCurrentMenu())
        {
            case SignupMenu -> {

                System.exit(0);
                return null;
            }
            case LoginMenu -> {
                App.setCurrentMenu(Menu.SignupMenu);
                return new Result(false , "Current menu: " + App.getCurrentMenu().toString());
            }
            case MainMenu -> {
                return new Result(false ,"To exit the main menu, you must use the \"logout\" command.");
            }
            case GameMenu -> {
                App.setCurrentMenu(Menu.MainMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case SettingsMenu -> {
                App.setCurrentMenu(Menu.MainMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case TravelLogMenu -> {
                App.setCurrentMenu(Menu.MainMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case NewsMenu -> {
                App.setCurrentMenu(Menu.MainMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case ProfileMenu -> {
                App.setCurrentMenu(Menu.MainMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case CollectionMenu -> {
                App.setCurrentMenu(Menu.GameMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case Shop -> {
                App.setCurrentMenu(Menu.GreenhousMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case EnterMenu -> {
                App.setCurrentMenu(Menu.SignupMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case GreenhousMenu -> {
                App.setCurrentMenu(Menu.GameMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case IZombieMenu -> {
                App.setCurrentMenu(Menu.GameMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case LeaderBoardMenu -> {
                App.setCurrentMenu(Menu.GameMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case NetworkMenu -> {
                App.setCurrentMenu(Menu.MainMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case MiniGameMenu -> {
                App.setCurrentMenu(Menu.MainMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case PlantSelectionMenu -> {
                App.setCurrentMenu(Menu.GameMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case PlayMenu -> {
                App.setCurrentMenu(Menu.GameMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            case VaseBreakerMenu -> {
                App.setCurrentMenu(Menu.GameMenu);
                return new Result(true , "Current menu: " + App.getCurrentMenu().toString());
            }
            default -> {
                return new Result(false , "There was a problem logging out! Please try again.");
            }
        }

    }
}
