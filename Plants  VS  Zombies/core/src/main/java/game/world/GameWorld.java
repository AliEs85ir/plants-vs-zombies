package game.world;

import app.controller.menu.MenuNavigator;
import app.enums.GameState;
import app.enums.Menu;
import app.enums.plant.PlantName;
import app.enums.quest.EventType;
import app.model.App;
import app.model.user.User;
import game.entity.lawnmower.LawnMower;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.plant.PlantCooldown;
import game.entity.plantfood.PlantFoodInventory;
import game.entity.plantfood.PlantFoodSettings;
import game.entity.projectile.Projectile;
import game.entity.quest.GameEvent;
import game.entity.quest.LevelFacts;
import game.entity.drop.Drop;
import game.entity.sun.Sun;
import game.entity.wave.Wave;
import game.entity.zombie.Zombie;
import game.service.quest.GameObserver;
import game.service.quest.QuestManager;
import game.world.chapter.Chapter;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.speciallevelRules.BowlingRule;
import game.world.chapter.rule.speciallevelRules.ConveyorBeltRule;
import game.world.chapter.rule.speciallevelRules.NightOpsRules;
import game.world.chapter.rule.speciallevelRules.VaseBreakerRule;
import game.service.special_levels.ConveyorBeltService;
import game.world.levelConditions.LevelEndHandler;
import game.world.levelConditions.conditions.LevelCondition;
import game.world.levelConditions.conditions.LevelOutcome;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.seasons.Season;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorld {
    private Season currentSeason;
    private Chapter currentChapter;
    private int sunAmount;
    private final PlantFoodInventory plantFoodInventory;
    private GameState gameState;
    private final List<String> events;
    private int currentWaveNumber;
    private int totalDeadZombies;
    private int totalDeadPlants;
    private int totalPlantsPlanted;
    private int totalSunsCollected;
    private Map<PlantName, PlantCooldown> selectedGamePlants;
    private final GameObserver observer;
    private final GameMap map;
    private int difficultyLevel = User.DEFAULT_DIFFICULTY_LEVEL;
    private LevelCondition levelCondition;
    private final LevelEndHandler levelEndHandler;
    private game.world.levelConditions.result.StageResult stageResult;
    private boolean resultHandled;

    public GameWorld() {
        this.sunAmount = 5000;
        User user = App.getCurrentUser();
        if (user != null && user.getFoodInventory() != null) {
            this.plantFoodInventory = user.getFoodInventory();
        } else {
            this.plantFoodInventory = new PlantFoodInventory();
        }
        this.map = new GameMap();
        this.gameState = GameState.PLAYING;
        this.events = new ArrayList<>();
        this.currentWaveNumber = 0;
        this.observer = new GameObserver(map);
        this.totalDeadZombies = 0;
        this.totalDeadPlants = 0;
        this.totalPlantsPlanted = 0;
        this.totalSunsCollected = 0;
        this.levelEndHandler = new LevelEndHandler();
        this.difficultyLevel = difficultyFromUser();
        this.resultHandled = false;
    }

    public void loadSelectedGamePlants(List<PlantName> selectedPlants) {
        this.selectedGamePlants = new HashMap<>();
        for (PlantName name : selectedPlants)
            selectedGamePlants.put(name, new PlantCooldown(name));
    }

    public PlantCooldown getCoolDownPlant(PlantName name) {
        return selectedGamePlants.getOrDefault(name, null);
    }

    public int getCurrentWaveNumber() {
        return currentWaveNumber;
    }

    public void setCurrentWaveNumber(int currentWaveNumber) {
        this.currentWaveNumber = currentWaveNumber;
    }

    public Season getSeason() {
        return currentSeason;
    }

    public int getSunAmount() {
        return sunAmount;
    }

    public PlantFoodInventory getPlantFoodInventory() {
        return plantFoodInventory;
    }

    public void addSunAmount(int amount) {
        this.sunAmount += amount;
    }

    public void recordSunCollected(int amount) {
        if (amount > 0) {
            this.totalSunsCollected += amount;
        }
    }

    public int getTotalSunsCollected() {
        return totalSunsCollected;
    }

    public void setTotalSunsCollected(int totalSunsCollected) {
        this.totalSunsCollected = Math.max(0, totalSunsCollected);
    }

    public game.world.levelConditions.result.StageResult getStageResult() {
        return stageResult;
    }

    public void setStageResult(game.world.levelConditions.result.StageResult stageResult) {
        this.stageResult = stageResult;
    }

    public boolean isResultHandled() {
        return resultHandled;
    }

    public void setResultHandled(boolean resultHandled) {
        this.resultHandled = resultHandled;
    }

    public void removeSunAmount(int amount) {
        this.sunAmount = Math.max(0, this.sunAmount - amount);

    }

    public void setSunAmount(int amount) {
        this.sunAmount = Math.max(0, amount);
    }

    public List<Sun> getSuns() {
        return map.getSuns();
    }

    public void addSun(Sun sun) {
        map.addSun(sun);
    }

    public void removeSun(Sun sun) {
        map.removeSun(sun);
    }

    public List<Drop> getDrops() {
        return map.getDrops();
    }

    public void addDrop(Drop drop) {
        map.addDrop(drop);
    }

    public void removeDrop(Drop drop) {
        map.removeDrop(drop);
    }

    public Grid getGrid() {
        return map.getGrid();
    }

    public ArrayList<LawnMower> getLawnMowers() {
        return map.getLawnMowers();
    }

    public ArrayList<Plant> getPlants() {
        return map.getPlants();
    }

    public ArrayList<Zombie> getZombies() {
        return map.getZombies();
    }

    public List<Projectile> getProjectiles() {
        return map.getProjectiles();
    }

    public void addEvent(String message) {
        events.add(message);
    }

    public List<String> getEvents() {
        return events;
    }

    public void clearEventLog() {
        events.clear();
    }

    public void addPlant(Plant plant) {
        map.addPlant(plant);
    }

    public void addZombie(Zombie zombie) {
        map.addZombie(zombie);
    }

    public void flushPendingZombies() {
        map.flushPendingZombies();
    }

    public void addProjectile(Projectile projectile) {
        map.addProjectile(projectile);
    }

    public void setCurrentSeason(Season currentSeason) {
        this.currentSeason = currentSeason;
    }

    public Chapter getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(Chapter currentChapter) {
        this.currentChapter = currentChapter;
    }

    public void setGrid(Grid grid) {
        map.setGrid(grid);
    }

    public int getDifficultyLevel() {
        User user = App.getCurrentUser();
        if (user != null) {
            return user.getDifficultyLevel();
        }
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = User.clampDifficultyLevel(difficultyLevel);
    }

    private static int difficultyFromUser() {
        User user = App.getCurrentUser();
        if (user == null) {
            return User.DEFAULT_DIFFICULTY_LEVEL;
        }
        return user.getDifficultyLevel();
    }


    public void triggerZombieVictory() {
        gameState = GameState.LOST;
        App.getCurrentUser().getQuestManager().fireEvent(new GameEvent(EventType.LEVEL_LOST, 1));
        App.getCurrentUser().getQuestManager().saveQuests();
        addEvent("The zombie ate your brain; LOSER!!!");
        map.getZombies().clear();
        map.getPlants().clear();
        map.getProjectiles().clear();
        map.getDrops().clear();
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            App.getRealTimeEngine().getGraphicsEngine().clearDrops();
        }
        MenuNavigator.goTo(Menu.GameMenu);
    }

    public void triggerPlantVictory() {
        gameState = GameState.WON;
        onLevelWin();
        addEvent("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
        app.service.progression.ProgressionService.onStageWon(App.getCurrentUser(), currentChapter);

        MenuNavigator.goTo(Menu.GameMenu);
    }

    public List<Zombie> getZombiesInRow(int row) {
        return map.getZombiesInRow(row);
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public List<PlantName> getSelectedGamePlants() {
        return List.of(selectedGamePlants.keySet().toArray(new PlantName[0]));
    }

    public Map<PlantName, PlantCooldown> getCooldownPlants() {
        return selectedGamePlants;
    }

    public ArrayList<PlantCooldown> getPlantCooldowns() {
        return new ArrayList<>(selectedGamePlants.values());
    }

    public GameObserver getObserver() {
        return observer;
    }

    public GameMap getMap() {
        return map;
    }

    private void onLevelWin() {
        QuestManager questManager = App.getCurrentUser().getQuestManager();
        questManager.fireEvent(new GameEvent(
            EventType.LEVEL_WON,
            1,
            null, null, -1, -1,
            getDifficultyLevel(),
            false
        ));
        questManager.evaluateLevelEnd(LevelFacts.from(this));
        questManager.saveQuests();
    }


    public boolean isNight() {
        for(ChapterRule rule : currentChapter.getRules()) {
            if(rule instanceof NightOpsRules) {
                return true;
            }
        }
        return false;
    }

    public boolean isConveyorActive() {
        return getConveyorBeltRule() != null;
    }

    public ConveyorBeltService getConveyorBeltService() {
        ConveyorBeltRule rule = getConveyorBeltRule();
        return rule != null ? rule.getService() : null;
    }

    public ConveyorBeltRule getConveyorBeltRule() {
        if (currentChapter == null || currentChapter.getRules() == null) {
            return null;
        }
        for (ChapterRule rule : currentChapter.getRules()) {
            if (rule instanceof ConveyorBeltRule conveyorRule) {
                return conveyorRule;
            }
        }
        return null;
    }

    public boolean isVaseBreakerActive() {
        return getVaseBreakerRule() != null;
    }

    public VaseBreakerRule getVaseBreakerRule() {
        if (currentChapter == null || currentChapter.getRules() == null) {
            return null;
        }
        for (ChapterRule rule : currentChapter.getRules()) {
            if (rule instanceof VaseBreakerRule vaseBreakerRule) {
                return vaseBreakerRule;
            }
        }
        return null;
    }

    public boolean isFinalWave() {
        return map.getCurrentWave().isFinalWave();
    }

    public Wave getCurrentWave() {
        return map.getCurrentWave();
    }

    public List<Obstacle> getObstacles() {
        return map.getObstacles();
    }

    public void increaseTotalDeadZombies() {
        this.totalDeadZombies++;
    }

    public int getTotalDeadZombies() {
        return totalDeadZombies;
    }

    public void increaseTotalDeadPlants() {
        this.totalDeadPlants++;
    }

    public void notifyPlantEaten(Plant plant, Cell cell) {
    }

    public int getTotalDeadPlants() {
        return totalDeadPlants;
    }

    public void increaseTotalPlantsPlanted() {
        this.totalPlantsPlanted++;
    }

    public int getTotalPlantsPlanted() {
        return totalPlantsPlanted;
    }

    public void setTotalDeadZombies(int totalDeadZombies) {
        this.totalDeadZombies = Math.max(0, totalDeadZombies);
    }

    public void setTotalDeadPlants(int totalDeadPlants) {
        this.totalDeadPlants = Math.max(0, totalDeadPlants);
    }

    public void setTotalPlantsPlanted(int totalPlantsPlanted) {
        this.totalPlantsPlanted = Math.max(0, totalPlantsPlanted);
    }

    public int getAlivePlantCount() {
        int count = 0;
        for (Plant plant : getPlants()) {
            if (plant != null && plant.isAlive()) {
                count++;
            }
        }
        return count;
    }

    public int getUsedLawnMowerCount() {
        int used = 0;
        for (LawnMower mower : getLawnMowers()) {
            if (mower != null && (mower.isUsed() || mower.getState() == game.entity.lawnmower.LawnmowerState.DEAD)) {
                used++;
            }
        }
        return used;
    }

    public int getTotalLawnMowerCount() {
        return getLawnMowers() == null ? 0 : getLawnMowers().size();
    }

    public void setLevelCondition(LevelCondition levelCondition) {
        this.levelCondition = levelCondition;
    }

    public LevelCondition getLevelCondition() {
        return levelCondition;
    }

    public BowlingRule getBowlingRule() {
        for(ChapterRule rule : currentChapter.getRules()) {
            if(rule instanceof BowlingRule) {
                return (BowlingRule) rule;
            }
        }
        return null;
    }

    public LevelOutcome evaluateOutcome() {
        if (gameState != GameState.PLAYING || levelCondition == null || resultHandled) {
            return LevelOutcome.NONE;
        }
        LevelOutcome outcome = levelCondition.evaluate(this);
        if (outcome != null && outcome != LevelOutcome.NONE) {
            endLevel(outcome);
        }
        return outcome == null ? LevelOutcome.NONE : outcome;
    }

    private void endLevel(LevelOutcome outcome) {
        if (resultHandled) {
            return;
        }
        resultHandled = true;
        if (outcome == LevelOutcome.NEXT_LEVEL) {
            levelEndHandler.handle(this, outcome);
            return;
        }
        gameState = outcome == LevelOutcome.PLAYER_WON ? GameState.WON : GameState.LOST;
        levelEndHandler.handle(this, outcome);
    }

    public game.world.levelConditions.result.StageResult convertLoseToWin() {
        return levelEndHandler.applyBuyWin(this);
    }

}
