package app.view.ui.button;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ButtonRow extends Table {

    private final Map<String, ActionButton> buttons = new LinkedHashMap<>();
    private final ButtonTheme theme;

    public ButtonRow(List<ButtonSpec> specs, ButtonTheme theme) {
        this.theme = (theme != null) ? theme : ButtonTheme.DEFAULT;
        rebuild(specs);
    }

    public void rebuild(List<ButtonSpec> specs) {
        clearChildren();
        buttons.clear();
        if (specs == null) return;

        boolean first = true;
        for (ButtonSpec spec : specs) {
            ActionButton button = new ActionButton(spec.getText(), spec.getStyleName(), theme);
            button.onClick(spec.getAction());
            buttons.put(spec.getId(), button);

            Cell<ActionButton> cell = add(button).minWidth(theme.minWidth);
            if (theme.width > 0f) {
                cell.width(theme.width);
            } else if (spec.isExpand()) {
                cell.expandX().fillX();
            }
            if (theme.height > 0f) {
                cell.height(theme.height);
            }
            if (!first) {
                cell.padLeft(theme.gap);
            }
            first = false;
        }
    }

    public ActionButton get(String id) {
        return buttons.get(id);
    }

    public void setEnabled(String id, boolean enabled) {
        ActionButton button = buttons.get(id);
        if (button != null) {
            button.setEnabledState(enabled);
        }
    }

    public void setText(String id, String text) {
        ActionButton button = buttons.get(id);
        if (button != null) {
            button.setText(text);
        }
    }
}
