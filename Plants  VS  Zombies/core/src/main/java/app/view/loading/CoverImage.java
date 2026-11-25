package app.view.loading;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public final class CoverImage extends Widget {
    private Texture texture;
    private float imageWidth = 1f;
    private float imageHeight = 1f;
    private float offsetX;
    private float offsetY;
    private final float[] layout = new float[4];

    public void setTexture(Texture texture, float imageWidth, float imageHeight) {
        this.texture = texture;
        this.imageWidth = Math.max(1f, imageWidth);
        this.imageHeight = Math.max(1f, imageHeight);
        invalidate();
    }

    public void setOffsets(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        invalidate();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Stage stage = getStage();
        if (stage != null && getParent() == stage.getRoot()) {
            setPosition(0f, 0f);
            setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        }
        validate();
        if (texture == null) {
            return;
        }
        CoverLayout.compute(
            getWidth(),
            getHeight(),
            imageWidth,
            imageHeight,
            offsetX,
            offsetY,
            layout
        );
        batch.setColor(1f, 1f, 1f, parentAlpha * getColor().a);
        batch.draw(texture, getX() + layout[0], getY() + layout[1], layout[2], layout[3]);
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
