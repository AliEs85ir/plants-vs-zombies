package game.world.chapter;

import app.controller.game.GameController;
import app.controller.game.PlayScreenController;
import app.enums.lawnmower.LawnMowerType;
import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import app.model.user.User;
import app.view.game.states.CameraIntroState;
import app.view.game.states.PlayScreenState;
import app.view.hud.Hud;
import app.view.hud.NormalHud;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import game.entity.zombie.data.properties.Property;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.wave.WaveConfig;
import game.world.chapter.wave.WaveFormula;
import game.world.levelConditions.conditions.LevelCondition;
import game.world.levelConditions.conditions.NormalCondition;
import game.world.minigame.Minigame;
import game.world.playground.GridConfig;
import game.world.seasons.Season;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chapter {
    private final int chapterNumber;
    private final ChapterType chapterType;
    private final Season season;
    private final int totalWaves;
    private final GridConfig gridConfig;
    private final List<PlantName> availablePlants;
    private final List<Property> availableZombies;
    private final WaveFormula waveFormula;
    private final WaveConfig waveConfig;
    private final List<ChapterRule> rules;
    private final Minigame minigameLogic;
    private final boolean needsPlantSelection;
    private final int selectionCount;
    private final String description;
    private final List<PlantName> recommendedPlants;
    private List<PlantName> unlockPlants = Collections.emptyList();
    private String difficulty = "";
    private String mapPath;
    private TiledMap map;
    private LawnMowerType lawnMowerType;
    private List<ObstacleType> obstacleTypes;
    private Hud hud;
    private transient PlayScreenState firstState;
    private transient GameController screenController;
    private final LevelCondition levelCondition;

    public Chapter(int chapterNumber, ChapterType chapterType, Season season,
                   List<ObstacleType> obstacleTypes, int totalWaves,
                   GridConfig gridConfig, List<PlantName> availablePlants,
                   List<Property> availableZombies, LawnMowerType lawnMowerType,
                   WaveFormula waveFormula, WaveConfig waveConfig,
                   List<ChapterRule> rules, Minigame minigameLogic,
                   boolean needsPlantSelection, String mapPath,
                   String description, int selectionCount, List<PlantName> recommendedPlants, Hud hud,
                    PlayScreenState firstState, GameController controller, LevelCondition levelCondition) {
        this.chapterNumber = chapterNumber;
        this.chapterType = chapterType;
        this.season = season;
        this.totalWaves = totalWaves;
        this.gridConfig = gridConfig;
        this.availablePlants = availablePlants;
        this.availableZombies = availableZombies;
        this.waveFormula = waveFormula;
        this.waveConfig = waveConfig;
        this.rules = rules;
        this.minigameLogic = minigameLogic;
        this.needsPlantSelection = needsPlantSelection;
        this.selectionCount = selectionCount;
        this.description = (description != null) ? description : "";
        this.recommendedPlants = (recommendedPlants != null)
            ? Collections.unmodifiableList(new ArrayList<>(recommendedPlants))
            : Collections.emptyList();
        this.mapPath = mapPath;
        TmxMapLoader loader = new TmxMapLoader();
        this.map = loader.load(mapPath);
        this.lawnMowerType = lawnMowerType;
        this.obstacleTypes = obstacleTypes;
        this.hud = hud;
        this.firstState = firstState;
        this.screenController = controller;
        this.levelCondition = levelCondition;
    }

    public Chapter(int chapterNumber, ChapterType chapterType, Season season,
                   List<ObstacleType> obstacleTypes, int totalWaves,
                   GridConfig gridConfig, List<PlantName> availablePlants,
                   List<Property> availableZombies, LawnMowerType lawnMowerType,
                   WaveFormula waveFormula, WaveConfig waveConfig,
                   List<ChapterRule> rules, Minigame minigameLogic,
                   boolean needsPlantSelection, String mapPath) {
        this(chapterNumber, chapterType, season, obstacleTypes, totalWaves,
             gridConfig, availablePlants, availableZombies, lawnMowerType,
             waveFormula, waveConfig, rules, minigameLogic, needsPlantSelection, mapPath,
             "", 8, Collections.emptyList(), new NormalHud(),
            new CameraIntroState(), new PlayScreenController(),new NormalCondition());
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public ChapterType getChapterType() {
        return chapterType;
    }

    public Season getSeason() {
        return season;
    }

    public int getTotalWaves() {
        return totalWaves;
    }

    public GridConfig getGridConfig() {
        return gridConfig;
    }

    public List<PlantName> getAvailablePlants() {
        return availablePlants;
    }

    public List<Property> getAvailableZombies() {
        return availableZombies;
    }

    public WaveFormula getWaveFormula() {
        return waveFormula;
    }

    public List<ChapterRule> getRules() {
        return rules;
    }

    public boolean isLocked(User user) {
        if (user == null || season == null || season.getSeasonType() == null) {
            return false;
        }
        if (chapterType == ChapterType.MINIGAME
            || season.getSeasonType() == app.enums.SeasonType.MINIGAME) {
            return false;
        }
        return !user.getGameProgress().isStageUnlocked(season.getSeasonType(), chapterNumber);
    }

    public List<PlantName> getUnlockPlants() {
        return unlockPlants;
    }

    public void setUnlockPlants(List<PlantName> unlockPlants) {
        this.unlockPlants = unlockPlants != null
            ? Collections.unmodifiableList(new ArrayList<>(unlockPlants))
            : Collections.emptyList();
    }

    public List<PlantName> getSuggestedPlants(User user) {
        return app.service.progression.ProgressionService.suggestedPlants(this, user);
    }

    public List<PlantName> getSelectablePlants(User user) {
        List<PlantName> result = new ArrayList<>();
        for (PlantName plant : availablePlants) {
            if (user.isUnlocked(plant)) {
                result.add(plant);
            }
        }
        return result;
    }

    public List<PlantName> getAllUnlockedPlants(User user) {
        return user.getUnlockedPlants();
    }

    public Minigame getMinigameLogic() {
        return minigameLogic;
    }

    public boolean isMinigame() {
        return chapterType == ChapterType.MINIGAME;
    }

    public boolean isNeedsPlantSelection() {
        return needsPlantSelection;
    }

    public boolean allowsManualPlantSelection() {
        if (!needsPlantSelection) {
            return false;
        }
        if (rules == null) {
            return true;
        }
        for (ChapterRule rule : rules) {
            if (!rule.allowsManualPlantSelection()) {
                return false;
            }
        }
        return true;
    }

    public int getSelectionCount() {
        return selectionCount;
    }

    @Deprecated
    public int getMaxPlantsAvailable() {
        return selectionCount;
    }

    public String getDescription() {
        return description;
    }

    public List<PlantName> getRecommendedPlants() {
        return recommendedPlants;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = (difficulty != null) ? difficulty : "";
    }

    public TiledMap getMap() {
        return map;
    }

    public void setMapPath(TiledMap map) {
        this.map = map;
    }

    public WaveConfig getWaveConfig() {
        return waveConfig;
    }

    public LawnMowerType getLawnMowerType() {
        return lawnMowerType;
    }

    public List<ObstacleType> getObstacleTypes() {
        return obstacleTypes;
    }

    public Hud getHud() {
        return hud;
    }

    public PlayScreenState getFirstState() {
        return firstState;
    }

    public GameController getScreenController() {
        return screenController;
    }

    public LevelCondition getLevelCondition() {
        return levelCondition;
    }

    public String getSaveChapterName() {
        if (isMinigame() && minigameLogic != null) {
            return minigameLogic.getClass().getSimpleName();
        }
        if (season == null || season.getSeasonType() == null) {
            return String.valueOf(chapterNumber);
        }
        try {
            return new ChapterId(season.getSeasonType(), chapterNumber).toName();
        } catch (RuntimeException ignored) {
            return season.getSeasonType().name() + "." + chapterNumber;
        }
    }
}
