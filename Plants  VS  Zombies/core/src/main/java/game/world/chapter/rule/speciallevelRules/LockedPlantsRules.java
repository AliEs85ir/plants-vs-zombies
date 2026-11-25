package game.world.chapter.rule.speciallevelRules;

import app.enums.plant.PlantName;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import graphics.GraphicsEngine;

import java.util.List;

public class LockedPlantsRules implements ChapterRule {

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {

    }

    @Override
    public void onTick(GameWorld world, long currentTick) {

    }
    @Override
    public void onEngineStart(GameSimulationEngine engine) {

    }
    public void lockPlantFamilies(){}

    public void lockSpecificPlants(){}

    public boolean isPlantLocked(PlantName PlantName){return false;}

    public List<PlantName> getAvailablePlants(){return null;}

    public void unlockPlant(PlantName PlantName){}

    public void applyFamilyLockRule(){}
}
