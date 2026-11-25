package app.view.network;

import app.controller.game.izombie.IZombieNetworkController;
import app.controller.game.izombie.IZombieUiStages;
import app.view.screen.ScreenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import common.Result;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.world.minigame.izombie.IZombieRules;
import plants_vs_zombies.PlantsVSZombies;

public final class NetworkMinigameRouter {
    private final PlantsVSZombies game;
    private boolean navigating;

    public NetworkMinigameRouter(PlantsVSZombies game) {
        this.game = game;
    }

    public void activate(NetworkMenuConfig.GameConfig card) {
        if (navigating || this.game == null || card == null || !card.clickable()) {
            return;
        }
        switch (card.action) {
            case "open_izombie", "i_zombie", "izombie", "izombie_network" ->
                startIZombie(card);
            case "open_izombie_couch", "izombie_couch", "i_zombie_couch" ->
                startChapter(card, "I_ZOMBIE_COUCH");
            case "open_scoring_game", "scoring_game" ->
                startChapter(card, "SCORING_GAME");
            case "start_chapter", "start_minigame" ->
                startChapter(card, card.chapterName());
            case "none" -> {
            }
            default -> Gdx.app.log("NetworkMinigameRouter", "Unknown action: " + card.action);
        }
    }

    private void startIZombie(NetworkMenuConfig.GameConfig card) {
        String chapterName = chapterName(card, "I_ZOMBIE");
        navigating = true;
        Result result = ScreenManager.startChapter(chapterName);
        if (IZombieRules.isPendingMatchmaking(result)) {
            IZombieNetworkController controller = IZombieNetworkController.get();
            Stage stage = IZombieUiStages.current();
            if (controller == null || stage == null) {
                navigating = false;
                AudioManager.play(AudioId.UI_ERROR);
                Gdx.app.error("NetworkMinigameRouter", "Online play is not available.");
                return;
            }
            controller.openMatchPopup(stage, chapterName, () -> navigating = false);
            return;
        }
        if (!result.success()) {
            navigating = false;
            Gdx.app.error("NetworkMinigameRouter", "Could not start " + chapterName + ": " + result.message());
        }
    }

    private void startChapter(NetworkMenuConfig.GameConfig card, String fallbackChapter) {
        String chapterName = chapterName(card, fallbackChapter);
        if (chapterName == null || chapterName.isBlank()) {
            Gdx.app.error("NetworkMinigameRouter", "Missing chapter for " + card.id);
            return;
        }
        navigating = true;
        Result result = ScreenManager.startChapter(chapterName);
        if (!result.success()) {
            navigating = false;
            Gdx.app.error("NetworkMinigameRouter", "Could not start " + chapterName + ": " + result.message());
        }
    }

    private static String chapterName(NetworkMenuConfig.GameConfig card, String fallback) {
        String named = card.chapterName();
        if (named != null && !named.isBlank()) {
            return named;
        }
        return fallback;
    }
}
