package app.view.minigame;

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

public final class MiniGameRouter {
    private final PlantsVSZombies game;
    private boolean navigating;

    public MiniGameRouter(PlantsVSZombies game) {
        this.game = game;
    }

    public void activate(MiniGameDefinition definition) {
        if (navigating || this.game == null || definition == null || !definition.clickable()) {
            return;
        }
        switch (definition.action()) {
            case "start_chapter", "start_minigame", "open", "play" -> startChapter(definition);
            case "none" -> {
            }
            default -> Gdx.app.log("MiniGameRouter", "Unknown action: " + definition.action());
        }
    }

    private void startChapter(MiniGameDefinition definition) {
        String chapterName = definition.chapter();
        if (chapterName == null || chapterName.isBlank()) {
            Gdx.app.log("MiniGameRouter", "No chapter registered for " + definition.id());
            return;
        }
        navigating = true;
        Result result = ScreenManager.startChapter(chapterName);
        if (IZombieRules.isPendingMatchmaking(result)) {
            IZombieNetworkController controller = IZombieNetworkController.get();
            Stage stage = IZombieUiStages.current();
            if (controller == null || stage == null) {
                navigating = false;
                AudioManager.play(AudioId.UI_ERROR);
                Gdx.app.error("MiniGameRouter", "Online play is not available.");
                return;
            }
            controller.openMatchPopup(stage, chapterName, () -> navigating = false);
            return;
        }
        if (!result.success()) {
            navigating = false;
            AudioManager.play(AudioId.UI_ERROR);
            Gdx.app.error("MiniGameRouter", "Could not start " + chapterName + ": " + result.message());
            return;
        }
        navigating = false;
    }
}
