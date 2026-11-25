package app.view.ui.button;

import app.view.ui.UiTheme;

public class ButtonSpec {

    private final String id;
    private String text;
    private String styleName = UiTheme.BTN_DEFAULT;
    private Runnable action = () -> {};
    private boolean expand = true;
    private float weight = 1f;

    private ButtonSpec(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public static ButtonSpec of(String id, String text) {
        return new ButtonSpec(id, text);
    }

    public ButtonSpec style(String styleName) {
        this.styleName = styleName;
        return this;
    }

    public ButtonSpec action(Runnable action) {
        this.action = (action != null) ? action : () -> {};
        return this;
    }

    public ButtonSpec weight(float weight) {
        this.weight = weight;
        return this;
    }

    public ButtonSpec fixedWidth() {
        this.expand = false;
        return this;
    }

    public ButtonSpec text(String text) {
        this.text = text;
        return this;
    }

    public String getId()        { return id; }
    public String getText()      { return text; }
    public String getStyleName() { return styleName; }
    public Runnable getAction()  { return action; }
    public boolean isExpand()    { return expand; }
    public float getWeight()     { return weight; }
}
