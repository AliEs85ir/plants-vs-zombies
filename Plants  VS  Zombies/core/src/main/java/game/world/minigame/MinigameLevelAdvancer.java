package game.world.minigame;

import app.controller.menu.PlantSelectionController;
import app.model.App;
import com.badlogic.gdx.Gdx;
import game.engine.pause.PauseReason;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterRegistry;
import game.world.minigame.beghouled.BeghouledMode;
import game.world.minigame.plantzombies.PlantZombiesMode;
import game.world.minigame.vasebreaker.VaseBreakerMode;
import game.world.minigame.wallnut_bowling.WallNutBowlingMode;

public final class MinigameLevelAdvancer {
    private MinigameLevelAdvancer() {
    }

    public static void advanceAndRestart(GameWorld world) {
        if (world == null || world.getCurrentChapter() == null) {
            return;
        }
        Chapter next = nextChapter(world.getCurrentChapter());
        if (next == null) {
            return;
        }
        App.getPendingGameSession().setSelectedChapter(next);
        if (App.getRealTimeEngine() != null) {
            App.getRealTimeEngine().pause(PauseReason.SYSTEM);
        }
        Runnable restart = PlantSelectionController::restartCurrentLevel;
        if (Gdx.app != null) {
            Gdx.app.postRunnable(restart);
        } else {
            restart.run();
        }
    }

    private static Chapter nextChapter(Chapter current) {
        Minigame logic = current.getMinigameLogic();
        int nextLevel = 2;
        if (logic instanceof MultiLevelMinigame multi) {
            nextLevel = Math.min(multi.getLevel() + 1, multi.getTotalLevels());
        }
        if (logic instanceof VaseBreakerMode) {
            return ChapterRegistry.createVaseBreakerChapter(nextLevel);
        }
        if (logic instanceof WallNutBowlingMode) {
            return ChapterRegistry.createWallNutBowlingChapter(nextLevel);
        }
        if (logic instanceof PlantZombiesMode) {
            return ChapterRegistry.createPlantZombiesChapter(nextLevel);
        }
        if (logic instanceof BeghouledMode) {
            return ChapterRegistry.createBeghouledChapter(nextLevel);
        }
        return null;
    }
}
