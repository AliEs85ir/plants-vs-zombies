package app.view.mainmenu.game;

import app.view.mainmenu.config.MainMenuConfig;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.Gdx;
import plants_vs_zombies.PlantsVSZombies;

public final class GameCardRouter {
    private final PlantsVSZombies game;

    public GameCardRouter(PlantsVSZombies game) {
        this.game = game;
    }

    public void activate(MainMenuConfig.GameCardConfig card) {
        if (card == null || !card.clickable()) {
            return;
        }
        switch (card.action) {
            case "open_adventure", "adventure" -> ScreenManager.showGameScreen(game);
            case "open_network", "network" -> ScreenManager.showNetworkMenu(game);
            case "open_minigame", "minigame", "mini_game" -> ScreenManager.showMiniGameMenu(game);
            case "none" -> {
            }
            default -> Gdx.app.log("GameCardRouter", "Unknown action: " + card.action);
        }
    }
}
