package game.component.physic;

import com.badlogic.gdx.physics.box2d.*;
import game.component.contacts.ContactBits;
import game.world.GameWorld;
import game.world.playground.Grid;

public class BodyFactory {
    private static BodyFactory instance;
    private final World b2world;
    private final Grid grid;


    private BodyFactory(World world, GameWorld gameWorld) {
        this.b2world = world;
        this.grid = gameWorld.getGrid();
    }

    public static void initialize(World b2world, GameWorld gameWorld) {
        instance = new BodyFactory(b2world, gameWorld);
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static BodyFactory getInstance() {
        if (instance == null) {
            throw new IllegalStateException("BodyFactory must be initialized with a World first!");
        }
        return instance;
    }

    public Body createZombieBody(float x, float y, float width, float height) {
        float normX = x / PhysicService.PIXEL_PER_METER;
        float normY = y / PhysicService.PIXEL_PER_METER;
        width = width / PhysicService.PIXEL_PER_METER;
        height = height / PhysicService.PIXEL_PER_METER;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(normX, normY);
        //bodyDef.fixedRotation = true;
        Body body = b2world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        //fixtureDef.friction = 0.0f;
        fixtureDef.filter.categoryBits = ContactBits.ZOMBIE;
        fixtureDef.filter.maskBits = ContactBits.PROJECTILE | ContactBits.PLANT |
            ContactBits.OBSTACLE | ContactBits.LAWNMOWER;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }


    public Body createPlantBody(float x, float y, float width, float height) {
        float normX = x / PhysicService.PIXEL_PER_METER;
        float normY = y / PhysicService.PIXEL_PER_METER;
        width = width / PhysicService.PIXEL_PER_METER;
        height = height / PhysicService.PIXEL_PER_METER;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(normX, normY);

        Body body = b2world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = ContactBits.PLANT;
        fixtureDef.filter.maskBits = ContactBits.ZOMBIE | ContactBits.OBSTACLE;
        //fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }


    public Body createProjectileBody(
        float x,
        float y,
        float radius
    ) {


        float normX =
            x / PhysicService.PIXEL_PER_METER;

        float normY =
            y / PhysicService.PIXEL_PER_METER;

        float normRadius =
            radius / PhysicService.PIXEL_PER_METER;



        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(normX, normY);
        bodyDef.bullet = true;

        Body body = b2world.createBody(bodyDef);



        CircleShape shape = new CircleShape();
        shape.setRadius(normRadius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        fixtureDef.filter.categoryBits = ContactBits.PROJECTILE;
        fixtureDef.filter.maskBits = ContactBits.OBSTACLE | ContactBits.ZOMBIE;

        body.createFixture(fixtureDef);
        shape.dispose();

        System.out.println("type = " + body.getType());
        System.out.println("velocity = " + body.getLinearVelocity());

        return body;
    }

    public Body createLawnmowerBody(float x, float y, float width, float height) {
        float normX = x / PhysicService.PIXEL_PER_METER;
        float normY = y / PhysicService.PIXEL_PER_METER;
        width = width / PhysicService.PIXEL_PER_METER;
        height = height / PhysicService.PIXEL_PER_METER;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(normX, normY);

        Body body = b2world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = ContactBits.LAWNMOWER;
        fixtureDef.filter.maskBits = ContactBits.ZOMBIE;
        //fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }


    public Body createSunBody(float x, float y, float radius) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(
            x / PhysicService.PIXEL_PER_METER,
            y / PhysicService.PIXEL_PER_METER
        );

        Body body = b2world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius / PhysicService.PIXEL_PER_METER);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = shape;
        fixture.isSensor = true;

        body.createFixture(fixture);
        shape.dispose();

        return body;
    }

    public Body createObstacleBody(float x, float y, float width, float height) {
        float normX = x / PhysicService.PIXEL_PER_METER;
        float normY = y / PhysicService.PIXEL_PER_METER;
        width = width / PhysicService.PIXEL_PER_METER;
        height = height / PhysicService.PIXEL_PER_METER;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(normX, normY);

        Body body = b2world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(width, height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }
}
