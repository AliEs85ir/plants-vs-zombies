package app.view.hud.elements;

import app.view.ui.UiTheme;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class NormalTextPopup extends Label {
    private static final float DISPLAY_TIME = 2.2f;
    private static final float ENTER_ANIMATION_TIME = 0.45f;
    private static final float EXIT_ANIMATION_TIME = 0.35f;
    private static final float HORIZONTAL_PADDING = 36f;
    private static final float VERTICAL_PADDING = 14f;
    private static final float BOTTOM_OFFSET = 80f;

    private final float targetY;

    public NormalTextPopup(String message, float worldWidth) {
        super(message, UiTheme.skin(), UiTheme.labelStyleOrDefault(UiTheme.LBL_MULTIPLIER));

        setAlignment(Align.center);
        setWrap(false);

        float width = getPrefWidth() + HORIZONTAL_PADDING;
        float height = getPrefHeight() + VERTICAL_PADDING;
        setSize(width, height);

        targetY = BOTTOM_OFFSET;
        float centerX = (worldWidth - width) / 2f;
        float hiddenY = -height;
        setPosition(centerX, hiddenY);
        getColor().a = 0f;

        addAction(
            Actions.sequence(
                Actions.alpha(0),
                Actions.parallel(
                    Actions.moveTo(centerX, targetY, ENTER_ANIMATION_TIME, Interpolation.pow2Out),
                    Actions.fadeIn(ENTER_ANIMATION_TIME, Interpolation.fade)
                ),
                Actions.delay(DISPLAY_TIME),
                Actions.parallel(
                    Actions.moveTo(centerX, hiddenY, EXIT_ANIMATION_TIME, Interpolation.pow2In),
                    Actions.fadeOut(EXIT_ANIMATION_TIME, Interpolation.fade)
                ),
                Actions.removeActor()
            )
        );
    }

    public float getTargetY() {
        return targetY;
    }
}
