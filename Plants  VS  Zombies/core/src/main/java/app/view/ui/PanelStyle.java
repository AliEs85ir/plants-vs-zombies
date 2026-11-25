package app.view.ui;

import com.badlogic.gdx.graphics.Color;

public class PanelStyle {

    public String drawable;
    public String fallback;
    public Color tint;

    public static PanelStyle of(String drawable) {
        return of(drawable, null);
    }

    public static PanelStyle of(String drawable, String fallback) {
        PanelStyle style = new PanelStyle();
        style.drawable = drawable;
        style.fallback = fallback;
        return style;
    }

    public PanelStyle tint(Color tint) {
        this.tint = tint;
        return this;
    }
}
