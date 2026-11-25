package app.view.game.pregame.couch;

import app.enums.plant.PlantName;
import app.view.game.pregame.PlantSelectionState;
import app.view.game.pregame.PregameLayout;
import app.view.game.pregame.zombie.ZombieSelectionState;
import app.view.ui.ModalOverlay;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.List;

public class CouchDualSelectionOverlay extends ModalOverlay {
    public interface Callback {
        void onBothReady(List<PlantName> plants, List<String> zombies);
    }

    private final CouchPlantSelectionPanel plantPanel;
    private final KeyboardZombieSelectionPanel zombiePanel;
    private final Callback callback;
    private boolean plantReady;
    private boolean zombieReady;
    private boolean started;

    public CouchDualSelectionOverlay(
        PlantSelectionState plantState,
        ZombieSelectionState zombieState,
        PregameLayout plantLayout,
        PregameLayout zombieLayout,
        Callback callback
    ) {
        this.callback = callback;
        this.plantPanel = new CouchPlantSelectionPanel(plantState, plantLayout, this::onPlantReady);
        this.zombiePanel = new KeyboardZombieSelectionPanel(zombieState, zombieLayout, this::onZombieReady);

        Table content = new Table();
        content.defaults().pad(8f);
        content.add(plantPanel)
            .size(plantLayout.selection.width, plantLayout.selection.height)
            .align(Align.left);
        content.add(zombiePanel)
            .size(zombieLayout.selection.width, zombieLayout.selection.height)
            .align(Align.right);
        add(content).expand().center();

        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return zombiePanel.handleKey(keycode);
            }

            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                return zombiePanel.scrollGrid(amountY);
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getStage() != null && getStage().getKeyboardFocus() != this) {
            getStage().setKeyboardFocus(this);
        }
    }

    private void onPlantReady(List<PlantName> selected) {
        plantReady = true;
        tryStart();
    }

    private void onZombieReady(List<String> selected) {
        zombieReady = true;
        tryStart();
    }

    private void tryStart() {
        if (started || !plantReady || !zombieReady || callback == null) {
            return;
        }
        started = true;
        callback.onBothReady(plantPanel.getSelectedPlants(), zombiePanel.getSelectedZombies());
    }

    public KeyboardZombieSelectionPanel getZombiePanel() {
        return zombiePanel;
    }
}
