package game.world.chapter.rule.speciallevelRules;

import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.entity.plant.Plant;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import graphics.GraphicsEngine;

public class LovePlantsRules implements ChapterRule {
    private int count;
    private int lastObservedDeadPlants;

    public LovePlantsRules(int count) {
        this.count = count;
        this.lastObservedDeadPlants = 0;
    }

    @Override
    public void onChapterStart(
        GameWorld world,
        GraphicsEngine graphicsEngine,
        PhysicService physicService
    ) {
        lastObservedDeadPlants = world.getTotalDeadPlants();
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        int totalDeadPlants = world.getTotalDeadPlants();

        int newlyDeadPlants =
            totalDeadPlants - lastObservedDeadPlants;

        if (newlyDeadPlants > 0) {
            count = Math.max(0, count - newlyDeadPlants);
        }

        lastObservedDeadPlants = totalDeadPlants;
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
    }

    public int getCount() {
        return count;
    }
}
