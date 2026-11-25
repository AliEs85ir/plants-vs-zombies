package app.view.game.pregame;

import app.enums.plant.PlantName;
import app.service.plant.PlantBoostService;
import app.view.game.pregame.info.PlantInfoCard;
import app.view.hud.elements.NormalTextPopup;
import app.view.hud.elements.plantcard.SelectionPlantCard;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.ModalOverlay;
import app.view.ui.SkinPanel;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import common.Result;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.service.plant.PlantUpgradeService;

import java.util.Arrays;
import java.util.List;

public class PlantSelectionPopup extends ModalOverlay
        implements PlantSelectionListener, SelectionPlantCard.HoverListener {

    protected static final String ID_RANDOM      = "random";
    protected static final String ID_RECOMMENDED = "recommended";
    protected static final String ID_CLEAR       = "clear";
    protected static final String ID_START       = "start";

    public interface Callback {
        void onStartGame(List<PlantName> selectedPlants);
    }

    protected final PlantSelectionState state;
    protected final PregameLayout layout;
    protected final Callback callback;

    protected PlantInfoCard infoCard;
    protected PlantGridPanel gridPanel;
    protected SelectedPlantsPanel selectedPanel;
    protected ButtonRow actions;
    protected Label counterLabel;
    protected Label statusLabel;

    private PlantName hoveredPlant;
    protected boolean selectionLocked;

    public PlantSelectionPopup(PlantSelectionState state, PregameLayout layout,
                               Callback callback) {
        this(state, layout, callback, true);
    }

    protected PlantSelectionPopup(PlantSelectionState state, PregameLayout layout,
                                  Callback callback, boolean modal) {
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

        infoCard = new PlantInfoCard(layout);
        infoCard.setAction(PlantInfoCard.ACTION_BOOST, this::boostCurrentPlant);
        infoCard.setAction(PlantInfoCard.ACTION_UPGRADE, this::upgradeCurrentPlant);
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

        selectedPanel = new SelectedPlantsPanel(state, layout);
        middle.add(selectedPanel).growY()
              .width(layout.selection.selectedBarWidth);

        gridPanel = new PlantGridPanel(state, layout, this);

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
            ButtonSpec.of(ID_RECOMMENDED, "Suggested")
                .style(layout.buttons.tertiaryStyle)
                .action(this::applyRecommended)
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

    private void applyRecommended() {
        String error = state.applyRecommended();
        if (error != null) {
            AudioManager.play(AudioId.UI_ERROR);
            setStatus(error, layout.text.statusErrorColor);
        }
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
            actions.setEnabled(ID_RECOMMENDED, !locked);
            actions.setEnabled(ID_CLEAR, !locked && !state.getSelected().isEmpty());
            actions.setEnabled(confirmId(), !locked && state.isComplete());
            if (locked) {
                actions.setText(confirmId(), "Ready!");
            }
        }
    }

    @Override
    public void onSelectionChanged(List<PlantName> selected) {
        counterLabel.setText(selected.size() + " / " + state.getCapacity());

        if (gridPanel != null) gridPanel.refresh();
        if (actions != null) {
            actions.setEnabled(confirmId(), !selectionLocked && state.isComplete());
            actions.setEnabled(ID_CLEAR, !selectionLocked && !selected.isEmpty());
            actions.setEnabled(ID_RANDOM, !selectionLocked);
            actions.setEnabled(ID_RECOMMENDED, !selectionLocked);
        }

        switch (state.getPhase()) {
            case EMPTY:
                setStatus("Choose your plants", layout.text.statusIdleColor);
                break;
            case PARTIAL:
                setStatus((state.getCapacity() - selected.size()) + " left",
                          layout.text.statusIdleColor);
                break;
            case FULL:
                setStatus("Ready to start", layout.text.statusReadyColor);
                break;
        }

        if (hoveredPlant == null && infoCard.getCurrent() == null && !selected.isEmpty()) {
            infoCard.showPlant(selected.get(selected.size() - 1));
        }
    }

    @Override
    public void onSelectionRejected(PlantName plant, String reason) {
        AudioManager.play(AudioId.UI_ERROR);
        setStatus(reason, layout.text.statusErrorColor);
    }

    @Override
    public void onPlantHovered(PlantName plantName) {
        hoveredPlant = plantName;
        infoCard.showPlant(plantName);
    }

    @Override
    public void onPlantHoverExit(PlantName plantName) {
        if (plantName.equals(hoveredPlant)) {
            hoveredPlant = null;
        }
    }

    private void boostCurrentPlant() {
        PlantName plantName = infoCard.getCurrent();
        Result result = PlantBoostService.tryBoost(plantName);
        if (!result.success()) {
            showError(result.message());
            return;
        }
        refreshAfterPlantChange(plantName);
    }

    private void upgradeCurrentPlant() {
        PlantName plantName = infoCard.getCurrent();
        Result result = PlantUpgradeService.upgrade(plantName);
        if (!result.success()) {
            showError(result.message());
            infoCard.refresh();
            return;
        }
        refreshAfterPlantChange(plantName);
    }

    private void refreshAfterPlantChange(PlantName plantName) {
        refreshBoostAppearances();
        if (gridPanel != null) {
            gridPanel.refresh();
        }
        infoCard.showPlant(plantName);
    }

    private void refreshBoostAppearances() {
        if (gridPanel != null) {
            gridPanel.refreshBoostAppearances();
        }
        if (selectedPanel != null) {
            selectedPanel.refreshBoostAppearances();
        }
    }

    private void showError(String message) {
        AudioManager.play(AudioId.UI_ERROR);
        Stage stage = getStage();
        if (stage == null || message == null || message.isBlank()) {
            return;
        }
        NormalTextPopup popup = new NormalTextPopup(message, stage.getViewport().getWorldWidth());
        stage.addActor(popup);
        popup.toFront();
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
