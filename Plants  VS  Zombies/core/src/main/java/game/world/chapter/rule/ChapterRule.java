package game.world.chapter.rule;

import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.world.GameWorld;
import graphics.GraphicsEngine;

public interface ChapterRule {
    void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService);
    void onTick(GameWorld world, long currentTick);
    void onEngineStart(GameSimulationEngine engine);

    default boolean allowsManualPlantSelection() {
        return true;
    }
}
