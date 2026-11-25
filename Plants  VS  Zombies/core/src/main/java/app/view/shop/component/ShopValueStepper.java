package app.view.shop.component;

import app.view.shop.ShopTextureCache;
import app.view.shop.ShopUiFactory;
import app.view.shop.config.ShopConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public final class ShopValueStepper extends WidgetGroup {
    private final ShopConfig.QuantityStepperConfig style;
    private final Texture backgroundTexture;
    private final WidgetGroup minusButton;
    private final WidgetGroup plusButton;
    private final Label valueLabel;

    public ShopValueStepper(
        ShopConfig config,
        ShopTextureCache textures,
        BitmapFont font,
        Color textColor,
        Runnable onMinus,
        Runnable onPlus
    ) {
        this.style = config.productDetail.quantityStepper;
        setTransform(true);
        setName("shop-stepper");
        setTouchable(Touchable.enabled);

        backgroundTexture = textures.get(style.backgroundPath);
        Image background = new Image();
        if (backgroundTexture != null) {
            background.setDrawable(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        }
        background.setScaling(Scaling.fit);
        background.setAlign(Align.center);
        background.setFillParent(true);
        background.setTouchable(Touchable.disabled);
        addActor(background);

        minusButton = iconButton(textures.get(style.minusPath), "shop-stepper-minus", onMinus, config);
        plusButton = iconButton(textures.get(style.plusPath), "shop-stepper-plus", onPlus, config);
        addActor(minusButton);
        addActor(plusButton);

        valueLabel = ShopUiFactory.label("1", font, textColor, Align.center);
        valueLabel.setFontScale(style.fontScale);
        valueLabel.setColor(ShopUiFactory.parseColor(style.fontColor, textColor));
        valueLabel.setTouchable(Touchable.disabled);
        addActor(valueLabel);

        float width = style.width;
        float height = style.height;
        if (style.keepAspect && backgroundTexture != null && backgroundTexture.getHeight() > 0) {
            width = height * (backgroundTexture.getWidth() / (float) backgroundTexture.getHeight());
        }
        setSize(width, height);
        setOrigin(Align.center);
        layoutContents();
    }

    public void setValueText(String text) {
        valueLabel.setText(text == null ? "" : text);
        layoutContents();
    }

    public void applySize() {
        float height = style.height;
        float width = style.width;
        if (style.keepAspect && backgroundTexture != null && backgroundTexture.getHeight() > 0) {
            width = height * (backgroundTexture.getWidth() / (float) backgroundTexture.getHeight());
        }
        setSize(width, height);
        setOrigin(Align.center);
        layoutContents();
    }

    public void fitTo(float maxWidth) {
        applySize();
        if (maxWidth > 1f && getWidth() > maxWidth && getWidth() > 0f) {
            float shrink = maxWidth / getWidth();
            setSize(maxWidth, getHeight() * shrink);
            setOrigin(Align.center);
            layoutContents();
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        layoutContents();
    }

    private WidgetGroup iconButton(Texture texture, String name, Runnable onClick, ShopConfig config) {
        WidgetGroup group = new WidgetGroup();
        group.setTransform(true);
        group.setName(name);
        group.setTouchable(Touchable.enabled);
        Image image = new Image();
        if (texture != null) {
            image.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        }
        image.setScaling(Scaling.fit);
        image.setAlign(Align.center);
        image.setFillParent(true);
        image.setTouchable(Touchable.disabled);
        group.addActor(image);
        group.setSize(style.buttonSize, style.buttonSize);
        group.setOrigin(Align.center);
        ShopUiFactory.attachHoverScale(group, config.interaction, style.iconHoverScale);
        group.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
        return group;
    }

    private void layoutContents() {
        if (minusButton == null || plusButton == null || valueLabel == null) {
            return;
        }
        float width = getWidth();
        float height = getHeight();
        if (width <= 1f || height <= 1f) {
            return;
        }
        float buttonSize = Math.min(style.buttonSize, height * 0.78f);
        float padX = style.buttonPadX;
        minusButton.setSize(buttonSize, buttonSize);
        plusButton.setSize(buttonSize, buttonSize);
        minusButton.setOrigin(Align.center);
        plusButton.setOrigin(Align.center);
        minusButton.setPosition(padX + style.minusOffsetX, (height - buttonSize) * 0.5f + style.minusOffsetY);
        plusButton.setPosition(width - padX - buttonSize + style.plusOffsetX, (height - buttonSize) * 0.5f + style.plusOffsetY);
        float textX = padX + buttonSize;
        float textWidth = Math.max(8f, width - textX - padX - buttonSize);
        valueLabel.setSize(textWidth, height);
        valueLabel.setPosition(textX, style.textOffsetY);
    }
}
