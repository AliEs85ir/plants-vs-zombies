package graphics.npc;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import common.model.Position;
import graphics.general.animation.AnimationController;
import graphics.general.renderer.EntityRenderer;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class NpcRenderer implements EntityRenderer<NpcRenderInstance> {
    private final PamPlayer pamPlayer;

    public NpcRenderer(PamPlayer pamPlayer) {
        this.pamPlayer = pamPlayer;
    }

    @Override
    public void render(SpriteBatch batch, NpcRenderInstance instance) {
        AnimationController<NpcAnimationVariant> controller = instance.getAnimationController();
        ClipRef clip = controller.getCurrentClip();
        NpcAnimationVariant variant = controller.getCurrentVariant();
        String pamPath = controller.getCurrentPamPath();
        if (clip == null || variant == null || pamPath == null) {
            return;
        }

        Position pos = instance.getRenderPosition();
        if (pos == null) {
            return;
        }

        instance.getModifierController().apply(batch);
        pamPlayer.draw(
            batch,
            clip,
            controller.getStateTime(),
            (float) pos.getX(),
            (float) pos.getY(),
            variant.isLoop()
        );
        batch.setColor(1f, 1f, 1f, 1f);
    }
}
