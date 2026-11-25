package app.view.hud;

import app.enums.plant.PlantName;
import app.view.game.pregame.PlantSelectionState;
import app.view.game.pregame.zombie.ZombieSelectionState;
import app.view.hud.config.HudLayoutConfig;
import app.view.hud.config.PlantFoodBankStyle;
import app.view.hud.elements.plantcard.PlantCard;
import app.view.hud.elements.plantcard.PlantCardBar;
import app.view.hud.elements.zombiecard.ZombieCard;
import app.view.hud.elements.zombiecard.ZombieCardBar;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;
import game.world.minigame.izombie_couchplay.IZombieCouchPlayWorld;

import java.util.List;

public class IZombieCouchPlayHud extends AbstractHud {
    private static final float TOP_BAR_HEIGHT = 70f;

    private Table rootTable;
    private PlantCardBar plantCardBar;
    private ZombieCardBar zombieCardBar;
    private Label plantSunLabel;
    private Label zombieSunLabel;
    private Label hintLabel;

    @Override
    protected void buildUI() {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setTouchable(Touchable.childrenOnly);
        stage.addActor(rootTable);

        buildTopBar();
        buildCardBars();
        buildDownBar();
        buildHint();
    }

    private void buildTopBar() {
        Table topBar = new Table();
        topBar.setTouchable(Touchable.childrenOnly);

        addSunCounter(topBar, true);
        Label plantsTitle = new Label("Plants", AssetManager.skin);
        plantsTitle.setColor(Color.WHITE);
        plantsTitle.setTouchable(Touchable.disabled);
        topBar.add(plantsTitle).padLeft(8f);

        topBar.add().expandX();

        Label zombiesTitle = new Label("Zombies", AssetManager.skin);
        zombiesTitle.setColor(Color.WHITE);
        zombiesTitle.setTouchable(Touchable.disabled);
        topBar.add(zombiesTitle).padRight(8f);
        addSunCounter(topBar, false);

        shovelParent = topBar;
        rootTable.add(topBar)
            .top()
            .growX()
            .height(TOP_BAR_HEIGHT)
            .row();
    }

    private void addSunCounter(Table topBar, boolean plantSide) {
        TextureRegion bgRegion = AssetManager.textureBank.region("IMAGE_UI_STORE_SPROUTSTAB_DOWN");
        TextureRegion sunRegion = AssetManager.textureBank.region(
            "IMAGE_UI_SEASONS_UNCOMPRESSED_PVZ2_SEASONS_UIASSET_ICON_SUN"
        );

        Table sunCounterTable = new Table();
        sunCounterTable.setSize(bgRegion.getRegionWidth(), bgRegion.getRegionHeight() - 10);
        sunCounterTable.setBackground(new TextureRegionDrawable(bgRegion));
        Image sunIcon = new Image(sunRegion);
        Label sunLabel = new Label("0", AssetManager.skin);
        sunLabel.setColor(Color.WHITE);

        sunCounterTable.add(sunIcon).padLeft(-15).padTop(-10);
        sunCounterTable.add(sunLabel).expandX().center().padRight(10);

        if (plantSide) {
            plantSunLabel = sunLabel;
            topBar.add(sunCounterTable).left().padLeft(20);
        } else {
            zombieSunLabel = sunLabel;
            topBar.add(sunCounterTable).right().padRight(8);
        }
    }

    private void buildCardBars() {
        Table cardsRow = new Table();
        cardsRow.setTouchable(Touchable.childrenOnly);

        plantCardBar = new PlantCardBar(1.0f, 6f, 520f);
        plantCardBar.setWorldListener(this);

        zombieCardBar = new ZombieCardBar(1.0f, 6f, 520f);
        zombieCardBar.setWorldListener(this);
        zombieCardBar.setTouchable(Touchable.disabled);

        cardsRow.add(plantCardBar).top().left().padLeft(15).padTop(10);
        cardsRow.add().expand();
        cardsRow.add(zombieCardBar).top().right().padRight(15).padTop(10);

        rootTable.add(cardsRow).grow().top().row();
    }

    private void buildDownBar() {
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
            .row();
    }

    private void buildHint() {
        hintLabel = new Label("Zombies: 1-8 select  WASD/Arrows move  Enter place  Esc cancel", AssetManager.skin);
        hintLabel.setColor(Color.WHITE);
        hintLabel.setAlignment(Align.right);
        hintLabel.setTouchable(Touchable.disabled);
        hintLabel.setFontScale(0.7f);
        hintLabel.setPosition(HUD_WIDTH - 620f, 12f);
        hintLabel.setSize(600f, 22f);
        stage.addActor(hintLabel);
    }

    @Override
    public void onZombieCardSelected(String alias) {
        if (!seedSelectionEnabled) {
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

    public void deselectZombieCards() {
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
        if (plantSunLabel != null) {
            plantSunLabel.setText("☀ " + sunAmount);
        }
        int zombieSun = sunAmount;
        if (world instanceof IZombieCouchPlayWorld couchWorld) {
            zombieSun = couchWorld.getZombieSunAmount();
        }
        if (zombieSunLabel != null) {
            zombieSunLabel.setText("☀ " + zombieSun);
        }
    }

    @Override
    public HudType getType() {
        return HudType.I_ZOMBIE_COUCH_PLAY;
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
        zombieCardBar.setTouchable(Touchable.disabled);
    }

    @Override
    public void refreshPlantCardBoostStates() {
        if (plantCardBar != null) {
            plantCardBar.refreshBoostAppearances();
        }
    }

    public void activateZombieCardAt(int index) {
        if (zombieCardBar != null) {
            zombieCardBar.activateCardAt(index);
        }
    }

    public int getZombieCardCount() {
        return zombieCardBar != null ? zombieCardBar.getCardCount() : 0;
    }

    public int getSelectedZombieIndex() {
        if (zombieCardBar == null || zombieCardBar.getSelectedCard() == null) {
            return -1;
        }
        List<ZombieCard> cards = zombieCardBar.getZombieCards();
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == zombieCardBar.getSelectedCard()) {
                return i;
            }
        }
        return -1;
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
    }
}
