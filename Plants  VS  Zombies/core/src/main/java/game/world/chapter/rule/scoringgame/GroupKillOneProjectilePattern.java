package game.world.chapter.rule.scoringgame;

import game.component.physic.PhysicService;
import game.entity.Entity;
import game.entity.projectile.Projectile;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import graphics.GraphicsEngine;

import java.util.IdentityHashMap;
import java.util.Set;

public class GroupKillOneProjectilePattern extends ScoringPattern {
    private final int count;
    private final int mioPoints;
    private final IdentityHashMap<Projectile, Integer> killsByProjectile = new IdentityHashMap<>();
    private final Set<Zombie> countedDeaths = java.util.Collections.newSetFromMap(new IdentityHashMap<>());

    public GroupKillOneProjectilePattern(int count) {
        this.count = boundedCount(count);
        this.mioPoints = 15 * this.count;
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        killsByProjectile.clear();
        countedDeaths.clear();
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        if (world == null || world.getZombies() == null) {
            return;
        }
        for (Zombie zombie : world.getZombies()) {
            if (zombie == null || !zombie.isDead() || !countedDeaths.add(zombie)) {
                continue;
            }
            Projectile projectile = projectileOf(zombie.getLastAttacker());
            if (projectile == null) {
                continue;
            }
            int kills = killsByProjectile.merge(projectile, 1, Integer::sum);
            if (kills == count) {
                award(
                    count + " with one shot! +" + mioPoints + " Meow",
                    mioPoints
                );
            }
        }
        pruneInactiveProjectiles();
    }

    private void pruneInactiveProjectiles() {
        killsByProjectile.entrySet().removeIf(entry ->
            entry.getKey() == null || !entry.getKey().isActive()
        );
    }

    private static Projectile projectileOf(Entity attacker) {
        if (attacker instanceof Projectile projectile) {
            return projectile;
        }
        return null;
    }

    public int getCount() {
        return count;
    }
}
