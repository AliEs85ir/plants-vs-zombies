package game.entity.zombie.data;

import app.enums.zombie.ZombieStatTier;
import app.enums.zombie.ZombieStatsType;

import java.util.HashMap;
import java.util.Map;

public class StatsData {
    private final Map<ZombieStatsType, ZombieStatTier> stats;

    public StatsData() {
        this.stats = new HashMap<>();
    }

    public void addStats(ZombieStatsType type, ZombieStatTier tier)
    {
        stats.put(type,tier);
    }
    public ZombieStatTier getStatTier(ZombieStatsType type) {
        return stats.getOrDefault(type, ZombieStatTier.TIER_1);
    }

    public boolean hasStat(ZombieStatsType type) {
        return stats.containsKey(type);
    }
}
