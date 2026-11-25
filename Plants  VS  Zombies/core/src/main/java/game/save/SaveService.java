package game.save;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import app.model.App;
import app.model.user.User;
import app.view.game.PlayScreen;
import common.model.Position;
import game.engine.RealTimeGameEngine;
import game.engine.RuntimeEntityInitializer;
import game.engine.system.wave.WaveSimulationSystem;
import game.engine.time.GameClock;
import game.entity.lawnmower.LawnMower;
import game.entity.lawnmower.LawnmowerState;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.IceBlockAbility;
import game.entity.obstacle.ability.ObstacleAbility;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.plant.Plant;
import game.entity.plant.PlantCooldown;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.service.plant.PlantFactory;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.playground.Cell;
import graphics.GraphicsEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SaveService {
    private static final SaveStore STORE = new SaveStore();
    private static LevelSaveData pendingRestore;

    private SaveService() {
    }

    public static SaveKey keyFor(Chapter chapter) {
        User user = App.getCurrentUser();
        String userId = user == null ? "guest" : user.getUsername();
        String chapterName = chapter == null ? "" : chapter.getSaveChapterName();
        return SaveKey.manual(userId, chapterName);
    }

    public static SaveKey keyFor(String chapterName) {
        User user = App.getCurrentUser();
        String userId = user == null ? "guest" : user.getUsername();
        return SaveKey.manual(userId, chapterName);
    }

    public static boolean hasSave(Chapter chapter) {
        return STORE.exists(keyFor(chapter));
    }

    public static boolean hasSave(String chapterName) {
        return STORE.exists(keyFor(chapterName));
    }

    public static SaveResult saveCurrent() {
        RealTimeGameEngine engine = App.getRealTimeEngine();
        if (engine == null || engine.getWorld() == null) {
            return SaveResult.failure("No active game to save.");
        }
        try {
            LevelSaveData data = capture(engine);
            return STORE.save(keyFor(engine.getWorld().getCurrentChapter()), data);
        } catch (RuntimeException exception) {
            return SaveResult.failure("Could not save the game.");
        }
    }

    public static SaveResult load(SaveKey key) {
        return STORE.load(key);
    }

    public static SaveResult delete(SaveKey key) {
        return STORE.delete(key);
    }

    public static SaveResult delete(String chapterName) {
        return STORE.delete(keyFor(chapterName));
    }

    public static void prepareRestore(LevelSaveData data) {
        pendingRestore = data;
    }

    public static void clearPendingRestore() {
        pendingRestore = null;
    }

    public static LevelSaveData consumePendingRestore() {
        LevelSaveData data = pendingRestore;
        pendingRestore = null;
        return data;
    }

    public static LevelSaveData peekPendingRestore() {
        return pendingRestore;
    }

    public static LevelSaveData capture(RealTimeGameEngine engine) {
        GameWorld world = engine.getWorld();
        Chapter chapter = world.getCurrentChapter();
        GameClock clock = engine.getClock();
        WaveSimulationSystem waves = engine.getSimulationEngine().findSystem(WaveSimulationSystem.class);

        LevelSaveData data = new LevelSaveData();
        data.setChapterName(chapter == null ? "" : chapter.getSaveChapterName());
        data.setCurrentTick(clock.getCurrentTick());
        data.setClockAccumulator(clock.getAccumulator());
        data.setSunAmount(world.getSunAmount());
        data.setCurrentWaveNumber(world.getCurrentWaveNumber());
        data.setTotalDeadZombies(world.getTotalDeadZombies());
        data.setTotalDeadPlants(world.getTotalDeadPlants());
        data.setTotalPlantsPlanted(world.getTotalPlantsPlanted());
        if (waves != null) {
            data.setNextWaveIndex(waves.getNextWaveIndex());
            data.setSpawnDeposit(waves.getSpawnDeposit());
            data.setSpawnThreshold(waves.getSpawnThreshold());
            data.setFinalWaveStarted(waves.isFinalWaveStarted());
            data.setVictoryTriggered(waves.isVictoryTriggered());
        }

        List<String> selected = new ArrayList<>();
        if (world.getSelectedGamePlants() != null) {
            for (PlantName name : world.getSelectedGamePlants()) {
                selected.add(name.name());
            }
        }
        data.setSelectedPlants(selected);

        List<LevelSaveData.PlantSaveData> plants = new ArrayList<>();
        for (Plant plant : world.getPlants()) {
            if (plant == null || !plant.isAlive() || plant.getDefinition() == null || plant.getCell() == null) {
                continue;
            }
            LevelSaveData.PlantSaveData entry = new LevelSaveData.PlantSaveData();
            entry.setName(plant.getDefinition().getName().name());
            entry.setRow(plant.getCell().getRow());
            entry.setCol(plant.getCell().getCol());
            entry.setHealth(plant.getCurrentHealth() == null ? 0 : plant.getCurrentHealth().getCurrentHealth());
            entry.setPlantedTick(plant.getPlantedTick());
            entry.setBoosted(plant.isBoosted());
            plants.add(entry);
        }
        data.setPlants(plants);

        List<LevelSaveData.ZombieSaveData> zombies = new ArrayList<>();
        for (Zombie zombie : world.getZombies()) {
            if (zombie == null || zombie.isDead() || zombie.getPosition() == null) {
                continue;
            }
            LevelSaveData.ZombieSaveData entry = new LevelSaveData.ZombieSaveData();
            entry.setAlias(zombie.getName());
            entry.setX((float) zombie.getPosition().getX());
            entry.setY((float) zombie.getPosition().getY());
            entry.setHealth(zombie.getCurrentHealth());
            entry.setState(zombie.getState() == null ? Zombie.ZombieState.WALKING.name() : zombie.getState().name());
            zombies.add(entry);
        }
        data.setZombies(zombies);

        List<LevelSaveData.MowerSaveData> mowers = new ArrayList<>();
        for (LawnMower mower : world.getLawnMowers()) {
            if (mower == null) {
                continue;
            }
            LevelSaveData.MowerSaveData entry = new LevelSaveData.MowerSaveData();
            entry.setRow(mower.getRow());
            entry.setUsed(mower.isUsed() || mower.getState() == LawnmowerState.DEAD);
            entry.setState(mower.getState() == null ? LawnmowerState.IDLE.name() : mower.getState().name());
            mowers.add(entry);
        }
        data.setMowers(mowers);

        List<LevelSaveData.ObstacleSaveData> obstacles = new ArrayList<>();
        for (Obstacle obstacle : world.getObstacles()) {
            if (obstacle == null || obstacle.isDead() || !ObstacleFactory.isPersistentMapObstacle(obstacle.getType())) {
                continue;
            }
            Cell cell = world.getGrid().getCellAt(obstacle.getPosition());
            if (cell == null) {
                continue;
            }
            LevelSaveData.ObstacleSaveData entry = new LevelSaveData.ObstacleSaveData();
            entry.setType(obstacle.getType().name());
            entry.setRow(cell.getRow());
            entry.setCol(cell.getCol());
            entry.setHealth(obstacleHealth(obstacle));
            obstacles.add(entry);
        }
        data.setObstacles(obstacles);

        List<LevelSaveData.CooldownSaveData> cooldowns = new ArrayList<>();
        Map<PlantName, PlantCooldown> cooldownMap = world.getCooldownPlants();
        if (cooldownMap != null) {
            for (Map.Entry<PlantName, PlantCooldown> entry : cooldownMap.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                LevelSaveData.CooldownSaveData saved = new LevelSaveData.CooldownSaveData();
                saved.setName(entry.getKey().name());
                saved.setLastPlantTick(entry.getValue().getLastPlantTick());
                saved.setClearCooldown(entry.getValue().isCooldownCleared());
                cooldowns.add(saved);
            }
        }
        data.setCooldowns(cooldowns);
        return data;
    }

    public static void restore(PlayScreen screen, LevelSaveData data) {
        if (screen == null || data == null) {
            return;
        }
        RealTimeGameEngine engine = screen.getEngine();
        GameWorld world = engine.getWorld();
        GraphicsEngine graphicsEngine = engine.getGraphicsEngine();
        engine.getClock().restore(data.getCurrentTick(), data.getClockAccumulator());
        world.setSunAmount(data.getSunAmount());
        world.setCurrentWaveNumber(data.getCurrentWaveNumber());
        world.setTotalDeadZombies(data.getTotalDeadZombies());
        world.setTotalDeadPlants(data.getTotalDeadPlants());
        world.setTotalPlantsPlanted(data.getTotalPlantsPlanted());

        WaveSimulationSystem waves = engine.getSimulationEngine().findSystem(WaveSimulationSystem.class);
        if (waves != null) {
            waves.restore(
                data.getNextWaveIndex(),
                data.getSpawnDeposit(),
                data.getSpawnThreshold(),
                data.isFinalWaveStarted(),
                data.isVictoryTriggered()
            );
        }

        restorePlants(world, graphicsEngine, data);
        restoreZombies(world, data);
        restoreObstacles(world, data);
        restoreMowers(world, data);
        restoreCooldowns(world, data);

        List<PlantName> selected = new ArrayList<>();
        if (data.getSelectedPlants() != null) {
            for (String name : data.getSelectedPlants()) {
                PlantName plantName = parsePlantName(name);
                if (plantName != null) {
                    selected.add(plantName);
                }
            }
        }
        if (!selected.isEmpty()) {
            world.loadSelectedGamePlants(selected);
            restoreCooldowns(world, data);
            screen.getHud().finalizePlantCardBar(selected);
        }
        screen.getHud().updateSunDisplay(world.getSunAmount());
        Chapter chapter = world.getCurrentChapter();
        int totalWaves = chapter == null ? 0 : chapter.getTotalWaves();
        screen.getHud().updateWaveDisplay(world.getCurrentWaveNumber(), totalWaves);
    }

    private static void restorePlants(GameWorld world, GraphicsEngine graphicsEngine, LevelSaveData data) {
        if (data.getPlants() == null) {
            return;
        }
        for (LevelSaveData.PlantSaveData saved : data.getPlants()) {
            PlantName name = parsePlantName(saved.getName());
            if (name == null) {
                continue;
            }
            Cell cell = world.getGrid().getCell(saved.getRow(), saved.getCol());
            if (cell == null || cell.hasObstacle()) {
                continue;
            }
            try {
                Plant plant = PlantFactory.create(
                    name,
                    new Position(cell.getWorldX(), cell.getWorldY()),
                    saved.getPlantedTick(),
                    cell
                );
                if (plant == null) {
                    continue;
                }
                plant.setBoosted(saved.isBoosted());
                if (plant.getCurrentHealth() != null) {
                    plant.getCurrentHealth().setCurrentHealth(saved.getHealth());
                }
                cell.setPlant(plant);
                world.addPlant(plant);
                graphicsEngine.registerPlant(plant);
            } catch (RuntimeException ignored) {
            }
        }
    }

    private static void restoreZombies(GameWorld world, LevelSaveData data) {
        if (data.getZombies() == null) {
            return;
        }
        ZombieService zombieService = new ZombieService(world);
        for (LevelSaveData.ZombieSaveData saved : data.getZombies()) {
            if (saved.getAlias() == null || saved.getAlias().isBlank()) {
                continue;
            }
            try {
                Zombie zombie = zombieService.spawnZombie(
                    saved.getAlias(),
                    new Position(saved.getX(), saved.getY())
                );
                if (zombie == null) {
                    continue;
                }
                zombie.setCurrentHealth(saved.getHealth());
                if (saved.getState() != null) {
                    try {
                        zombie.setState(Zombie.ZombieState.valueOf(saved.getState()));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            } catch (RuntimeException ignored) {
            }
        }
        world.flushPendingZombies();
    }

    private static void restoreObstacles(GameWorld world, LevelSaveData data) {
        if (data.getObstacles() == null) {
            RealTimeGameEngine engine = App.getRealTimeEngine();
            if (engine != null) {
                RuntimeEntityInitializer.startChapterEntities(
                    world,
                    engine.getGraphicsEngine(),
                    engine.getPhysicService()
                );
            }
            return;
        }
        ObstacleFactory factory = ObstacleFactory.getInstance();
        for (LevelSaveData.ObstacleSaveData saved : data.getObstacles()) {
            ObstacleType type = parseObstacleType(saved.getType());
            Cell cell = world.getGrid().getCell(saved.getRow(), saved.getCol());
            if (type == null || cell == null || cell.hasPlant()) {
                continue;
            }
            try {
                factory.restoreCellObstacle(type, cell, saved.getHealth());
            } catch (RuntimeException ignored) {
            }
        }
    }

    private static double obstacleHealth(Obstacle obstacle) {
        if (obstacle.getAbilities() == null) {
            return 0;
        }
        for (ObstacleAbility ability : obstacle.getAbilities()) {
            if (ability instanceof TakeDamageAbility takeDamageAbility) {
                return takeDamageAbility.getCurrentHealth();
            }
            if (ability instanceof IceBlockAbility iceBlockAbility) {
                return iceBlockAbility.getCurrentHealth();
            }
        }
        return 0;
    }

    private static ObstacleType parseObstacleType(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return ObstacleType.valueOf(raw);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static void restoreMowers(GameWorld world, LevelSaveData data) {
        if (data.getMowers() == null) {
            return;
        }
        List<LawnMower> mowers = world.getLawnMowers();
        boolean uniqueRows = hasUniqueMowerRows(data.getMowers());
        boolean[] claimed = new boolean[mowers.size()];
        for (int i = 0; i < data.getMowers().size(); i++) {
            LevelSaveData.MowerSaveData saved = data.getMowers().get(i);
            int index = uniqueRows
                ? indexOfUnclaimedRow(mowers, saved.getRow(), claimed)
                : (i < mowers.size() ? i : -1);
            if (index < 0) {
                index = firstUnclaimed(claimed);
            }
            if (index < 0 || index >= mowers.size()) {
                continue;
            }
            claimed[index] = true;
            LawnMower mower = mowers.get(index);
            if (mower == null) {
                continue;
            }
            boolean gone = saved.isUsed()
                || LawnmowerState.DEAD.name().equals(saved.getState())
                || LawnmowerState.ATTACK.name().equals(saved.getState());
            if (gone) {
                mower.retire();
            }
        }
    }

    private static boolean hasUniqueMowerRows(List<LevelSaveData.MowerSaveData> saved) {
        boolean[] seen = new boolean[32];
        for (LevelSaveData.MowerSaveData entry : saved) {
            int row = entry.getRow();
            if (row < 0 || row >= seen.length || seen[row]) {
                return false;
            }
            seen[row] = true;
        }
        return true;
    }

    private static int indexOfUnclaimedRow(List<LawnMower> mowers, int row, boolean[] claimed) {
        for (int i = 0; i < mowers.size(); i++) {
            LawnMower mower = mowers.get(i);
            if (!claimed[i] && mower != null && mower.getRow() == row) {
                return i;
            }
        }
        return -1;
    }

    private static int firstUnclaimed(boolean[] claimed) {
        for (int i = 0; i < claimed.length; i++) {
            if (!claimed[i]) {
                return i;
            }
        }
        return -1;
    }

    private static void restoreCooldowns(GameWorld world, LevelSaveData data) {
        if (data.getCooldowns() == null) {
            return;
        }
        Map<PlantName, PlantCooldown> cooldownMap = world.getCooldownPlants();
        if (cooldownMap == null) {
            return;
        }
        for (LevelSaveData.CooldownSaveData saved : data.getCooldowns()) {
            PlantName name = parsePlantName(saved.getName());
            if (name == null) {
                continue;
            }
            PlantCooldown cooldown = cooldownMap.get(name);
            if (cooldown != null) {
                cooldown.restore(saved.getLastPlantTick(), saved.isClearCooldown(), data.getCurrentTick());
            }
        }
    }

    private static PlantName parsePlantName(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return PlantName.valueOf(raw);
        } catch (IllegalArgumentException ignored) {
            return PlantName.parsePlantName(raw);
        }
    }
}
