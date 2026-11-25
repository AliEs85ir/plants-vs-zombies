package game.entity.quest;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantTag;
import game.entity.plant.Plant;
import game.service.quest.GameObserver;
import game.world.GameWorld;

import java.util.Set;

/**
 * End-of-level snapshot of what happened, independent of which quests are active.
 * Quests query this with their own targets instead of baking answers into the observer.
 */
public final class LevelFacts {
    private final GameWorld world;
    private final GameObserver observer;

    private LevelFacts(GameWorld world, GameObserver observer) {
        this.world = world;
        this.observer = observer;
    }

    public static LevelFacts from(GameWorld world) {
        if (world == null || world.getObserver() == null) {
            return null;
        }
        return new LevelFacts(world, world.getObserver());
    }

    public int remainingSun() {
        return world.getSunAmount();
    }

    public int lostPlantsCount() {
        return observer.getLostPlantsCount();
    }

    public int sunProducerCount() {
        return observer.getSunProducerCount();
    }

    public boolean isDayLevel() {
        return !world.isNight();
    }

    public int difficulty() {
        return world.getDifficultyLevel();
    }

    public boolean usedFamily(PlantCategory category) {
        return category != null && observer.familiesPlaced().contains(category);
    }

    public boolean usedOnlyCombatFamily(PlantCategory category) {
        if (category == null || !usedFamily(category)) {
            return false;
        }
        for (PlantCategory placed : observer.familiesPlaced()) {
            if (placed == PlantCategory.SUN_PRODUCER || placed == PlantCategory.WALL_NUT) {
                continue;
            }
            if (placed != category) {
                return false;
            }
        }
        return killedOnlyWithCombatFamily(category);
    }

    public boolean killedOnlyWithCombatFamily(PlantCategory category) {
        if (category == null) {
            return false;
        }
        for (PlantCategory killed : observer.familiesThatKilled()) {
            if (killed == PlantCategory.SUN_PRODUCER || killed == PlantCategory.WALL_NUT) {
                continue;
            }
            if (killed != category) {
                return false;
            }
        }
        return true;
    }

    public boolean neverPlantedInRow(int row) {
        return !observer.rowsPlanted().contains(row);
    }

    public boolean neverPlantedInColumn(int col) {
        return !observer.columnsPlanted().contains(col);
    }

    public boolean neverPlantedInCross(int index) {
        return neverPlantedInRow(index) && neverPlantedInColumn(index);
    }

    public boolean isRowEmpty(int row) {
        return observer.isRowEmpty(row);
    }

    public boolean isColumnEmpty(int col) {
        return observer.isColumnEmpty(col);
    }

    public boolean isCrossEmpty(int index) {
        return observer.isCrossEmpty(index);
    }

    public boolean isSymmetric() {
        return observer.isSymmetric();
    }

    public boolean isAsymmetricExceptMiddle() {
        return observer.isAsymmetricExceptMiddle();
    }

    public boolean allPlantsAreMushrooms() {
        return observer.allPlantsAreMushrooms();
    }

    public boolean usedOnlyMushrooms() {
        return observer.usedOnlyMushrooms();
    }

    public boolean allLivingPlantsHaveTag(PlantTag tag) {
        if (tag == null || world.getMap() == null || world.getMap().getPlants() == null) {
            return true;
        }
        for (Plant plant : world.getMap().getPlants()) {
            if (plant == null || plant.getDefinition() == null || plant.getDefinition().getTags() == null) {
                continue;
            }
            if (!plant.getDefinition().getTags().hasTag(tag)) {
                return false;
            }
        }
        return true;
    }

    public Set<PlantCategory> familiesPlaced() {
        return observer.familiesPlaced();
    }
}
