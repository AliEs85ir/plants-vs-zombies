package game.world.chapter.rule.scoringgame;

import game.component.physic.PhysicService;
import game.world.GameWorld;
import graphics.GraphicsEngine;

public class CloseCallPattern extends ScoringPattern {
    private static final int MIO_POINTS = 20;

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        forEachNewDead(world, zombie -> {
            if (columnOf(world, zombie) == 0) {
                award("Close Call! +" + MIO_POINTS + " Meow", MIO_POINTS);
            }
        });
    }
}
