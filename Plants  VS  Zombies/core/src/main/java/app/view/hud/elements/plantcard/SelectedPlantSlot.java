package app.view.hud.elements.plantcard;

import app.enums.plant.PlantName;
import app.view.game.pregame.PlantSelectionState;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

public class SelectedPlantSlot extends AbstractPlantCard {

    private static final float HOVER_SPEED = 9f;

    private final PlantSelectionState state;
    private float hoverAmount = 0f;

    public SelectedPlantSlot(PlantName plantName, float cardScale, PlantSelectionState state) {
        super(plantName, cardScale);
        this.state = state;
    }

    @Override
    protected void onCardClicked() {
        if (state != null) {
            state.deselect(plantName);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float target = hovered ? 1f : 0f;
        hoverAmount = MathUtils.clamp(
            hoverAmount + (target - hoverAmount) * Math.min(1f, delta * HOVER_SPEED), 0f, 1f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float shift = Interpolation.smooth.apply(0f, 2.5f, hoverAmount);
        float x = getX() + shift;
        float y = getY();
        float w = getWidth();
        float h = getHeight();

        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        drawBackground(batch, x, y, w, h);
        drawPlantIcon(batch, x, y, w, h);

        if (hoverAmount > 0.01f) {
            batch.setColor(UiTheme.TEXT_ERROR.r, UiTheme.TEXT_ERROR.g, UiTheme.TEXT_ERROR.b,
                           0.28f * hoverAmount);
            drawBackground(batch, x, y, w, h);
        }

        batch.setColor(Color.WHITE);
    }
}
