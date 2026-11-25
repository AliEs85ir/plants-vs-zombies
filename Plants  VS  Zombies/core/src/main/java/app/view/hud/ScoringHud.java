package app.view.hud;

import app.view.hud.elements.MioPointBoard;
import app.view.hud.elements.ProgressZombieBar;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.engine.RealTimeGameEngine;

public class ScoringHud extends NormalHud {
    private MioPointBoard mioPointBoard;

    @Override
    public void initialize(RealTimeGameEngine engine) {
        super.initialize(engine);
        attachMioPointBoard();
    }

    @Override
    protected void buildProgressBar() {
        Table spacer = new Table();
        spacer.setTouchable(Touchable.disabled);
        rootTable.add(spacer)
            .size(ProgressZombieBar.WIDTH, ProgressZombieBar.HEIGHT)
            .pad(5f)
            .colspan(2)
            .center()
            .row();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        positionMioPointBoard();
    }

    @Override
    public void updateProgressBar(float progress) {
    }

    @Override
    public void updateMioPointDisplay(int points) {
        if (mioPointBoard != null) {
            mioPointBoard.setPoints(points, points > 0);
        }
    }

    @Override
    public HudType getType() {
        return HudType.SCORING;
    }

    @Override
    public void dispose() {
        if (mioPointBoard != null) {
            mioPointBoard.remove();
            mioPointBoard.dispose();
            mioPointBoard = null;
        }
        super.dispose();
    }

    private void attachMioPointBoard() {
        if (stage == null) {
            return;
        }
        mioPointBoard = new MioPointBoard();
        mioPointBoard.setSize(MioPointBoard.WIDTH, MioPointBoard.HEIGHT);
        mioPointBoard.setTouchable(Touchable.disabled);
        positionMioPointBoard();
        stage.addActor(mioPointBoard);
    }

    private void positionMioPointBoard() {
        if (mioPointBoard == null) {
            return;
        }
        float x = (HUD_WIDTH - MioPointBoard.WIDTH) / 2f;
        float y = HUD_HEIGHT - TOP_BAR_HEIGHT - MioPointBoard.HEIGHT - 6f;
        mioPointBoard.setPosition(x, y);
    }
}
