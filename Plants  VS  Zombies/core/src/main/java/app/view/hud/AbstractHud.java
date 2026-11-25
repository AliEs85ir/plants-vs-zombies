package app.view.hud;

import app.controller.game.WorldListener;
import app.enums.plant.PlantName;
import app.view.game.pregame.PlantSelectionState;
import app.view.game.pregame.zombie.ZombieSelectionState;
import app.view.hud.config.HudLayoutConfig;
import app.view.hud.config.PlantFoodBankStyle;
import app.view.hud.elements.NormalTextPopup;
import app.view.hud.elements.PauseButton;
import app.view.hud.elements.PlantFoodBankView;
import app.view.hud.elements.ShovelButton;
import app.view.hud.notification.NotificationManager;
import app.view.hud.notification.NotificationSpec;
import app.view.ui.UiSoundBinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import game.engine.RealTimeGameEngine;
import game.settings.GameSettings;
import game.world.GameWorld;

import java.util.List;

public abstract class AbstractHud implements Hud, WorldListener{
    protected Stage stage;
    protected FitViewport hudViewport;
    protected GameWorld world;
    protected RealTimeGameEngine engine;
    protected boolean initialized = false;
    protected NotificationManager notificationManager;
    protected WorldListener forwardListener;
    protected ShovelButton shovelButton;
    protected Table shovelParent;
    protected PauseButton pauseButton;
    protected Table pauseParent;
    protected Runnable pauseHandler;
    protected PlantFoodBankView plantFoodBankView;
    protected boolean seedSelectionEnabled = true;
    protected boolean gameplayHudInteractive = true;

    protected static final float HUD_WIDTH = 1280f;
    protected static final float HUD_HEIGHT = 720f;

    @Override
    public void initialize(RealTimeGameEngine engine) {
        this.world = engine.getWorld();
        this.engine = engine;
        this.hudViewport = new FitViewport(HUD_WIDTH, HUD_HEIGHT);
        this.stage = new Stage(hudViewport);
        this.stage.getRoot().setTouchable(Touchable.childrenOnly);
        this.initialized = true;
        UiSoundBinder.bind(stage);


        buildUI();
        enableWorldClickThrough();
        buildShovelButton();
        buildPauseButton();
        initializeNotifications();
    }

    private void initializeNotifications() {
        notificationManager = new NotificationManager(stage, HUD_WIDTH, HUD_HEIGHT);
    }

    protected abstract void buildUI();

    private void enableWorldClickThrough() {
        for (Actor actor : stage.getRoot().getChildren()) {
            if (actor instanceof Table) {
                actor.setTouchable(Touchable.childrenOnly);
            }
        }
    }

    protected void buildShovelButton() {
        shovelButton = new ShovelButton();
        shovelButton.setListener(this);
        layoutShovelButton();
    }

    protected void layoutShovelButton() {
        if (shovelButton == null) {
            return;
        }
        if (shovelParent != null) {
            shovelParent.add(shovelButton)
                .size(shovelButton.getPrefWidth(), shovelButton.getPrefHeight())
                .right()
                .padRight(10f)
                .padTop(2f);
            shovelParent.invalidateHierarchy();
            return;
        }
        shovelButton.setPosition(
            HUD_WIDTH - shovelButton.getWidth() - 12f,
            HUD_HEIGHT - shovelButton.getHeight() - 6f
        );
        stage.addActor(shovelButton);
    }

    protected void buildPauseButton() {
        pauseButton = new PauseButton();
        pauseButton.setOnPause(() -> {
            if (pauseHandler != null) {
                pauseHandler.run();
            }
        });
        layoutPauseButton();
    }

    protected void layoutPauseButton() {
        if (pauseButton == null) {
            return;
        }
        Table parent = pauseParent != null ? pauseParent : shovelParent;
        if (parent != null) {
            parent.add(pauseButton)
                .size(pauseButton.getPrefWidth(), pauseButton.getPrefHeight())
                .right()
                .padRight(8f)
                .padTop(2f);
            parent.invalidateHierarchy();
            return;
        }
        float shovelWidth = shovelButton == null ? 0f : shovelButton.getWidth();
        pauseButton.setPosition(
            HUD_WIDTH - pauseButton.getWidth() - shovelWidth - 20f,
            HUD_HEIGHT - pauseButton.getHeight() - 6f
        );
        stage.addActor(pauseButton);
    }

    @Override
    public void onShovelToggled() {
        if (forwardListener != null) {
            forwardListener.onShovelToggled();
        }
    }

    @Override
    public void onPlantFoodBankClicked() {
        if (forwardListener != null) {
            forwardListener.onPlantFoodBankClicked();
        }
    }

    protected void createPlantFoodBank() {
        if (plantFoodBankView != null) {
            return;
        }
        plantFoodBankView = new PlantFoodBankView();
        plantFoodBankView.setClickListener(this::onPlantFoodBankClicked);
    }

    protected void layoutPlantFoodBankOnStage(float extraLeft) {
        createPlantFoodBank();
        PlantFoodBankStyle style = HudLayoutConfig.PLANT_FOOD_BANK;
        plantFoodBankView.pack();
        float width = style.width > 0f ? style.width : plantFoodBankView.getPrefWidth();
        float height = style.height > 0f ? style.height : plantFoodBankView.getPrefHeight();
        plantFoodBankView.setSize(width, height);
        plantFoodBankView.setPosition(
            extraLeft + style.x + style.padLeft,
            style.y + style.padBottom
        );
        stage.addActor(plantFoodBankView);
    }

    protected void refreshPlantFoodBank() {
        if (plantFoodBankView == null || world == null || world.getPlantFoodInventory() == null) {
            return;
        }
        var inventory = world.getPlantFoodInventory();
        plantFoodBankView.setAmount(inventory.getAmount(), inventory.getMaxAmount());
    }

    @Override
    public void updatePlantFood(int currentAmount, int maxAmount) {
        if (plantFoodBankView != null) {
            plantFoodBankView.setAmount(currentAmount, maxAmount);
        }
    }

    @Override
    public void setPlantFoodSelecting(boolean selecting) {
        if (plantFoodBankView != null) {
            plantFoodBankView.setSelecting(selecting);
        }
    }

    @Override
    public boolean isPlantFoodBankActor(Actor actor) {
        if (plantFoodBankView == null || actor == null) {
            return false;
        }
        Actor current = actor;
        while (current != null) {
            if (current == plantFoodBankView) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    @Override
    public void update(float deltaTime) {
        if (!initialized) return;
        stage.act(deltaTime);
        if (notificationManager != null) {
            notificationManager.update(deltaTime);
        }
        refreshPlantFoodBank();
        if (!gameplayHudInteractive) {
            return;
        }
        if (shovelButton != null && shovelButton.getParent() == stage.getRoot()) {
            shovelButton.toFront();
        }
        if (pauseButton != null && pauseButton.isVisible()) {
            pauseButton.toFront();
        }
    }

    @Override
    public void render() {
        if (!initialized) return;
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
        if (notificationManager != null) {
            notificationManager.resize(HUD_WIDTH, HUD_HEIGHT);
        }
    }

    @Override
    public boolean onTouchDown(float x, float y, int pointer, int button) {
        if (stage == null) {
            return false;
        }
        Vector2 coords = stage.screenToStageCoordinates(new Vector2(x, y));
        return stage.hit(coords.x, coords.y, true) != null;
    }

    @Override
    public boolean isShovelActor(Actor actor) {
        while (actor != null) {
            if (actor == shovelButton) {
                return true;
            }
            actor = actor.getParent();
        }
        return false;
    }

    @Override
    public boolean isPauseActor(Actor actor) {
        while (actor != null) {
            if (actor == pauseButton) {
                return true;
            }
            actor = actor.getParent();
        }
        return false;
    }

    @Override
    public void setPauseHandler(Runnable handler) {
        this.pauseHandler = handler;
        if (pauseButton != null) {
            pauseButton.setOnPause(() -> {
                if (pauseHandler != null) {
                    pauseHandler.run();
                }
            });
        }
    }

    @Override
    public void setGameplayHudInteractive(boolean interactive) {
        this.gameplayHudInteractive = interactive;
        if (pauseButton != null) {
            pauseButton.setMenuOpen(!interactive);
        }
        if (shovelButton != null) {
            shovelButton.setTouchable(interactive ? Touchable.enabled : Touchable.disabled);
        }
        applySeedSelectionEnabled(interactive && seedSelectionEnabled);
    }

    @Override
    public void requestPause() {
        if (!gameplayHudInteractive || pauseHandler == null) {
            return;
        }
        pauseHandler.run();
    }

    @Override
    public void dispose() {
        if (notificationManager != null) {
            notificationManager.clear();
        }
        if (stage != null) {
            stage.dispose();
        }
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void setForwardListener(WorldListener listener) {
        this.forwardListener = listener;
    }

    @Override
    public void setShovelActive(boolean active) {
        if (shovelButton != null) {
            shovelButton.setSelected(active);
        }
    }

    @Override
    public void setSeedSelectionEnabled(boolean enabled) {
        this.seedSelectionEnabled = enabled;
        applySeedSelectionEnabled(enabled);
    }

    protected void applySeedSelectionEnabled(boolean enabled) {
    }

    @Override
    public void showErrorPopup(String message) {
        if (message == null || message.isBlank() || stage == null) {
            return;
        }
        NormalTextPopup popup = new NormalTextPopup(message, stage.getViewport().getWorldWidth());
        stage.addActor(popup);
        popup.toFront();
    }

    @Override
    public void showNotification(NotificationSpec spec) {
        if (!initialized || notificationManager == null) {
            return;
        }
        if (!GameSettings.gameplay().notificationsEnabled()) {
            return;
        }
        notificationManager.show(spec);
    }

    @Override
    public void updateSunDisplay(int sunAmount) {

    }

    @Override
    public void deselectAllCards() {

    }

    @Override
    public void updateWaveDisplay(int currentWave, int totalWaves) {

    }

    @Override
    public void updateProgressBar(float progress) {

    }

    @Override
    public void updateConveyorBelt(float deltaTime) {

    }

    @Override
    public void bindPlantSelection(PlantSelectionState state) {

    }

    @Override
    public void finalizePlantCardBar(List<PlantName> selectedPlants) {

    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {

    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {

    }

    @Override
    public void onZombieCardDeselected(String alias) {

    }

    @Override
    public void onZombieCardSelected(String alias) {

    }

    @Override
    public void finalizeZombieCardBar(List<String> selectedZombies) {

    }

    @Override
    public void bindZombieSelection(ZombieSelectionState state) {

    }

    @Override
    public void hideWaveStartButton() {

    }

    @Override
    public void setWaveStartCallback(Runnable callback) {

    }
}
