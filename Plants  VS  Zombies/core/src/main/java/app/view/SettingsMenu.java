package app.view;

import app.controller.menu.SettingsMenuController;
import app.enums.commands.SettingsMenuCommands;
import common.Input;
import common.Result;

import java.util.regex.Matcher;

public class SettingsMenu implements AppMenu{
    private final SettingsMenuController controller = new SettingsMenuController();

    @Override
    public void checkCommand() {
        String input = Input.nextLine();
        //Checking whether it matches the common commands of the program or not
        if (AppMenu.reviewCommonCommandsMatches(input)) return;

        Matcher matcher;
        if ((matcher = SettingsMenuCommands.ChangeDifficulty.getMatcher(input)) != null) {
            handleChangingDifficulty(matcher);
            return;
        }
        invalidCommand();
    }

    private void handleChangingDifficulty(Matcher matcher){
        try {
            int level = Integer.parseInt(matcher.group("level"));
            Result result = controller.changeDifficulty(level);
            System.out.println(result);
        } catch (Exception e) {
            invalidCommand();
        }
    }

}
