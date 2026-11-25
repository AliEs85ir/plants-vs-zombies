package graphics.general.modifier.visualModifiers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import graphics.general.modifier.RenderContext;

public interface VisualModifier {

    void update(float delta);
    void apply(SpriteBatch batch);
    boolean isFinished();
    default void beforeRender(SpriteBatch batch, RenderContext context){}
    default void afterRender(SpriteBatch batch, RenderContext context){}
}
