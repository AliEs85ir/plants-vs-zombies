package app.view.shop;

import app.view.shop.config.ShopConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

public final class ShopMessageToast extends WidgetGroup {
    private final ShopConfig.MessagesOverlayConfig config;
    private final Label label;
    private final Color errorColor;
    private final Color successColor;

    public ShopMessageToast(ShopConfig.MessagesOverlayConfig config, BitmapFont font) {
        this.config = config == null ? new ShopConfig.MessagesOverlayConfig() : config;
        setTransform(false);
        setTouchable(Touchable.disabled);
        setSize(this.config.width, this.config.height);

        errorColor = parseColor(this.config.color, Color.valueOf("FF6B5A"));
        successColor = parseColor(this.config.successColor, Color.valueOf("7CFF7A"));
        Label.LabelStyle style = new Label.LabelStyle(font, errorColor.cpy());
        label = new Label("", style);
        label.setAlignment(align(this.config.alignment));
        label.setFontScale(this.config.fontSize);
        label.setSize(this.config.width, this.config.height);
        label.setTouchable(Touchable.disabled);
        label.setVisible(false);
        addActor(label);
    }

    public void showError(String message) {
        show(message, false);
    }

    public void showSuccess(String message) {
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
        float y = parentHeight * config.anchorY + config.offsetY - getHeight() * config.anchorY;
        setPosition(x, y);
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
            return fallback;
        }
        try {
            return Color.valueOf(hex.startsWith("#") ? hex : "#" + hex);
        } catch (RuntimeException exception) {
            return fallback;
        }
    }
}
