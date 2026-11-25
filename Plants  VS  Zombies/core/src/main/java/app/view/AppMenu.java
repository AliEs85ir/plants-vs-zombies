package app.view;

import app.controller.menu.MenuController;
import app.enums.commands.GlobalCommand;
import common.Result;

import java.util.regex.Matcher;

// >>> This is an Interface that all menu's implement so they can have same type
public interface AppMenu {

    void checkCommand();

    default void invalidCommand() {
        //System.out.println("Invalid command.");
    }

    static boolean menuEnterMatches(String input)
    {

        Matcher menuenterMatcher = GlobalCommand.CHANGE_MENU.getMatcher(input);
        if (menuenterMatcher.matches())
        {
            Result result = MenuController.menuEnter(menuenterMatcher);
            System.out.println(result.message());
            return true;
        }
        return false;
    }



    static boolean reviewCommonCommandsMatches(String input)
    {
        return menuEnterMatches(input)|| showMenuMatches(input)|| menuExitMatches(input);
    }

    static boolean showMenuMatches(String input)
    {

        Matcher menuShowMatcher = GlobalCommand.SHOW_MENU.getMatcher(input);
        if (menuShowMatcher.matches())
        {
            Result result = MenuController.showMenu();
            System.out.println(result.message());
            return true;
        }
        return false;
    }



    private static boolean menuExitMatches(String input)
    {
        Matcher menuExitMatcher = GlobalCommand.EXIT_MENU.getMatcher(input);
        if (menuExitMatcher.matches())
        {
            Result result = MenuController.menuExit(menuExitMatcher);
            System.out.println(result.message());
            return true;
        }
        return false;
    }

}

