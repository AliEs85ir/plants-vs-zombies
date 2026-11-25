package app.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class SkinPanel extends Table {

    private Color backgroundTint;
    private boolean customPad;
    private float customPadTop;
    private float customPadLeft;
    private float customPadBottom;
    private float customPadRight;

    public SkinPanel(String backgroundName) {
        this(backgroundName, null, null);
    }

    public SkinPanel(String backgroundName, String fallbackName) {
        this(backgroundName, fallbackName, null);
    }

    public SkinPanel(PanelStyle style) {
        this(style != null ? style.drawable : null,
             style != null ? style.fallback : null,
             style != null ? style.tint : null);
    }

    private SkinPanel(String backgroundName, String fallbackName, Color tint) {
        Drawable bg = (fallbackName != null)
            ? UiTheme.drawableOrFallback(backgroundName, fallbackName)
            : UiTheme.drawable(backgroundName);
        if (bg != null) {
            setBackground(bg);
        }
        this.backgroundTint = tint;
    }

    public SkinPanel tint(Color tint) {
        this.backgroundTint = tint;
        return this;
    }

    public SkinPanel padded(float pad) {
        return padded(pad, pad, pad, pad);
    }

    public SkinPanel padded(float top, float left, float bottom, float right) {
        customPad = true;
        customPadTop = top;
        customPadLeft = left;
        customPadBottom = bottom;
        customPadRight = right;
        pad(top, left, bottom, right);
        return this;
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        if (customPad) {
            pad(customPadTop, customPadLeft, customPadBottom, customPadRight);
        }
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        if (backgroundTint == null) {
            super.drawBackground(batch, parentAlpha, x, y);
            return;
        }
        Drawable background = getBackground();
        if (background == null) {
            return;
        }
        Color self = getColor();
        batch.setColor(self.r * backgroundTint.r,
                       self.g * backgroundTint.g,
                       self.b * backgroundTint.b,
                       self.a * backgroundTint.a * parentAlpha);
        background.draw(batch, x, y, getWidth(), getHeight());
    }
}
