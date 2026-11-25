package game.service.drop;

import app.model.App;
import com.badlogic.gdx.math.Vector2;
import game.entity.drop.Drop;
import game.world.GameWorld;
import graphics.GraphicsEngine;

import java.util.List;

public class DropCollector {
    private final GameWorld gameWorld;

    public DropCollector(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public Drop collect(Vector2 mouseWorld) {
        if (mouseWorld == null || gameWorld == null) {
            return null;
        }
        Drop closest = null;
        float closestDistance = DropSettings.COLLECT_RADIUS;
        for (Drop drop : List.copyOf(gameWorld.getDrops())) {
            if (!drop.isCollectable() || drop.getPosition() == null) {
                continue;
            }
            float distance = mouseWorld.dst(
                (float) drop.getPosition().getX(),
                (float) drop.getPosition().getY()
            );
            if (distance <= closestDistance) {
                closest = drop;
                closestDistance = distance;
            }
        }
        if (closest == null) {
            return null;
        }
        collect(closest);
        return closest;
    }

    public void collect(Drop drop) {
        if (drop == null || !drop.isCollectable()) {
            return;
        }
        drop.markCollected();
        game.audio.Sfx.dropCollected(drop.getType());
        DropRewardService.apply(drop.getType(), gameWorld);
        remove(drop);
    }

    public void remove(Drop drop) {
        if (drop == null) {
            return;
        }
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
            graphicsEngine.unregisterDrop(drop);
        }
        gameWorld.removeDrop(drop);
    }
}
