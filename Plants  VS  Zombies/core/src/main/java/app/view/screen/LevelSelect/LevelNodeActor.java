package app.view.screen.LevelSelect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import pvz.libpvz.pam.PamPlayer;

public class LevelNodeActor extends Actor {

    public enum State {
        LOCKED,
        UNLOCKED,
        FINISHED
    }

    private final PamPlayer pamPlayer;
    private final String pamPath;

    private State state;
    private String clipName;
    private float stateTime;

    private final float nativeWidth;
    private final float nativeHeight;

    private final Matrix4 oldTransform = new Matrix4();
    private final Matrix4 nodeTransform = new Matrix4();

    public LevelNodeActor(
        PamPlayer pamPlayer,
        String pamPath,
        State state
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

        setSize(nativeWidth, nativeHeight);
        setOrigin(nativeWidth / 2f, nativeHeight / 2f);
        setTouchable(Touchable.enabled);

        setState(state);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (clipName == null) {
            return;
        }

        float centerX = getX() + getOriginX();
        float centerY = getY() + getOriginY();

        oldTransform.set(batch.getTransformMatrix());
        nodeTransform.set(oldTransform);
        nodeTransform.translate(centerX, centerY, 0);
        nodeTransform.scale(getScaleX(), getScaleY(), 1);

        batch.setTransformMatrix(nodeTransform);

        pamPlayer.draw(
            batch,
            pamPath,
            clipName,
            stateTime,
            0,
            0,
            true
        );

        batch.setTransformMatrix(oldTransform);
    }

    public void setState(State state) {

        this.state = state;
        this.stateTime = 0f;

        switch (state) {

            case LOCKED:
                clipName = "locked_idle";
                break;

            case UNLOCKED:
                clipName = "unlocked";
                break;

            case FINISHED:
                clipName = "finished";
                break;
        }
    }

    public State getState() {
        return state;
    }

    public float getCenterX() {
        return getX() + getOriginX();
    }

    public float getCenterY() {
        return getY() + getOriginY();
    }

    public void setCenter(float x, float y) {

        setPosition(
            x - getOriginX(),
            y - getOriginY()
        );
    }

    public void setOnClick(Runnable action) {

        clearListeners();

        addListener(new ClickListener() {

            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {
                if (action != null) {
                    action.run();
                }
            }
        });
    }
}
