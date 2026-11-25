package app.view;

import app.controller.greenhouse.GreenhouseController;
import app.enums.commands.GreenhouseCommands;
import common.Input;
import common.Result;

public class Greenhouse implements AppMenu {
    @Override
    public void checkCommand() {
        String input = Input.nextLine();

        //Checking whether it matches the common commands of the program or not
        if (AppMenu.reviewCommonCommandsMatches(input)) return;

        if (GreenhouseCommands.SHOW_GREENHOUSE.matches(input)) {
            handleShowGreenhouse();
        } else if (GreenhouseCommands.UNLOCK_POT.matches(input)) {
            handleUnlockPot(input);
        } else if (GreenhouseCommands.PLANT_POT.matches(input)) {
            handlePlantPot(input);
        } else if (GreenhouseCommands.COLLECT.matches(input)) {
            handleCollect(input);
        } else if (GreenhouseCommands.GROW.matches(input)) {
            handleGrowWithHours(input);
        } else if (GreenhouseCommands.Full_GROW.matches(input)) {
            handleGrow(input);
        } else {
            invalidCommand();
        }
    }

    private void handleShowGreenhouse() {
        Result result = GreenhouseController.showGreenhouse();
        System.out.println(result.message());
    }

    private void handleUnlockPot(String input) {
        String xCoord = GreenhouseCommands.UNLOCK_POT.getGroup(input, "x");
        String yCoord = GreenhouseCommands.UNLOCK_POT.getGroup(input, "y");
        Result result = GreenhouseController.unlockPot(xCoord, yCoord);
        System.out.println(result.message());
    }

    private void handlePlantPot(String input) {
        String xCoord = GreenhouseCommands.PLANT_POT.getGroup(input, "x");
        String yCoord = GreenhouseCommands.PLANT_POT.getGroup(input, "y");
        Result result = GreenhouseController.plantPot(xCoord, yCoord);
        System.out.println(result.message());
    }

    private void handleCollect(String input) {
        String xCoord = GreenhouseCommands.COLLECT.getGroup(input, "x");
        String yCoord = GreenhouseCommands.COLLECT.getGroup(input, "y");
        Result result = GreenhouseController.collect(xCoord, yCoord);
        System.out.println(result.message());
    }

    private void handleGrow(String input) {
        String xCoord = GreenhouseCommands.Full_GROW.getGroup(input, "x");
        String yCoord = GreenhouseCommands.Full_GROW.getGroup(input, "y");
        Result result = GreenhouseController.grow(xCoord, yCoord);
        System.out.println(result.message());
    }

    private void handleGrowWithHours(String input) {
        String xCoord = GreenhouseCommands.GROW.getGroup(input, "x");
        String yCoord = GreenhouseCommands.GROW.getGroup(input, "y");
        String hours = GreenhouseCommands.GROW.getGroup(input, "hours");
        Result result = GreenhouseController.grow(xCoord, yCoord, hours);
        System.out.println(result.message());
    }
}
