package app.controller.menu;

import app.enums.Menu;
import app.model.App;
import com.badlogic.gdx.Gdx;
import common.Result;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterRegistry;
import game.world.minigame.izombie.IZombieRules;

public class GameMenuController {

    public Result manageEnteringChapter(String chapterName){
        Result selected = selectChapter(chapterName);
        if (!selected.success()) {
            return selected;
        }
        MenuNavigator.goTo(Menu.PlantSelectionMenu);
        return selected;
    }

    public Result startChapterFromUi(String chapterName) {
        Result selected = selectChapter(chapterName);
        if (!selected.success()) {
            return selected;
        }
        App.setCurrentMenu(Menu.PlantSelectionMenu);
        Chapter chapter = App.getPendingGameSession().getSelectedChapter();
        if (IZombieRules.needsMatchmaking(chapter)) {
            return Result.success(IZombieRules.PENDING_MATCHMAKING);
        }
        return PlantSelectionController.startGame();
    }

    private Result selectChapter(String chapterName) {
        Chapter chapter;
        try {
            chapter = ChapterRegistry.getChapterByName(chapterName);
        } catch (RuntimeException primary) {
            try {
                chapter = ChapterRegistry.getMinigameChapterByName(chapterName);
            } catch (RuntimeException secondary) {
                Gdx.app.error("GameMenuController", "Minigame lookup failed for " + chapterName, secondary);
                return new Result(false, "Chapter " + chapterName + " does not exist.");
            }
        }

        if (chapter == null) {
            return new Result(false, "Chapter " + chapterName + " does not exist.");
        }

        if (App.getCurrentUser() == null) {
            return new Result(false, "No user is logged in.");
        }

        if (chapter.isLocked(App.getCurrentUser())) {
            return new Result(false, "This chapter is currently locked.");
        }

        App.getPendingGameSession().clear();
        App.getPendingGameSession().setSelectedChapter(chapter);
        return new Result(true, "Entering selection for " + chapterName);
    }

    public Result manageCheatCode(String amount, String currency) {
        int amountNumber = 0;
        try {
            amountNumber = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            return new Result(false, "invalid amount");
        }
        switch (currency) {
            case "coin":
                App.getCurrentUser().addCoins(amountNumber);
                break;
            case "diamond":
                App.getCurrentUser().addGems(amountNumber);
                break;
            default:
                return new Result(false, "invalid currency");
        }
        return new Result(true, "cheat code applied");
    }



    public int getCoins() {
        return App.getCurrentUser().getCoins();
    }

    public int getGems() {
        return App.getCurrentUser().getGems();
    }
}
