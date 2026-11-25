package app.view;

import app.controller.menu.NewsMenuController;
import app.enums.commands.NewsMenuCommands;
import common.Input;

public class NewsMenu implements AppMenu{
    private final NewsMenuController controller = new NewsMenuController();

    @Override
    public void checkCommand() {
        String input = Input.nextLine();
        //Checking whether it matches the common commands of the program or not
        if (AppMenu.reviewCommonCommandsMatches(input)) return;

        if (NewsMenuCommands.ShowUnreadNews.matches(input)) {
            System.out.print(controller.showUnreadNews());
            return;
        }
        if (NewsMenuCommands.ShowAllNews.matches(input)) {
            System.out.print(controller.showAllNews());
            return;
        }
        invalidCommand();
    }

}
