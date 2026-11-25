package app.view;

import app.controller.shop.ShopController;
import app.enums.commands.ShopCommands;
import common.Input;

import java.util.regex.Matcher;

public class ShopMenu implements AppMenu {

    private final ShopController controller = new ShopController();

    @Override
    public void checkCommand() {
        String input = Input.nextLine();

        //Checking whether it matches the common commands of the program or not
        if (AppMenu.reviewCommonCommandsMatches(input)) return;

        Matcher matcher;
        if (ShopCommands.List.matches(input.trim())) {
//            System.out.print(controller.showList());
            return;
        }
        if (ShopCommands.Daily.matches(input)) {
//            System.out.println(controller.showDaily());
            return;
        }
        if (ShopCommands.BuyDaily.matches(input)) {
            System.out.println(controller.buyDaily());
            return;
        }
        if ((matcher = ShopCommands.Buy.getMatcher(input)) != null) {
            String itemID = matcher.group("itemID");
            int count = 0;
            try {
                count = Integer.parseInt(matcher.group("count"));
            } catch (Exception e) {
                System.out.println("Wrong Number");
                return;
            }
            String PlantName = matcher.group("type");
//            System.out.println(controller.buy(itemID, count, PlantName));
            return;
        }


        invalidCommand();


    }
}
