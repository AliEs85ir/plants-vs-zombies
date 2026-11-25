package graphics.general.renderer;

import app.enums.asset.GameIcon;
import app.enums.obstacle.ObstacleType;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import game.component.AssetManager;
import game.component.ResolvedIcon;
import graphics.general.animation.AnimationController;
import graphics.obstacle.ObstacleAnimationVariant;
import graphics.obstacle.ObstacleRenderInstance;
import graphics.obstacle.ObstacleVisualState;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;

public class ObstacleRenderer implements EntityRenderer<ObstacleRenderInstance>{
    private final PamPlayer pamPlayer;
    private final Matrix4 oldTransform = new Matrix4();
    private final Matrix4 newTransform = new Matrix4();

    public ObstacleRenderer(PamPlayer pamPlayer) {
        this.pamPlayer = pamPlayer;
    }

    public void render(SpriteBatch batch, ObstacleRenderInstance instance) {
        AnimationController<ObstacleAnimationVariant> controller = instance.getAnimationController();
        ClipRef clip = controller.getCurrentClip();
        ObstacleAnimationVariant variant = controller.getCurrentVariant();
        String pamPath = controller.getCurrentPamPath();

        float x = (float) instance.getObstacle().getPosition().getX();
        float y = (float) instance.getObstacle().getPosition().getY();

        instance.getModifierController().apply(batch);
        try {
            if (clip != null && variant != null && pamPath != null) {
                applyRotation(batch, instance, x, y);

                pamPlayer.draw(
                    batch,
                    clip,
                    controller.getStateTime(),
                    x,
                    y,
                    variant.isLoop(),
                    instance.getVisibilityController().getLayerVisibilityMap()
                );

                if (instance.getRotation() != 0) {
                    batch.flush();
                    batch.setTransformMatrix(oldTransform);
                }
            }

            drawBrainIcon(batch, instance, x, y);
        } finally {
            batch.setColor(1f, 1f, 1f, 1f);
            batch.setShader(null);
        }
    }

    private void applyRotation(SpriteBatch batch, ObstacleRenderInstance instance, float x, float y) {
        float rotation = instance.getRotation();
        boolean needsRotation = (rotation != 0f);

        if (needsRotation) {
            batch.flush();
            oldTransform.set(batch.getTransformMatrix());

            newTransform.set(oldTransform);
            newTransform.translate(x, y, 0);
            newTransform.rotate(0, 0, 1, rotation);
            newTransform.translate(-x, -y, 0);

            batch.setTransformMatrix(newTransform);
        }
    }

    private void drawBrainIcon(SpriteBatch batch, ObstacleRenderInstance instance, float x, float y) {
        if (instance.getObstacle().getType() != ObstacleType.BRAIN) {
            return;
        }
        if (instance.getObstacle().isDead()
            || instance.getCurrentVisualState() == ObstacleVisualState.DEATH) {
            return;
        }
        ResolvedIcon icon = AssetManager.icon(GameIcon.IMAGE_UI_CALENDAR_TIMER_DECO_BIGBRAINZ);
        if (!icon.isPresent()) {
            return;
        }
        TextureRegion region = icon.region();
        float width = icon.width();
        float height = icon.height();
        batch.draw(region, x - width / 2f, y - height / 2f, width, height);
    }
}
