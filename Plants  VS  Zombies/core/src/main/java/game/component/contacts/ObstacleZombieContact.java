package game.component.contacts;

import app.model.App;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import game.entity.zombie.boss.boss.Zomboss;
import game.world.GameWorld;
import game.world.playground.Grid;

public final class ObstacleZombieContact {
    private static final float DEFAULT_ZOMBIE_WIDTH = 40f;
    private static final float DEFAULT_ZOMBIE_HEIGHT = 80f;

    private static final float DEFAULT_OBSTACLE_WIDTH = 40f;
    private static final float DEFAULT_OBSTACLE_HEIGHT = 40f;

    private ObstacleZombieContact() {
    }

    public static void registerHit(Obstacle obstacle, Zombie zombie) {
        if (obstacle == null || zombie == null || zombie.isDead() || obstacle.isDead()) {
            return;
        }
        if (zombie instanceof Zomboss) {
            return;
        }

        obstacle.addTargetZombie(zombie);
    }

    public static void resolveOverlaps(Zombie zombie) {
        if (zombie == null || zombie.isDead() || zombie.getPosition() == null) {
            return;
        }
        if (App.getCurrentGameEngine() == null) {
            return;
        }
        GameWorld world = App.getCurrentGameEngine().getGameWorld();
        if (world == null || world.getObstacles() == null) {
            return;
        }
        Grid grid = world.getGrid();
        if (grid == null) {
            return;
        }
        int zombieLane = grid.getEntityLane(zombie);
        if (zombieLane < 0) {
            return;
        }

        float zx = (float) zombie.getPosition().getX();
        float zy = (float) zombie.getPosition().getY();
        float[] hitbox = zombieHitbox(zombie);
        float zombieWidth = hitbox[0];
        float zombieHeight = hitbox[1];

        for (Obstacle obstacle : world.getObstacles()) {
            if (obstacle == null || obstacle.isDead() || obstacle.getPosition() == null) {
                continue;
            }
            if (grid.getEntityLane(obstacle) != zombieLane) {
                continue;
            }
            float ox = (float) obstacle.getPosition().getX();
            float oy = (float) obstacle.getPosition().getY();

            float[] obsHitbox = obstacleHitbox(obstacle);
            float obstacleWidth = obsHitbox[0];
            float obstacleHeight = obsHitbox[1];

            if (overlaps(
                zx, zy, zombieWidth, zombieHeight,
                ox, oy, obstacleWidth, obstacleHeight
            )) {
                registerHit(obstacle, zombie);
            }
        }
    }

    private static float[] zombieHitbox(Zombie zombie) {
        String name = zombie.getName();
        if (name != null && name.contains("Gargantuar")) {
            return new float[]{70f, 90f};
        }
        if (name != null && name.contains("Imp")) {
            return new float[]{30f, 50f};
        }
        return new float[]{DEFAULT_ZOMBIE_WIDTH, DEFAULT_ZOMBIE_HEIGHT};
    }

    private static float[] obstacleHitbox(Obstacle obstacle) {
        return new float[]{DEFAULT_OBSTACLE_WIDTH, DEFAULT_OBSTACLE_HEIGHT};
    }

    private static boolean overlaps(
        float ax,
        float ay,
        float aw,
        float ah,
        float bx,
        float by,
        float bw,
        float bh
    ) {
        float aLeft = ax - aw * 0.5f;
        float aRight = ax + aw * 0.5f;
        float aBottom = ay - ah * 0.5f;
        float aTop = ay + ah * 0.5f;

        float bLeft = bx - bw * 0.5f;
        float bRight = bx + bw * 0.5f;
        float bBottom = by - bh * 0.5f;
        float bTop = by + bh * 0.5f;

        return aLeft < bRight && aRight > bLeft && aBottom < bTop && aTop > bBottom;
    }
}
