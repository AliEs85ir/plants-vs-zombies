package app.view.greenhouse;

import app.view.greenhouse.config.GreenhouseConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.time.Duration;

public final class GreenhouseTimerGroup extends WidgetGroup {
    private final GreenhouseConfig.TimerConfig config;
    private final GreenhouseConfig.FormatsConfig formats;
    private final Image background;
    private final Image icon;
    private final Label label;
    private final Color normalColor;
    private final Color finishedColor;

    public GreenhouseTimerGroup(
        GreenhouseConfig.TimerConfig config,
        GreenhouseConfig.FormatsConfig formats,
        Texture backgroundTexture,
        Texture iconTexture,
        BitmapFont font
    ) {
        this.config = config == null ? new GreenhouseConfig.TimerConfig() : config;
        this.formats = formats == null ? new GreenhouseConfig.FormatsConfig() : formats;
        setTransform(true);
        setTouchable(Touchable.disabled);
        setVisible(this.config.visible);

        float bgW = Math.max(1f, this.config.backgroundWidth * this.config.backgroundScale);
        float bgH = Math.max(1f, this.config.backgroundHeight * this.config.backgroundScale);
        setSize(bgW, bgH);
        setOrigin(Align.center);

        background = new Image();
        if (backgroundTexture != null) {
            background.setDrawable(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        }
        background.setSize(bgW, bgH);
        background.setOrigin(Align.center);
        background.setRotation(this.config.backgroundRotation);
        background.getColor().a = this.config.backgroundAlpha;
        background.setPosition(0f, 0f);
        background.setTouchable(Touchable.disabled);
        addActor(background);

        icon = new Image();
        if (iconTexture != null) {
            icon.setDrawable(new TextureRegionDrawable(new TextureRegion(iconTexture)));
        }
        float iconW = this.config.iconWidth * this.config.iconScale;
        float iconH = this.config.iconHeight * this.config.iconScale;
        icon.setSize(iconW, iconH);
        icon.setOrigin(Align.center);
        icon.setRotation(this.config.iconRotation);
        icon.getColor().a = this.config.iconAlpha;
        icon.setPosition(
            getWidth() * 0.5f + this.config.iconOffsetX - iconW * 0.5f,
            getHeight() * 0.5f + this.config.iconOffsetY - iconH * 0.5f
        );
        icon.setTouchable(Touchable.disabled);
        addActor(icon);

        normalColor = parseColor(this.config.color, Color.valueOf("fffdf5"));
        finishedColor = parseColor(this.config.finishedColor, Color.valueOf("A8A8A8"));
        Label.LabelStyle style = new Label.LabelStyle(font, normalColor.cpy());
        label = new Label("00:00", style);
        label.setFontScale(this.config.fontSize * this.config.textScale);
        label.setAlignment(Align.left);
        label.setColor(normalColor.r, normalColor.g, normalColor.b, this.config.textAlpha);
        label.setTouchable(Touchable.disabled);
        label.pack();
        label.setPosition(
            getWidth() * 0.5f + this.config.textOffsetX,
            getHeight() * 0.5f + this.config.textOffsetY - label.getHeight() * 0.5f
        );
        addActor(label);
    }

    public void refresh(Duration remaining, boolean finished) {
        if (!config.visible) {
            setVisible(false);
            return;
        }
        setVisible(true);
        label.setText(GreenhouseTimerFormat.format(remaining, formats));
        Color color = finished ? finishedColor : normalColor;
        label.setColor(color.r, color.g, color.b, config.textAlpha);
        label.pack();
        label.setPosition(
            getWidth() * 0.5f + config.textOffsetX,
            getHeight() * 0.5f + config.textOffsetY - label.getHeight() * 0.5f
        );
    }

    public float offsetX() {
        return config.backgroundOffsetX;
    }

    public float offsetY() {
        return config.backgroundOffsetY;
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
