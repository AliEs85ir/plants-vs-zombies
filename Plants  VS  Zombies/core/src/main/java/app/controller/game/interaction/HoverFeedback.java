package app.controller.game.interaction;

import com.badlogic.gdx.graphics.Color;

public final class HoverFeedback {
    private static final Color DEFAULT_VALID = Color.WHITE.cpy();
    private static final Color DEFAULT_INVALID = new Color(1f, 0.18f, 0.18f, 1f);
    private static final HoverFeedback NONE = new HoverFeedback(
        false,
        false,
        DEFAULT_VALID,
        DEFAULT_INVALID,
        ""
    );

    private final boolean highlight;
    private final boolean valid;
    private final Color validColor;
    private final Color invalidColor;
    private final String message;

    public static HoverFeedback none() {
        return NONE;
    }

    public static HoverFeedback cell(
        boolean valid,
        Color validColor,
        Color invalidColor,
        String message
    ) {
        return new HoverFeedback(
            true,
            valid,
            validColor != null ? validColor.cpy() : DEFAULT_VALID.cpy(),
            invalidColor != null ? invalidColor.cpy() : DEFAULT_INVALID.cpy(),
            message != null ? message : ""
        );
    }

    private HoverFeedback(
        boolean highlight,
        boolean valid,
        Color validColor,
        Color invalidColor,
        String message
    ) {
        this.highlight = highlight;
        this.valid = valid;
        this.validColor = validColor;
        this.invalidColor = invalidColor;
        this.message = message;
    }

    public boolean shouldHighlight() {
        return highlight;
    }

    public boolean isValid() {
        return valid;
    }

    public Color validColor() {
        return validColor;
    }

    public Color invalidColor() {
        return invalidColor;
    }

    public String message() {
        return message;
    }
}
