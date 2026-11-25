package app.view.screen;

import app.controller.menu.GameMenuController;
import app.enums.Menu;
import app.enums.SeasonType;
import app.model.App;
import app.view.screen.LevelSelect.EgyptLevelSelectScreen;
import app.view.screen.shop.ShopScreen;
import common.Result;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.world.chapter.Chapter;
import plants_vs_zombies.PlantsVSZombies;

public final class ScreenManager {

    private ScreenManager() {
        // Prevent creating an instance
    }

    public static void showLoading(PlantsVSZombies game) {
        game.setScreen(new LoadingScreen(game));
    }

    public static void showLogin(PlantsVSZombies game) {
        game.setScreen(new LoginScreen(game));
    }

    public static void showSignup(PlantsVSZombies game) {
        game.setScreen(new SignupScreen(game));
    }

    public static void showMainMenu(PlantsVSZombies game) {
        App.setCurrentMenu(Menu.MainMenu);
        game.setScreen(new MainMenuScreen(game));
    }

    public static void showProfile(PlantsVSZombies game) {
        game.setScreen(new ProfileScreen(game));
    }

    public static void showGameScreen(PlantsVSZombies game) {
        App.setCurrentMenu(Menu.GameMenu);
        game.setScreen(new AdventureScreen(game));
    }

    public static Result startChapter(String chapterName) {
        Result result = new GameMenuController().startChapterFromUi(chapterName);
        if (!result.success()) {
            AudioManager.play(AudioId.UI_ERROR);
        }
        return result;
    }

    public static void showNews(PlantsVSZombies game) {
        game.setScreen(new NewsScreen(game));
    }

    public static void showSettings(PlantsVSZombies game) {
        game.setScreen(new SettingsScreen(game));
    }

    public static void showEgyptLevels(PlantsVSZombies game) {
        showLevelSelectForSeason(game, SeasonType.ANCIENT_EGYPT);
    }

    public static void showLevelSelectForSeason(PlantsVSZombies game, SeasonType season) {
        if (game == null || season == null || season == SeasonType.MINIGAME) {
            return;
        }
        game.setScreen(new EgyptLevelSelectScreen(game, season));
    }

    public static void showLevelSelectFor(PlantsVSZombies game, Chapter chapter) {
        if (chapter == null || chapter.getSeason() == null
            || chapter.getSeason().getSeasonType() == SeasonType.MINIGAME) {
            showGameScreen(game);
            return;
        }
        showLevelSelectForSeason(game, chapter.getSeason().getSeasonType());
    }

    public static void showShop(PlantsVSZombies game) {
        App.setCurrentMenu(Menu.Shop);
        game.setScreen(new ShopScreen(game));
    }

    public static void showCollection(PlantsVSZombies game) {
        App.setCurrentMenu(Menu.CollectionMenu);
        game.setScreen(new CollectionScreen(game));
    }

    public static void showGreenhouse(PlantsVSZombies game) {
        App.setCurrentMenu(Menu.GreenhousMenu);
        game.setScreen(new GreenhouseScreen(game));
    }

    public static void showNetworkMenu(PlantsVSZombies game) {
        App.setCurrentMenu(Menu.NetworkMenu);
        game.setScreen(new NetworkScreen(game));
    }

    public static void showMiniGameMenu(PlantsVSZombies game) {
        App.setCurrentMenu(Menu.MiniGameMenu);
        game.setScreen(new MiniGameScreen(game));
    }
}
