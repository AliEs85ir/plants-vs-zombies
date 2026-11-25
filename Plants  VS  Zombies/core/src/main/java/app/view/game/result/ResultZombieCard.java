package app.view.game.result;

import app.view.hud.elements.zombiecard.AbstractZombieCard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public final class ResultZombieCard extends AbstractZombieCard {
    public ResultZombieCard(String alias, float cardScale) {
        super(alias, cardScale);
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
        drawZombieIcon(batch, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }
}
