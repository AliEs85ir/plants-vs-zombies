package game.world.chapter;

import common.model.Position;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.minigame.MinigameWorld;
import game.world.minigame.beghouled.BeghouledMode;
import game.world.minigame.beghouled.BeghouledWorld;
import game.world.minigame.izombie.IZombieMode;
import game.world.minigame.izombie.IZombieWorld;
import game.world.minigame.izombie_couchplay.IZombieCouchPlayMode;
import game.world.minigame.izombie_couchplay.IZombieCouchPlayWorld;
import game.world.minigame.vasebreaker.VaseBreakerMode;
import game.world.minigame.vasebreaker.VaseBreakerWorld;
import game.world.minigame.wallnut_bowling.WallNutBowingWorld;
import game.world.minigame.wallnut_bowling.WallNutBowlingMode;
import game.world.playground.Grid;
import game.world.playground.MapConfig;

public class ChapterWorldBuilder {

    public static GameWorld buildWorld(Chapter chapter) {
        GameWorld world = null;
        Grid grid =
            new Grid(
                MapConfig.fromTiledMap(
                    chapter.getMap(),
                    5,
                    9
                )
            );

        chapter.getSeason().setupMap(grid);

        if (chapter.isMinigame() && chapter.getMinigameLogic() != null) {
            MinigameWorld minigameWorld;

            if (chapter.getMinigameLogic() instanceof VaseBreakerMode) {
                minigameWorld = new VaseBreakerWorld(
                        chapter.getMinigameLogic(),
                        chapter.getSeason()
                );
            } else if (chapter.getMinigameLogic() instanceof IZombieCouchPlayMode) {
                minigameWorld = new IZombieCouchPlayWorld(
                        chapter.getMinigameLogic(),
                        chapter.getSeason()
                );
            } else if (chapter.getMinigameLogic() instanceof IZombieMode) {
                minigameWorld = new IZombieWorld(
                        chapter.getMinigameLogic(),
                        chapter.getSeason()
                );
            } else if (chapter.getMinigameLogic() instanceof WallNutBowlingMode) {
                minigameWorld = new WallNutBowingWorld(
                        chapter.getMinigameLogic(),
                        chapter.getSeason()
                );
            } else if (chapter.getMinigameLogic() instanceof BeghouledMode beghouledMode) {
                minigameWorld = new BeghouledWorld(
                        chapter.getMinigameLogic(),
                        chapter.getSeason(),
                        beghouledMode.getComboTarget()
                );
            } else {
                minigameWorld = new MinigameWorld(
                        chapter.getMinigameLogic(),
                        chapter.getSeason()
                );
            }

            minigameWorld.setCurrentChapter(chapter);
            minigameWorld.setCurrentSeason(chapter.getSeason());
            minigameWorld.setGrid(grid);
            chapter.getMinigameLogic().setWorld(minigameWorld);

            world = minigameWorld;
        } else {
            world = new GameWorld();
            world.setCurrentChapter(chapter);
            world.setCurrentSeason(chapter.getSeason());
            world.setGrid(grid);
        }

        world.getMap().initFromGrid(
            chapter.getMap(),
            grid,
            chapter.getLawnMowerType()
        );

        world.setLevelCondition(chapter.getLevelCondition());
        return world;
    }
}
