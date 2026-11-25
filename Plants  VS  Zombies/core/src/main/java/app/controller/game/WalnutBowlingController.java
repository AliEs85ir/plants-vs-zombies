package app.controller.game;

import app.controller.game.interaction.InteractionType;
import app.controller.game.interaction.hover.BowlingHoverHandler;
import app.enums.plant.PlantName;
import app.model.App;
import app.view.game.PlayScreen;
import app.view.hud.Hud;
import app.view.hud.notification.GameplayNotifications;
import game.engine.time.GameClock;
import game.world.GameWorld;
import game.world.minigame.wallnut_bowling.placement.BowlingPlacementService;

public class WalnutBowlingController extends GameController{
    private BowlingPlacementService placementService;
    private BowlingHoverHandler bowlingHoverHandler;
    private int lastNotifiedWaveNumber;



    public WalnutBowlingController(PlayScreen screen) {
        super(screen);
        bindPlacement();
        hud.setForwardListener(this);
    }

    public WalnutBowlingController() {
        super();
    }

    @Override
    public void initialize(PlayScreen screen) {
        super.initialize(screen);
        bindPlacement();
        hud.setForwardListener(this);
    }

    private void bindPlacement() {
        this.placementService = new BowlingPlacementService(screen);
        this.bowlingHoverHandler = new BowlingHoverHandler(placementService);
    }

    @Override
    protected boolean handleGameplayClick(float worldX, float worldY) {
        return tryCollectSun(worldX, worldY);
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {
        if (plantName == null || interaction.blocksSeedSelection()) {
            return;
        }

        bowlingHoverHandler.setPlantName(plantName);
        interaction.beginBowling(plantName, bowlingHoverHandler);
    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {
        if(interaction.selectedPlant() == plantName) {
            cancelPlanting();
        }
    }

    public void cancelPlanting() {
        if (interaction.is(InteractionType.SHOVEL)) {
            return;
        }
        interaction.clear();
        hoveredCell = null;
        hud.deselectAllCards();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        ChapterData.updateWaveDisplay(
            hud,
            world
        );
        notifyWaveNotifications();
        updateProgressBar();
    }

    private void updateProgressBar() {
        int currentWave = world.getCurrentWaveNumber();
        var chapter = world.getCurrentChapter();
        int totalWaves = chapter.getTotalWaves();
        var config = chapter.getWaveConfig();

        float totalProgress = 0f;

        if (currentWave < totalWaves) {
            long finalWaveStartTick = config.firstWaveTicks() + (long)(totalWaves - 1) * config.waveIntervalTicks();
            float timeProgress = (float) GameClock.currentTick / finalWaveStartTick;
            timeProgress = Math.max(0f, Math.min(1f, timeProgress));
            float maxTimeProgress = (float) (totalWaves - 1) / totalWaves;
            totalProgress = timeProgress * maxTimeProgress;
        } else {
            float baseProgress = (float) (totalWaves - 1) / totalWaves;
            float healthProgress = getFinalWaveHealthProgress();
            totalProgress = baseProgress + (healthProgress / totalWaves);
        }

        hud.updateProgressBar(Math.min(totalProgress, 1f));
    }

    private void notifyWaveNotifications() {
        var chapter = world.getCurrentChapter();
        if (chapter == null) {
            return;
        }
        int currentWave = world.getCurrentWaveNumber();
        if (currentWave <= lastNotifiedWaveNumber) {
            return;
        }
        lastNotifiedWaveNumber = currentWave;
        if (currentWave >= chapter.getTotalWaves()) {
            hud.showNotification(GameplayNotifications.finalWave());
        } else {
            hud.showNotification(GameplayNotifications.waveStarted(currentWave));
        }
    }

    private float getFinalWaveHealthProgress() {
        var currentWave = world.getMap().getCurrentWave();
        if (currentWave == null || currentWave.getZombies().isEmpty()) {
            return 1f;
        }

        float totalMaxHealth = 0;
        float currentTotalHealth = 0;

        for (var zombie : currentWave.getZombies()) {
            totalMaxHealth += zombie.getPropertyData().getInitialHealth();

            if (!zombie.isDead()) {
                currentTotalHealth += Math.max(0, zombie.getCurrentHealth());
            }
        }

        if (totalMaxHealth == 0) return 0f;
        return 1f - (currentTotalHealth / totalMaxHealth);
    }

    public PlantName getSelectedPlantName() {
        return interaction.selectedPlant();
    }


    private static class ChapterData {
        private static void updateWaveDisplay(
            Hud hud,
            GameWorld world
        ) {
            if (App.getPendingGameSession().getSelectedChapter() == null) {
                return;
            }

            hud.updateWaveDisplay(
                world.getCurrentWaveNumber(),
                App.getPendingGameSession()
                    .getSelectedChapter()
                    .getTotalWaves()
            );
        }
    }
}
