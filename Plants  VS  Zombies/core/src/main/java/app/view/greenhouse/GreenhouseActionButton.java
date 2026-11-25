package app.view.greenhouse;

import app.view.greenhouse.config.GreenhouseConfig;
import app.view.mainmenu.config.MainMenuEasings;
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

public final class GreenhouseActionButton extends WidgetGroup {
    public enum State {
        GROWING,
        READY
    }

    private final GreenhouseConfig.ActionButtonConfig config;
    private final Image background;
    private final Image icon;
    private final Label label;
    private final TextureRegionDrawable growingIconDrawable;
    private final TextureRegionDrawable readyIconDrawable;
    private boolean hovered;
    private boolean pressed;
    private Runnable action = () -> {
    };
    private State state = State.GROWING;

    public GreenhouseActionButton(
        GreenhouseConfig.ActionButtonConfig config,
        Texture backgroundTexture,
        Texture growingIconTexture,
        Texture readyIconTexture,
        BitmapFont font
    ) {
        this.config = config == null ? new GreenhouseConfig.ActionButtonConfig() : config;
        setName("greenhouse-action");
        setTransform(true);
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

        growingIconDrawable = growingIconTexture == null
            ? null
            : new TextureRegionDrawable(new TextureRegion(growingIconTexture));
        readyIconDrawable = readyIconTexture == null
            ? null
            : new TextureRegionDrawable(new TextureRegion(readyIconTexture));

        icon = new Image();
        if (growingIconDrawable != null) {
            icon.setDrawable(growingIconDrawable);
        }
        float iconW = this.config.iconWidth * this.config.iconScale;
        float iconH = this.config.iconHeight * this.config.iconScale;
        icon.setSize(iconW, iconH);
        icon.setOrigin(Align.center);
        icon.setPosition(
            getWidth() * 0.5f + this.config.iconOffsetX - iconW * 0.5f,
            getHeight() * 0.5f + this.config.iconOffsetY - iconH * 0.5f
        );
        icon.setTouchable(Touchable.disabled);
        addActor(icon);

        Color textColor = parseColor(this.config.color, Color.valueOf("fffdf5"));
        Label.LabelStyle style = new Label.LabelStyle(font, textColor.cpy());
        label = new Label(this.config.readyText, style);
        label.setFontScale(this.config.fontSize);
        label.setAlignment(Align.left);
        label.setTouchable(Touchable.disabled);
        label.pack();
        label.setPosition(
            getWidth() * 0.5f + this.config.textOffsetX,
            getHeight() * 0.5f + this.config.textOffsetY - label.getHeight() * 0.5f
        );
        addActor(label);

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
                action.run();
            }
        });
    }

    public void setAction(Runnable action) {
        this.action = action != null ? action : () -> {
        };
    }

    public void setState(State state, int gems) {
        this.state = state == null ? State.GROWING : state;
        if (this.state == State.READY) {
            if (readyIconDrawable != null) {
                icon.setDrawable(readyIconDrawable);
            }
            label.setText(config.readyText);
        } else {
            if (growingIconDrawable != null) {
                icon.setDrawable(growingIconDrawable);
            }
            label.setText(config.growingLabel(gems));
        }
        label.pack();
        label.setPosition(
            getWidth() * 0.5f + config.textOffsetX,
            getHeight() * 0.5f + config.textOffsetY - label.getHeight() * 0.5f
        );
    }

    public State state() {
        return state;
    }

    public float offsetX() {
        return config.backgroundOffsetX;
    }

    public float offsetY() {
        return config.backgroundOffsetY;
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
