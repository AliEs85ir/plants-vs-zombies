package game.world.chapter.rule.scoringgame;

import game.component.physic.PhysicService;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import graphics.GraphicsEngine;

import java.util.IdentityHashMap;

public class ArmorPopPattern extends ScoringPattern {
    private static final int MIO_POINTS = 10;
    private final IdentityHashMap<Zombie, Integer> lastArmorCount = new IdentityHashMap<>();

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        lastArmorCount.clear();
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        if (world == null || world.getZombies() == null) {
            return;
        }
        for (Zombie zombie : world.getZombies()) {
            if (zombie == null) {
                continue;
            }
            int armorCount = zombie.getArmors() == null ? 0 : zombie.getArmors().size();
            Integer previous = lastArmorCount.put(zombie, armorCount);
            if (previous != null && armorCount < previous) {
                award("Popped! +" + MIO_POINTS + " Meow", MIO_POINTS);
            }
        }
        lastArmorCount.entrySet().removeIf(entry ->
            entry.getKey() == null || entry.getKey().isDead()
        );
    }
}
