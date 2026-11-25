package app.view.game.pregame.zombie;

import app.view.game.pregame.PregameLayout;

import app.view.hud.elements.zombiecard.EmptyZombieSlot;
import app.view.hud.elements.zombiecard.SelectedZombieSlot;
import app.view.hud.elements.zombiecard.ZombieCardConfigLoader;
import app.view.ui.ScrollPanes;
import app.view.ui.SkinPanel;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.List;

public class SelectedZombiesPanel extends SkinPanel implements ZombieSelectionListener {
    private final ZombieSelectionState state;
    private final PregameLayout layout;

    private final Label header;
    private final Table slots = new Table();
    private int lastCount = -1;

    public SelectedZombiesPanel(ZombieSelectionState state, PregameLayout layout) {
        super(layout.backgrounds.selectedPanel);
        this.state = state;
        this.layout = layout;

        header = TextSpec.of(layout.text.selectedHeader).build("");

        pad(layout.spacing.innerPadding);
        top();
        add(header).growX().padBottom(layout.spacing.tightPadding).row();

        slots.top();
        ScrollPane scroll = ScrollPanes.vertical(slots, layout.cards.scrollbarWidth);
        add(scroll).grow();

        state.addListener(this);
    }

    @Override
    public void onSelectionChanged(List<String> selected) {
        header.setText(selected.size() + " / " + state.getCapacity());
        rebuild(selected);
    }

    private void rebuild(List<String> selected) {
        slots.clearChildren();

        int capacity = state.getCapacity();
        float slotWidth = slotWidth();
        float scale = resolveScale(slotWidth);
        float slotHeight = referenceHeight() * scale;
        boolean grew = selected.size() > lastCount;

        for (int i = 0; i < capacity; i++) {
            Actor actor;
            if (i < selected.size()) {
                SelectedZombieSlot slot =
                    new SelectedZombieSlot(selected.get(i), scale, state);
                if (grew && i == selected.size() - 1) {
                    animateEntry(slot);
                }
                actor = slot;
            } else {
                actor = new EmptyZombieSlot(slotWidth, slotHeight);
            }

            Container<Actor> cell = new Container<>(actor);
            cell.size(slotWidth, slotHeight);
            slots.add(cell).padBottom(layout.cards.selectedSlotGap).row();
        }

        lastCount = selected.size();
    }

    private void animateEntry(SelectedZombieSlot slot) {
        slot.getColor().a = 0f;
        slot.addAction(Actions.parallel(
            Actions.fadeIn(layout.cards.selectAnimTime),
            Actions.sequence(
                Actions.moveBy(-14f, 0f),
                Actions.moveBy(14f, 0f, layout.cards.selectAnimTime,
                    com.badlogic.gdx.math.Interpolation.swingOut)
            )
        ));
    }

    private float slotWidth() {
        return layout.selection.selectedBarWidth
            - (layout.spacing.innerPadding * 2f)
            - layout.cards.scrollbarWidth;
    }

    private float resolveScale(float slotWidth) {
        float nativeWidth = referenceWidth();
        return (nativeWidth > 0f) ? slotWidth / nativeWidth : 0.4f;
    }

    private ZombieCardConfigLoader.ZombieCardConfig referenceConfig() {
        List<String> allowed = state.getAllowedZombies();
        if (allowed.isEmpty()) return null;
        return ZombieCardConfigLoader.getConfig(allowed.get(0));
    }

    private float referenceWidth() {
        ZombieCardConfigLoader.ZombieCardConfig config = referenceConfig();
        return (config != null && config.cachedBgRegion != null)
            ? config.cachedBgRegion.getRegionWidth() : 0f;
    }

    private float referenceHeight() {
        ZombieCardConfigLoader.ZombieCardConfig config = referenceConfig();
        return (config != null && config.cachedBgRegion != null)
            ? config.cachedBgRegion.getRegionHeight() : 1f;
    }

    public void detach() {
        state.removeListener(this);
    }
}
