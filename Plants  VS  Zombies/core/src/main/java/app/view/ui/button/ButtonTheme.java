package app.view.ui.button;

import com.badlogic.gdx.graphics.Color;

public class ButtonTheme {

    public static final ButtonTheme DEFAULT = new ButtonTheme();

    public float width      = 0f;
    public float height     = 0f;
    public float minWidth   = 74f;
    public float gap        = 10f;
    public float padX       = 0f;
    public float padY       = 0f;
    public float labelScale = 1f;

    public Color textColor     = new Color(1f, 1f, 1f, 1f);
    public Color normalColor   = new Color(1f, 1f, 1f, 1f);
    public Color hoverColor    = new Color(1.15f, 1.15f, 1.05f, 1f);
    public Color pressedColor  = new Color(0.86f, 0.86f, 0.86f, 1f);
    public Color disabledColor = new Color(0.55f, 0.55f, 0.55f, 0.8f);

    public float hoverScale    = 1.045f;
    public float hoverDuration = 0.09f;

    public ButtonTheme size(float width, float height) {
        this.width  = width;
        this.height = height;
        return this;
    }

    public ButtonTheme spacing(float minWidth, float gap) {
        this.minWidth = minWidth;
        this.gap      = gap;
        return this;
    }

    public ButtonTheme padding(float padX, float padY) {
        this.padX = padX;
        this.padY = padY;
        return this;
    }

    public ButtonTheme label(float labelScale) {
        this.labelScale = labelScale;
        return this;
    }

    public ButtonTheme text(Color textColor) {
        this.textColor = textColor;
        return this;
    }
}
