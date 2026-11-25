package app.view.game.states;

import app.view.game.PlayScreen;
import app.view.hud.Hud;
import game.engine.pause.PauseReason;

public class PlantingState implements PlayScreenState{
    @Override
    public void enter(PlayScreen screen) {
        PlayScreenState.super.enter(screen);
        // TODO -> ADD A NOTIFICATION
        bindWaveStartButton(screen);
    }

    @Override
    public void update(PlayScreen screen) {
        PlayScreenState.super.update(screen);
    }

    @Override
    public void exit(PlayScreen screen) {
        PlayScreenState.super.exit(screen);
        screen.getHud().setWaveStartCallback(null);
    }
    private void bindWaveStartButton(PlayScreen screen) {
        Hud hud = screen.getHud();
        hud.setWaveStartCallback(() -> {
            hud.hideWaveStartButton();
            startGame(screen);
        });
    }


}
