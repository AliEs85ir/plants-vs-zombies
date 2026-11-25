package app.view.hud;
import app.enums.plant.PlantName;
import app.view.hud.elements.ClippedGroup;
import app.view.hud.elements.ConveyorBeltBar;
import app.view.hud.elements.ConveyorBeltLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import game.component.AssetManager;
import game.engine.RealTimeGameEngine;
import game.service.special_levels.ConveyorBeltService;
import game.world.chapter.rule.speciallevelRules.ConveyorBeltRule;
import java.util.List;

public class ConveyorBeltHud extends AbstractHud {
    private int beltNumbers;
    private Image[] belts;
    private float beltHeight;
    private ConveyorBeltRule rule;
    private final ConveyorBeltLayout layout;
    private ConveyorBeltBar beltBar;

    public ConveyorBeltHud(ConveyorBeltRule rule) {
        this(rule, ConveyorBeltLayout.defaults());
    }

    public ConveyorBeltHud(ConveyorBeltRule rule, ConveyorBeltLayout layout) {
        this.rule = rule;
        this.layout = (layout != null) ? layout : ConveyorBeltLayout.defaults();
    }

    public ConveyorBeltHud() {
        this(null, ConveyorBeltLayout.defaults());
    }

    @Override
    public void initialize(RealTimeGameEngine engine) {
        super.initialize(engine);
        if (this.rule == null && world != null) {
            this.rule = world.getConveyorBeltRule();
        }
        bindService();
    }

    @Override
    protected void buildUI() {
        TextureRegion beltRegion = AssetManager.textureBank.region("IMAGE_UI_CONVEYOR_CONVEYOR_BELT");
        TextureRegion sideRegion = AssetManager.textureBank.region("IMAGE_UI_CONVEYOR_CONVEYOR_SIDE");
        TextureRegion topRegion = AssetManager.textureBank.region("IMAGE_UI_CONVEYOR_CONVEYOR_TOP");
        float width = beltRegion.getRegionWidth();
        this.beltHeight = HUD_HEIGHT - topRegion.getRegionHeight();
        this.beltNumbers = ((int) beltHeight / beltRegion.getRegionHeight()) + 2;
        layout.maxBarHeight = beltHeight;
        ClippedGroup scrollGroup = new ClippedGroup();
        scrollGroup.setBounds(0, 0, width, beltHeight);
        scrollGroup.setTransform(true);
        scrollGroup.setCullingArea(new Rectangle(0, 0, width, beltHeight));
        float tileHeight = beltRegion.getRegionHeight();
        float startOffset = 20;
        belts = new Image[beltNumbers];
        for (int i = 0; i < beltNumbers; i++) {
            belts[i] = new Image(beltRegion);
            belts[i].setSize(width, tileHeight);
            belts[i].setPosition(0, -i * tileHeight - startOffset);
            scrollGroup.addActor(belts[i]);
        }
        stage.addActor(scrollGroup);
        beltBar = new ConveyorBeltBar(width, beltHeight, layout);
        beltBar.setWorldListener(this);
        beltBar.setSize(width, beltHeight);
        beltBar.setPosition(0, 0);
        stage.addActor(beltBar);
        Image sideBorder = new Image(sideRegion);
        sideBorder.setSize(sideRegion.getRegionWidth(), HUD_HEIGHT);
        sideBorder.setPosition(beltRegion.getRegionWidth(), 0);
        stage.addActor(sideBorder);
        Image topCap = new Image(topRegion);
        topCap.setPosition(0, beltHeight);
        stage.addActor(topCap);
        float bankLeft = beltRegion.getRegionWidth() + sideRegion.getRegionWidth() + 12f;
        layoutPlantFoodBankOnStage(bankLeft);
    }

    @Override
    public void updateConveyorBelt(float deltaTime) {
        if (!initialized || belts == null) return;
        float dy = layout.scrollSpeed * deltaTime;
        for (int i = 0; i < beltNumbers; i++) {
            Image belt = belts[i];
            belt.setY(belt.getY() + dy);
            if (belt.getY() >= beltHeight) {
                belt.setY(belt.getY() - beltHeight - belt.getHeight());
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (engine != null && engine.canAdvanceSimulation()) {
            updateConveyorBelt(deltaTime);
        }
    }

    @Override
    public void finalizePlantCardBar(List<PlantName> selectedPlants) {
        if (rule == null && world != null) {
            rule = world.getConveyorBeltRule();
        }
        bindService();
        if (rule != null) {
            long currentTick = (engine != null) ? engine.getClock().getCurrentTick() : 0L;
            rule.applyPlantPool(selectedPlants, currentTick);
        }
    }

    @Override
    public void deselectAllCards() {
        if (beltBar != null) {
            beltBar.deselectAll();
        }
    }

    @Override
    protected void applySeedSelectionEnabled(boolean enabled) {
        if (beltBar != null) {
            beltBar.setSelectionEnabled(enabled);
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
    public HudType getType() {
        return HudType.CONVEYOR_BELT;
    }

    public ConveyorBeltBar getBeltBar() {
        return beltBar;
    }

    @Override
    public void refreshPlantCardBoostStates() {
        if (beltBar != null) {
            beltBar.refreshBoostAppearances();
        }
    }

    private void bindService() {
        if (beltBar == null) return;
        ConveyorBeltService service = (rule != null) ? rule.getService() : null;
        if (service == null && world != null) {
            service = world.getConveyorBeltService();
        }
        beltBar.bindService(service);
    }
}
