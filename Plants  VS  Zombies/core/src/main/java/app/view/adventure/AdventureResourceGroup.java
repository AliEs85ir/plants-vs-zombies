package app.view.adventure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.audio.AudioId;
import game.audio.AudioManager;

public final class AdventureResourceGroup extends WidgetGroup {
    private final AdventureHudConfig config;
    private final AdventureHudConfig.ResourceKindConfig kind;
    private final Image background;
    private final Image icon;
    private final Image plus;
    private final Label value;
    private final Label error;
    private Runnable plusAction = () -> {
    };
    private boolean plusHovered;
    private boolean plusPressed;
    private float errorTime;

    public AdventureResourceGroup(
        AdventureHudConfig config,
        AdventureHudConfig.ResourceKindConfig kind,
        Texture backgroundTexture,
        Texture iconTexture,
        Texture plusTexture,
        BitmapFont valueFont,
        BitmapFont errorFont
    ) {
        this.config = config;
        this.kind = kind;
        setTransform(true);
        setTouchable(Touchable.childrenOnly);
        background = image(backgroundTexture, false);
        icon = image(iconTexture, false);
        plus = image(plusTexture, plusTexture != null);
        plus.setOrigin(Align.center);
        value = new Label("", new Label.LabelStyle(
            valueFont,
            AdventureHudConfig.color(config.resolvedColor(kind), Color.WHITE)
        ));
        value.setAlignment(config.resolvedTextAlign(kind));
        value.setTouchable(Touchable.disabled);
        error = new Label("", new Label.LabelStyle(
            errorFont,
            AdventureHudConfig.color(config.messages.color, Color.valueOf("FF6B5A"))
        ));
        error.setAlignment(config.messages.textAlign());
        error.setTouchable(Touchable.disabled);
        error.setVisible(false);
        addActor(background);
        addActor(icon);
        addActor(plus);
        addActor(value);
        addActor(error);
        if (plusTexture != null) {
            plus.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    if (pointer != -1) {
                        return;
                    }
                    plusHovered = true;
                    applyPlusVisual(true);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    plusHovered = false;
                    plusPressed = false;
                    applyPlusVisual(true);
                }

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    plusPressed = true;
                    applyPlusVisual(false);
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    super.touchUp(event, x, y, pointer, button);
                    plusPressed = false;
                    applyPlusVisual(true);
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioManager.play(AudioId.UI_CLICK);
                    plusAction.run();
                }
            });
        }
        plus.setName("menu-adventure-plus-" + kind.id);
    }

    public AdventureHudConfig.ResourceKindConfig kind() {
        return kind;
    }

    public void setPlusAction(Runnable action) {
        plusAction = action != null ? action : () -> {
        };
    }

    public void setValueText(String text) {
        value.setText(text == null ? "" : text);
    }

    public void showError(String message) {
        boolean show = message != null && !message.isBlank();
        error.setText(show ? message : "");
        error.setVisible(show);
        errorTime = show ? config.messages.duration : 0f;
        if (show) {
            AudioManager.play(AudioId.UI_ERROR);
        }
    }

    public void clearError() {
        error.setText("");
        error.setVisible(false);
        errorTime = 0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (errorTime > 0f) {
            errorTime -= delta;
            if (errorTime <= 0f) {
                clearError();
            }
        }
    }

    public void applyLayout(float x, float barBottomY) {
        float padding = config.resolvedPadding(kind);
        float bgW = config.backgroundDrawWidth(kind);
        float bgH = config.backgroundDrawHeight(kind);
        float iconW = config.iconDrawWidth(kind);
        float iconH = config.iconDrawHeight(kind);
        float plusW = config.plusDrawWidth(kind);
        float plusH = config.plusDrawHeight(kind);
        float contentW = config.groupContentWidth(kind);
        float contentH = config.groupContentHeight(kind);
        float groupScale = config.resolvedGroupScale(kind);
        setTransform(true);
        setSize(contentW, contentH + config.messages.height + Math.max(0f, -config.messages.offsetY));
        setOrigin(0f, 0f);
        setScale(groupScale);
        setRotation(config.resolvedGroupRotation(kind));
        Color groupColor = getColor();
        groupColor.a = config.resolvedGroupAlpha(kind);
        setColor(groupColor);

        float minX = Math.min(0f, config.resolvedIconOffsetX(kind));
        float bgX = padding - minX + config.resolvedBackgroundOffsetX(kind);
        float bgY = padding + config.resolvedBackgroundOffsetY(kind);
        background.setBounds(bgX, bgY, bgW, bgH);
        background.setOrigin(bgW * 0.5f, bgH * 0.5f);
        background.setRotation(config.resolvedBackgroundRotation(kind));
        tint(background, config.resolvedBackgroundAlpha(kind));
        background.setZIndex(Math.max(0, config.resolvedBackgroundZ(kind)));

        float iconX = bgX + config.resolvedIconOffsetX(kind);
        float iconY = bgY + (bgH - iconH) * 0.5f + config.resolvedIconOffsetY(kind);
        icon.setBounds(iconX, iconY, iconW, iconH);
        icon.setOrigin(iconW * 0.5f, iconH * 0.5f);
        icon.setRotation(config.resolvedIconRotation(kind));
        tint(icon, config.resolvedIconAlpha(kind));
        icon.setTouchable(Touchable.disabled);
        icon.setZIndex(Math.max(0, config.resolvedIconZ(kind)));

        float plusX = bgX + bgW - plusW + config.resolvedPlusOffsetX(kind);
        float plusY = bgY + (bgH - plusH) * 0.5f + config.resolvedPlusOffsetY(kind);
        plus.setBounds(plusX, plusY, plusW, plusH);
        plus.setOrigin(plusW * 0.5f, plusH * 0.5f);
        plus.setRotation(config.resolvedPlusRotation(kind));
        tint(plus, config.resolvedPlusAlpha(kind));
        plus.setZIndex(Math.max(0, config.resolvedPlusZ(kind)));

        value.setFontScale(config.resolvedFontSize(kind) * config.resolvedTextScale(kind));
        Color valueColor = AdventureHudConfig.color(config.resolvedColor(kind), Color.WHITE);
        valueColor.a = config.resolvedTextAlpha(kind);
        value.setColor(valueColor);
        value.setAlignment(config.resolvedTextAlign(kind));
        value.setBounds(
            bgX + config.resolvedTextOffsetX(kind),
            bgY + config.resolvedTextOffsetY(kind),
            bgW,
            bgH
        );
        value.setZIndex(Math.max(0, config.resolvedValueZ(kind)));

        error.setFontScale(config.messages.fontSize);
        Color errorColor = AdventureHudConfig.color(config.messages.color, Color.valueOf("FF6B5A"));
        errorColor.a = config.messages.alpha;
        error.setColor(errorColor);
        float errorY = bgY + config.messages.offsetY - config.messages.height;
        error.setBounds(
            bgX + (bgW - config.messages.width) * 0.5f + config.messages.offsetX,
            errorY,
            config.messages.width,
            config.messages.height
        );

        setPosition(x, barBottomY - bgY);
    }

    private void applyPlusVisual(boolean animate) {
        float target = 1f;
        if (plusPressed) {
            target = config.resolvedPlusPressedScale(kind);
        } else if (plusHovered) {
            target = config.resolvedPlusHoverScale(kind);
        }
        plus.clearActions();
        float duration = config.resolvedPlusHoverDuration(kind);
        Interpolation easing = config.resolvedPlusInterpolation(kind);
        if (!animate || duration <= 0f) {
            plus.setScale(target);
        } else {
            plus.addAction(Actions.scaleTo(target, target, duration, easing));
        }
    }

    private static Image image(Texture texture, boolean interactive) {
        Image created = texture == null
            ? new Image()
            : new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        created.setTouchable(interactive ? Touchable.enabled : Touchable.disabled);
        return created;
    }

    private static void tint(Image image, float alpha) {
        Color color = image.getColor();
        color.a = alpha;
        image.setColor(color);
    }
}
