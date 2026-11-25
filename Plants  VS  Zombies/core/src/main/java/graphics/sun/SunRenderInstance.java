package graphics.sun;

import com.badlogic.gdx.physics.box2d.*;
import game.component.physic.PhysicService;
import game.entity.sun.Sun;
import graphics.general.event.SunVisualEventType;
import graphics.general.instance.RenderInstance;

public class SunRenderInstance extends RenderInstance<Sun, SunVisualState, SunVisualEventType, SunAnimationVariant> {

    private static final float BASE_RADIUS_PIXELS = 60f;

    private final boolean originallyRadioactive;
    private float lastScale = 1.0f;

    public SunRenderInstance(Sun sun) {
        super(sun, SunVisualState.IDLE);
        this.originallyRadioactive = sun.getType() == app.enums.sun.SunType.RADIOACTIVE;
    }

    public float getScale() {
        int value = getModel().getValue();
        float baseValue = 50f;
        float step = (value - baseValue) / baseValue;
        return (float) Math.pow(1.25, step);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        float currentScale = getScale();
        if (currentScale != lastScale) {
            lastScale = currentScale;
            resizeBody(currentScale);
        }
    }

    private void resizeBody(float scale) {
        Body body = getModel().getBody();
        if (body == null) return;

        for (Fixture fixture : body.getFixtureList()) {
            body.destroyFixture(fixture);
        }

        float radiusMeters = BASE_RADIUS_PIXELS * scale / PhysicService.PIXEL_PER_METER;

        CircleShape shape = new CircleShape();
        shape.setRadius(radiusMeters);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    protected float generateRandomVariationInterval() {
        return Float.MAX_VALUE;
    }
}
