package game.component.contacts;

import app.model.App;
import game.entity.lawnmower.LawnMower;
import game.entity.lawnmower.LawnmowerState;
import game.entity.zombie.Zombie;
import game.entity.zombie.boss.boss.Zomboss;
import game.world.GameWorld;
import game.world.playground.Grid;
import graphics.general.event.LMVisualEventType;
import graphics.general.event.VisualEvent;
import graphics.lawnmower.LMRenderInstance;

public final class LawnMowerZombieContact {
    private static final float DEFAULT_ZOMBIE_WIDTH = 40f;
    private static final float DEFAULT_ZOMBIE_HEIGHT = 80f;

    private LawnMowerZombieContact() {
    }

    public static void registerHit(LawnMower lawnMower, Zombie zombie) {
        if (lawnMower == null || zombie == null || zombie.isDead()) {
            return;
        }
        if (lawnMower.getState() == LawnmowerState.DEAD) {
            return;
        }
        if(zombie instanceof Zomboss) {
            return;
        }
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        LMRenderInstance instance = App.getRealTimeEngine().getGraphicsEngine().getLMInstance(lawnMower);
        if (instance != null) {
            instance.pushEvent(new VisualEvent<>(LMVisualEventType.HIT_ZOMBIE));
        }
        lawnMower.addTargetZombie(zombie);
    }

    public static void resolveOverlaps(Zombie zombie) {
        if (zombie == null || zombie.isDead() || zombie.getPosition() == null) {
            return;
        }
        if (App.getCurrentGameEngine() == null) {
            return;
        }
        GameWorld world = App.getCurrentGameEngine().getGameWorld();
        if (world == null || world.getLawnMowers() == null) {
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

        for (LawnMower mower : world.getLawnMowers()) {
            if (mower == null || mower.getState() == LawnmowerState.DEAD || mower.getPosition() == null) {
                continue;
            }
            if (grid.getEntityLane(mower) != zombieLane) {
                continue;
            }
            float mx = (float) mower.getPosition().getX();
            float my = (float) mower.getPosition().getY();
            if (overlaps(
                zx, zy, zombieWidth, zombieHeight,
                mx, my, LawnMower.LAWNMOWER_WIDTH, LawnMower.LAWNMOWER_HEIGHT
            ) || crossedMower(zx, zombieWidth, mx)) {
                registerHit(mower, zombie);
            }
        }
    }

    private static boolean crossedMower(float zombieCenterX, float zombieWidth, float mowerCenterX) {
        float mowerBack = mowerCenterX + LawnMower.LAWNMOWER_WIDTH * 0.5f;
        float mowerFront = mowerCenterX - LawnMower.LAWNMOWER_WIDTH * 0.5f;
        float zombieFront = zombieCenterX - zombieWidth * 0.5f;
        if (zombieFront > mowerBack) {
            return false;
        }
        if (zombieCenterX < mowerFront - zombieWidth) {
            return false;
        }
        return true;
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
