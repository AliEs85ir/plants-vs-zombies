package graphics.general.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import graphics.general.animation.AnimationController;
import graphics.sun.SunAnimationVariant;
import graphics.sun.SunRenderInstance;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class SunRenderer implements EntityRenderer<SunRenderInstance>{
    private final PamPlayer pamPlayer;

    public SunRenderer(PamPlayer pamPlayer) {
        this.pamPlayer = pamPlayer;
    }

    public void render(SpriteBatch batch, SunRenderInstance instance) {
        AnimationController<SunAnimationVariant> controller = instance.getAnimationController();
        ClipRef clip = controller.getCurrentClip();
        SunAnimationVariant variant = controller.getCurrentVariant();
        String pamPath = controller.getCurrentPamPath();

        if (clip == null || variant == null || pamPath == null) {
            return;
        }

        float x = (float) instance.getModel().getPosition().getX();
        float y = (float) instance.getModel().getPosition().getY();
        float scale = instance.getScale();

        Matrix4 originalTransform = batch.getTransformMatrix().cpy();
        batch.end();
        batch.getTransformMatrix()
            .translate(x, y, 0)
            .scale(scale, scale, 1)
            .translate(-x, -y, 0);
        batch.begin();

        pamPlayer.draw(
            batch,
            clip,
            controller.getStateTime(),
            x,
            y,
            variant.isLoop()
        );

        batch.end();
        batch.setTransformMatrix(originalTransform);
        batch.begin();
    }
}
