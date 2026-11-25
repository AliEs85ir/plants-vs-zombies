package app.view.greenhouse;

import app.enums.greenhouse.BillAvailability;
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
import game.settings.GameSettings;

import java.time.Duration;

public final class GreenhouseBillButton extends WidgetGroup {
    private final GreenhouseConfig.BillConfig config;
    private final Image icon;
    private final Label cooldownLabel;
    private final Image speedIcon;
    private boolean hovered;
    private boolean pressed;
    private boolean speedHovered;
    private boolean speedPressed;
    private boolean selected;
    private boolean available = true;
    private Runnable action = () -> {
    };
    private Runnable speedAction = () -> {
    };

    public GreenhouseBillButton(
        GreenhouseConfig.BillConfig config,
        Texture texture,
        Texture speedTexture,
        BitmapFont font
    ) {
        this.config = config == null ? new GreenhouseConfig.BillConfig() : config;
        setName("greenhouse-bill");
        setTransform(true);
        setVisible(this.config.visible);

        float width = Math.max(1f, this.config.width * this.config.scale);
        float height = Math.max(1f, this.config.height * this.config.scale);
        float speedW = Math.max(1f, this.config.speedIconWidth * this.config.speedIconScale);
        float speedH = Math.max(1f, this.config.speedIconHeight * this.config.speedIconScale);
        float minX = Math.min(0f, this.config.speedIconOffsetX);
        float maxX = Math.max(width, this.config.speedIconOffsetX + speedW);
        float minY = Math.min(0f, this.config.speedIconOffsetY);
        float maxY = Math.max(height, Math.max(this.config.cooldownLabelOffsetY + 24f, this.config.speedIconOffsetY + speedH));
        setSize(Math.max(1f, maxX - minX), Math.max(1f, maxY - minY));
        setOrigin(Align.center);

        float iconX = -minX;
        float iconY = -minY;

        icon = new Image();
        if (texture != null) {
            icon.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
            setTouchable(Touchable.childrenOnly);
        } else {
            setTouchable(Touchable.disabled);
        }
        icon.setSize(width, height);
        icon.setOrigin(Align.center);
        icon.setRotation(this.config.rotation);
        icon.getColor().a = this.config.alpha;
        icon.setPosition(iconX, iconY);
        icon.setName("greenhouse-bill-icon");
        icon.setTouchable(Touchable.enabled);
        addActor(icon);

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE.cpy());
        cooldownLabel = new Label("", style);
        cooldownLabel.setFontScale(this.config.cooldownLabelFontScale);
        cooldownLabel.setAlignment(Align.center);
        cooldownLabel.setVisible(false);
        cooldownLabel.setTouchable(Touchable.disabled);
        cooldownLabel.setWidth(width);
        cooldownLabel.setPosition(
            iconX + this.config.cooldownLabelOffsetX,
            iconY + this.config.cooldownLabelOffsetY
        );
        addActor(cooldownLabel);

        speedIcon = new Image();
        if (speedTexture != null) {
            speedIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(speedTexture)));
        }
        speedIcon.setSize(speedW, speedH);
        speedIcon.setOrigin(Align.center);
        speedIcon.setPosition(
            iconX + (width - speedW) * 0.5f + this.config.speedIconOffsetX,
            iconY + this.config.speedIconOffsetY
        );
        speedIcon.setName("greenhouse-bill-speed");
        speedIcon.setVisible(this.config.speedIconVisible);
        speedIcon.setTouchable(Touchable.enabled);
        addActor(speedIcon);

        icon.addListener(new ClickListener() {
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
                if (available) {
                    pressed = true;
                    applyVisual(false);
                }
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
                if (!available) {
                    return;
                }
                AudioManager.play(AudioId.UI_CLICK);
                action.run();
            }
        });

        speedIcon.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer != -1) {
                    return;
                }
                speedHovered = true;
                applySpeedVisual(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                speedHovered = false;
                speedPressed = false;
                applySpeedVisual(true);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean handled = super.touchDown(event, x, y, pointer, button);
                speedPressed = true;
                applySpeedVisual(false);
                return handled;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                speedPressed = false;
                applySpeedVisual(true);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.play(AudioId.UI_CLICK);
                speedAction.run();
            }
        });
    }

    public void setAction(Runnable action) {
        this.action = action != null ? action : () -> {
        };
    }

    public void setSpeedAction(Runnable speedAction) {
        this.speedAction = speedAction != null ? speedAction : () -> {
        };
    }

    public void refresh(BillAvailability availability, Duration remainingCooldown) {
        available = availability != BillAvailability.UNAVAILABLE;
        selected = availability == BillAvailability.SELECTED;
        float alpha = available ? config.alpha : config.unavailableAlpha;
        Color color = icon.getColor();
        color.a = alpha;
        icon.setColor(color);
        icon.setTouchable(available && icon.getDrawable() != null ? Touchable.enabled : Touchable.disabled);
        boolean showCooldown = !available
            && remainingCooldown != null
            && !remainingCooldown.isZero()
            && !remainingCooldown.isNegative();
        if (showCooldown) {
            cooldownLabel.setText(GreenhouseTimerFormat.format(remainingCooldown, null));
            cooldownLabel.setVisible(true);
            cooldownLabel.pack();
            cooldownLabel.setX(icon.getX() + (icon.getWidth() - cooldownLabel.getWidth()) * 0.5f + config.cooldownLabelOffsetX);
        } else {
            cooldownLabel.setVisible(false);
        }
        boolean showSpeed = config.speedIconVisible
            && speedIcon.getDrawable() != null
            && showCooldown
            && GameSettings.gameplay().debugMode();
        speedIcon.setVisible(showSpeed);
        speedIcon.setTouchable(showSpeed ? Touchable.enabled : Touchable.disabled);
        applyVisual(false);
        applySpeedVisual(false);
    }

    public void layoutInParent(float parentWidth, float parentHeight) {
        float width = getWidth();
        float height = getHeight();
        float x = parentWidth * config.anchorX + config.x + config.offsetX - width * config.anchorX;
        float y = parentHeight * config.anchorY + config.y + config.offsetY - height * config.anchorY;
        setPosition(x, y);
        setZIndex(Math.max(0, config.z));
    }

    private void applyVisual(boolean animate) {
        float target = 1f;
        if (available) {
            if (pressed) {
                target = config.pressedScale;
            } else if (hovered) {
                target = selected ? config.selectedScale * config.hoverScale : config.hoverScale;
            } else if (selected) {
                target = config.selectedScale;
            }
        }
        icon.clearActions();
        Interpolation easing = MainMenuEasings.parse(config.easing);
        float duration = config.hoverDuration;
        if (!animate || duration <= 0f) {
            icon.setScale(target);
        } else {
            icon.addAction(Actions.scaleTo(target, target, duration, easing));
        }
    }

    private void applySpeedVisual(boolean animate) {
        float target = 1f;
        if (speedPressed) {
            target = config.speedPressedScale;
        } else if (speedHovered) {
            target = config.speedHoverScale;
        }
        speedIcon.clearActions();
        Interpolation easing = MainMenuEasings.parse(config.speedEasing);
        float duration = config.speedHoverDuration;
        if (!animate || duration <= 0f) {
            speedIcon.setScale(target);
        } else {
            speedIcon.addAction(Actions.scaleTo(target, target, duration, easing));
        }
    }
}
