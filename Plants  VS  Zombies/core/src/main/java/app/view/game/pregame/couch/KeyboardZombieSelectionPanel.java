package app.view.game.pregame.couch;

import app.view.game.pregame.PregameLayout;
import app.view.game.pregame.zombie.ZombieSelectionPopup;
import app.view.game.pregame.zombie.ZombieSelectionState;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import game.audio.AudioId;
import game.audio.AudioManager;

import java.util.List;
import java.util.function.Consumer;

public class KeyboardZombieSelectionPanel extends ZombieSelectionPopup {
    private final Consumer<List<String>> onReady;
    private boolean ready;
    private int focusIndex;

    public KeyboardZombieSelectionPanel(
        ZombieSelectionState state,
        PregameLayout layout,
        Consumer<List<String>> onReady
    ) {
        super(state, layout, selected -> {}, false);
        this.onReady = onReady;
        setTouchable(Touchable.disabled);
        if (gridPanel != null) {
            gridPanel.setKeyboardFocusIndex(0);
        }
        setStatus("Arrows move  Enter select  Wheel/PgUp-PgDn scroll  G random  C clear  R ready",
            layout.text.statusIdleColor);
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
        setStatus("Waiting for plant player", layout.text.statusReadyColor);
        if (onReady != null) {
            onReady.accept(state.getSelected());
        }
    }

    public boolean handleKey(int keycode) {
        if (ready || selectionLocked) {
            return keycode == Input.Keys.R || keycode == Input.Keys.ENTER;
        }
        return switch (keycode) {
            case Input.Keys.UP, Input.Keys.W -> moveFocus(0, -1);
            case Input.Keys.DOWN, Input.Keys.S -> moveFocus(0, 1);
            case Input.Keys.LEFT, Input.Keys.A -> moveFocus(-1, 0);
            case Input.Keys.RIGHT, Input.Keys.D -> moveFocus(1, 0);
            case Input.Keys.ENTER, Input.Keys.SPACE -> toggleFocused();
            case Input.Keys.G -> {
                state.applyRandom();
                yield true;
            }
            case Input.Keys.C -> {
                state.clear();
                yield true;
            }
            case Input.Keys.R -> {
                onConfirm();
                yield true;
            }
            case Input.Keys.PAGE_UP -> moveFocus(0, -pageRows());
            case Input.Keys.PAGE_DOWN -> moveFocus(0, pageRows());
            default -> false;
        };
    }

    private boolean moveFocus(int columnDelta, int rowDelta) {
        List<String> zombies = state.getAllowedZombies();
        if (zombies.isEmpty()) {
            return true;
        }
        int columns = Math.max(1, layout.cards.gridColumns);
        int count = zombies.size();
        int column = focusIndex % columns;
        int row = focusIndex / columns;
        column = Math.max(0, Math.min(columns - 1, column + columnDelta));
        row = Math.max(0, row + rowDelta);
        int next = Math.min(count - 1, row * columns + column);
        focusIndex = Math.max(0, next);
        if (gridPanel != null) {
            gridPanel.setKeyboardFocusIndex(focusIndex);
        }
        return true;
    }

    public boolean scrollGrid(float amountY) {
        if (ready || selectionLocked || gridPanel == null) {
            return false;
        }
        gridPanel.scrollBy(amountY);
        return true;
    }

    private int pageRows() {
        return 3;
    }

    private boolean toggleFocused() {
        List<String> zombies = state.getAllowedZombies();
        if (focusIndex < 0 || focusIndex >= zombies.size()) {
            return true;
        }
        state.toggle(zombies.get(focusIndex));
        return true;
    }

    @Override
    public void onSelectionChanged(List<String> selected) {
        super.onSelectionChanged(selected);
        if (ready) {
            setStatus("Waiting for plant player", layout.text.statusReadyColor);
            return;
        }
        if (state.isComplete()) {
            setStatus("R to ready   Arrows move   Enter select", layout.text.statusReadyColor);
        } else {
            setStatus("Arrows move  Enter select  Wheel/PgUp-PgDn scroll  G random  C clear  R ready",
                layout.text.statusIdleColor);
        }
    }

    public List<String> getSelectedZombies() {
        return state.getSelected();
    }
}
