package app.view.adventure;

import app.view.mainmenu.MenuTextures;
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
import com.badlogic.gdx.utils.Scaling;
import game.audio.AudioId;
import game.audio.AudioManager;
import game.component.AssetManager;

public final class AdventureFrontOverlay extends WidgetGroup {
    private final AdventureMenuConfig config;
    private final Image title;
    private final WidgetGroup progressGroup;
    private final Image progressBackground;
    private final Image levelIcon;
    private final Label progressLabel;
    private final Image enterButton;
    private AdventureMenuConfig.ChapterConfig chapter;
    private boolean hovered;
    private boolean rotating;
    private float frontCenterX;
    private float frontCenterY;
    private float globalScale = 1f;
    private Runnable enterAction = () -> {
    };

    public AdventureFrontOverlay(AdventureMenuConfig config) {
        this.config = config;
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        title = image(null);
        progressGroup = new WidgetGroup();
        progressGroup.setTransform(false);
        progressGroup.setTouchable(Touchable.disabled);
        progressBackground = image(null);
        levelIcon = image(null);
        progressLabel = createLabel();
        progressGroup.addActor(progressBackground);
        progressGroup.addActor(levelIcon);
        progressGroup.addActor(progressLabel);
        enterButton = image(null);
        enterButton.setName("menu-adventure-enter");
        enterButton.setOrigin(Align.center);
        enterButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if (pointer != -1 || !enterButton.isVisible()) {
                    return;
                }
                animateEnter(config.enterButton.hoverScale);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                animateEnter(1f);
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                animateEnter(config.enterButton.pressedScale);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                animateEnter(isOver() ? config.enterButton.hoverScale : 1f);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (rotating || chapter == null || !chapter.interactive()) {
                    return;
                }
                AudioManager.play(AudioId.UI_CLICK);
                enterAction.run();
            }
        });
        addActor(title);
        addActor(progressGroup);
        addActor(enterButton);
        hide();
    }

    public void setEnterAction(Runnable enterAction) {
        this.enterAction = enterAction != null ? enterAction : () -> {
        };
    }

    public void setChapter(AdventureMenuConfig.ChapterConfig chapter) {
        boolean same = this.chapter == chapter;
        this.chapter = chapter;
        if (!same) {
            refreshAssets();
        }
        refreshProgress();
        updateVisibility();
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
        updateVisibility();
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
        if (rotating) {
            this.hovered = false;
        }
        updateVisibility();
    }

    public void setFrontPose(float centerX, float centerY, float globalScale) {
        this.frontCenterX = centerX;
        this.frontCenterY = centerY;
        this.globalScale = globalScale <= 0f ? 1f : globalScale;
        layoutOverlay();
    }

    public void refreshProgress() {
        if (progressLabel == null) {
            return;
        }
        int total = SeasonProgressView.totalLevels(
            chapter == null ? null : chapter.seasonType(),
            chapter == null ? 0 : chapter.totalLevels
        );
        int played = SeasonProgressView.clampedPlayed(
            chapter == null ? 0 : SeasonProgressView.playedLevels(chapter.seasonType()),
            total
        );
        progressLabel.setText(config.progress.formatted(played, total));
    }

    private void refreshAssets() {
        title.setDrawable(drawable(chapter == null ? null : chapter.titleAsset, title));
        levelIcon.setDrawable(drawable(chapter == null ? null : chapter.levelIcon, levelIcon));
        enterButton.setDrawable(drawable(chapter == null ? null : chapter.enterButtonAsset, enterButton));
        String progressBg = chapter == null ? null : chapter.levelProgressBackground;
        if (progressBg == null || progressBg.isBlank()) {
            progressBg = config.progress.backgroundPath;
        }
        progressBackground.setDrawable(drawable(progressBg, progressBackground));
    }

    private void layoutOverlay() {
        float s = overlayScale();
        float titleW = config.title.drawWidth(s);
        float titleH = config.title.drawHeight(s);
        float boxW = config.progress.drawWidth(s);
        float boxH = config.progress.drawHeight(s);
        float enterW = config.enterButton.drawWidth(s);
        float enterH = config.enterButton.drawHeight(s);

        float titleCenterX = frontCenterX + config.title.offsetX * s;
        float titleCenterY;
        float progressCenterX = frontCenterX + config.progress.offsetX * s;
        float progressCenterY;
        float enterCenterX = frontCenterX + config.enterButton.offsetX * s;
        float enterCenterY;

        if (config.hoverOverlay.useStackedGaps) {
            titleCenterY = frontCenterY + config.title.offsetY * s;
            float titleBottom = titleCenterY - titleH * 0.5f;
            progressCenterY = titleBottom
                - config.hoverOverlay.titleToProgressGap * s
                - boxH * 0.5f
                + config.progress.offsetY * s;
            float progressBottom = progressCenterY - boxH * 0.5f;
            enterCenterY = progressBottom
                - config.hoverOverlay.progressToEnterGap * s
                - enterH * 0.5f
                + config.enterButton.offsetY * s;
        } else {
            titleCenterY = frontCenterY + config.title.offsetY * s;
            progressCenterY = frontCenterY + config.progress.offsetY * s;
            enterCenterY = frontCenterY + config.enterButton.offsetY * s;
        }

        title.setBounds(titleCenterX - titleW * 0.5f, titleCenterY - titleH * 0.5f, titleW, titleH);
        title.setColor(1f, 1f, 1f, config.title.alpha);

        float boxX = progressCenterX - boxW * 0.5f;
        float boxY = progressCenterY - boxH * 0.5f;
        progressGroup.setBounds(boxX, boxY, boxW, boxH);
        progressBackground.setBounds(0f, 0f, boxW, boxH);

        float iconW = config.levelIcon.drawWidth(s);
        float iconH = config.levelIcon.drawHeight(s);
        levelIcon.setBounds(
            config.levelIcon.offsetX * s,
            (boxH - iconH) * 0.5f + config.levelIcon.offsetY * s,
            iconW,
            iconH
        );
        Color iconColor = levelIcon.getColor();
        iconColor.a = config.levelIcon.alpha;
        levelIcon.setColor(iconColor);

        progressLabel.setFontScale(config.progress.fontSize * s);
        progressLabel.setBounds(
            config.progress.textOffsetX * s,
            config.progress.textOffsetY * s,
            Math.max(1f, boxW - config.progress.textOffsetX * s),
            boxH
        );

        enterButton.setBounds(enterCenterX - enterW * 0.5f, enterCenterY - enterH * 0.5f, enterW, enterH);
        enterButton.setOrigin(enterW * 0.5f, enterH * 0.5f);
        applyLayerOrder();
    }

    /** Carousel scale × hoverOverlay.scale — one knob for the whole hover stack. */
    private float overlayScale() {
        float hover = config.hoverOverlay.scale;
        if (hover <= 0f || Float.isNaN(hover)) {
            hover = 1f;
        }
        return globalScale * hover;
    }

    private void applyLayerOrder() {
        title.setZIndex(0);
        progressGroup.setZIndex(1);
        enterButton.setZIndex(2);
    }

    private void updateVisibility() {
        boolean show = chapter != null && !rotating && (!config.title.visibleOnHover || hovered);
        boolean showProgress = chapter != null && !rotating && (!config.progress.visibleOnHover || hovered);
        boolean showEnter = config.enterButton.visible
            && chapter != null
            && chapter.interactive()
            && !rotating
            && (!config.enterButton.visibleOnHover || hovered)
            && enterButton.getDrawable() != null;
        title.setVisible(show && title.getDrawable() != null);
        progressGroup.setVisible(showProgress);
        progressBackground.setVisible(showProgress && progressBackground.getDrawable() != null);
        levelIcon.setVisible(showProgress && levelIcon.getDrawable() != null);
        progressLabel.setVisible(showProgress);
        enterButton.setVisible(showEnter);
        enterButton.setTouchable(showEnter ? Touchable.enabled : Touchable.disabled);
        if (!showEnter) {
            enterButton.clearActions();
            enterButton.setScale(1f);
        }
        applyLayerOrder();
        setVisible(show || showProgress || showEnter);
    }

    private void hide() {
        hovered = false;
        setVisible(false);
        title.setVisible(false);
        progressGroup.setVisible(false);
        progressBackground.setVisible(false);
        levelIcon.setVisible(false);
        progressLabel.setVisible(false);
        enterButton.setVisible(false);
        enterButton.setTouchable(Touchable.disabled);
        enterButton.clearActions();
        enterButton.setScale(1f);
    }

    private void animateEnter(float scale) {
        Interpolation easing = config.enterButton.interpolation();
        float duration = config.enterButton.hoverDuration;
        enterButton.clearActions();
        if (duration <= 0f) {
            enterButton.setScale(scale);
        } else {
            enterButton.addAction(Actions.scaleTo(scale, scale, duration, easing));
        }
    }

    private static Image image(Texture texture) {
        Image created = texture == null
            ? new Image()
            : new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        created.setTouchable(Touchable.disabled);
        created.setScaling(Scaling.fit);
        created.setAlign(Align.center);
        return created;
    }

    private Label createLabel() {
        BitmapFont font = resolveFont(config.progress.font, config.progress.fontFallback);
        Label.LabelStyle style = new Label.LabelStyle(font, AdventureMenuConfig.color(config.progress.color, Color.WHITE));
        Label label = new Label("", style);
        label.setAlignment(Align.center);
        label.setTouchable(Touchable.disabled);
        return label;
    }

    private static TextureRegionDrawable drawable(String path, Image target) {
        if (path == null || path.isBlank()) {
            disposeTexture(target);
            return null;
        }
        Texture texture = MenuTextures.load(path, "AdventureFrontOverlay");
        if (texture == null) {
            return null;
        }
        Object existing = target.getUserObject();
        if (existing instanceof Texture previous) {
            previous.dispose();
        }
        target.setUserObject(texture);
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private static BitmapFont resolveFont(String name, String fallback) {
        if (AssetManager.skin == null) {
            return new BitmapFont();
        }
        if (name != null && !name.isBlank() && AssetManager.skin.has(name, BitmapFont.class)) {
            return AssetManager.font(name);
        }
        if (fallback != null && !fallback.isBlank() && AssetManager.skin.has(fallback, BitmapFont.class)) {
            return AssetManager.font(fallback);
        }
        return AssetManager.font(AssetManager.DEFAULT_FONT);
    }

    public void dispose() {
        disposeTexture(title);
        disposeTexture(progressBackground);
        disposeTexture(levelIcon);
        disposeTexture(enterButton);
    }

    private static void disposeTexture(Image image) {
        if (image == null) {
            return;
        }
        Object existing = image.getUserObject();
        if (existing instanceof Texture texture) {
            texture.dispose();
            image.setUserObject(null);
        }
    }
}
