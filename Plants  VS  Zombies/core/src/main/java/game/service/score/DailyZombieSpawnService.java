package game.service.score;

import game.entity.score.DailyZombieSpawnPattern;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DailyZombieSpawnService {
    private final Map<LocalDate, DailyZombieSpawnPattern> patterns;

    public DailyZombieSpawnService() {
        this.patterns = new HashMap<>();
    }

    public DailyZombieSpawnPattern getPatternForDate(LocalDate date) {
        return patterns.get(date);
    }

    public void generatePatternForDate(LocalDate date) {
    }
}
