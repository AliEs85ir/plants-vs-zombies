package game.entity.score;

import java.time.LocalDate;
import java.util.List;

public class DailyZombieSpawnPattern {
    private final LocalDate date;
    private List<String> zombieTypes;
    private List<Integer> spawnRows;
    private List<Integer> spawnColumns;
    private List<Long> spawnTicks;

    public DailyZombieSpawnPattern(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<String> getZombieTypes() {
        return zombieTypes;
    }

    public void setZombieTypes(List<String> zombieTypes) {
        this.zombieTypes = zombieTypes;
    }

    public List<Integer> getSpawnRows() {
        return spawnRows;
    }

    public void setSpawnRows(List<Integer> spawnRows) {
        this.spawnRows = spawnRows;
    }

    public List<Integer> getSpawnColumns() {
        return spawnColumns;
    }

    public void setSpawnColumns(List<Integer> spawnColumns) {
        this.spawnColumns = spawnColumns;
    }

    public List<Long> getSpawnTicks() {
        return spawnTicks;
    }

    public void setSpawnTicks(List<Long> spawnTicks) {
        this.spawnTicks = spawnTicks;
    }
}
