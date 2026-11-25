package game.world.tide.ambush;

import game.world.playground.Cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class RandomExposedLandPlacement implements AmbushPlacement {
    private final Random random;

    public RandomExposedLandPlacement() {
        this(new Random());
    }

    public RandomExposedLandPlacement(Random random) {
        this.random = random;
    }

    @Override
    public List<Cell> choose(List<Cell> newlyExposedLand, int count) {
        if (newlyExposedLand == null || newlyExposedLand.isEmpty() || count <= 0) {
            return List.of();
        }
        List<Cell> pool = new ArrayList<>(newlyExposedLand);
        Collections.shuffle(pool, random);
        int n = Math.min(count, pool.size());
        return List.copyOf(pool.subList(0, n));
    }
}
