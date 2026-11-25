package app.view.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import game.component.physic.PhysicService;
import game.entity.Entity;

public class GameCameraController {
    public enum State { INTRO, LOCKED }

    private static final float INTRO_DURATION = 6f;
    private static final float CAMERA_LEFT_OFFSET = -0f;
    private static final float CAMERA_RIGHT_OFFSET = 0f;

    private final OrthographicCamera camera;
    private final GameViewport viewport;
    private final Rectangle playgroundBounds;

    private State state = State.INTRO;
    private float introTime;

    private final Array<Vector3> pendingClicks = new Array<>();
    private final Vector3 shakeOffset = new Vector3();


    public GameCameraController(OrthographicCamera camera, GameViewport viewport, Rectangle playgroundBounds) {
        this.camera = camera;
        this.viewport = viewport;
        this.playgroundBounds = playgroundBounds;
    }

    public void initialize() {
        camera.position.set(getLeftCameraX(), playgroundBounds.y + playgroundBounds.height / 2f, 0);
        camera.update();
    }

    public void setShakeOffset(float x, float y) {
        shakeOffset.set(x, y, 0f);
    }

    public void update(float delta) {
        if (state == State.INTRO) {
            introTime += delta;
            if (introTime >= INTRO_DURATION) {
                state = State.LOCKED;
                introTime = INTRO_DURATION;
            }
        }
        applyCameraPosition();
    }

    public void onResize() {
        applyCameraPosition();
    }

    private void applyCameraPosition() {
        float baseX = state == State.LOCKED ? getLeftCameraX() : getIntroCameraX();
        float baseY = playgroundBounds.y + playgroundBounds.height / 2f;
        camera.position.set(clampCameraX(baseX) + shakeOffset.x, baseY + shakeOffset.y, 0);
        camera.update();
    }

    private float getIntroCameraX() {
        float progress = Math.min(introTime / INTRO_DURATION, 1f);
        float leftX = getLeftCameraX();
        float rightX = getRightCameraX();

        return progress < .5f
            ? Interpolation.sine.apply(leftX, rightX, progress * 2f)
            : Interpolation.sine.apply(rightX, leftX, (progress - .5f) * 2f);
    }

    public float getLeftCameraX() {
        return playgroundBounds.x + viewport.getWorldWidth() / 2f + CAMERA_LEFT_OFFSET;
    }

    public float getRightCameraX() {
        float halfWidth = viewport.getWorldWidth() / 2f;
        return Math.max(
            getLeftCameraX(),
            playgroundBounds.x + playgroundBounds.width - halfWidth + CAMERA_RIGHT_OFFSET
        );
    }

    public void registerClick(float screenX, float screenY) {
        pendingClicks.clear();
        Vector3 clickPosition = new Vector3(screenX, screenY, 0);
        viewport.unproject(clickPosition);
        pendingClicks.add(clickPosition);
    }

    private float clampCameraX(float x) {
        return Math.max(getLeftCameraX(), Math.min(x, getRightCameraX()));
    }

    public boolean isIntroFinished() {
        return state == State.LOCKED;
    }

    public State getState() {
        return state;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public boolean didIGotClickedRectangle(Entity entity, float width, float height) {
        float entityX = (float) entity.getPosition().getX();
        float entityY = (float) entity.getPosition().getY();

        float bottomLeftX = entityX - (width / 2f);
        float bottomLeftY = entityY - (height / 2f);

        Rectangle bounds = new Rectangle(bottomLeftX, bottomLeftY, width, height);


        for (int i = 0; i < pendingClicks.size; i++) {
            Vector3 clickPos = pendingClicks.get(i);

            if (bounds.contains(clickPos.x, clickPos.y)) {
                pendingClicks.removeIndex(i);
                return true;
            }
        }
        return false;
    }

    public void clearPendingClicks() {
        pendingClicks.clear();
    }
}
