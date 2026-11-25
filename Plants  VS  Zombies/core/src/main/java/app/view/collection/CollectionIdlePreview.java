package app.view.collection;

import app.enums.plant.PlantName;
import app.view.collection.config.CollectionConfig;
import app.view.game.pregame.visual.providers.IdlePlantVisualProvider;
import app.view.game.pregame.zombie.visual.providers.IdleZombieVisualProvider;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import game.component.AssetManager;

public final class CollectionIdlePreview extends Table {
    private final CollectionConfig config;
    private final Container<Actor> slot = new Container<>();
    private final Image lockImage = new Image();
    private final IdlePlantVisualProvider plantProvider = new IdlePlantVisualProvider();
    private final IdleZombieVisualProvider zombieProvider = new IdleZombieVisualProvider();

    public CollectionIdlePreview(CollectionConfig config) {
        this.config = config;
        lockImage.setScaling(Scaling.fit);
        lockImage.setVisible(false);
        Stack stack = new Stack();
        stack.add(slot);
        stack.add(lockImage);
        pad(config.details.previewPad);
        add(stack).grow();
    }

    public void showPlant(PlantName plantName, boolean unlocked, String backgroundId) {
        applyBackground(backgroundId);
        float innerW = innerWidth();
        float innerH = innerHeight();
        slot.size(innerW, innerH);
        slot.setActor(plantName == null ? null : plantProvider.createVisual(plantName, innerW, innerH));
        applyLocked(!unlocked);
        getColor().a = unlocked ? 1f : config.details.lockedPreviewAlpha;
    }

    public void showZombie(String alias, boolean seen, String backgroundId) {
        applyBackground(backgroundId);
        float innerW = innerWidth();
        float innerH = innerHeight();
        slot.size(innerW, innerH);
        if (seen && alias != null) {
            slot.setActor(zombieProvider.createVisual(alias, innerW, innerH));
        } else {
            slot.setActor(null);
        }
        applyLocked(!seen);
        getColor().a = seen ? 1f : config.details.lockedPreviewAlpha;
    }

    public void clearPreview() {
        slot.setActor(null);
        lockImage.setVisible(false);
        setBackground((Drawable) null);
    }

    private void applyBackground(String backgroundId) {
        TextureRegion region = region(backgroundId);
        if (region == null) {
            region = region(config.defaultDetailsBackground);
        }
        if (region == null) {
            setBackground((Drawable) null);
            return;
        }
        setBackground(new TextureRegionDrawable(region));
    }

    private void applyLocked(boolean locked) {
        TextureRegion lock = region(config.lockIconId);
        if (!locked || lock == null) {
            lockImage.setVisible(false);
            lockImage.setDrawable(null);
            return;
        }
        lockImage.setVisible(true);
        lockImage.setDrawable(new TextureRegionDrawable(lock));
    }

    private float innerWidth() {
        return Math.max(1f, config.details.previewWidth - config.details.previewPad * 2f);
    }

    private float innerHeight() {
        return Math.max(1f, config.details.previewHeight - config.details.previewPad * 2f);
    }

    private static TextureRegion region(String id) {
        if (id == null || id.isBlank() || AssetManager.textureBank == null) {
            return null;
        }
        return AssetManager.textureBank.region(id);
    }
}
