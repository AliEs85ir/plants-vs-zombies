package graphics.projectile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import common.model.Position;
import game.entity.projectile.Projectile;
import graphics.general.animation.AnimationController;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class ProjectileRenderer {
    private final PamPlayer pamPlayer;

    public ProjectileRenderer(PamPlayer pamPlayer) {
        this.pamPlayer = pamPlayer;
    }

    public void render(SpriteBatch batch, ProjectileRenderInstance instance) {
        AnimationController<ProjectileAnimationVariant> controller = instance.getAnimationController();
        ClipRef clip = controller.getCurrentClip();
        ProjectileAnimationVariant variant = controller.getCurrentVariant();
        String pamPath = controller.getCurrentPamPath();

        if (clip == null || variant == null || pamPath == null) return;

        Position pos = instance.getRenderPosition();
        if (pos == null) return;

        float x = (float) pos.getX();
        float y = (float) pos.getY() + instance.getModel().getVisualHeight();
        float angle = getProjectileAngle(instance.getModel());

        Matrix4 originalTransform = batch.getTransformMatrix().cpy();
        batch.end();
        batch.getTransformMatrix()
            .translate(x, y, 0)
            .rotate(0, 0, 1, angle)
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

    private float getProjectileAngle(Projectile projectile) {
        if (projectile instanceof game.entity.projectile.ShooterProjectile sp) {
            return (float) sp.getAngle();
        }
        return 0f;
    }
}
