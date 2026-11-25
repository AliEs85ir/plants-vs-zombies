package app.view.collection;

import app.view.collection.config.CollectionAnchors;
import app.view.collection.config.CollectionConfig;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import game.audio.AudioId;
import game.audio.AudioManager;

public final class CollectionTabActor extends Image {
    private final CollectionConfig config;
    private final CollectionConfig.AnchoredConfig element;
    private final boolean zombieElement;
    private final Runnable action;
    private float restX;
    private float restY;
    private float restAlpha = 1f;
    private float hoverAmount;
    private float browseAmount;
    private boolean hovered;
    private boolean browsing;
    private boolean zombiesTabOpen;

    public CollectionTabActor(
        CollectionConfig config,
        CollectionConfig.AnchoredConfig element,
        Texture texture,
        String name,
        boolean zombieElement,
        Runnable action
    ) {
        this.config = config;
        this.element = element;
        this.zombieElement = zombieElement;
        this.action = action;
        setName(name);
        if (texture != null) {
            setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        }
        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer == -1 && !browsing) {
                    hovered = true;
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                hovered = false;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (action == null) {
                    return;
                }
                AudioManager.play(AudioId.UI_CLICK);
                action.run();
            }
        });
    }

    public void place(float parentWidth, float parentHeight, Texture texture) {
        CollectionAnchors.applyImage(this, element, texture, parentWidth, parentHeight);
        restX = getX();
        restY = getY();
        restAlpha = element.alpha;
        applyVisual();
    }

    public void showHub(boolean instant) {
        browsing = false;
        hovered = false;
        setVisible(true);
        setTouchable(Touchable.enabled);
        if (instant) {
            browseAmount = 0f;
            hoverAmount = 0f;
            applyVisual();
        }
    }

    public void showBrowse(boolean zombiesTabOpen, boolean instant) {
        this.zombiesTabOpen = zombiesTabOpen;
        browsing = true;
        hovered = false;
        setVisible(true);
        setTouchable(Touchable.disabled);
        if (instant) {
            browseAmount = 1f;
            hoverAmount = 0f;
            applyVisual();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float hoverTarget = hovered && !browsing ? 1f : 0f;
        float hoverStep = Math.min(1f, delta * Math.max(0.01f, config.tabs.hoverSpeed));
        hoverAmount = MathUtils.lerp(hoverAmount, hoverTarget, hoverStep);
        if (Math.abs(hoverAmount - hoverTarget) < 0.01f) {
            hoverAmount = hoverTarget;
        }
        float fadeDuration = Math.max(0.0001f, config.interaction.fadeDuration);
        float browseTarget = browsing ? 1f : 0f;
        float browseStep = Math.min(1f, delta / fadeDuration);
        browseAmount = MathUtils.lerp(browseAmount, browseTarget, browseStep);
        if (Math.abs(browseAmount - browseTarget) < 0.02f) {
            browseAmount = browseTarget;
        }
        applyVisual();
    }

    private void applyVisual() {
        float lift = config.tabs.hoverLift * hoverAmount * (1f - browseAmount);
        setPosition(restX, restY + lift);
        CollectionConfig.TabBrowseLook look = config.tabs.browseLook(zombiesTabOpen, zombieElement);
        float hubAlpha = MathUtils.lerp(restAlpha, config.tabs.hoverAlpha, hoverAmount);
        float targetAlpha = MathUtils.lerp(hubAlpha, look.alpha, browseAmount);
        float brightness = MathUtils.lerp(1f, 1f - look.dim, browseAmount);
        setColor(brightness, brightness, brightness, targetAlpha);
    }
}
