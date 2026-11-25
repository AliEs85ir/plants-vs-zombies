package game.entity.projectile;

import common.model.Position;
import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.world.GameWorld;

/**
 * Swept previous-to-current overlap against existing obstacle list, then
 * delegates to {@link Projectile#handleHit(Obstacle, long)} (Box2D remains SoT
 * when a contact fires; hitObstacles de-dupes).
 */
public final class ProjectileObstacleSweep {
    private static final float PAD = 36f;

    private ProjectileObstacleSweep() {
    }

    public static void collideAlongMove(GameWorld world, Projectile projectile) {
        if (world == null || projectile == null || !projectile.isActive()) {
            return;
        }
        if (!projectile.hasLastPosition()) {
            return;
        }
        Position current = projectile.getPosition();
        if (current == null) {
            return;
        }
        float x0 = (float) projectile.getLastX();
        float y0 = (float) projectile.getLastY();
        float x1 = (float) current.getX();
        float y1 = (float) current.getY();
        float minX = Math.min(x0, x1) - PAD;
        float maxX = Math.max(x0, x1) + PAD;
        float minY = Math.min(y0, y1) - PAD;
        float maxY = Math.max(y0, y1) + PAD;
        long tick = GameClock.currentTick;
        for (Obstacle obstacle : world.getObstacles()) {
            if (!projectile.isActive()) {
                return;
            }
            if (obstacle == null || obstacle.isDead() || !obstacle.isDamageable()) {
                continue;
            }
            if (!projectile.lineMatches(obstacle)) {
                continue;
            }
            Position position = obstacle.getPosition();
            if (position == null) {
                continue;
            }
            float ox = (float) position.getX();
            float oy = (float) position.getY();
            if (ox < minX || ox > maxX || oy < minY || oy > maxY) {
                continue;
            }
            projectile.handleHit(obstacle, tick);
        }
    }
}
