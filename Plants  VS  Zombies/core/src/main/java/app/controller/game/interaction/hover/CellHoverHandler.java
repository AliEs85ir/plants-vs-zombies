package app.controller.game.interaction.hover;

import app.controller.game.interaction.HoverFeedback;
import app.controller.game.interaction.HoverHandler;
import com.badlogic.gdx.graphics.Color;
import game.world.playground.Cell;

public abstract class CellHoverHandler implements HoverHandler {
    private static final Color VALID_COLOR = Color.WHITE.cpy();
    private static final Color INVALID_COLOR = new Color(1f, 0.18f, 0.18f, 1f);

    @Override
    public final HoverFeedback evaluate(Cell cell) {
        if (!isActive() || cell == null) {
            return HoverFeedback.none();
        }
        boolean valid = isValid(cell);
        return HoverFeedback.cell(
            valid,
            validColor(),
            invalidColor(),
            message(cell, valid)
        );
    }

    @Override
    public boolean isActive() {
        return true;
    }

    protected abstract boolean isValid(Cell cell);

    protected abstract String message(Cell cell, boolean valid);

    protected Color validColor() {
        return app.view.hud.config.HudLayoutConfig.GRID_HOVER.validColor;
    }

    protected Color invalidColor() {
        return app.view.hud.config.HudLayoutConfig.GRID_HOVER.invalidColor;
    }
}
