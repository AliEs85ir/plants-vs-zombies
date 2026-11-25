package app.view.greenhouse;

import app.view.greenhouse.config.GreenhouseConfig;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;

public final class GreenhouseMessageToast extends WidgetGroup {
    private final GreenhouseConfig.MessagesOverlayConfig config;
    private final Label label;
    private final Color errorColor;
    private final Color successColor;
    private final Label.LabelStyle errorStyle;
    private final Label.LabelStyle rewardStyle;

    public GreenhouseMessageToast(GreenhouseConfig.MessagesOverlayConfig config, BitmapFont font) {
        this.config = config == null ? new GreenhouseConfig.MessagesOverlayConfig() : config;
        setTransform(false);
        setTouchable(Touchable.disabled);
        setSize(this.config.width, this.config.height);

        errorColor = parseColor(this.config.color, Color.valueOf("FF6B5A"));
        successColor = parseColor(this.config.successColor, Color.valueOf("7CFF7A"));
        errorStyle = new Label.LabelStyle(font, errorColor.cpy());
        rewardStyle = resolveRewardStyle(font, successColor);

        label = new Label("", errorStyle);
        label.setAlignment(align(this.config.alignment));
        label.setFontScale(this.config.fontSize);
        label.setColor(errorColor.r, errorColor.g, errorColor.b, this.config.alpha);
        label.setSize(this.config.width, this.config.height);
        label.setTouchable(Touchable.disabled);
        label.setVisible(false);
        addActor(label);
    }

    public void showError(String message) {
        label.setStyle(errorStyle);
        show(message, false);
    }

    public void showSuccess(String message) {
        label.setStyle(rewardStyle);
        show(message, true);
    }

    public void show(String message, boolean success) {
        if (message == null || message.isBlank()) {
            return;
        }
        Color color = success ? successColor : errorColor;
        label.setColor(color.r, color.g, color.b, config.alpha);
        label.setText(message);
        label.setVisible(true);
        label.clearActions();
        label.getColor().a = config.alpha;
        label.addAction(Actions.sequence(
            Actions.delay(config.duration),
            Actions.fadeOut(0.25f),
            Actions.run(() -> label.setVisible(false))
        ));
    }

    public void layoutInParent(float parentWidth, float parentHeight) {
        float x = parentWidth * config.anchorX + config.offsetX - getWidth() * 0.5f;
        // Near the bottom (anchorY ~ 0), prefer bottomOffset so Ali can tune without top-offset semantics.
        float verticalOffset = config.anchorY <= 0.5f ? config.bottomOffset : config.offsetY;
        float y = parentHeight * config.anchorY + verticalOffset - getHeight() * config.anchorY;
        setPosition(x, y);
        label.setPosition(0f, 0f);
        label.setSize(getWidth(), getHeight());
    }

    private Label.LabelStyle resolveRewardStyle(BitmapFont font, Color fallbackColor) {
        String styleName = config.rewardLabelStyle == null || config.rewardLabelStyle.isBlank()
            ? UiTheme.LBL_MULTIPLIER
            : config.rewardLabelStyle.trim();
        styleName = UiTheme.labelStyleOrDefault(styleName);
        if (AssetManager.skin != null && UiTheme.hasLabelStyle(styleName)) {
            Label.LabelStyle fromSkin = new Label.LabelStyle(
                AssetManager.skin.get(styleName, Label.LabelStyle.class)
            );
            if (fromSkin.fontColor == null) {
                fromSkin.fontColor = fallbackColor.cpy();
            }
            return fromSkin;
        }
        return new Label.LabelStyle(font, fallbackColor.cpy());
    }

    private static int align(String value) {
        if (value == null || value.isBlank()) {
            return Align.center;
        }
        return switch (value.trim().toLowerCase()) {
            case "left" -> Align.left;
            case "right" -> Align.right;
            default -> Align.center;
        };
    }

    private static Color parseColor(String hex, Color fallback) {
        if (hex == null || hex.isBlank()) {
            return fallback.cpy();
        }
        try {
            return Color.valueOf(hex.trim());
        } catch (Exception ignored) {
            return fallback.cpy();
        }
    }
}
