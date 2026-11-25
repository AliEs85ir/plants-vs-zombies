package app.view.hud;

import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PlantWhatYouGotHud extends NormalHud{
    private ActionButton waveButton;
    private Runnable waveStartCallback;

    @Override
    protected void buildDownBar() {
        Table downBar = new Table();
        buildPlantFoodBank(downBar);
        buildWaveStartButton(downBar);

        rootTable.add(downBar)
            .bottom()
            .center()
            .colspan(3)
            .row();
    }


    private void buildWaveStartButton(Table downBar) {
        ButtonTheme wideTheme = new ButtonTheme().size(500, 60);
        waveButton = new ActionButton(
            "Start zombie wave"
            , "purple",
                wideTheme);
        waveButton.onClick(() -> {
            Runnable callback = waveStartCallback;
            waveStartCallback = null;
            if (callback != null) {
                callback.run();
            }
        });
        downBar.add(waveButton).padRight(200);
    }

    @Override
    public void setWaveStartCallback(Runnable callback) {
        this.waveStartCallback = callback;
    }

    @Override
    public void hideWaveStartButton() {
        if (waveButton == null) return;

        waveButton.setTouchable(Touchable.disabled);
        waveButton.clearListeners();
        waveButton.clearActions();
        waveButton.addAction(
            Actions.sequence(
            Actions.fadeOut(0.25f),
            Actions.removeActor()
        ));
    }
}
