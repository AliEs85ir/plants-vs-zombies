    package app.view;

    import app.controller.menu.GameMenuController;
    import app.controller.menu.MenuController;
    import app.enums.commands.GameMenuCommands;
    import app.enums.commands.GlobalCommand;
    import com.badlogic.gdx.Gdx;
    import common.Input;
    import common.Result;

    import java.util.regex.Matcher;

    public class GameMenu implements AppMenu{
        private final GameMenuController controller = new GameMenuController();


        private boolean menuEnterMatches(String input)
        {
            Matcher menuenterMatcher = GlobalCommand.CHANGE_MENU.getMatcher(input);
            Matcher menuMatcher = GlobalCommand.CHANGE_MENU_IN_GAME_MENU.getMatcher(input);
            if (menuenterMatcher.matches())
            {
                Result result = MenuController.menuEnter(menuenterMatcher);
                System.out.println(result.message());
                return true;
            }
            if (menuMatcher.matches())
            {
                Result result = MenuController.menuEnter(menuMatcher);
                System.out.println(result.message());
                return true;
            }
            return false;
        }

        @Override
        public void checkCommand() {
            String input = Input.nextLine();
            if (menuEnterMatches(input)) return;
            if (AppMenu.showMenuMatches(input) || AppMenu.reviewCommonCommandsMatches(input)) return;

            if (GameMenuCommands.EnterChapter.matches(input)) {
                String chapterName = GameMenuCommands.EnterChapter.getGroup(input,"chaptername");
                handleEnteringChapter(chapterName);
                return;
            }

            if (GameMenuCommands.CoinWallet.matches(input)) {
                System.out.println(controller.getCoins());
                return;
            }

            if (GameMenuCommands.GemWallet.matches(input)) {
                System.out.println(controller.getGems());
                return;
            }

            if (GameMenuCommands.CheatAddCoin.matches(input)) {
                String amount = GameMenuCommands.CheatAddCoin.getGroup(input,"amount");
                String currency = GameMenuCommands.CheatAddCoin.getGroup(input, "currency");
                System.out.println(controller.manageCheatCode(amount, currency));
                return;
            }


            invalidCommand();
        }

        // >>> Handling each type of command.
        private void handleEnteringChapter(String chapterName){
            Gdx.app.postRunnable(() -> {
                Result result = controller.manageEnteringChapter(chapterName);
                System.out.println(result);
            });
        }


    }
