package app.view.shop;

import app.view.greenhouse.GreenhouseTimerFormat;
import app.view.greenhouse.config.GreenhouseConfig;
import app.view.shop.config.ShopConfig;
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

public final class ShopTimerGroup extends WidgetGroup {
    private final ShopConfig.TimerUiConfig config;
    private final ShopConfig.FormatsConfig formats;
    private final Image icon;
    private final Label label;
    private final Color normalColor;
    private final Color finishedColor;

    public ShopTimerGroup(
        ShopConfig.TimerUiConfig config,
        ShopConfig.FormatsConfig formats,
        Texture iconTexture,
        BitmapFont font
    ) {
        this.config = config == null ? new ShopConfig.TimerUiConfig() : config;
        this.formats = formats == null ? new ShopConfig.FormatsConfig() : formats;
        setTransform(true);
        setTouchable(Touchable.disabled);
        float width = Math.max(1f, this.config.width);
        float height = Math.max(1f, this.config.height);
        setSize(width, height);
        setOrigin(Align.center);

        icon = new Image();
        if (iconTexture != null) {
            icon.setDrawable(new TextureRegionDrawable(new TextureRegion(iconTexture)));
        }
        icon.setSize(this.config.iconSize, this.config.iconSize);
        icon.setOrigin(Align.center);
        icon.setPosition(this.config.iconOffsetX, (height - this.config.iconSize) * 0.5f + this.config.iconOffsetY);
        icon.setTouchable(Touchable.disabled);
        addActor(icon);

        normalColor = ShopUiFactory.parseColor(this.config.color, Color.valueOf("fffdf5"));
        finishedColor = ShopUiFactory.parseColor(this.config.finishedColor, Color.valueOf("A8A8A8"));
        Label.LabelStyle style = new Label.LabelStyle(font, normalColor.cpy());
        label = new Label("00:00", style);
        label.setFontScale(this.config.fontSize);
        label.setAlignment(Align.left);
        label.setTouchable(Touchable.disabled);
        label.pack();
        label.setPosition(
            this.config.iconOffsetX + this.config.iconSize + this.config.textOffsetX,
            (height - label.getHeight()) * 0.5f + this.config.textOffsetY
        );
        addActor(label);
    }

    public void refresh(Duration remaining, boolean finished) {
        label.setText(GreenhouseTimerFormat.format(remaining, new GreenhouseConfig.FormatsConfig()));
        Color color = finished ? finishedColor : normalColor;
        label.setColor(color);
        label.pack();
        label.setPosition(
            config.iconOffsetX + config.iconSize + config.textOffsetX,
            (getHeight() - label.getHeight()) * 0.5f + config.textOffsetY
        );
    }
}
