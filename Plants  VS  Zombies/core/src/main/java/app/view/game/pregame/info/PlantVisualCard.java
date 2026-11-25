package app.view.game.pregame.info;

import app.enums.plant.PlantName;
import app.view.game.pregame.PregameLayout;
import app.view.game.pregame.visual.providers.PlantVisualProvider;
import app.view.game.pregame.visual.providers.StaticPlantVisualProvider;
import app.view.ui.SkinPanel;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.component.AssetManager;

public class PlantVisualCard extends SkinPanel {

    private final PregameLayout layout;
    private final Container<Actor> slot = new Container<>();
    private PlantVisualProvider provider;
    private PlantName current;

    public PlantVisualCard(PregameLayout layout) {
        super(layout.backgrounds.visualCard);
        this.layout = layout;
        this.provider = (layout.selection.visualProvider != null)
            ? layout.selection.visualProvider
            : new StaticPlantVisualProvider();

        applyPacketBackground();
        pad(layout.selection.visualCardPadding);
        float inner = innerSize();
        slot.size(inner, inner);
        add(slot).grow();
    }

    private void applyPacketBackground() {
        String key = layout.selection.visualCardBackgroundKey;
        if (key == null || key.isEmpty()) {
            return;
        }
        TextureRegion region = AssetManager.textureBank.region(key);
        if (region != null) {
            setBackground(new TextureRegionDrawable(region));
        }
    }

    private float innerSize() {
        return Math.max(1f,
            layout.selection.visualCardSize - (layout.selection.visualCardPadding * 2f));
    }

    public void setProvider(PlantVisualProvider provider) {
        if (provider == null) return;
        this.provider = provider;
        if (current != null) {
            show(current);
        }
    }

    public PlantVisualProvider getProvider() {
        return provider;
    }

    public void show(PlantName plantName) {
        current = plantName;
        if (plantName == null) {
            slot.setActor(null);
            return;
        }
        float inner = innerSize();
        slot.setActor(provider.createVisual(plantName, inner, inner));
    }

    public void clear() {
        current = null;
        slot.setActor(null);
    }

    @Override
    public float getPrefWidth() {
        return layout.selection.visualCardSize;
    }

    @Override
    public float getPrefHeight() {
        return layout.selection.visualCardSize;
    }

    @Override
    public float getMinWidth() {
        return layout.selection.visualCardSize;
    }

    @Override
    public float getMinHeight() {
        return layout.selection.visualCardSize;
    }
}
