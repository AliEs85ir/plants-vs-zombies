package game.entity.obstacle.ability;

import app.model.App;
import com.badlogic.gdx.math.Vector2;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import game.service.obstacle.ObstacleService;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.event.ObstacleVisualEventType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SlideAbility implements ObstacleAbility{
    private Cell myCell;
    private enum State {IDLE, ACTIVE}
    private State state;
    private Set<Zombie> zombiesOnMe;
    private final Set<Zombie> pendingRemoval;

    public SlideAbility(Cell myCell) {
        this.myCell = myCell;
        this.state = State.IDLE;
        this.zombiesOnMe = new HashSet<>();
        this.pendingRemoval = new HashSet<>();
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        switch (state) {
            case IDLE -> {
                obstacle.pushEvent(ObstacleVisualEventType.IDLE);
                searchZombies(obstacleService);
                if(!zombiesOnMe.isEmpty()) {
                    pushActive(obstacle, obstacleService);
                    state = State.ACTIVE;
                }
            }
            case ACTIVE -> {
                searchZombies(obstacleService);
                applyVelocity(obstacle, obstacleService, currentTick);
                if(zombiesOnMe.isEmpty()) {
                    pushIdle(obstacle, obstacleService);
                    state = State.IDLE;
                }
            }
        }
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }

    private void searchZombies(ObstacleService service) {
        Grid grid = service.getWorld().getGrid();

        for (Zombie zombie : service.getWorld().getZombies()) {
            if (zombie.isDead() || zombie.getBody() == null) {
                continue;
            }

            Cell zombieCell = grid.getCellAt(zombie.getPosition());

            if (myCell.equals(zombieCell)) {
                zombiesOnMe.add(zombie);
            }
        }
    }
    private void pushActive(Obstacle obstacle, ObstacleService obstacleService){
        switch (obstacle.getType()) {
            case SLIDING_TILE_UP -> {
                obstacle.pushEvent(ObstacleVisualEventType.SLIDING_UP_START);
                obstacle.pushEvent(ObstacleVisualEventType.SLIDING_UP_LOOP);
            }
            case SLIDING_TILE_DOWN -> {
                obstacle.pushEvent(ObstacleVisualEventType.SLIDING_DOWN_START);
                obstacle.pushEvent(ObstacleVisualEventType.SLIDING_DOWN_LOOP);
            }
        }
    }
    private void pushIdle(Obstacle obstacle, ObstacleService obstacleService){
        switch (obstacle.getType()) {
            case SLIDING_TILE_UP -> {
                obstacle.pushEvent(ObstacleVisualEventType.SLIDING_UP_END);
                obstacle.pushEvent(ObstacleVisualEventType.IDLE);
            }
            case SLIDING_TILE_DOWN -> {
                obstacle.pushEvent(ObstacleVisualEventType.SLIDING_DOWN_END);
                obstacle.pushEvent(ObstacleVisualEventType.IDLE);
            }
        }
    }

    private void applyVelocity(
        Obstacle obstacle,
        ObstacleService service,
        long currentTick
    ) {
        float direction = switch (obstacle.getType()) {
            case SLIDING_TILE_UP -> 1f;
            case SLIDING_TILE_DOWN -> -1f;
            default -> 0f;
        };

        Grid grid = service.getWorld().getGrid();

        for (Zombie zombie : zombiesOnMe) {
            if (zombie.isDead() || zombie.getBody() == null) {
                scheduleRemoval(zombie, currentTick);
                continue;
            }

            boolean leftCell =
                !myCell.equals(grid.getCellAt(zombie.getPosition()));

            if (leftCell) {
                scheduleRemoval(zombie, currentTick);
            }

            zombie.getBody().setLinearVelocity(
                zombie.getBody().getLinearVelocity().x,
                direction
            );
        }
    }

    private void scheduleRemoval(Zombie zombie, long currentTick) {
        if (!pendingRemoval.add(zombie)) {
            return;
        }

        App.getCurrentGameEngine()
            .getTaskManager()
            .scheduleRelative(
                currentTick,
                10,
                () -> removeZombieAfterDelay(zombie)
            );
    }

    private void removeZombieAfterDelay(Zombie zombie) {
        pendingRemoval.remove(zombie);
        zombiesOnMe.remove(zombie);
        stopVerticalMovement(zombie);
    }


    private void stopVerticalMovement(Zombie zombie) {
        if (zombie.getBody() == null) {
            return;
        }

        Vector2 velocity = zombie.getBody().getLinearVelocity();

        zombie.getBody().setLinearVelocity(
            velocity.x,
            0f
        );
    }


}
