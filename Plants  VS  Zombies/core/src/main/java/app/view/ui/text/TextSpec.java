package app.view.ui.text;

import app.view.ui.UiColor;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TextSpec {

    private String styleName = UiTheme.LBL_DEFAULT;
    private TextAlign align = TextAlign.LEFT;
    private Color color = UiTheme.TEXT_PRIMARY;
    private float fontScale = 1f;
    private boolean wrap = false;

    public static TextSpec of(String styleName) {
        return new TextSpec().style(styleName);
    }

    public static TextSpec of(TextStyle style) {
        if (style == null) {
            return new TextSpec();
        }
        return new TextSpec()
            .style(style.styleName)
            .align(style.align)
            .color(style.color)
            .scale(style.fontScale)
            .wrap(style.wrap);
    }

    public TextSpec style(String styleName) {
        this.styleName = styleName;
        return this;
    }

    public TextSpec align(TextAlign align) {
        this.align = align;
        return this;
    }

    public TextSpec color(Color color) {
        if (color != null) {
            this.color = color.cpy();
        }
        return this;
    }

    public TextSpec color(UiColor color) {
        if (color != null) {
            this.color = color.gdx();
        }
        return this;
    }

    public TextSpec scale(float fontScale) {
        this.fontScale = fontScale;
        return this;
    }

    public TextSpec wrap(boolean wrap) {
        this.wrap = wrap;
        return this;
    }

    public Label build(String text) {
        Label.LabelStyle style = copyStyle(styleName, color);
        Label label = new Label(text == null ? "" : text, style);
        label.setColor(Color.WHITE);
        label.setAlignment(align.gdx());
        label.setWrap(wrap);
        label.setFontScale(Math.max(0.01f, fontScale));
        return label;
    }

    private static Label.LabelStyle copyStyle(String styleName, Color fontColor) {
        Skin skin = UiTheme.skin();
        Label.LabelStyle copy = new Label.LabelStyle();
        if (skin != null) {
            String resolved = UiTheme.labelStyleOrDefault(styleName);
            if (skin.has(resolved, Label.LabelStyle.class)) {
                Label.LabelStyle base = skin.get(resolved, Label.LabelStyle.class);
                copy.font = base.font;
                copy.background = base.background;
            } else if (skin.has("default", Label.LabelStyle.class)) {
                Label.LabelStyle base = skin.get("default", Label.LabelStyle.class);
                copy.font = base.font;
                copy.background = base.background;
            }
        }
        copy.fontColor = fontColor == null ? Color.WHITE.cpy() : fontColor.cpy();
        return copy;
    }

    public TextAlign getAlign() {
        return align;
    }

    public float getFontScale() {
        return fontScale;
    }
}
