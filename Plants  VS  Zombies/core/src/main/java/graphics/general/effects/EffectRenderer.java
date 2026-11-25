package graphics.general.effects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import graphics.general.animation.AnimationController;
import pvz.libpvz.pam.PamPlayer;

public class EffectRenderer {

    private final PamPlayer pamPlayer;

    public EffectRenderer(PamPlayer pamPlayer) {
        this.pamPlayer = pamPlayer;
    }

    public <M> void render(SpriteBatch batch, EffectInstance<M> instance) {
        EffectVariant<M, EffectTag> variant = instance.getCurrentVariant();
        if (variant == null) {
            return;
        }

        AnimationController<EffectVariant<M, EffectTag>> controller = instance.getController();
        if (controller.getCurrentClip() == null) {
            return;
        }

        if (controller.getCurrentPamPath() == null) {
            return;
        }

        Vector2 pos = instance.getAnchor().resolvePosition();

        instance.getModifierController().apply(batch);

        Matrix4 previous = null;
        float scaleX = instance.getScaleX();
        float scaleY = instance.getScaleY();
        boolean transform = instance.isFlipX() || scaleX != 1f || scaleY != 1f;
        if (transform) {
            previous = batch.getTransformMatrix().cpy();
            Matrix4 scaled = previous.cpy();
            scaled.translate(pos.x, pos.y, 0f);
            scaled.scale(instance.isFlipX() ? -scaleX : scaleX, scaleY, 1f);
            scaled.translate(-pos.x, -pos.y, 0f);
            batch.setTransformMatrix(scaled);
        }

        pamPlayer.draw(
            batch,
            controller.getCurrentClip(),
            controller.getStateTime(),
            pos.x,
            pos.y,
            variant.isLoop(),
            instance.getVisibilityController().getLayerVisibilityMap()
        );

        if (previous != null) {
            batch.setTransformMatrix(previous);
        }

        batch.setColor(1f, 1f, 1f, 1f);
    }
}
