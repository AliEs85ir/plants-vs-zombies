package app.view.cursor;

import app.enums.plant.PlantName;
import app.view.game.pregame.visual.providers.PlantVisualProvider;
import app.view.game.pregame.zombie.visual.providers.ZombieVisualProvider;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.Objects;

public final class PlacementCursorPreview {
    private static final float DEFAULT_SIZE = 80f;

    private final PlantVisualProvider plantVisuals = PlantVisualProvider.idleAnimation();
    private final ZombieVisualProvider zombieVisuals = ZombieVisualProvider.idleAnimation();

    private Actor actor;
    private PlantName plant;
    private String zombie;

    public void showPlant(PlantName plantName, float width, float height) {
        if (plantName == null) {
            clear();
            return;
        }
        if (plantName == plant && actor != null) {
            return;
        }
        clear();
        plant = plantName;
        actor = plantVisuals.createVisual(plantName, size(width), size(height));
    }

    public void showZombie(String alias, float width, float height) {
        if (alias == null || alias.isBlank()) {
            clear();
            return;
        }
        if (Objects.equals(zombie, alias) && actor != null) {
            return;
        }
        clear();
        zombie = alias;
        actor = zombieVisuals.createVisual(alias, size(width), size(height));
    }

    public void clear() {
        plant = null;
        zombie = null;
        actor = null;
    }

    public void act(float delta) {
        if (actor != null) {
            actor.act(delta);
        }
    }

    public Actor actor() {
        return actor;
    }

    private static float size(float value) {
        return value > 1f ? value : DEFAULT_SIZE;
    }
}
