package app.view.game.result;

import app.enums.plant.PlantName;
import app.view.hud.elements.plantcard.AbstractPlantCard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public final class ResultPlantCard extends AbstractPlantCard {
    public ResultPlantCard(PlantName plantName, float cardScale) {
        super(plantName, cardScale);
        setTouchable(Touchable.disabled);
        if (getWidth() < 8f) {
            setSize(72f * cardScale, 90f * cardScale);
        }
    }

    @Override
    protected void onCardClicked() {
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        drawBackground(batch, getX(), getY(), getWidth(), getHeight());
        drawPlantIcon(batch, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }
}
