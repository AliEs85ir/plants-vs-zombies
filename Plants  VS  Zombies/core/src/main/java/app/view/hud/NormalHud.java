package app.view.hud;

import app.enums.plant.PlantName;
import app.model.App;
import app.view.game.pregame.PlantSelectionState;
import app.view.hud.config.HudLayoutConfig;
import app.view.hud.config.PlantFoodBankStyle;
import app.view.hud.elements.PlantFoodBankView;
import app.view.hud.elements.PlayHudResourceStrip;
import app.view.hud.elements.ProgressZombieBar;
import app.view.hud.elements.plantcard.PlantCard;
import app.view.hud.elements.plantcard.PlantCardBar;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;
import game.engine.RealTimeGameEngine;
import game.entity.plantfood.PlantFoodInventory;
import game.world.chapter.Chapter;

import java.util.List;

public class NormalHud extends AbstractHud {
    protected static final float TOP_BAR_HEIGHT = 70f;

    protected Table rootTable;
    protected PlantCardBar plantCardBar;
    protected ProgressZombieBar progressBar;
    protected Label sunLabel;
    protected Label waveLabel;
    protected Table slotsTable;
    protected PlayHudResourceStrip resourceStrip;

    @Override
    public void initialize(RealTimeGameEngine engine) {
        super.initialize(engine);
    }

    @Override
    protected void buildUI() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setTouchable(Touchable.childrenOnly);
        stage.addActor(rootTable);

        buildTopBar();
        buildProgressBar();
        buildCardBar();
        buildDownBar();
    }

    protected void buildTopBar() {
        Table topBar = new Table();
        topBar.setTouchable(Touchable.childrenOnly);

        addSunCounter(topBar);
        addWaveLabel(topBar);

        rootTable.add(topBar)
            .top()
            .growX()
            .height(TOP_BAR_HEIGHT)
            .colspan(2)
            .row();
    }

    protected void addWaveLabel(Table topBar) {
        waveLabel.setTouchable(Touchable.disabled);
        topBar.add(waveLabel)
            .expandX()
            .align(Align.right)
            .padRight(8);
    }

    protected void addSunCounter(Table topBar) {
        waveLabel = new Label("Wave 0/0", AssetManager.skin);
        waveLabel.setColor(Color.WHITE);
        resourceStrip = new PlayHudResourceStrip(world, amount -> {
            if (world != null) {
                world.addSunAmount(amount);
            }
        });
        topBar.add(resourceStrip).expandX().right().padRight(8f);
        shovelParent = topBar;
    }

    protected void buildDownBar() {
        Table downBar = new Table();
        downBar.setTouchable(Touchable.childrenOnly);

        buildPlantFoodBank(downBar);

        rootTable.add(downBar)
            .bottom()
            .left()
            .colspan(2)
            .row();
    }

    protected void buildPlantFoodBank(Table downBar) {
        PlantFoodBankStyle style = HudLayoutConfig.PLANT_FOOD_BANK;
        createPlantFoodBank();
        var cell = downBar.add(plantFoodBankView)
            .left()
            .padLeft(style.x + style.padLeft)
            .padBottom(style.y + style.padBottom)
            .padRight(style.padRight);
        if (style.width > 0f) {
            cell.width(style.width);
        }
        if (style.height > 0f) {
            cell.height(style.height);
        }
    }

    protected void buildProgressBar() {
        Chapter chapter = App.getPendingGameSession().getSelectedChapter();
        int totalWaves = chapter != null ? chapter.getTotalWaves() : 1;

        progressBar = ProgressZombieBar.createRaidProgressBar();
        progressBar.setSize(ProgressZombieBar.WIDTH, ProgressZombieBar.HEIGHT);
        progressBar.clearWaveFlags();

        if (chapter != null && totalWaves > 1) {
            var config = chapter.getWaveConfig();

            long firstWaveTick = config.firstWaveTicks();
            long waveInterval = config.waveIntervalTicks();
            long finalWaveStartTick =
                firstWaveTick + (long) (totalWaves - 1) * waveInterval;

            float maxTimeProgress = (float) (totalWaves - 1) / totalWaves;

            for (int i = 0; i < totalWaves; i++) {
                long waveStartTick = firstWaveTick + (long) i * waveInterval;

                float timeProgress = finalWaveStartTick > 0
                    ? (float) waveStartTick / (float) finalWaveStartTick
                    : 0f;

                float flagPosition = timeProgress * maxTimeProgress;
                progressBar.addWaveFlag(flagPosition);
            }
        }

        Table barContainer = new Table();
        barContainer.add(progressBar)
            .size(ProgressZombieBar.WIDTH, ProgressZombieBar.HEIGHT)
            .pad(5);

        rootTable.add(barContainer)
            .colspan(2)
            .center()
            .row();
    }

    protected void buildCardBar() {
        plantCardBar =
            new PlantCardBar(1.0f, 6f, 520f);

        plantCardBar.setWorldListener(this);

        rootTable.add(plantCardBar)
            .top()
            .left()
            .padLeft(15)
            .padTop(10);

        rootTable.add().expand().row();
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {
        if (!seedSelectionEnabled) {
            return;
        }
        if (forwardListener != null) {
            forwardListener.onPlantCardSelected(plantName);
        }
    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {
        if (forwardListener != null) {
            forwardListener.onPlantCardDeselected(plantName);
        }
    }
    @Override
    public void deselectAllCards() {
        if (plantCardBar != null) {
            plantCardBar.deselectAll();
        }
    }

    @Override
    protected void applySeedSelectionEnabled(boolean enabled) {
        if (plantCardBar != null) {
            plantCardBar.setSelectionEnabled(enabled);
        }
    }
    @Override
    public void updateSunDisplay(int sunAmount) {
        if (resourceStrip != null) {
            resourceStrip.refresh();
        }
    }

    @Override
    public void updateWaveDisplay(
        int currentWave,
        int totalWaves
    ) {
        if (waveLabel != null) {
            waveLabel.setText(
                String.format(
                    "Wave %d/%d",
                    currentWave,
                    totalWaves
                )
            );
        }
    }

    @Override
    public void updateProgressBar(float progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
    }

    @Override
    public HudType getType() {
        return HudType.NORMAL;
    }

    @Override
    public void bindPlantSelection(PlantSelectionState state) {
        if (plantCardBar != null) {
            plantCardBar.bindSelectionState(state);
        }
    }

    @Override
    public void finalizePlantCardBar(List<PlantName> selectedPlants) {
        if (plantCardBar == null) return;
        plantCardBar.unbindSelectionState();
        plantCardBar.setEmptySlotCount(0);
        plantCardBar.setPlants(selectedPlants);
    }

    public PlantCardBar getPlantCardBar() {
        return plantCardBar;
    }

    public List<PlantCard> getPlantCards() {
        return plantCardBar != null
            ? plantCardBar.getPlantCards()
            : null;
    }

    @Override
    public void refreshPlantCardBoostStates() {
        if (plantCardBar != null) {
            plantCardBar.refreshBoostAppearances();
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        plantCardBar.update(deltaTime);
        updateSunDisplay(world.getSunAmount());
    }

    @Override
    public void dispose() {
        if (resourceStrip != null) {
            resourceStrip.dispose();
        }
        super.dispose();
    }
}
