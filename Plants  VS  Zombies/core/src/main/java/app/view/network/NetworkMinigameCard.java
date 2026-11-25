package app.view.network;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import game.audio.AudioId;
import game.audio.AudioManager;

public final class NetworkMinigameCard extends WidgetGroup {
    private final NetworkMenuConfig.MinigamesConfig style;
    private final NetworkMenuConfig.GameConfig definition;
    private final Image frame;
    private final Image card;
    private final Image play;
    private final Runnable action;
    private boolean hovered;

    public NetworkMinigameCard(
        Texture frameTexture,
        Texture cardTexture,
        Texture playTexture,
        NetworkMenuConfig.MinigamesConfig style,
        NetworkMenuConfig.GameConfig definition,
        Runnable action
    ) {
        this.style = style;
        this.definition = definition;
        this.action = action != null ? action : () -> {
        };
        setName("menu-network-card-" + definition.id);
        setTransform(false);
        setTouchable(Touchable.enabled);
        frame = image(region(frameTexture, 0f, 0f, 1f, 1f), Scaling.stretch);
        card = image(region(cardTexture, 0f, 0f, 1f, 1f), scaling(style.cardScaling));
        play = image(playRegion(playTexture, style), scaling(style.playScaling));
        play.getColor().a = 0f;
        play.setVisible(false);
        addActor(frame);
        addActor(card);
        addActor(play);
        applyVisual(false);
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
                applyVisual(true);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.play(AudioId.UI_CLICK);
                NetworkMinigameCard.this.action.run();
            }
        });
    }

    public NetworkMenuConfig.GameConfig definition() {
        return definition;
    }

    public void place(float x, float y, float width, float height) {
        setBounds(x, y, Math.max(1f, width), Math.max(1f, height));
        layoutChildren();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        layoutChildren();
    }

    private void layoutChildren() {
        float width = getWidth();
        float height = getHeight();
        frame.setBounds(0f, 0f, width, height);
        card.setBounds(
            width * style.cardX,
            height * style.cardY,
            Math.max(1f, width * style.cardW),
            Math.max(1f, height * style.cardH)
        );
        card.setOrigin(Align.center);
        play.setBounds(
            width * style.playX,
            height * style.playY,
            Math.max(1f, width * style.playW),
            Math.max(1f, height * style.playH)
        );
        play.setOrigin(Align.center);
    }

    private void applyVisual(boolean animate) {
        float targetScale = hovered ? style.hoverScale : 1f;
        float darkness = hovered ? style.hoverDarkness : style.normalDarkness;
        Color targetColor = darknessColor(darkness);
        Interpolation easing = style.interpolation();
        float duration = animate ? style.hoverDuration : 0f;
        card.clearActions();
        play.clearActions();
        if (!animate || duration <= 0f) {
            card.setScale(targetScale);
            card.setColor(targetColor);
        } else {
            card.addAction(Actions.parallel(
                Actions.scaleTo(targetScale, targetScale, duration, easing),
                Actions.color(targetColor, duration, easing)
            ));
        }
        float playAlpha = hovered ? 1f : 0f;
        float fade = animate ? style.playFadeDuration : 0f;
        if (!animate || fade <= 0f) {
            Color playColor = play.getColor();
            playColor.a = playAlpha;
            play.setColor(playColor);
            play.setVisible(hovered);
        } else {
            play.setVisible(true);
            play.addAction(Actions.sequence(
                Actions.alpha(playAlpha, fade, easing),
                Actions.run(() -> play.setVisible(hovered || play.getColor().a > 0.01f))
            ));
        }
    }

    private static Image image(TextureRegion region, Scaling scaling) {
        Image image = new Image();
        image.setTouchable(Touchable.disabled);
        image.setScaling(scaling);
        if (region != null) {
            image.setDrawable(new TextureRegionDrawable(region));
        }
        return image;
    }

    private static TextureRegion playRegion(Texture texture, NetworkMenuConfig.MinigamesConfig style) {
        return region(texture, style.playRegionX, style.playRegionY, style.playRegionWidth, style.playRegionHeight);
    }

    private static TextureRegion region(Texture texture, float x, float y, float width, float height) {
        if (texture == null) {
            return null;
        }
        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();
        int regionX = Math.max(0, Math.round(x * textureWidth));
        int regionY = Math.max(0, Math.round(y * textureHeight));
        int regionWidth = Math.max(1, Math.round(width * textureWidth));
        int regionHeight = Math.max(1, Math.round(height * textureHeight));
        if (regionX + regionWidth > textureWidth) {
            regionWidth = textureWidth - regionX;
        }
        if (regionY + regionHeight > textureHeight) {
            regionHeight = textureHeight - regionY;
        }
        return new TextureRegion(texture, regionX, regionY, Math.max(1, regionWidth), Math.max(1, regionHeight));
    }

    private static Scaling scaling(String value) {
        if ("fit".equals(value)) {
            return Scaling.fit;
        }
        if ("stretch".equals(value) || "none".equals(value)) {
            return Scaling.stretch;
        }
        return Scaling.fill;
    }

    private static Color darknessColor(float darkness) {
        float tint = 1f - Math.max(0f, Math.min(1f, darkness));
        return new Color(tint, tint, tint, 1f);
    }
}
