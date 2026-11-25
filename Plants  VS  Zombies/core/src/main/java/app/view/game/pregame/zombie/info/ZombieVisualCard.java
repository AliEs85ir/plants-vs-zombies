package app.view.game.pregame.zombie.info;


import app.view.game.pregame.PregameLayout;
import app.view.game.pregame.visual.providers.PlantVisualProvider;
import app.view.game.pregame.visual.providers.StaticPlantVisualProvider;
import app.view.game.pregame.zombie.visual.providers.IdleZombieVisualProvider;
import app.view.game.pregame.zombie.visual.providers.StaticZombieVisualProvider;
import app.view.game.pregame.zombie.visual.providers.ZombieVisualProvider;
import app.view.ui.SkinPanel;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.component.AssetManager;

public class ZombieVisualCard extends SkinPanel {

    private final PregameLayout layout;
    private final Container<Actor> slot = new Container<>();
    private ZombieVisualProvider provider;
    private String current;

    public ZombieVisualCard(PregameLayout layout) {
        super(layout.backgrounds.visualCard);
        this.layout = layout;
        this.provider = new IdleZombieVisualProvider();

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

    public void setProvider(ZombieVisualProvider provider) {
        if (provider == null) return;
        this.provider = provider;
        if (current != null) {
            show(current);
        }
    }

    public ZombieVisualProvider getProvider() {
        return provider;
    }

    public void show(String alias) {
        current = alias;
        if (alias == null) {
            slot.setActor(null);
            return;
        }
        float inner = innerSize();
        slot.setActor(provider.createVisual(alias, inner, inner));
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
