package app.view.shop;

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
import com.badlogic.gdx.utils.Scaling;

public final class ShopCompositeButton extends WidgetGroup {
    private final Texture backgroundTexture;
    private final Image background;
    private final Image icon;
    private final Label label;
    private float iconSize;
    private float iconPadLeft;
    private float iconPadY;
    private float textPadLeft;
    private float textPadRight;
    private boolean keepAspect;
    private int textAlign = Align.left;

    public ShopCompositeButton(
        Texture backgroundTexture,
        Texture iconTexture,
        String text,
        BitmapFont font,
        float fontScale,
        float width,
        float height,
        float iconSize,
        float iconPadLeft,
        float iconPadY,
        float textPadLeft,
        float textPadRight
    ) {
        this(
            backgroundTexture,
            iconTexture,
            text,
            font,
            fontScale,
            width,
            height,
            iconSize,
            iconPadLeft,
            iconPadY,
            textPadLeft,
            textPadRight,
            false,
            Color.WHITE
        );
    }

    public ShopCompositeButton(
        Texture backgroundTexture,
        Texture iconTexture,
        String text,
        BitmapFont font,
        float fontScale,
        float width,
        float height,
        float iconSize,
        float iconPadLeft,
        float iconPadY,
        float textPadLeft,
        float textPadRight,
        boolean keepAspect,
        Color fontColor
    ) {
        this.backgroundTexture = backgroundTexture;
        this.iconSize = iconSize;
        this.iconPadLeft = iconPadLeft;
        this.iconPadY = iconPadY;
        this.textPadLeft = textPadLeft;
        this.textPadRight = textPadRight;
        this.keepAspect = keepAspect;
        setTransform(true);
        setTouchable(Touchable.enabled);

        background = new Image();
        if (backgroundTexture != null) {
            background.setDrawable(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        }
        background.setScaling(keepAspect ? Scaling.fit : Scaling.stretch);
        background.setAlign(Align.center);
        background.setTouchable(Touchable.disabled);
        addActor(background);

        icon = new Image();
        if (iconTexture != null) {
            icon.setDrawable(new TextureRegionDrawable(new TextureRegion(iconTexture)));
            icon.setVisible(true);
        } else {
            icon.setVisible(false);
        }
        icon.setScaling(Scaling.fit);
        icon.setAlign(Align.center);
        icon.setTouchable(Touchable.disabled);
        addActor(icon);

        Label.LabelStyle style = new Label.LabelStyle(font, fontColor == null ? Color.WHITE : fontColor.cpy());
        label = new Label(text == null ? "" : text, style);
        label.setFontScale(fontScale);
        label.setAlignment(textAlign);
        label.setWrap(true);
        label.setTouchable(Touchable.disabled);
        addActor(label);

        float[] fitted = fitSize(width, height);
        setSize(fitted[0], fitted[1]);
        setOrigin(Align.center);
        layoutContents();
    }

    public void setLabelText(String text) {
        label.setText(text == null ? "" : text);
        layoutContents();
    }

    public void setIcon(Texture iconTexture) {
        if (iconTexture != null) {
            icon.setDrawable(new TextureRegionDrawable(new TextureRegion(iconTexture)));
            icon.setVisible(true);
        } else {
            icon.setDrawable(null);
            icon.setVisible(false);
        }
        layoutContents();
    }

    public void setFontColor(Color fontColor) {
        if (fontColor != null) {
            label.setColor(fontColor);
        }
    }

    public void setTextAlignment(int alignment) {
        this.textAlign = alignment;
        label.setAlignment(alignment);
        layoutContents();
    }

    public void applyLayout(
        float maxWidth,
        float height,
        float iconSize,
        float iconPadLeft,
        float iconPadY,
        float textPadLeft,
        float textPadRight,
        float fontScale,
        Color fontColor,
        boolean keepAspect
    ) {
        this.keepAspect = keepAspect;
        background.setScaling(keepAspect ? Scaling.fit : Scaling.stretch);
        this.iconSize = iconSize;
        this.iconPadLeft = iconPadLeft;
        this.iconPadY = iconPadY;
        this.textPadLeft = textPadLeft;
        this.textPadRight = textPadRight;
        label.setFontScale(fontScale);
        if (fontColor != null) {
            label.setColor(fontColor);
        }
        float[] fitted = fitSize(maxWidth, height);
        setSize(fitted[0], fitted[1]);
        setOrigin(Align.center);
        layoutContents();
    }

    public void applyLayout(float width, float height, float iconSize, float fontScale) {
        applyLayout(
            width,
            height,
            iconSize,
            iconPadLeft,
            iconPadY,
            textPadLeft,
            textPadRight,
            fontScale,
            null,
            keepAspect
        );
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        layoutContents();
    }

    private float[] fitSize(float width, float height) {
        float fittedWidth = Math.max(1f, width);
        float fittedHeight = Math.max(1f, height);
        if (keepAspect && backgroundTexture != null && backgroundTexture.getHeight() > 0) {
            float aspect = backgroundTexture.getWidth() / (float) backgroundTexture.getHeight();
            fittedWidth = fittedHeight * aspect;
            if (width > 1f && fittedWidth > width) {
                float shrink = width / fittedWidth;
                fittedWidth = width;
                fittedHeight *= shrink;
            }
        }
        return new float[] {fittedWidth, fittedHeight};
    }

    private void layoutContents() {
        if (icon == null || label == null || background == null) {
            return;
        }
        float height = getHeight();
        float width = getWidth();
        background.setScaling(keepAspect ? Scaling.fit : Scaling.stretch);
        background.setSize(width, height);
        background.setPosition(0f, 0f);
        boolean hasIcon = icon.isVisible() && icon.getDrawable() != null;
        float drawnIcon = iconSize;
        if (keepAspect && height > 1f) {
            drawnIcon = Math.min(iconSize, height * 0.85f);
        }
        float iconX = iconPadLeft;
        float iconY = (height - drawnIcon) * 0.5f + iconPadY;
        if (hasIcon) {
            icon.setSize(drawnIcon, drawnIcon);
            icon.setPosition(iconX, iconY);
        }
        label.setAlignment(textAlign);
        if (textAlign == Align.center) {
            float textWidth = Math.max(8f, width - textPadLeft - textPadRight);
            label.setSize(textWidth, height);
            label.setPosition(textPadLeft, 0f);
        } else {
            float textX = hasIcon ? iconX + drawnIcon + textPadLeft : textPadLeft + iconPadLeft;
            float textWidth = Math.max(8f, width - textX - textPadRight);
            label.setSize(textWidth, height);
            label.setPosition(textX, 0f);
        }
    }
}
