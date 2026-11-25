package game.save;

import java.util.ArrayList;
import java.util.List;

public class LevelSaveData {
    public static final int CURRENT_VERSION = 1;

    private int version = CURRENT_VERSION;
    private String chapterName;
    private long currentTick;
    private float clockAccumulator;
    private int sunAmount;
    private int currentWaveNumber;
    private int nextWaveIndex;
    private double spawnDeposit;
    private double spawnThreshold;
    private boolean finalWaveStarted;
    private boolean victoryTriggered;
    private int totalDeadZombies;
    private int totalDeadPlants;
    private int totalPlantsPlanted;
    private List<String> selectedPlants = new ArrayList<>();
    private List<PlantSaveData> plants = new ArrayList<>();
    private List<ZombieSaveData> zombies = new ArrayList<>();
    private List<MowerSaveData> mowers = new ArrayList<>();
    private List<ObstacleSaveData> obstacles = new ArrayList<>();
    private List<CooldownSaveData> cooldowns = new ArrayList<>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(long currentTick) {
        this.currentTick = currentTick;
    }

    public float getClockAccumulator() {
        return clockAccumulator;
    }

    public void setClockAccumulator(float clockAccumulator) {
        this.clockAccumulator = clockAccumulator;
    }

    public int getSunAmount() {
        return sunAmount;
    }

    public void setSunAmount(int sunAmount) {
        this.sunAmount = sunAmount;
    }

    public int getCurrentWaveNumber() {
        return currentWaveNumber;
    }

    public void setCurrentWaveNumber(int currentWaveNumber) {
        this.currentWaveNumber = currentWaveNumber;
    }

    public int getNextWaveIndex() {
        return nextWaveIndex;
    }

    public void setNextWaveIndex(int nextWaveIndex) {
        this.nextWaveIndex = nextWaveIndex;
    }

    public double getSpawnDeposit() {
        return spawnDeposit;
    }

    public void setSpawnDeposit(double spawnDeposit) {
        this.spawnDeposit = spawnDeposit;
    }

    public double getSpawnThreshold() {
        return spawnThreshold;
    }

    public void setSpawnThreshold(double spawnThreshold) {
        this.spawnThreshold = spawnThreshold;
    }

    public boolean isFinalWaveStarted() {
        return finalWaveStarted;
    }

    public void setFinalWaveStarted(boolean finalWaveStarted) {
        this.finalWaveStarted = finalWaveStarted;
    }

    public boolean isVictoryTriggered() {
        return victoryTriggered;
    }

    public void setVictoryTriggered(boolean victoryTriggered) {
        this.victoryTriggered = victoryTriggered;
    }

    public int getTotalDeadZombies() {
        return totalDeadZombies;
    }

    public void setTotalDeadZombies(int totalDeadZombies) {
        this.totalDeadZombies = totalDeadZombies;
    }

    public int getTotalDeadPlants() {
        return totalDeadPlants;
    }

    public void setTotalDeadPlants(int totalDeadPlants) {
        this.totalDeadPlants = totalDeadPlants;
    }

    public int getTotalPlantsPlanted() {
        return totalPlantsPlanted;
    }

    public void setTotalPlantsPlanted(int totalPlantsPlanted) {
        this.totalPlantsPlanted = totalPlantsPlanted;
    }

    public List<String> getSelectedPlants() {
        return selectedPlants;
    }

    public void setSelectedPlants(List<String> selectedPlants) {
        this.selectedPlants = selectedPlants == null ? new ArrayList<>() : selectedPlants;
    }

    public List<PlantSaveData> getPlants() {
        return plants;
    }

    public void setPlants(List<PlantSaveData> plants) {
        this.plants = plants == null ? new ArrayList<>() : plants;
    }

    public List<ZombieSaveData> getZombies() {
        return zombies;
    }

    public void setZombies(List<ZombieSaveData> zombies) {
        this.zombies = zombies == null ? new ArrayList<>() : zombies;
    }

    public List<MowerSaveData> getMowers() {
        return mowers;
    }

    public void setMowers(List<MowerSaveData> mowers) {
        this.mowers = mowers == null ? new ArrayList<>() : mowers;
    }

    public List<ObstacleSaveData> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<ObstacleSaveData> obstacles) {
        this.obstacles = obstacles == null ? new ArrayList<>() : obstacles;
    }

    public List<CooldownSaveData> getCooldowns() {
        return cooldowns;
    }

    public void setCooldowns(List<CooldownSaveData> cooldowns) {
        this.cooldowns = cooldowns == null ? new ArrayList<>() : cooldowns;
    }

    public static final class PlantSaveData {
        private String name;
        private int row;
        private int col;
        private double health;
        private long plantedTick;
        private boolean boosted;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public double getHealth() {
            return health;
        }

        public void setHealth(double health) {
            this.health = health;
        }

        public long getPlantedTick() {
            return plantedTick;
        }

        public void setPlantedTick(long plantedTick) {
            this.plantedTick = plantedTick;
        }

        public boolean isBoosted() {
            return boosted;
        }

        public void setBoosted(boolean boosted) {
            this.boosted = boosted;
        }
    }

    public static final class ZombieSaveData {
        private String alias;
        private float x;
        private float y;
        private double health;
        private String state;
        private boolean canMove;

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public double getHealth() {
            return health;
        }

        public void setHealth(double health) {
            this.health = health;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public boolean isCanMove() {
            return canMove;
        }

        public void setCanMove(boolean canMove) {
            this.canMove = canMove;
        }
    }

    public static final class MowerSaveData {
        private int row;
        private boolean used;
        private String state;

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    public static final class ObstacleSaveData {
        private String type;
        private int row;
        private int col;
        private double health;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public double getHealth() {
            return health;
        }

        public void setHealth(double health) {
            this.health = health;
        }
    }

    public static final class CooldownSaveData {
        private String name;
        private long lastPlantTick;
        private boolean clearCooldown;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getLastPlantTick() {
            return lastPlantTick;
        }

        public void setLastPlantTick(long lastPlantTick) {
            this.lastPlantTick = lastPlantTick;
        }

        public boolean isClearCooldown() {
            return clearCooldown;
        }

        public void setClearCooldown(boolean clearCooldown) {
            this.clearCooldown = clearCooldown;
        }
    }
}
