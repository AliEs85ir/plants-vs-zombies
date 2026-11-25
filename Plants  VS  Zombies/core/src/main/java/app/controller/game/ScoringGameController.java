package app.controller.game;

import app.controller.game.interaction.InteractionType;
import app.controller.game.interaction.hover.PlantingHoverHandler;
import app.enums.plant.PlantName;
import app.model.App;
import app.view.game.PlayScreen;
import app.view.hud.Hud;
import app.view.hud.notification.GameplayNotifications;
import app.view.hud.notification.NotificationSpec;
import game.service.plant.PlantPlacementService;
import game.world.GameWorld;
import game.world.chapter.rule.scoringgame.ScoringSession;

public class ScoringGameController extends GameController{
    private PlantPlacementService placementService;
    private PlantingHoverHandler plantingHoverHandler;
    private int lastNotifiedWaveNumber;
    private int scoringSessionId;


    public ScoringGameController(PlayScreen screen) {
        super(screen);
        bindPlacement();
        hud.setForwardListener(this);
    }

    public ScoringGameController() {super();}

    @Override
    public void initialize(PlayScreen screen) {
        super.initialize(screen);
        bindPlacement();
        hud.setForwardListener(this);
        lastNotifiedWaveNumber = 0;
        scoringSessionId = ScoringSession.begin();
        notifyMioScore(0);
    }

    public int scoringSessionId() {
        return scoringSessionId;
    }

    private void bindPlacement() {
        this.placementService = new PlantPlacementService(screen);
        this.plantingHoverHandler = new PlantingHoverHandler(placementService);
    }

    @Override
    protected boolean handleGameplayClick(float worldX, float worldY) {
        return tryCollectSun(worldX, worldY);
    }

    @Override
    public void onPlantCardSelected(
        PlantName plantName
    ) {
        if (plantName == null || interaction.blocksSeedSelection()) {
            return;
        }

        plantingHoverHandler.setPlantName(plantName);
        interaction.beginPlanting(plantName, plantingHoverHandler);
    }

    @Override
    public void onPlantCardDeselected(
        PlantName plantName
    ) {
        if (interaction.selectedPlant() == plantName) {
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

    public void notifyScoring(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        notifyScoring(GameplayNotifications.scoringBonus(text));
    }

    public void notifyScoring(NotificationSpec spec) {
        if (spec == null) {
            return;
        }
        if (hud != null) {
            hud.showNotification(spec);
            return;
        }
        GameplayNotifications.show(spec);
    }

    public void notifyMioScore(int points) {
        if (hud == null) {
            return;
        }
        hud.updateMioPointDisplay(points);
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
