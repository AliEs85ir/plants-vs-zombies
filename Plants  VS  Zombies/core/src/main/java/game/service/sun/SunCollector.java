package game.service.sun;

import app.enums.quest.EventType;
import app.enums.sun.SunState;
import app.enums.sun.SunType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import game.component.physic.PhysicService;
import game.entity.quest.GameEvent;
import game.entity.sun.Sun;
import game.world.GameWorld;
import graphics.GraphicsEngine;
import graphics.general.event.SunVisualEventType;
import graphics.general.event.VisualEvent;

public class SunCollector {
    private final GameWorld gameWorld;

    public SunCollector(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    public Sun collect(Vector2 mouseWorld) {
        for (Sun sun : gameWorld.getSuns()) {
            if (!sun.isCollectable())
                continue;

            Vector2 sunPos = new Vector2(
                (float)sun.getPosition().getX(),
                (float)sun.getPosition().getY()
            );

            if (sunPos.dst(mouseWorld) > 40f)
                continue;

            GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
            if (sun.getType() == SunType.RADIOACTIVE)
            {
                graphicsEngine.getSunInstance(sun).pushEvent(new VisualEvent<>(SunVisualEventType.EXPLODE));
            } else {
                gameWorld.addSunAmount(sun.getValue());
                gameWorld.recordSunCollected(sun.getValue());
            }

            sun.setState(SunState.COLLECTED);

            game.audio.Sfx.sunCollected(sun.getType());

            if (sun.getBody() != null &&
                App.getRealTimeEngine() != null) {
                App.getRealTimeEngine()
                    .getPhysicService()
                    .destroyBody(sun.getBody());

                sun.setBody(null);
            }

            if (sun.getType() != SunType.RADIOACTIVE) graphicsEngine.unregisterSun(sun);
            gameWorld.removeSun(sun);

            if (App.getCurrentUser() != null) App.getCurrentUser().getQuestManager().
                fireEvent(new GameEvent(EventType.SUN_COLLECTED, sun.getValue()));

            return sun;
        }

        return null;
    }
}
