package app.view.game.pregame.zombie;

import app.enums.plant.PlantName;
import app.view.game.pregame.*;
import app.view.game.pregame.info.PlantInfoCard;
import app.view.game.pregame.zombie.info.ZombieInfoCard;
import app.view.hud.elements.zombiecard.SelectionZombieCard;
import app.view.ui.ModalOverlay;
import app.view.ui.SkinPanel;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.audio.AudioId;
import game.audio.AudioManager;

import java.util.Arrays;
import java.util.List;

public class ZombieSelectionPopup extends ModalOverlay
    implements ZombieSelectionListener, SelectionZombieCard.HoverListener {


    protected static final String ID_RANDOM      = "random";
    protected static final String ID_CLEAR       = "clear";
    protected static final String ID_START       = "start";

    public interface Callback {
        void onStartGame(List<String> selectedZombies);
    }

    protected final ZombieSelectionState state;
    protected final PregameLayout layout;
    protected final Callback callback;

    protected ZombieInfoCard infoCard;
    protected ZombieGridPanel gridPanel;
    protected SelectedZombiesPanel selectedPanel;
    protected ButtonRow actions;
    protected Label counterLabel;
    protected Label statusLabel;

    private String hoveredZombie;
    protected boolean selectionLocked;

    public ZombieSelectionPopup(ZombieSelectionState state, PregameLayout layout,
                               ZombieSelectionPopup.Callback callback) {
        this(state, layout, callback, true);
    }

    protected ZombieSelectionPopup(ZombieSelectionState state, PregameLayout layout,
                                   ZombieSelectionPopup.Callback callback, boolean modal) {
        super(modal);
        this.state = state;
        this.layout = layout;
        this.callback = callback;
        build();
        state.addListener(this);
    }

    private void build() {
        SkinPanel root = new SkinPanel(layout.backgrounds.popupRoot);
        root.pad(layout.spacing.contentPadding);

        Table top = buildTop();
        top.setClip(true);
        root.add(top).growX()
            .height(layout.selection.topSectionHeight).row();

        Table middle = buildMiddle();
        middle.setClip(true);
        root.add(middle).grow()
            .padTop(layout.selection.topToMiddleGap).row();

        Table footer = buildFooter();
        root.add(footer).growX()
            .height(layout.selection.footerHeight)
            .padTop(layout.selection.middleToFooterGap).row();

        add(root).size(layout.selection.width, layout.selection.height);
    }

    private Table buildTop() {
        Table top = new Table();
        top.top().left();

        infoCard = new ZombieInfoCard(layout);
        top.add(infoCard).grow()
            .height(layout.selection.topSectionHeight);

        top.add(buildCounterBadge()).growY()
            .width(layout.selection.counterWidth)
            .padLeft(layout.selection.counterGap);
        return top;
    }

    private Table buildCounterBadge() {
        SkinPanel panel = new SkinPanel(layout.backgrounds.counterBadge);
        panel.pad(layout.spacing.innerPadding);
        panel.top();

        counterLabel = TextSpec.of(layout.text.counter).build("");
        statusLabel  = TextSpec.of(layout.text.status).build("");

        panel.add(counterLabel).growX().row();
        panel.add(statusLabel).growX()
            .padTop(layout.spacing.tightPadding).row();
        return panel;
    }

    private Table buildMiddle() {
        Table middle = new Table();
        middle.top().left();

        selectedPanel = new SelectedZombiesPanel(state, layout);
        middle.add(selectedPanel).growY()
            .width(layout.selection.selectedBarWidth);

        gridPanel = new ZombieGridPanel(state, layout, this);

        SkinPanel gridFrame = new SkinPanel(layout.backgrounds.gridPanel);
        gridFrame.pad(layout.spacing.innerPadding);
        gridFrame.add(gridPanel).grow();

        middle.add(gridFrame).grow().padLeft(layout.selection.selectedBarGap);
        return middle;
    }

    private Table buildFooter() {
        List<ButtonSpec> specs = Arrays.asList(
            ButtonSpec.of(ID_RANDOM, "Random")
                .style(layout.buttons.secondaryStyle)
                .action(state::applyRandom)
                .fixedWidth(),
            ButtonSpec.of(ID_CLEAR, "Clear")
                .style(layout.buttons.secondaryStyle)
                .action(state::clear)
                .fixedWidth(),
            ButtonSpec.of(confirmId(), confirmLabel())
                .style(layout.buttons.primaryStyle)
                .action(this::onConfirm)
                .fixedWidth()
        );

        actions = new ButtonRow(specs, layout.buttons.selectionFooter);
        return actions;
    }


    protected String confirmId() {
        return ID_START;
    }

    protected String confirmLabel() {
        return "Start Game";
    }

    protected void onConfirm() {
        if (selectionLocked) {
            return;
        }
        String error = state.validateForStart();
        if (error != null) {
            AudioManager.play(AudioId.UI_ERROR);
            setStatus(error, layout.text.statusErrorColor);
            return;
        }
        detach();
        callback.onStartGame(state.getSelected());
    }

    public void setLocked(boolean locked) {
        this.selectionLocked = locked;
        Touchable touchable = locked ? Touchable.disabled : Touchable.enabled;
        if (gridPanel != null) {
            gridPanel.setTouchable(touchable);
        }
        if (selectedPanel != null) {
            selectedPanel.setTouchable(touchable);
        }
        if (actions != null) {
            actions.setEnabled(ID_RANDOM, !locked);
            actions.setEnabled(ID_CLEAR, !locked && !state.getSelected().isEmpty());
            actions.setEnabled(confirmId(), !locked && state.isComplete());
            if (locked) {
                actions.setText(confirmId(), "Ready!");
            }
        }
    }

    @Override
    public void onSelectionChanged(List<String> selected) {
        counterLabel.setText(selected.size() + " / " + state.getCapacity());

        if (gridPanel != null) gridPanel.refresh();
        if (actions != null) {
            actions.setEnabled(confirmId(), !selectionLocked && state.isComplete());
            actions.setEnabled(ID_CLEAR, !selectionLocked && !selected.isEmpty());
            actions.setEnabled(ID_RANDOM, !selectionLocked);
        }

        switch (state.getPhase()) {
            case EMPTY:
                setStatus("Choose your Zombies", layout.text.statusIdleColor);
                break;
            case PARTIAL:
                setStatus((state.getCapacity() - selected.size()) + " left",
                    layout.text.statusIdleColor);
                break;
            case FULL:
                setStatus("Ready to start", layout.text.statusReadyColor);
                break;
        }

        if (hoveredZombie == null && infoCard.getCurrent() == null && !selected.isEmpty()) {
            infoCard.showZombie(selected.get(selected.size() - 1));
        }
    }

    @Override
    public void onSelectionRejected(String plant, String reason) {
        AudioManager.play(AudioId.UI_ERROR);
        setStatus(reason, layout.text.statusErrorColor);
    }

    @Override
    public void onZombieHovered(String plantName) {
        hoveredZombie = plantName;
        infoCard.showZombie(plantName);
    }

    @Override
    public void onZombieHoverExit(String plantName) {
        if (plantName.equals(hoveredZombie)) {
            hoveredZombie = null;
        }
    }

    protected void setStatus(String text, Color color) {
        if (statusLabel == null) return;
        statusLabel.setText(text);
        statusLabel.setColor(color);
    }

    public void detach() {
        state.removeListener(this);
        if (selectedPanel != null) selectedPanel.detach();
    }
}
