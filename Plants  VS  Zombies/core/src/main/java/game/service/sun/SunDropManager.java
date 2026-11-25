package game.service.sun;

import app.enums.sun.SunState;
import common.model.Position;
import game.entity.sun.PlantSun;
import game.entity.sun.SkySun;
import game.entity.sun.Sun;
import game.world.GameWorld;

public class SunDropManager {
    private static final long MAX_DROP_TICKS = 50;
    private final GameWorld gameWorld;
    private final float groundY;

    public SunDropManager(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        var config = gameWorld.getGrid().getConfig();
        this.groundY = config.getPlaygroundY() + 35;
    }

    public void update(long currentTick) {
        for (Sun sun : gameWorld.getSuns()) {
            if (sun instanceof PlantSun plantSun && plantSun.isTossing()) {
                plantSun.advanceToss();
                continue;
            }
            if (!(sun instanceof SkySun skySun) || skySun.getState() != SunState.DROPPING)
                continue;

            skySun.move();
            skySun.setLastMoveTick(currentTick);

            if (skySun.getPosition().getY() <= groundY
                || currentTick - skySun.getSpawnTick() >= MAX_DROP_TICKS) {
                land(skySun);
            }
        }
    }

    private void land(SkySun skySun) {
        skySun.syncBody();
        skySun.land();

        Position p = skySun.getPosition();
        gameWorld.addEvent(
            "New " + skySun.getType().name().toLowerCase()
                + " Sun reached the ground at position (<"
                + (int) p.getX() + ">, <" + (int) p.getY() + ">)"
        );
    }
}
