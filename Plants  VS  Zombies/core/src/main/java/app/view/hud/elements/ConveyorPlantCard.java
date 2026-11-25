package app.view.hud.elements;

import app.controller.game.WorldListener;
import app.enums.plant.PlantName;
import app.view.hud.elements.plantcard.AbstractPlantCard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import game.component.AssetManager;
import game.service.special_levels.ConveyorBeltItem;

public class ConveyorPlantCard extends AbstractPlantCard {

    public enum Phase { ENTERING, ACTIVE, SELECTED, CONSUMED }

    private final ConveyorBeltItem item;
    private final ConveyorBeltLayout layout;
    private final TextureRegion selectBorder;
    private WorldListener listener;
    private Phase phase = Phase.ENTERING;
    private boolean selected;
    private float hoverAmount;
    private float stateTime;

    public ConveyorPlantCard(ConveyorBeltItem item, float cardScale, ConveyorBeltLayout layout) {
        super(item.getPlantName(), cardScale);
        this.item = item;
        this.layout = (layout != null) ? layout : ConveyorBeltLayout.defaults();
        this.selectBorder = AssetManager.textureBank.region("IMAGE_UI_PACKETS_SELECT");
        setTouchable(Touchable.disabled);
        getColor().a = 0f;
    }

    public void playEnter(float targetX, float targetY) {
        if (phase == Phase.CONSUMED) return;
        phase = Phase.ENTERING;
        float duration = layout.moveDuration(getY(), targetY);
        float fadeDuration = Math.min(duration, layout.enterFadeDuration);
        clearActions();
        addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveTo(targetX, targetY, duration, Interpolation.linear),
                Actions.fadeIn(fadeDuration)
            ),
            Actions.run(this::markActive)
        ));
    }

    public void slideTo(float targetX, float targetY) {
        if (phase == Phase.CONSUMED) return;
        float duration = layout.moveDuration(getY(), targetY);
        clearActions();
        addAction(Actions.moveTo(targetX, targetY, duration, Interpolation.linear));
        if (phase == Phase.ENTERING && getColor().a < 1f) {
            addAction(Actions.fadeIn(Math.min(duration, layout.enterFadeDuration)));
        }
    }

    public void playExit(Runnable onComplete) {
        phase = Phase.CONSUMED;
        setTouchable(Touchable.disabled);
        clearActions();
        addAction(Actions.sequence(
            Actions.fadeOut(layout.exitDuration),
            Actions.run(() -> {
                if (onComplete != null) onComplete.run();
            }),
            Actions.removeActor()
        ));
    }

    public void markActive() {
        if (phase == Phase.CONSUMED) return;
        phase = selected ? Phase.SELECTED : Phase.ACTIVE;
        setTouchable(Touchable.enabled);
    }

    @Override
    protected void onCardClicked() {
        if (listener == null || phase == Phase.ENTERING || phase == Phase.CONSUMED) return;
        if (selected) {
            listener.onPlantCardDeselected(plantName);
        } else {
            listener.onPlantCardSelected(plantName);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
        boolean showHover = hovered && phase != Phase.CONSUMED;
        float targetHover = showHover ? 1f : 0f;
        hoverAmount = MathUtils.clamp(
            hoverAmount + (targetHover - hoverAmount) * Math.min(1f, delta * layout.hoverLerpSpeed),
            0f, 1f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float wave = 0.5f + 0.5f * MathUtils.sin(stateTime * layout.hoverWaveSpeed);
        float offsetX = hoverAmount * layout.hoverWaveAmplitude * wave;
        float x = getX() + offsetX;
        float y = getY();
        float w = getWidth();
        float h = getHeight();

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        drawBackground(batch, x, y, w, h);
        drawPlantIcon(batch, x, y, w, h);

        if (selected) {
            drawSelectBorder(batch, x, y, w, h, color.a * parentAlpha,
                0.72f + 0.28f * MathUtils.sin(stateTime * 6f));
        } else if (hoverAmount > 0.02f) {
            drawSelectBorder(batch, x, y, w, h, color.a * parentAlpha * hoverAmount, 1f);
        }

        batch.setColor(Color.WHITE);
    }

    private void drawSelectBorder(Batch batch, float x, float y, float w, float h,
                                  float parentAlpha, float pulse) {
        if (selectBorder == null) return;
        float pad = layout.selectBorderPad * cardScale;
        batch.setColor(1f, 1f, 1f, pulse * parentAlpha);
        batch.draw(selectBorder, x - pad, y - pad, w + pad * 2f, h + pad * 2f);
    }

    public void setListener(WorldListener listener) {
        this.listener = listener;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (phase != Phase.ENTERING && phase != Phase.CONSUMED) {
            phase = selected ? Phase.SELECTED : Phase.ACTIVE;
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public Phase getPhase() {
        return phase;
    }

    public ConveyorBeltItem getItem() {
        return item;
    }

    public long getItemId() {
        return item.getId();
    }

    @Override
    public PlantName getPlantName() {
        return plantName;
    }
}
