package graphics.general.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import graphics.general.animation.AnimationController;
import graphics.lawnmower.LMAnimationVariant;
import graphics.lawnmower.LMRenderInstance;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class LawnMowerRenderer implements EntityRenderer<LMRenderInstance>{
    private final PamPlayer pamPlayer;

    public LawnMowerRenderer(PamPlayer pamPlayer) {
        this.pamPlayer = pamPlayer;
    }

    public void render(SpriteBatch batch, LMRenderInstance instance) {
        AnimationController<LMAnimationVariant> controller = instance.getAnimationController();
        ClipRef clip = controller.getCurrentClip();
        LMAnimationVariant variant = controller.getCurrentVariant();
        String pamPath = controller.getCurrentPamPath();

        if (clip == null || variant == null || pamPath == null) {
            return;
        }

        float x = (float) instance.getlawnmower().getPosition().getX();
        float y = (float) instance.getlawnmower().getPosition().getY();
        pamPlayer.draw(
            batch,
            clip,
            controller.getStateTime(),
            x,
            y,
            variant.isLoop()
        );
    }
}
