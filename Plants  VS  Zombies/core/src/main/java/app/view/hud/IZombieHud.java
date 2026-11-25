package app.view.hud;

import app.controller.game.izombie.IZombieMatchContext;
import app.controller.game.izombie.IZombieNetworkController;
import app.enums.plant.PlantName;
import app.model.App;
import app.view.game.pregame.PlantSelectionState;
import app.view.game.pregame.zombie.ZombieSelectionState;
import app.view.hud.config.HudLayoutConfig;
import app.view.hud.config.PlantFoodBankStyle;
import app.view.hud.elements.IZombieMessageButton;
import app.view.hud.elements.ProgressZombieBar;
import app.view.hud.elements.plantcard.PlantCard;
import app.view.hud.elements.plantcard.PlantCardBar;
import app.view.hud.elements.zombiecard.ZombieCard;
import app.view.hud.elements.zombiecard.ZombieCardBar;
import app.view.izombie.IZombieMessagePanel;
import app.view.izombie.IZombieReaction;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;
import game.engine.RealTimeGameEngine;
import game.world.chapter.Chapter;
import game.world.minigame.izombie.IZombieRules;
import game.world.minigame.izombie.IZombieSide;
import game.world.minigame.izombie.IZombieWorld;

import java.util.List;

public class IZombieHud extends AbstractHud {
    protected static final float TOP_BAR_HEIGHT = 70f;
    private static final float MATCH_DURATION_SECONDS = 240f;

    private IZombieSide side = IZombieSide.ZOMBIES;
    protected Table rootTable;
    protected PlantCardBar plantCardBar;
    protected ZombieCardBar zombieCardBar;
    protected ProgressZombieBar progressBar;
    protected Label sunLabel;
    protected Label sideLabel;
    protected Label timedLabel;
    private IZombieMessageButton messageButton;
    private IZombieMessagePanel messagePanel;

    private float remainingTime = MATCH_DURATION_SECONDS;

    @Override
    public void initialize(RealTimeGameEngine engine) {
        this.side = IZombieRules.localSide();
        super.initialize(engine);
        if (side.isZombies() && shovelButton != null) {
            shovelButton.setVisible(false);
            shovelButton.setTouchable(Touchable.disabled);
        }
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
        addMessageButton(topBar);
        addTimer(topBar);
        addSideLabel(topBar);

        rootTable.add(topBar)
            .top()
            .growX()
            .height(TOP_BAR_HEIGHT)
            .colspan(2)
            .row();
    }

    protected void addSideLabel(Table topBar) {
        String text = side.isPlants() ? "Plants" : "Zombies";
        IZombieMatchContext match = IZombieMatchContext.get();
        if (match != null && match.opponentUsername() != null && !match.opponentUsername().isBlank()) {
            text = text + "  vs  " + match.opponentUsername();
        }
        sideLabel = new Label(text, AssetManager.skin);
        sideLabel.setColor(Color.WHITE);
        sideLabel.setTouchable(Touchable.disabled);
        topBar.add(sideLabel)
            .expandX()
            .align(Align.right)
            .padRight(8);
    }
    protected void addTimer(Table topBar) {
        timedLabel = new Label("Time", AssetManager.skin, "promo_ribbon");
        timedLabel.setSize(80, 50);
        topBar.add(timedLabel);
    }

    protected void addSunCounter(Table topBar) {
        TextureRegion bgRegion = AssetManager.textureBank.region("IMAGE_UI_STORE_SPROUTSTAB_DOWN");
        TextureRegion sunRegion = AssetManager.textureBank.region("IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN");

        Table sunCounterTable = new Table();
        sunCounterTable.setSize(bgRegion.getRegionWidth(), bgRegion.getRegionHeight() - 10);
        sunCounterTable.setBackground(new TextureRegionDrawable(bgRegion));
        Image sunIcon = new Image(sunRegion);
        sunLabel = new Label("0", AssetManager.skin);
        sunLabel.setColor(Color.WHITE);

        sunCounterTable.add(sunIcon).padLeft(-15).padTop(-10);
        sunCounterTable.add(sunLabel).expandX().center().padRight(10);

        topBar.add(sunCounterTable).left().padLeft(30);
        shovelParent = topBar;
    }

    protected void addMessageButton(Table topBar) {
        if (!IZombieMatchContext.isActive()) {
            return;
        }
        messageButton = new IZombieMessageButton(this::toggleMessagePanel);
        topBar.add(messageButton)
            .size(messageButton.getPrefWidth(), messageButton.getPrefHeight())
            .padLeft(10f)
            .padTop(2f);
    }

    protected void toggleMessagePanel() {
        if (messagePanel != null && messagePanel.getStage() != null) {
            messagePanel.close();
            messagePanel = null;
            return;
        }
        messagePanel = new IZombieMessagePanel(this::sendReaction);
        stage.addActor(messagePanel);
        messagePanel.toFront();
    }

    private void sendReaction(IZombieReaction reaction) {
        if (reaction == null) {
            return;
        }
        IZombieNetworkController.publishReaction(reaction);
        messagePanel = null;
    }

    protected void buildDownBar() {
        if (!side.isPlants()) {
            return;
        }
        Table downBar = new Table();
        downBar.setTouchable(Touchable.childrenOnly);

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

        rootTable.add(downBar)
            .bottom()
            .left()
            .colspan(2)
            .row();
    }

    protected void buildProgressBar() {
        Chapter chapter =
            App.getPendingGameSession().getSelectedChapter();

        int totalWaves =
            chapter != null ? chapter.getTotalWaves() : 1;

        progressBar =
            ProgressZombieBar.createRaidProgressBar();

        progressBar.setSize(
            ProgressZombieBar.WIDTH,
            ProgressZombieBar.HEIGHT
        );

        if (totalWaves > 1) {
            for (int i = 1; i < totalWaves; i++) {
                progressBar.addWaveFlag((float) i / totalWaves);
            }
        }

        Table barContainer = new Table();

        barContainer.add(progressBar)
            .size(
                ProgressZombieBar.WIDTH,
                ProgressZombieBar.HEIGHT
            )
            .pad(5);

        rootTable.add(barContainer)
            .colspan(2)
            .center()
            .row();
    }

    protected void buildCardBar() {
        if (side.isPlants()) {
            plantCardBar = new PlantCardBar(1.0f, 6f, 520f);
            plantCardBar.setWorldListener(this);
            rootTable.add(plantCardBar)
                .top()
                .left()
                .padLeft(15)
                .padTop(10);
        } else {
            zombieCardBar = new ZombieCardBar(1.0f, 6f, 520f);
            zombieCardBar.setWorldListener(this);
            rootTable.add(zombieCardBar)
                .top()
                .left()
                .padLeft(15)
                .padTop(10);
        }
        rootTable.add().expand().row();
    }

    @Override
    public void onZombieCardSelected(String alias) {
        if (!seedSelectionEnabled || !side.isZombies()) {
            return;
        }
        if (forwardListener != null) {
            forwardListener.onZombieCardSelected(alias);
        }
    }

    @Override
    public void onZombieCardDeselected(String alias) {
        if (forwardListener != null) {
            forwardListener.onZombieCardDeselected(alias);
        }
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {
        if (!seedSelectionEnabled || !side.isPlants()) {
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
        if (zombieCardBar != null) {
            zombieCardBar.deselectAll();
        }
    }

    @Override
    protected void applySeedSelectionEnabled(boolean enabled) {
        if (plantCardBar != null) {
            plantCardBar.setSelectionEnabled(enabled);
        }
        if (zombieCardBar != null) {
            zombieCardBar.setSelectionEnabled(enabled);
        }
    }

    @Override
    public void updateSunDisplay(int sunAmount) {
        if (sunLabel != null) {
            sunLabel.setText("☀ " + sunAmount);
        }
    }

    @Override
    public void updateWaveDisplay(
        int currentWave,
        int totalWaves
    ) {
    }

    @Override
    public void updateProgressBar(float progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
    }

    @Override
    public HudType getType() {
        return HudType.I_ZOMBIE;
    }

    @Override
    public void bindPlantSelection(PlantSelectionState state) {
        if (plantCardBar != null) {
            plantCardBar.bindSelectionState(state);
        }
    }

    @Override
    public void bindZombieSelection(ZombieSelectionState state) {
        if (zombieCardBar != null) {
            zombieCardBar.bindSelectionState(state);
        }
    }

    @Override
    public void finalizePlantCardBar(List<PlantName> selectedPlants) {
        if (plantCardBar == null) {
            return;
        }
        plantCardBar.unbindSelectionState();
        plantCardBar.setEmptySlotCount(0);
        plantCardBar.setPlants(selectedPlants);
    }

    @Override
    public void finalizeZombieCardBar(List<String> selectedZombies) {
        if (zombieCardBar == null) {
            return;
        }
        zombieCardBar.unbindSelectionState();
        zombieCardBar.setEmptySlotCount(0);
        zombieCardBar.setZombies(selectedZombies);
    }

    @Override
    public void refreshPlantCardBoostStates() {
        if (plantCardBar != null) {
            plantCardBar.refreshBoostAppearances();
        }
    }

    public PlantCardBar getPlantCardBar() {
        return plantCardBar;
    }

    public ZombieCardBar getZombieCardBar() {
        return zombieCardBar;
    }

    public List<PlantCard> getPlantCards() {
        return plantCardBar != null ? plantCardBar.getPlantCards() : null;
    }

    public List<ZombieCard> getZombieCards() {
        return zombieCardBar != null ? zombieCardBar.getZombieCards() : null;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (plantCardBar != null) {
            plantCardBar.update(deltaTime);
        }
        if (zombieCardBar != null) {
            zombieCardBar.update(deltaTime);
        }
        if (world != null) {
            updateSunDisplay(world.getSunAmount());
        }
        if (remainingTime > 0f) {
            remainingTime = Math.max(0f, remainingTime - deltaTime);
            if (timedLabel != null) {
                timedLabel.setText(formatTime(remainingTime));
            }
        }
        else {
            ((IZombieWorld) world).setTimeUp(true);
        }
    }

    private String formatTime(float totalSeconds) {
        int seconds = (int) Math.max(0, Math.ceil(totalSeconds));
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }
}
