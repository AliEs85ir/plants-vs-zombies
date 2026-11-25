package app.controller.game.interaction;

import game.world.playground.Cell;

public final class IdleHoverHandler implements HoverHandler {
    public static final IdleHoverHandler INSTANCE = new IdleHoverHandler();

    private IdleHoverHandler() {
    }

    @Override
    public HoverFeedback evaluate(Cell cell) {
        return HoverFeedback.none();
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
