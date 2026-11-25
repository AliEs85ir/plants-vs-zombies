package app.view.hud;

import app.controller.game.WorldListener;
import app.enums.plant.PlantName;
import app.view.game.pregame.PlantSelectionState;
import app.view.game.pregame.zombie.ZombieSelectionState;
import app.view.hud.notification.NotificationSpec;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import game.engine.RealTimeGameEngine;

import java.util.List;

public interface Hud {
    /**
     * Called once when the HUD is first created.
     * Use this to build the Stage actors and UI elements.
     */
    void initialize(RealTimeGameEngine engine);

    /**
     * Called every frame for logic updates.
     */
    void update(float deltaTime);

    /**
     * Called every frame for rendering.
     * The Stage handles actor rendering, but custom drawing can go here.
     */
    void render();

    /**
     * Called when the screen is resized.
     */
    void resize(int width, int height);

    /**
     * Check if the HUD has intercepted a click (e.g., on a UI button).
     * Returns true if the click was handled and should not be passed to the game.
     */
    boolean onTouchDown(float x, float y, int pointer, int button);

    /**
     * Called when the HUD should be disposed.
     */
    void dispose();

    /**
     * Get the type of this HUD.
     */
    HudType getType();

    Stage getStage();

    public void setForwardListener(WorldListener listener);

    void showErrorPopup(String message);
    default void showPlantPlacementMessage(String message) {
        showErrorPopup(message);
    }
    default void refreshPlantCardBoostStates() {
    }
    default void setPlantFoodSelecting(boolean selecting) {
    }
    default boolean isPlantFoodBankActor(Actor actor) {
        return false;
    }
    void showNotification(NotificationSpec spec);
    void updateSunDisplay(int sunAmount);
    void deselectAllCards();
    void updateWaveDisplay(int currentWave, int totalWaves);
    void updateProgressBar(float progress);
    default void updateMioPointDisplay(int points) {
    }
    void updateConveyorBelt(float deltaTime);
    void updatePlantFood(int currentAmount, int maxAmount);
    void bindPlantSelection(PlantSelectionState state);
    void bindZombieSelection(ZombieSelectionState state);
    void finalizePlantCardBar(List<PlantName> selectedPlants);
    void finalizeZombieCardBar(List<String> selectedZombies);
    void setShovelActive(boolean active);
    void setSeedSelectionEnabled(boolean enabled);
    default boolean isShovelActor(Actor actor) {
        return false;
    }
    default void setPauseHandler(Runnable handler) {
    }
    default boolean isPauseActor(Actor actor) {
        return false;
    }
    default void setGameplayHudInteractive(boolean interactive) {
    }
    default void requestPause() {
    }
    void setWaveStartCallback(Runnable callback);
    void hideWaveStartButton();
    /**
     * World-space plant cards (Vasebreaker). Returns true if a card consumed the click.
     */
    default boolean trySelectWorldPlantCard(float worldX, float worldY) {
        return false;
    }
}
