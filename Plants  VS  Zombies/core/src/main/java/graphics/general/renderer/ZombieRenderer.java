package graphics.general.renderer;

import app.model.App;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import graphics.general.animation.AnimationController;
import graphics.general.modifier.RenderContext;
import graphics.zombie.ZombieAnimationVariant;
import graphics.zombie.ZombieRenderInstance;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class ZombieRenderer implements EntityRenderer<ZombieRenderInstance>{
    private final PamPlayer pamPlayer;

    public ZombieRenderer(PamPlayer pamPlayer) {
        this.pamPlayer = pamPlayer;
    }

    public void render(SpriteBatch batch, ZombieRenderInstance instance) {
        AnimationController<ZombieAnimationVariant> controller =
            instance.getAnimationController();

        ClipRef clip = controller.getCurrentClip();
        ZombieAnimationVariant variant = controller.getCurrentVariant();
        String pamPath = controller.getCurrentPamPath();

        if (clip == null || variant == null || pamPath == null) {
            return;
        }

        float x = (float) instance.getZombie().getPosition().getX();
        float y = (float) instance.getZombie().getPosition().getY();

        instance.getModifierController().apply(batch);

        RenderContext context = applyRenderingParts(batch, instance, controller, variant, x, y);

        try {
            pamPlayer.draw(
                batch,
                clip,
                controller.getStateTime(),
                x,
                y,
                variant.isLoop(),
                instance.getVisibilityController().getLayerVisibilityMap()
            );
        } finally {
            instance.getModifierController().afterRender(batch, context);
            batch.setColor(1f, 1f, 1f, 1f);
            batch.setShader(null);
        }
    }

    private RenderContext applyRenderingParts(SpriteBatch batch, ZombieRenderInstance instance, AnimationController<ZombieAnimationVariant> controller, ZombieAnimationVariant variant, float x, float y) {
        Rectangle localBounds = pamPlayer.bounds(controller.getCurrentPamPath(), variant.getClipName());

        Rectangle worldBounds = new Rectangle(
            x + localBounds.x,
            y + localBounds.y,
            localBounds.width > 0 ? localBounds.width : 200f,
            localBounds.height > 0 ? localBounds.height : 250f
        );

        OrthographicCamera camera = App.getCameraController().getCamera();
        RenderContext context = new RenderContext(camera, worldBounds);

        instance.getModifierController().beforeRender(batch, context);
        return context;
    }
}
