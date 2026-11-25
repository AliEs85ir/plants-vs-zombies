package app.view;

import app.controller.menu.MainMenuController;
import app.enums.commands.MainMenuCommands;
import common.Input;
import common.Result;

public class MainMenu implements AppMenu{
    private final MainMenuController controller = new MainMenuController();
    @Override
    public void checkCommand() {

        String input = Input.nextLine();
        //Checking whether it matches the common commands of the program or not
        if (AppMenu.reviewCommonCommandsMatches(input)) return;

        if (MainMenuCommands.Logout.matches(input)) {
            handleLogout();
            return;
        }
        invalidCommand();
    }

    private void handleLogout() {
        Result result = controller.manageLogout();
        System.out.println(result.message());
    }

}

