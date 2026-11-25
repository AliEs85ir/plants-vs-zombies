package app.view.greenhouse;

import app.view.greenhouse.config.GreenhouseConfig;
import app.view.mainmenu.config.MainMenuEasings;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;

public final class GreenhouseQuestionActor extends WidgetGroup {
    private final GreenhouseConfig.QuestionIconConfig config;
    private final Image icon;
    private final Table bubble;
    private final Label bubbleLabel;
    private final float baseY;
    private boolean hovered;
    private boolean pressed;
    private Runnable onClick = () -> {
    };

    public GreenhouseQuestionActor(GreenhouseConfig.QuestionIconConfig config, Texture texture) {
        this.config = config == null ? new GreenhouseConfig.QuestionIconConfig() : config;
        setName("greenhouse-question");
        setTransform(true);
        setVisible(this.config.visible);

        float width = Math.max(1f, this.config.width * this.config.scale);
        float height = Math.max(1f, this.config.height * this.config.scale);
        setSize(width, height);
        setOrigin(Align.center);

        icon = new Image();
        if (texture != null) {
            icon.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        }
        icon.setSize(width, height);
        icon.setOrigin(Align.center);
        icon.setRotation(this.config.rotation);
        icon.getColor().a = this.config.alpha;
        icon.setPosition(0f, 0f);
        icon.setTouchable(Touchable.disabled);
        addActor(icon);

        String styleName = UiTheme.labelStyleOrDefault(
            this.config.labelStyle == null || this.config.labelStyle.isBlank()
                ? UiTheme.LBL_DEFAULT
                : this.config.labelStyle
        );
        Label.LabelStyle baseStyle = AssetManager.skin.get(styleName, Label.LabelStyle.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(baseStyle);
        labelStyle.background = null;
        Color textColor = parseColor(this.config.textColor, Color.valueOf("1A1A1A"));
        labelStyle.fontColor = textColor;
        bubbleLabel = new Label("", labelStyle);
        bubbleLabel.setAlignment(Align.center);
        bubbleLabel.setWrap(true);
        bubbleLabel.setFontScale(this.config.fontSize);
        bubbleLabel.setColor(textColor);

        bubble = new Table();
        bubble.setVisible(false);
        bubble.setTouchable(Touchable.disabled);
        Drawable background = UiTheme.drawableOrFallback(
            this.config.bubbleBackgroundDrawable,
            UiTheme.BG_GUARANTEE
        );
        if (background != null) {
            bubble.setBackground(background);
        }
        bubble.pad(this.config.bubblePadY, this.config.bubblePadX, this.config.bubblePadY, this.config.bubblePadX);
        bubble.add(bubbleLabel).width(this.config.bubbleWidth).growX();
        addActor(bubble);
        baseY = height + this.config.bubbleOffsetY;

        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer != -1) {
                    return;
                }
                hovered = true;
                applyVisual(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                hovered = false;
                pressed = false;
                applyVisual(true);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean handled = super.touchDown(event, x, y, pointer, button);
                pressed = true;
                applyVisual(false);
                return handled;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                pressed = false;
                applyVisual(true);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.play(AudioId.UI_CLICK);
                onClick.run();
            }
        });
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick != null ? onClick : () -> {
        };
    }

    public void showText(String text) {
        String safe = text == null ? "" : text.trim();
        if (safe.isBlank()) {
            return;
        }
        bubble.clearActions();
        bubbleLabel.setText(safe);
        bubbleLabel.setWrap(true);
        bubbleLabel.setWidth(config.bubbleWidth);
        bubble.pack();
        bubble.setSize(
            Math.max(config.bubbleWidth + config.bubblePadX * 2f, bubble.getPrefWidth()),
            Math.max(bubble.getPrefHeight(), config.bubblePadY * 2f + 24f)
        );
        float startY = baseY;
        float riseY = baseY + config.animation.riseOffsetY;
        bubble.setPosition(
            (getWidth() - bubble.getWidth()) * 0.5f + config.bubbleOffsetX,
            startY
        );
        bubble.setVisible(true);
        bubble.getColor().a = 0f;
        int words = Math.max(1, safe.split("\\s+").length);
        float hold = Math.max(config.minDisplaySeconds, words * config.secondsPerWord);
        Interpolation easing = MainMenuEasings.parse(config.animation.easing);
        bubble.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveTo(bubble.getX(), riseY, config.animation.riseDuration, easing),
                Actions.fadeIn(config.animation.riseDuration, easing)
            ),
            Actions.delay(hold),
            Actions.parallel(
                Actions.moveTo(bubble.getX(), startY, config.animation.fallDuration, easing),
                Actions.fadeOut(config.animation.fallDuration, easing)
            ),
            Actions.run(() -> bubble.setVisible(false))
        ));
    }

    public float offsetX() {
        return config.offsetX;
    }

    public float offsetY() {
        return config.offsetY;
    }

    private void applyVisual(boolean animate) {
        float target = 1f;
        if (pressed) {
            target = config.pressedScale;
        } else if (hovered) {
            target = config.hoverScale;
        }
        clearActions();
        Interpolation easing = MainMenuEasings.parse(config.easing);
        float duration = config.hoverDuration;
        if (!animate || duration <= 0f) {
            setScale(target);
        } else {
            addAction(Actions.scaleTo(target, target, duration, easing));
        }
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
