package app.view.ui.text;

import com.badlogic.gdx.utils.Align;

public enum TextAlign {

    LEFT(Align.left),
    CENTER(Align.center),
    RIGHT(Align.right),
    TOP_LEFT(Align.topLeft),
    TOP_CENTER(Align.top),
    TOP_RIGHT(Align.topRight);

    private final int gdxAlign;

    TextAlign(int gdxAlign) {
        this.gdxAlign = gdxAlign;
    }

    public int gdx() {
        return gdxAlign;
    }
}
