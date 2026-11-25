package game.entity.zombie.ability;

import app.view.game.PlayScreen;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import common.utils.ProjectileMath;
import game.engine.time.GameClock;
import game.entity.zombie.Zombie;
import game.entity.zombie.strategy.movement.FlyingMovement;
import game.entity.zombie.strategy.movement.ReverseMovement;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Grid;
import graphics.general.event.ZombieVisualEventType;

public class DynamiteThrowAbility extends AbstractZombieAbility{
    private static final long DYNAMITE_DURATION_TICKS = 10 * GameClock.TICKS_PER_SECOND;
    private long startTick = -1;
    private boolean exploded = false;

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {

        if(startTick == -1) {
            startTick = currentTick;
        }

        if(exploded || !isTimeToExplode(currentTick)) {
            return;
        }
        zombie.pushEvent(ZombieVisualEventType.BLAST_OFF);
        PlayScreen.shake(0.5f, 16f);
        executeExplosion(zombie);
    }

    private void executeExplosion(Zombie zombie) {
        exploded = true;
        Grid grid = zombie.getWorld().getGrid();
        Position startP = zombie.getPosition();
        Position endP = new Position(grid.getConfig().getPlaygroundX() + 50, startP.getY());

        float arcHeightPx = 400f;
        float gravityPxPerTick2 = -20f;

        Vector2 startVec = startP.toVector();
        Vector2 endVec   = endP.toVector();
        Vector2 velocity = ProjectileMath.calculateRequiredVelocityPx(startVec, endVec, arcHeightPx, gravityPxPerTick2);

        FlyingMovement flyMovement = new FlyingMovement(
            startVec,
            velocity,
            gravityPxPerTick2,
            endVec.y
        );

        zombie.setMovementStrategy(flyMovement);
    }


    @Override
    public void onDeath(Zombie zombie) {

    }

    private boolean isTimeToExplode(long currentTick) {
        return (currentTick - startTick) > DYNAMITE_DURATION_TICKS;
    }


}
