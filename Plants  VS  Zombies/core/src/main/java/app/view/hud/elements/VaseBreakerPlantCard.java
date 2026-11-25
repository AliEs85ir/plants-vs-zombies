package app.view.hud.elements;

import app.controller.game.WorldListener;
import app.view.hud.elements.plantcard.AbstractPlantCard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;
import game.world.minigame.vasebreaker.VaseBreakerInventoryPlant;

public class VaseBreakerPlantCard extends AbstractPlantCard {

    /**
     * Size of the vase plant card relative to the normal PlantCardBar packet.
     * 1.0f = same as chapter 1 cards. Raise this if cards look too small.
     */
    public static final float WORLD_CARD_SCALE = 1.00f;
    private static final float POP_HEIGHT = 42f;
    private static final float HIT_PADDING = 16f;
    private static final Color SELECTION_GLOW = new Color(1f, 0.92f, 0.2f, 1f);

    public enum Phase { ENTERING, AVAILABLE, SELECTED, CONSUMED }

    private final VaseBreakerInventoryPlant item;
    private final TextureRegion selectBorder;
    private WorldListener listener;
    private Phase phase = Phase.ENTERING;
    private boolean selected;
    private float stateTime;

    public VaseBreakerPlantCard(VaseBreakerInventoryPlant item, float cardScale) {
        super(item.getPlantName(), cardScale);
        this.item = item;
        this.selectBorder = AssetManager.textureBank.region("IMAGE_UI_PACKETS_SELECT");
        setOrigin(Align.center);
        setTouchable(Touchable.disabled);
        getColor().a = 0f;
    }

    public void playSpawnOnVase(float worldX, float worldY) {
        phase = Phase.ENTERING;
        setTouchable(Touchable.disabled);
        setOrigin(Align.center);

        float restX = worldX - getWidth() / 2f;
        float restY = worldY - getHeight() / 2f + POP_HEIGHT;
        setPosition(restX, worldY - getHeight() / 2f);
        setScale(0.12f);
        setRotation(-22f);
        getColor().a = 0f;

        clearActions();
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(1f, 1f, 0.48f, Interpolation.swingOut),
                Actions.rotateTo(0f, 0.4f, Interpolation.swingOut),
                Actions.fadeIn(0.22f),
                Actions.moveTo(restX, restY, 0.42f, Interpolation.circleOut)
            ),
            Actions.run(this::markAvailable)
        ));
    }

    public void playConsume(Runnable onComplete) {
        phase = Phase.CONSUMED;
        setTouchable(Touchable.disabled);
        clearActions();
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(0.2f, 0.2f, 0.22f, Interpolation.swingIn),
                Actions.fadeOut(0.22f),
                Actions.moveBy(0f, 28f, 0.22f, Interpolation.circleOut)
            ),
            Actions.run(() -> {
                if (onComplete != null) {
                    onComplete.run();
                }
            }),
            Actions.removeActor()
        ));
    }

    public void markAvailable() {
        if (phase == Phase.CONSUMED) {
            return;
        }
        phase = selected ? Phase.SELECTED : Phase.AVAILABLE;
        setTouchable(Touchable.enabled);
        startIdleFloat();
    }

    private void startIdleFloat() {
        addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0f, 8f, 0.85f, Interpolation.sine),
            Actions.moveBy(0f, -8f, 0.85f, Interpolation.sine)
        )));
    }

    @Override
    protected void onCardClicked() {
        if (listener == null || phase == Phase.ENTERING || phase == Phase.CONSUMED) {
            return;
        }
        if (selected) {
            listener.onPlantCardPlaceAt(item.getWorldX(), item.getWorldY());
        } else {
            listener.onPlantCardSelected(plantName);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX();
        float y = getY();
        float w = getWidth();
        float h = getHeight();
        float originX = getOriginX();
        float originY = getOriginY();
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        float rotation = getRotation();

        if (selected) {
            float pulse = 1f + 0.06f * MathUtils.sin(stateTime * 6f);
            scaleX *= pulse;
            scaleY *= pulse;
        }

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        TextureRegion background = backgroundRegion();
        if (background != null) {
            batch.draw(background, x, y, originX, originY, w, h, scaleX, scaleY, rotation);
        }
        drawPlantIconTransformed(batch, x, y, w, h, originX, originY, scaleX, scaleY, rotation);

        if (selected) {
            drawSelectionHighlight(batch, x, y, w, h, originX, originY, scaleX, scaleY, rotation,
                color.a * parentAlpha);
        } else if (hovered && phase != Phase.CONSUMED && phase != Phase.ENTERING) {
            drawSelectionHighlight(batch, x, y, w, h, originX, originY, scaleX, scaleY, rotation,
                color.a * parentAlpha * 0.45f);
        }

        batch.setColor(Color.WHITE);
    }

    private void drawPlantIconTransformed(Batch batch, float x, float y, float w, float h,
                                          float originX, float originY,
                                          float scaleX, float scaleY, float rotation) {
        if (cardConfig == null || cardConfig.cachedPlantRegion == null) {
            return;
        }
        float imgW = cardConfig.cachedPlantRegion.getRegionWidth() * cardScale * cardConfig.scale;
        float imgH = cardConfig.cachedPlantRegion.getRegionHeight() * cardScale * cardConfig.scale;
        float imgX = x + (w - imgW) / 2f - (w * PLANT_X_OFFSET_RATIO) + (cardConfig.offsetX * cardScale);
        float imgY = y + h - imgH + (h * PLANT_Y_OFFSET_RATIO) + (cardConfig.offsetY * cardScale);
        batch.draw(cardConfig.cachedPlantRegion, imgX, imgY,
            originX - (imgX - x), originY - (imgY - y),
            imgW, imgH, scaleX, scaleY, rotation);
    }

    private void drawSelectionHighlight(Batch batch, float x, float y, float w, float h,
                                        float originX, float originY, float scaleX, float scaleY,
                                        float rotation, float parentAlpha) {
        if (selectBorder == null) {
            return;
        }
        float alpha = 0.75f + 0.25f * MathUtils.sin(stateTime * 6f);
        float borderThick = 3f * cardScale;
        batch.setColor(SELECTION_GLOW.r, SELECTION_GLOW.g, SELECTION_GLOW.b, alpha * parentAlpha);
        batch.draw(selectBorder,
            x - borderThick, y - borderThick,
            originX + borderThick, originY + borderThick,
            w + borderThick * 2f, h + borderThick * 2f,
            scaleX, scaleY, rotation);
    }

    public boolean containsWorldPoint(float worldX, float worldY) {
        if (phase == Phase.CONSUMED || phase == Phase.ENTERING) {
            return false;
        }
        return worldX >= getX() - HIT_PADDING
            && worldX <= getX() + getWidth() + HIT_PADDING
            && worldY >= getY() - HIT_PADDING
            && worldY <= getY() + getHeight() + HIT_PADDING;
    }

    public void setListener(WorldListener listener) {
        this.listener = listener;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (phase != Phase.CONSUMED) {
            phase = selected ? Phase.SELECTED : (phase == Phase.ENTERING ? Phase.ENTERING : Phase.AVAILABLE);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public Phase getPhase() {
        return phase;
    }

    public VaseBreakerInventoryPlant getItem() {
        return item;
    }

    public long getItemId() {
        return item.getId();
    }
}
