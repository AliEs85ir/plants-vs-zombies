package app.view.hud.elements.zombiecard;

import app.enums.plant.PlantName;
import app.view.hud.elements.plantcard.PlantCardConfigLoader;
import app.view.hud.elements.plantcard.PlantCardState;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;

public abstract class AbstractZombieCard extends Actor {
    protected static final float ZOMBIE_X_OFFSET_RATIO = 0.22f;
    protected static final float ZOMBIE_Y_OFFSET_RATIO = 0.02f;

    protected final String alias;
    protected final ZombieCardConfigLoader.ZombieCardConfig cardConfig;
    protected float cardScale;
    protected boolean hovered = false;
    private TextureRegion stateBackground;

    protected AbstractZombieCard(String alias, float cardScale) {
        this.alias  = alias;
        this.cardScale  = cardScale;
        this.cardConfig = ZombieCardConfigLoader.getConfig(alias);

        updateCardBounds();
        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.play(AudioId.UI_CLICK);
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

    protected void drawZombieIcon(Batch batch, float x, float y, float w, float h) {
        if (cardConfig == null || cardConfig.cachedZombieRegion == null) return;

        float imgW = cardConfig.cachedZombieRegion.getRegionWidth()  * cardScale * cardConfig.scale;
        float imgH = cardConfig.cachedZombieRegion.getRegionHeight() * cardScale * cardConfig.scale;

        float imgX = x + (w - imgW) / 2f
            - (w * ZOMBIE_X_OFFSET_RATIO)
            + (cardConfig.offsetX * cardScale);
        float imgY = y + h - imgH
            + (h * ZOMBIE_Y_OFFSET_RATIO)
            + (cardConfig.offsetY * cardScale);

        batch.draw(cardConfig.cachedZombieRegion, imgX, imgY, imgW, imgH);
    }

    public String getZombieName() {
        return alias;
    }

    public ZombieCardConfigLoader.ZombieCardConfig getCardConfig() {
        return cardConfig;
    }

    protected abstract void onCardClicked();

    protected void onHoverEnter() {}

    protected void onHoverExit() {}
}
