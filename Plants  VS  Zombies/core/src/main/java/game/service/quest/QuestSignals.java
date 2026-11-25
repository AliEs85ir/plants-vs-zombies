package game.service.quest;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.quest.EventType;
import app.model.App;
import game.engine.time.GameClock;
import game.entity.Entity;
import game.entity.lawnmower.LawnMower;
import game.entity.plant.Plant;
import game.entity.projectile.Projectile;
import game.entity.quest.GameEvent;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.chapter.wave.WaveConfig;
import game.world.playground.Cell;

public final class QuestSignals {
    private QuestSignals() {
    }

    public static void zombieKilled(Zombie zombie, Entity killer) {
        if (zombie == null || zombie.getWorld() == null) {
            return;
        }
        GameWorld world = zombie.getWorld();
        String plantName = null;
        String family = null;
        PlantCategory killerFamily = null;
        Plant plant = resolvePlant(killer);
        if (plant != null && plant.getDefinition() != null) {
            PlantName name = plant.getDefinition().getName();
            plantName = name == null ? null : name.name();
            killerFamily = plant.getDefinition().getCategory();
            family = killerFamily == null ? null : killerFamily.name();
        }
        if (world.getObserver() != null) {
            world.getObserver().onZombieKilled(killerFamily);
        }
        int chapter = world.getCurrentChapter() == null ? -1 : world.getCurrentChapter().getChapterNumber();
        int seconds = secondsSinceFirstWave(world);
        fire(new GameEvent(
            EventType.ZOMBIE_KILLED,
            1,
            plantName,
            family,
            chapter,
            seconds,
            world.getDifficultyLevel(),
            isFirstColumnWithNoMower(world, zombie)
        ));
    }

    public static void fire(GameEvent event) {
        if (event == null || App.getCurrentUser() == null || App.getCurrentUser().getQuestManager() == null) {
            return;
        }
        App.getCurrentUser().getQuestManager().fireEvent(event);
    }

    public static void plantExploded(Plant plant) {
        String plantName = null;
        String family = null;
        if (plant != null && plant.getDefinition() != null) {
            PlantName name = plant.getDefinition().getName();
            plantName = name == null ? null : name.name();
            PlantCategory category = plant.getDefinition().getCategory();
            family = category == null ? null : category.name();
        }
        fire(new GameEvent(EventType.PLANT_EXPLODED, 1, plantName, family, -1, -1, -1, false));
    }

    public static void lawnMowerKilled(int amount) {
        if (amount <= 0) {
            return;
        }
        fire(new GameEvent(EventType.LAWN_MOWER_TRIGGERED, amount));
    }

    private static int secondsSinceFirstWave(GameWorld world) {
        WaveConfig config = WaveConfig.getDefaultWaveConfig();
        if (world != null && world.getCurrentChapter() != null && world.getCurrentChapter().getWaveConfig() != null) {
            config = world.getCurrentChapter().getWaveConfig();
        }
        return config.secondsSinceFirstWave(GameClock.currentTick);
    }

    private static Plant resolvePlant(Entity killer) {
        if (killer instanceof Plant plant) {
            return plant;
        }
        if (killer instanceof Projectile projectile) {
            return projectile.getOwner();
        }
        return null;
    }

    private static boolean isFirstColumnWithNoMower(GameWorld world, Zombie zombie) {
        if (world.getGrid() == null || zombie.getPosition() == null) {
            return false;
        }
        Cell cell = world.getGrid().getCellAt(zombie.getPosition());
        if (cell == null || cell.getCol() != 0) {
            return false;
        }
        int row = cell.getRow();
        if (world.getLawnMowers() == null) {
            return true;
        }
        for (LawnMower mower : world.getLawnMowers()) {
            if (mower != null && mower.getRow() == row && !mower.isUsed()) {
                return false;
            }
        }
        return true;
    }
}
