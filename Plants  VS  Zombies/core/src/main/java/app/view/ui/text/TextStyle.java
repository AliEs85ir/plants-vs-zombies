package app.view.ui.text;

import app.view.ui.UiColor;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;

public class TextStyle {

    public String styleName = UiTheme.LBL_DEFAULT;
    public TextAlign align  = TextAlign.LEFT;
    public Color color      = UiTheme.TEXT_PRIMARY;
    public float fontScale  = 1f;
    public boolean wrap     = false;

    public static TextStyle of(String styleName) {
        TextStyle style = new TextStyle();
        style.styleName = styleName;
        return style;
    }

    public TextStyle align(TextAlign align) {
        this.align = align;
        return this;
    }

    public TextStyle color(Color color) {
        if (color != null) {
            this.color = color.cpy();
        }
        return this;
    }

    public TextStyle color(UiColor color) {
        if (color != null) {
            this.color = color.gdx();
        }
        return this;
    }

    public TextStyle scale(float fontScale) {
        this.fontScale = fontScale;
        return this;
    }

    public TextStyle wrap(boolean wrap) {
        this.wrap = wrap;
        return this;
    }
}
