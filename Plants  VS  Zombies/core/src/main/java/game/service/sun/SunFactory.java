package game.service.sun;

import app.enums.sun.SunType;
import common.model.Position;
import com.badlogic.gdx.physics.box2d.Body;
import game.component.physic.BodyFactory;
import game.entity.sun.PlantSun;
import game.entity.sun.SkySun;
import game.entity.sun.Sun;

public final class SunFactory {

    private SunFactory() {}

    public static Sun createSkySun(SunType type, Position pos, int value, long tick) {
        Sun sun = new SkySun(type, pos, value, tick);
        attachBody(sun, pos, 60f);
        return sun;
    }

    public static Sun createPlantSun(Position pos, int value) {
        Sun sun = new PlantSun(pos, value);
        attachBody(sun, pos, 60f);
        return sun;
    }

    private static void attachBody(Sun sun, Position pos, float radius) {
        Body body = BodyFactory.getInstance().createSunBody(
            (float) pos.getX(),
            (float) pos.getY(),
            radius
        );
        body.setUserData(sun);
        sun.setBody(body);
    }
}
