package graphics.general.renderer;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface EntityRenderer<I> {
    void render(SpriteBatch batch, I instance);
}
