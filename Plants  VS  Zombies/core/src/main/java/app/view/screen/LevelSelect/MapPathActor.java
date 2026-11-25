package app.view.screen.LevelSelect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import pvz.libpvz.pam.PamPlayer;

public class MapPathActor extends Actor {

    private final PamPlayer pamPlayer;
    private final String pamPath;

    private String clipName;
    private float stateTime;

    private LevelNodeActor startNode;
    private LevelNodeActor endNode;

    private final Matrix4 oldTransform = new Matrix4();
    private final Matrix4 pathTransform = new Matrix4();

    /*
     * اندازه‌ی اصلی Path در PAM
     */
    private final float nativeWidth;
    private final float nativeHeight;

    /*
     * ضخامت Path.
     *
     * اگر بعداً نازک بود:
     * 0.8 -> 1.0 -> 1.2
     */
    private static final float PATH_THICKNESS = 0.8f;

    public MapPathActor(
        PamPlayer pamPlayer,
        String pamPath
    ) {
        this.pamPlayer = pamPlayer;
        this.pamPath = pamPath;

        var bounds = pamPlayer.bounds(pamPath);

        if (bounds == null) {
            throw new IllegalStateException(
                "Could not load PAM: " + pamPath
            );
        }

        nativeWidth = bounds.width;
        nativeHeight = bounds.height;

        clipName = "beam_path_on";

        setTouchable(Touchable.disabled);
    }

    public void connect(
        LevelNodeActor start,
        LevelNodeActor end
    ) {
        this.startNode = start;
        this.endNode = end;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        stateTime += delta;
    }

    @Override
    public void draw(
        Batch batch,
        float parentAlpha
    ) {

        if (startNode == null || endNode == null) {
            return;
        }

        /*
         * مرکز دو Node
         */
        float startX = startNode.getCenterX();
        float startY = startNode.getCenterY();

        float endX = endNode.getCenterX();
        float endY = endNode.getCenterY();

        /*
         * فاصله
         */
        float dx = endX - startX;
        float dy = endY - startY;

        float distance = (float) Math.sqrt(
            dx * dx + dy * dy
        );

        if (distance <= 0.001f) {
            return;
        }

        /*
         * زاویه‌ی Node اول -> Node دوم
         */
        float angle = (float) Math.toDegrees(
            Math.atan2(dy, dx)
        );

        /*
         * مرکز Path
         */
        float centerX = (startX + endX) / 2f;
        float centerY = (startY + endY) / 2f;

        /*
         * Transform فعلی Batch
         */
        oldTransform.set(
            batch.getTransformMatrix()
        );

        /*
         * یک Transform کاملاً جدید بر اساس
         * Transform فعلی Stage می‌سازیم.
         */
        pathTransform.set(oldTransform);

        /*
         * مرکز خط
         */
        pathTransform.translate(
            centerX,
            centerY,
            0
        );

        /*
         * چرخش
         */
        pathTransform.rotate(
            0,
            0,
            1,
            angle
        );

        /*
         * کشیدن Path در راستای X
         */
        pathTransform.scale(
            distance / nativeWidth,
            PATH_THICKNESS,
            1
        );

        batch.setTransformMatrix(pathTransform);

        /*
         * چون PAM در تست قبلی دقیقاً با
         * centerX / centerY وسط Nodeها قرار گرفت،
         * اینجا هم از همان سیستم استفاده می‌کنیم.
         */
        pamPlayer.draw(
            batch,
            pamPath,
            clipName,
            stateTime,
            0,
            0,
            true
        );

        /*
         * برگرداندن Batch
         */
        batch.setTransformMatrix(oldTransform);
    }

    public void setClip(String clipName) {
        this.clipName = clipName;
        this.stateTime = 0f;
    }

    public String getClipName() {
        return clipName;
    }
}
