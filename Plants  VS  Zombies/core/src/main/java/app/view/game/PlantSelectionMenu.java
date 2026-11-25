package app.view.game;

import app.controller.menu.PlantSelectionController;
import app.enums.commands.PlantSelectionMenuCommand;
import app.view.AppMenu;
import common.Input;
import common.Result;

public class PlantSelectionMenu implements AppMenu {

    @Override
    public void checkCommand() {
        String input = Input.nextLine();

        if (AppMenu.reviewCommonCommandsMatches(input)) return;

        if (PlantSelectionMenuCommand.SHOW_ALL.matches(input)) {
            handleShowAll();
        } else if (PlantSelectionMenuCommand.SHOW_AVAILABLE.matches(input)) {
            handleShowAvailable();
        } else if (PlantSelectionMenuCommand.ADD_PLANT.matches(input)) {
            handleAddPlant(input);
        } else if (PlantSelectionMenuCommand.REMOVE_PLANT.matches(input)) {
            handleRemovePlant(input);
        } else if (PlantSelectionMenuCommand.BOOST_PLANT.matches(input)) {
            handleBoostPlant(input);
        } else if (PlantSelectionMenuCommand.START_GAME.matches(input)) {
            handleStartGame();
        } else if (PlantSelectionMenuCommand.EXIT_GAME.matches(input)) {
            handleExit();
        } else {
            invalidCommand();
        }
    }

    private void handleShowAll() {
        Result result = PlantSelectionController.showAllPlants();
        System.out.println(result.message());
    }

    private void handleShowAvailable() {
        Result result = PlantSelectionController.showAvailablePlants();
        System.out.println(result.message());
    }

    private void handleAddPlant(String input) {
        String type = PlantSelectionMenuCommand.ADD_PLANT.getGroup(input, "type");
        Result result = PlantSelectionController.addPlant(type);
        System.out.println(result.message());
    }

    private void handleRemovePlant(String input) {
        String type = PlantSelectionMenuCommand.REMOVE_PLANT.getGroup(input, "type");
        Result result = PlantSelectionController.removePlant(type);
        System.out.println(result.message());
    }

    private void handleBoostPlant(String input) {
        String type = PlantSelectionMenuCommand.BOOST_PLANT.getGroup(input, "type");
        Result result = PlantSelectionController.boostPlant(type);
        System.out.println(result.message());
    }

    private void handleStartGame() {
        Result result = PlantSelectionController.startGame();
        System.out.println(result.message());
    }

    private void handleExit() {
        Result result = PlantSelectionController.manageExit();
        System.out.println(result.message());
    }
}
