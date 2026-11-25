package game.world.chapter.rule.speciallevelRules;

import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.engine.system.sun.SunSimulationSystem;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import graphics.GraphicsEngine;

public class NoSunRule implements ChapterRule {

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {

    }

    @Override
    public void onTick(GameWorld world, long currentTick) {

    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
        engine.removeSystem(SunSimulationSystem.class);
    }
}
