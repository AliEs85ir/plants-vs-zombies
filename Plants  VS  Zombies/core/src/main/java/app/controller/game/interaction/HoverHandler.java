package app.controller.game.interaction;

import app.controller.game.EventResult;
import game.world.playground.Cell;

public interface HoverHandler {
    HoverFeedback evaluate(Cell cell);

    boolean isActive();

    default EventResult execute(Cell cell) {
        return null;
    }

    default boolean endsAfterSuccess() {
        return true;
    }

    default boolean endsAfterFailure() {
        return true;
    }
}
