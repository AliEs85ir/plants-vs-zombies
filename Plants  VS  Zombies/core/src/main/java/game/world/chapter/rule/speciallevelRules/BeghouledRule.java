package game.world.chapter.rule.speciallevelRules;

import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.engine.system.beghouled.BeghouledSimulationSystem;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.minigame.beghouled.BeghouledWorld;
import graphics.GraphicsEngine;

public class BeghouledRule implements ChapterRule {
    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        if (world != null) {
            world.addEvent("Beghouled: swap adjacent plants to match three or more.");
        }
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
        if (engine != null && engine.getGameWorld() instanceof BeghouledWorld) {
            engine.addSystem(new BeghouledSimulationSystem(engine.getGameWorld()));
        }
    }

    @Override
    public boolean allowsManualPlantSelection() {
        return false;
    }
}
