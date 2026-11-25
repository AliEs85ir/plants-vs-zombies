package game.world.chapter.rule.scoringgame;

import app.enums.plant.PlantName;
import game.component.physic.PhysicService;
import game.world.GameWorld;
import graphics.GraphicsEngine;

public class SpudowPattern extends ScoringPattern {
    private static final int MIO_POINTS = 25;

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        forEachNewDead(world, zombie -> {
            PlantName plant = plantNameOf(zombie.getLastAttacker());
            if (plant == PlantName.POTATO_MINE || plant == PlantName.PRIMAL_POTATO_MINE) {
                award("SPUDOW! +" + MIO_POINTS + " Meow", MIO_POINTS);
            }
        });
    }
}
