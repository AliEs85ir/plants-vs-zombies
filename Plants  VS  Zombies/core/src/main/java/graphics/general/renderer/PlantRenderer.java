package graphics.general.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import common.model.Position;
import graphics.general.animation.AnimationController;
import graphics.general.modifier.visualModifiers.WhiteModifier;
import graphics.plant.PlantAnimationVariant;
import graphics.plant.PlantDamageFlashController;
import graphics.plant.PlantRenderInstance;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class PlantRenderer implements EntityRenderer<PlantRenderInstance>{

    private final PamPlayer pamPlayer;

    public PlantRenderer(PamPlayer pamPlayer) {
        this.pamPlayer = pamPlayer;
    }

    public void render(SpriteBatch batch, PlantRenderInstance instance) {
        AnimationController<PlantAnimationVariant> controller = instance.getAnimationController();
        ClipRef clip = controller.getCurrentClip();
        PlantAnimationVariant variant = controller.getCurrentVariant();
        String pamPath = controller.getCurrentPamPath();

        if (clip == null || variant == null || pamPath == null) {
            return;
        }

        Position pos = instance.getRenderPosition();
        if (pos == null) {
            return;
        }

        instance.getModifierController().apply(batch);

        float x = (float) pos.getX();
        float y = (float) pos.getY();
        pamPlayer.draw(
            batch,
            clip,
            controller.getStateTime(),
            x,
            y,
            variant.isLoop()
        );

        renderDamageFlashOverlay(batch, instance, controller, clip, variant, x, y);

        batch.setColor(1f, 1f, 1f, 1f);
        batch.setShader(null);
    }

    private void renderDamageFlashOverlay(
        SpriteBatch batch,
        PlantRenderInstance instance,
        AnimationController<PlantAnimationVariant> controller,
        ClipRef clip,
        PlantAnimationVariant variant,
        float x,
        float y
    ) {
        PlantDamageFlashController flash = instance.getDamageFlashController();
        if (!flash.isActive()) {
            return;
        }

        float alpha = flash.getOverlayAlpha();
        if (alpha <= 0f) {
            return;
        }

        ShaderProgram whiteShader = WhiteModifier.whiteShader();
        batch.flush();
        whiteShader.bind();
        whiteShader.setUniformf("u_whiteBlend", 1f);
        batch.setShader(whiteShader);
        batch.setColor(1f, 1f, 1f, alpha);

        pamPlayer.draw(
            batch,
            clip,
            controller.getStateTime(),
            x,
            y,
            variant.isLoop()
        );

        batch.flush();
        batch.setShader(null);
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
