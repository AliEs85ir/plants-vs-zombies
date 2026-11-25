package app.view.game.pregame.couch;

import app.enums.plant.PlantName;
import app.view.game.pregame.PlantSelectionPopup;
import app.view.game.pregame.PlantSelectionState;
import app.view.game.pregame.PregameLayout;
import game.audio.AudioId;
import game.audio.AudioManager;

import java.util.List;
import java.util.function.Consumer;

public class CouchPlantSelectionPanel extends PlantSelectionPopup {
    private final Consumer<List<PlantName>> onReady;
    private boolean ready;

    public CouchPlantSelectionPanel(
        PlantSelectionState state,
        PregameLayout layout,
        Consumer<List<PlantName>> onReady
    ) {
        super(state, layout, selected -> {}, false);
        this.onReady = onReady;
    }

    @Override
    protected String confirmLabel() {
        return "Ready";
    }

    @Override
    protected void onConfirm() {
        if (ready || selectionLocked) {
            return;
        }
        String error = state.validateForStart();
        if (error != null) {
            AudioManager.play(AudioId.UI_ERROR);
            setStatus(error, layout.text.statusErrorColor);
            return;
        }
        ready = true;
        setLocked(true);
        setStatus("Waiting for zombie player", layout.text.statusReadyColor);
        if (onReady != null) {
            onReady.accept(state.getSelected());
        }
    }

    @Override
    public void onSelectionChanged(List<PlantName> selected) {
        super.onSelectionChanged(selected);
        if (ready) {
            setStatus("Waiting for zombie player", layout.text.statusReadyColor);
        }
    }

    public List<PlantName> getSelectedPlants() {
        return state.getSelected();
    }
}
