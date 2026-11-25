package app.view.auth;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

final class AuthSecondaryLink extends Table {
    private final Label textLabel;
    private final Color promptColor;
    private final Color actionColor;
    private final Color hoverColor;
    private String prompt = "";
    private String actionText = "";
    private boolean hovered;

    AuthSecondaryLink(AuthMenuConfig.SecondaryTextsConfig config) {
        left().top();
        setTouchable(Touchable.disabled);
        promptColor = AuthMenuConfig.color(config.promptColor, Color.valueOf("E4DCC4"));
        actionColor = AuthMenuConfig.color(config.actionColor, Color.valueOf("F4C95F"));
        hoverColor = AuthMenuConfig.color(config.actionHoverColor, Color.valueOf("FFE08A"));
        BitmapFont font = AuthUiFonts.resolve(config.promptFont, "BRIANNETOD");
        font.getData().markupEnabled = true;
        textLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));
        textLabel.setFontScale(config.promptFontSize);
        textLabel.setAlignment(Align.topLeft);
        textLabel.setWrap(true);
        textLabel.setTouchable(Touchable.disabled);
        add(textLabel).grow().fill().left().top();
    }

    void setTexts(String prompt, String actionText) {
        this.prompt = prompt == null ? "" : prompt;
        this.actionText = actionText == null ? "" : actionText;
        refreshText();
    }

    void setHovered(boolean hovered) {
        if (this.hovered == hovered) {
            return;
        }
        this.hovered = hovered;
        refreshText();
    }

    @Override
    public void layout() {
        float width = Math.max(1f, getWidth());
        textLabel.setWrap(width > 24f);
        textLabel.setWidth(width);
        super.layout();
        textLabel.setWidth(width);
        textLabel.invalidate();
        textLabel.validate();
    }

    @Override
    public float getPrefWidth() {
        return 0f;
    }

    @Override
    public float getPrefHeight() {
        float width = getWidth();
        if (width <= 1f && getParent() != null) {
            width = Math.max(1f, getParent().getWidth());
        }
        if (width > 1f) {
            textLabel.setWrap(true);
            textLabel.setWidth(width);
        }
        return Math.max(super.getPrefHeight(), textLabel.getPrefHeight());
    }

    private void refreshText() {
        Color action = hovered ? hoverColor : actionColor;
        textLabel.setText(
            "[#" + promptColor.toString() + "]" + prompt
                + "[#" + action.toString() + "]" + actionText
        );
        textLabel.invalidateHierarchy();
    }
}
