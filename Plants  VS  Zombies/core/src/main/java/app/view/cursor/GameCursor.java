package app.view.cursor;

import app.enums.cursor.CursorType;
import app.enums.plant.PlantName;
import com.badlogic.gdx.scenes.scene2d.Actor;

public final class GameCursor {
    private CursorType type = CursorType.DEFAULT;
    private final CursorScale scale = new CursorScale();
    private final PlacementCursorPreview placementPreview = new PlacementCursorPreview();

    public void setType(CursorType type) {
        this.type = type != null ? type : CursorType.DEFAULT;
    }

    public CursorType type() {
        return type;
    }

    public CursorScale scale() {
        return scale;
    }

    public void syncPlacement(PlantName plant, String zombieAlias, float width, float height) {
        if (plant != null) {
            placementPreview.showPlant(plant, width, height);
            return;
        }
        if (zombieAlias != null && !zombieAlias.isBlank()) {
            placementPreview.showZombie(zombieAlias, width, height);
            return;
        }
        placementPreview.clear();
    }

    public void clearPlacement() {
        placementPreview.clear();
    }

    public void act(float delta) {
        placementPreview.act(delta);
    }

    public Actor placementActor() {
        return placementPreview.actor();
    }
}
