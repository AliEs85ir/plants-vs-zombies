package game.entity.zombie.ability;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.PushableAbility;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.movement.NormalMovement;
import game.entity.zombie.strategy.movement.StationaryMovement;
import game.service.Zombie.ZombieService;
import graphics.general.event.ZombieVisualEventType;

import java.util.List;

public class PushObjectAbility extends AbstractZombieAbility{
    private ObstacleType obstacleType;
    private boolean spawnObstacle;
    private Obstacle obstacle;
    private float width;
    private float height;

    private enum State { WALK, PUSH }
    private State state = State.WALK;
    private boolean pushed = false;

    public PushObjectAbility(ObstacleType type, float width, float height) {
        this.obstacleType = type;
        this.spawnObstacle = false;
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        spawnObstacle(zombie);

        switch (state) {
            case WALK -> {
                if (isObstacleClose(zombie)) {
                    state = State.PUSH;
                    zombie.pushEvent(ZombieVisualEventType.PUSH_CABINET);
                    zombie.setMovementStrategy(new StationaryMovement());
                }
            }
            case PUSH -> {
                zombie.setMovementStrategy(new StationaryMovement());
                if (!pushed) {
                    pushed = true;
                    App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                        GameClock.currentTick,
                        19,
                        () -> finishPush(zombie)
                    );
                }
            }
        }
    }

    private void finishPush(Zombie zombie) {
        if(obstacle.isDead()) {
            return;
        }
        float nudgeSpeed = 1f;
        float impulse = obstacle.getBody().getMass() * nudgeSpeed;
        obstacle.getBody().applyLinearImpulse(
            new Vector2(-impulse, 0f),
            obstacle.getBody().getWorldCenter(),
            true
        );
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(
            GameClock.currentTick,
            5,
            () -> normal(zombie)
        );
    }

    private void normal(Zombie zombie) {
        state = State.WALK;
        pushed = false;
        zombie.setMovementStrategy(new NormalMovement());
    }

    private void spawnObstacle(Zombie zombie) {
        if (!spawnObstacle) {
            this.obstacle = ObstacleFactory.getInstance().createRuntimeObstacle(
                obstacleType,
                width,
                height,
                (float) zombie.getPosition().getX() - width * 4,
                (float) zombie.getPosition().getY(),
                List.of(new TakeDamageAbility(400), new PushableAbility())
            );
            spawnObstacle = true;
        }
    }

    private boolean isObstacleClose(Zombie zombie) {
        if(obstacle.isDead()) {
            return false;
        }
        double obstacleX = obstacle.getPosition().getX();
        double zombieX = zombie.getPosition().getX();
        return zombieX - obstacleX >= 0 && zombieX - obstacleX <= 180f;
    }

    @Override
    public void onDeath(Zombie zombie) {
    }
}
