package app.view;

import app.controller.menu.CollectionMenuController;
import app.enums.commands.CollectionMenuCommands;
import common.Input;

import java.util.regex.Matcher;

public class CollectionMenu implements AppMenu {

    private final CollectionMenuController controller = new CollectionMenuController();

    @Override
    public void checkCommand() {
        String input = Input.nextLine();

        if (AppMenu.reviewCommonCommandsMatches(input)) {
            return;
        }

        if (CollectionMenuCommands.ShowPlants.matches(input)) {
            System.out.print(controller.showPlants());
            return;
        }

        if (CollectionMenuCommands.ShowAllPlants.matches(input)) {
            System.out.print(controller.showAllPlants());
            return;
        }

        if (CollectionMenuCommands.ShowZombies.matches(input)) {
            System.out.println(controller.showZombies());
            return;
        }

        if (CollectionMenuCommands.ShowAllZombies.matches(input)) {
            System.out.print(controller.showAllZombies());
            return;
        }

        Matcher matcher = CollectionMenuCommands.ShowPlant.getMatcher(input);
        if (matcher.matches()) {
            System.out.println(controller.showPlant(matcher.group("plantName")));
            return;
        }

        matcher = CollectionMenuCommands.ShowZombie.getMatcher(input);
        if (matcher.matches()) {
            System.out.println(controller.showZombie(matcher.group("zombieName")));
            return;
        }

        matcher = CollectionMenuCommands.UpgradePlant.getMatcher(input);
        if (matcher.matches()) {
            System.out.println(controller.upgradePlant(matcher.group("plantName")));
            return;
        }

        matcher = CollectionMenuCommands.PurchasePlant.getMatcher(input);
        if (matcher.matches()) {
            System.out.println(controller.purchasePlant(matcher.group("plantName")));
            return;
        }

        invalidCommand();
    }
}
