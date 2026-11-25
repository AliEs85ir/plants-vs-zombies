package app.view.hud.elements.plantcard;

import app.enums.plant.PlantName;
import app.service.plant.PlantBoostService;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;

public abstract class AbstractPlantCard extends Actor {

    protected static final float PLANT_X_OFFSET_RATIO = 0.22f;
    protected static final float PLANT_Y_OFFSET_RATIO = 0.02f;

    protected final PlantName plantName;
    protected final PlantCardConfigLoader.PlantCardConfig cardConfig;
    protected float cardScale;
    protected boolean hovered = false;
    private PlantCardState cardState = PlantCardState.NORMAL;
    private TextureRegion stateBackground;

    protected AbstractPlantCard(PlantName plantName, float cardScale) {
        this.plantName  = plantName;
        this.cardScale  = cardScale;
        this.cardConfig = PlantCardConfigLoader.getConfig(plantName);

        updateCardBounds();
        syncBoostAppearance();
        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.play(AudioId.SFX_SEEDLIFT);
                onCardClicked();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1) return;
                hovered = true;
                onHoverEnter();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) return;
                hovered = false;
                onHoverExit();
            }
        });
    }

    protected void updateCardBounds() {
        if (cardConfig != null && cardConfig.cachedBgRegion != null) {
            setSize(
                cardConfig.cachedBgRegion.getRegionWidth()  * cardScale,
                cardConfig.cachedBgRegion.getRegionHeight() * cardScale
            );
        }
    }

    public void setCardScale(float scale) {
        this.cardScale = scale;
        updateCardBounds();
    }

    public float getCardScale() {
        return cardScale;
    }

    public boolean isHovered() {
        return hovered;
    }

    public float nativeWidth() {
        return (cardConfig != null && cardConfig.cachedBgRegion != null)
            ? cardConfig.cachedBgRegion.getRegionWidth() : 1f;
    }

    public float nativeHeight() {
        return (cardConfig != null && cardConfig.cachedBgRegion != null)
            ? cardConfig.cachedBgRegion.getRegionHeight() : 1f;
    }

    public void setCardState(PlantCardState state) {
        this.cardState = (state != null) ? state : PlantCardState.NORMAL;
        this.stateBackground = null;
        if (this.cardState.hasOverride() && cardConfig != null) {
            this.stateBackground = AssetManager.textureBank.region(
                this.cardState.resolveBackgroundKey(cardConfig.bgKey));
        }
    }

    public PlantCardState getCardState() {
        return cardState;
    }

    public void syncBoostAppearance() {
        setCardState(PlantBoostService.isBoosted(plantName)
            ? PlantCardState.BOOSTED
            : PlantCardState.NORMAL);
    }

    protected TextureRegion backgroundRegion() {
        if (stateBackground != null) return stateBackground;
        return (cardConfig != null) ? cardConfig.cachedBgRegion : null;
    }

    protected void drawBackground(Batch batch, float x, float y, float w, float h) {
        TextureRegion region = backgroundRegion();
        if (region != null) {
            batch.draw(region, x, y, w, h);
        }
    }

    protected void drawPlantIcon(Batch batch, float x, float y, float w, float h) {
        if (cardConfig == null || cardConfig.cachedPlantRegion == null) return;

        float imgW = cardConfig.cachedPlantRegion.getRegionWidth()  * cardScale * cardConfig.scale;
        float imgH = cardConfig.cachedPlantRegion.getRegionHeight() * cardScale * cardConfig.scale;

        float imgX = x + (w - imgW) / 2f
                       - (w * PLANT_X_OFFSET_RATIO)
                       + (cardConfig.offsetX * cardScale);
        float imgY = y + h - imgH
                       + (h * PLANT_Y_OFFSET_RATIO)
                       + (cardConfig.offsetY * cardScale);

        batch.draw(cardConfig.cachedPlantRegion, imgX, imgY, imgW, imgH);
    }

    public PlantName getPlantName() {
        return plantName;
    }

    public PlantCardConfigLoader.PlantCardConfig getCardConfig() {
        return cardConfig;
    }

    protected abstract void onCardClicked();

    protected void onHoverEnter() {}

    protected void onHoverExit() {}
}
