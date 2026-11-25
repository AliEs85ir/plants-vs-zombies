package app.view.loading;

import app.view.cursor.GameCursor;
import app.view.loading.config.LoadingScreenConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;

public final class LoadingGroup extends WidgetGroup implements Disposable {
    private final LoadingScreenConfig config;
    private final GameCursor gameCursor;
    private final Image logoImage;
    private final Stack progressStack;
    private final Image fallbackTrack;
    private final Image fallbackFill;
    private final Table fallbackFillTable;
    private final Label creditsLabel;
    private final Label errorLabel;
    private final LoadingChainLayout.Result chain = new LoadingChainLayout.Result();
    private ImageEntryButton entryButton;
    private ProgressBar skinProgressBar;
    private Texture fallbackTrackTexture;
    private Texture fallbackFillTexture;
    private Texture entryTexture;
    private float floatTime;
    private float displayedProgress;
    private boolean entryRevealed;

    public LoadingGroup(LoadingScreenConfig config, Texture logoTexture, GameCursor gameCursor, BitmapFont bootstrapFont) {
        this.config = config;
        this.gameCursor = gameCursor;
        setTransform(false);
        setFillParent(false);
        logoImage = new Image();
        logoImage.setScaling(Scaling.fit);
        if (logoTexture != null) {
            logoImage.setDrawable(new TextureRegionDrawable(new TextureRegion(logoTexture)));
        }
        logoImage.setVisible(logoTexture != null);

        progressStack = new Stack();
        fallbackTrackTexture = solidTexture(config.progressBar.fallbackTrackR, config.progressBar.fallbackTrackG,
            config.progressBar.fallbackTrackB, config.progressBar.fallbackTrackA);
        fallbackFillTexture = solidTexture(config.progressBar.fallbackFillR, config.progressBar.fallbackFillG,
            config.progressBar.fallbackFillB, config.progressBar.fallbackFillA);
        fallbackTrack = new Image(new TextureRegionDrawable(new TextureRegion(fallbackTrackTexture)));
        fallbackFill = new Image(new TextureRegionDrawable(new TextureRegion(fallbackFillTexture)));
        fallbackFillTable = new Table();
        fallbackFillTable.setFillParent(true);
        fallbackFillTable.add(fallbackFill).width(0f).growY().left();
        fallbackFillTable.left();
        progressStack.add(fallbackTrack);
        progressStack.add(fallbackFillTable);

        Label.LabelStyle bootstrapStyle = new Label.LabelStyle(bootstrapFont, Color.WHITE);
        creditsLabel = new Label(config.credits.joinedText(), bootstrapStyle);
        creditsLabel.setAlignment(config.credits.gdxAlign());
        creditsLabel.setFontScale(config.credits.fontScale);
        creditsLabel.setWrap(config.credits.wrap);

        errorLabel = new Label("", bootstrapStyle);
        errorLabel.setAlignment(Align.center);
        errorLabel.setVisible(false);
        errorLabel.setWrap(true);

        addActor(creditsLabel);
        addActor(errorLabel);
        addActor(progressStack);
        addActor(logoImage);
        refreshChain();
    }

    public void attachSkin(Skin skin) {
        if (skin == null || skinProgressBar != null) {
            return;
        }
        String style = config.progressBar.style;
        if (!skin.has(style, ProgressBar.ProgressBarStyle.class)) {
            style = "default-horizontal";
        }
        if (!skin.has(style, ProgressBar.ProgressBarStyle.class)) {
            applySkinFonts(skin);
            return;
        }
        skinProgressBar = new ProgressBar(0f, 1f, 0.001f, false, skin, style);
        skinProgressBar.setValue(displayedProgress);
        skinProgressBar.setAnimateDuration(0f);
        progressStack.add(skinProgressBar);
        fallbackTrack.setVisible(false);
        fallbackFillTable.setVisible(false);
        applySkinFonts(skin);
        invalidateHierarchy();
    }

    public void setProgress(float progress) {
        displayedProgress = MathUtils.clamp(progress, 0f, 1f);
        if (skinProgressBar != null) {
            skinProgressBar.setValue(displayedProgress);
        }
        fallbackFillTable.getCell(fallbackFill).width(config.progressBar.drawWidth() * displayedProgress);
        fallbackFillTable.invalidateHierarchy();
    }

    public void showError(String message) {
        errorLabel.setText(message == null ? config.error.title : message);
        errorLabel.setVisible(true);
        invalidateHierarchy();
    }

    public void revealEntryButton(boolean loggedIn, Runnable onClick) {
        if (entryRevealed) {
            return;
        }
        String path = loggedIn ? config.entryButton.loginPath : config.entryButton.signUpPath;
        Texture texture = loadTexture(path);
        if (texture == null) {
            showError(config.error.title);
            return;
        }
        entryTexture = texture;
        entryButton = new ImageEntryButton(
            texture,
            gameCursor,
            config.entryButton.scale,
            config.entryButton.hoverScale,
            config.entryButton.pressedScale
        );
        entryButton.setAction(onClick);
        entryRevealed = true;
        addActorAt(progressStack.getZIndex(), entryButton);
        logoImage.toFront();
        invalidateHierarchy();
        float duration = config.transition.enabled
            ? Math.max(config.transition.entryButtonRevealDuration, config.entryButton.revealDuration)
            : 0f;
        entryButton.getColor().a = 0f;
        entryButton.setScale(config.entryButton.scale * config.entryButton.revealStartScale);
        if (duration <= 0f) {
            entryButton.getColor().a = 1f;
            entryButton.setScale(config.entryButton.scale);
            entryButton.setInteractive(true);
            return;
        }
        entryButton.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(duration),
                Actions.scaleTo(config.entryButton.scale, config.entryButton.scale, duration)
            ),
            Actions.run(() -> entryButton.setInteractive(true))
        ));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (config.floatingAnimation.enabled) {
            floatTime += delta * config.floatingAnimation.speed;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float dy = 0f;
        if (config.floatingAnimation.enabled) {
            dy = MathUtils.sin(floatTime) * config.floatingAnimation.amplitude;
        }
        float oldY = getY();
        setY(oldY + dy);
        super.draw(batch, parentAlpha);
        setY(oldY);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        float dy = 0f;
        if (config.floatingAnimation.enabled) {
            dy = MathUtils.sin(floatTime) * config.floatingAnimation.amplitude;
        }
        return super.hit(x, y - dy, touchable);
    }

    @Override
    public float getPrefWidth() {
        refreshChain();
        return chain.groupWidth;
    }

    @Override
    public float getPrefHeight() {
        refreshChain();
        return chain.groupHeight;
    }

    @Override
    public void layout() {
        refreshChain();
        logoImage.setVisible(chain.logoVisible);
        if (chain.logoVisible) {
            logoImage.setBounds(chain.logoX, chain.logoY, chain.logoWidth, chain.logoHeight);
        }
        progressStack.setBounds(chain.progressX, chain.progressY, chain.progressWidth, chain.progressHeight);
        creditsLabel.setBounds(chain.textX, chain.textY, chain.textWidth, chain.textHeight);
        if (entryButton != null) {
            entryButton.setVisible(chain.buttonVisible);
            entryButton.setBounds(chain.buttonX, chain.buttonY, chain.buttonWidth, chain.buttonHeight);
        }
        errorLabel.setVisible(chain.errorVisible);
        if (chain.errorVisible) {
            errorLabel.setBounds(chain.errorX, chain.errorY, chain.errorWidth, chain.errorHeight);
        }
    }

    @Override
    public void dispose() {
        if (fallbackTrackTexture != null) {
            fallbackTrackTexture.dispose();
            fallbackTrackTexture = null;
        }
        if (fallbackFillTexture != null) {
            fallbackFillTexture.dispose();
            fallbackFillTexture = null;
        }
        if (entryTexture != null) {
            entryTexture.dispose();
            entryTexture = null;
        }
    }

    private void refreshChain() {
        float textWidth = Math.max(config.credits.drawWidth(), 1f);
        creditsLabel.setWidth(textWidth);
        float textHeight = Math.max(
            creditsLabel.getPrefHeight(),
            config.credits.lineHeight * Math.max(1, config.credits.lines.size())
        );
        float errorWidth = textWidth;
        errorLabel.setWidth(errorWidth);
        float errorHeight = errorLabel.isVisible() ? Math.max(errorLabel.getPrefHeight(), config.credits.lineHeight) : 0f;
        LoadingChainLayout.compute(
            logoImage.isVisible() && logoImage.getDrawable() != null,
            config.logo.drawWidth(),
            config.logo.drawHeight(),
            config.logo.offsetX,
            config.progressBar.drawWidth(),
            config.progressBar.drawHeight(),
            config.progressBar.offsetX,
            config.spacing.logoToProgressBarOffset,
            entryButton != null && entryRevealed,
            config.entryButton.drawWidth(),
            config.entryButton.drawHeight(),
            config.entryButton.offsetX,
            config.spacing.progressBarToEntryButtonOffset,
            textWidth,
            textHeight,
            config.credits.offsetX,
            config.spacing.entryButtonToTextOffset,
            errorLabel.isVisible(),
            errorWidth,
            errorHeight,
            config.error.padTop,
            chain
        );
    }

    private void applySkinFonts(Skin skin) {
        BitmapFont font = fontOf(skin, config.credits.font);
        if (font != null) {
            creditsLabel.setStyle(new Label.LabelStyle(font, Color.WHITE));
            creditsLabel.setFontScale(config.credits.fontScale);
            creditsLabel.setWrap(config.credits.wrap);
        }
        BitmapFont errorFont = fontOf(skin, config.error.font);
        if (errorFont != null) {
            errorLabel.setStyle(new Label.LabelStyle(errorFont, Color.WHITE));
            errorLabel.setFontScale(config.error.fontScale);
        }
    }

    private static BitmapFont fontOf(Skin skin, String name) {
        if (name != null && skin.has(name, BitmapFont.class)) {
            return skin.getFont(name);
        }
        if (skin.has("BRIANNETOD", BitmapFont.class)) {
            return skin.getFont("BRIANNETOD");
        }
        if (skin.has("default-font", BitmapFont.class)) {
            return skin.getFont("default-font");
        }
        return skin.get(Label.LabelStyle.class).font;
    }

    private static Texture loadTexture(String path) {
        var file = InternalFiles.resolve(path);
        if (!file.exists()) {
            return null;
        }
        Texture texture = new Texture(file);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    private static Texture solidTexture(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(2, 2, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }
}
