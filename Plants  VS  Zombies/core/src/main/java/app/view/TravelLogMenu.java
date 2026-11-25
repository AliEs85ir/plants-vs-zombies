package app.view;

import app.controller.menu.TravelLogMenuController;
import app.enums.commands.TravelLogCommands;
import common.Input;
import common.Result;

public class TravelLogMenu implements AppMenu{
    private final TravelLogMenuController controller = new TravelLogMenuController();

    @Override
    public void checkCommand() {
        String input = Input.nextLine();

        if (AppMenu.reviewCommonCommandsMatches(input)) return;

        if(TravelLogCommands.EnterMinigame.matches(input)) {
            handleEnteringMinigame(input);
        }
        else if(TravelLogCommands.SHOW_QUESTS.matches(input)) {
            handleShowQuests();
        }
        else {
            invalidCommand();
        }
    }

    private void handleEnteringMinigame(String input) {
        String name  = TravelLogCommands.EnterMinigame.getGroup(input, "name");
        Result result = controller.manageEnteringMinigame(name);
        System.out.println(result);
    }


    private void handleShowQuests() {
        Result result = controller.manageShowQuests();
        System.out.println(result);
    }
}
