package app.view.game.result;

import app.view.game.pause.PauseLayout;
import app.view.ui.ModalOverlay;
import app.view.ui.SkinPanel;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import game.component.AssetManager;
import game.world.levelConditions.result.StageResult;

import java.util.Arrays;

public abstract class StageResultPopup extends ModalOverlay {
    protected final StageResult result;
    protected final ResultLayout layout;
    protected Label errorLabel;
    private Table panelHost;
    private final Color overlayColor;

    protected StageResultPopup(StageResult result, ResultLayout layout) {
        this.result = result;
        this.layout = layout;
        PauseLayout pause = layout.pause;
        this.overlayColor = new Color(
            pause.overlay.color.r,
            pause.overlay.color.g,
            pause.overlay.color.b,
            pause.overlay.opacity
        );
        setVisible(false);
        setTouchable(Touchable.disabled);
        getColor().a = 0f;
    }

    protected final void assemble() {
        if (panelHost != null) {
            return;
        }
        panelHost = build();
    }

    private Table build() {
        PauseLayout pause = layout.pause;
        SkinPanel root = new SkinPanel(pause.backgrounds.popupRoot);
        root.pad(
            pause.spacing.contentPadding,
            pause.spacing.contentPadding,
            pause.spacing.bottomPadding,
            pause.spacing.contentPadding
        );
        root.top();

        SkinPanel header = new SkinPanel(pause.backgrounds.header);
        header.pad(pause.spacing.tightPadding);
        Image icon = fitIcon(pause.icons.header);
        if (icon != null) {
            header.add(icon)
                .size(pause.header.sectionIconSize, pause.header.sectionIconSize)
                .padRight(pause.header.sectionIconGap);
        }
        header.add(TextSpec.of(pause.text.header).build(titleText())).grow();
        root.add(header).growX().height(pause.size.headerHeight).row();

        Label message = TextSpec.of(pause.text.channel)
            .align(app.view.ui.text.TextAlign.CENTER)
            .wrap(true)
            .build(messageText());
        message.setAlignment(Align.center);
        root.add(message).growX().padTop(pause.spacing.headerSpacing).row();

        root.add(ResultStatsRow.build(layout, result.getStatistics()))
            .growX()
            .padTop(pause.spacing.sectionSpacing)
            .row();

        addBody(root);

        errorLabel = TextSpec.of(pause.text.error).build("");
        errorLabel.setVisible(false);
        errorLabel.setAlignment(Align.center);
        root.add(errorLabel).growX().padTop(pause.spacing.tightPadding).row();

        root.add(createButtons()).growX()
            .height(pause.size.footerHeight)
            .padTop(pause.spacing.sectionSpacing);

        Table scaled = new Table();
        scaled.setTransform(true);
        scaled.setOrigin(Align.center);
        var panel = scaled.add(root).width(layout.panelWidth);
        if (layout.panelHeight > 0f) {
            panel.height(layout.panelHeight);
        }
        add(scaled);
        return scaled;
    }

    protected abstract String titleText();

    protected abstract String messageText();

    protected abstract void addBody(SkinPanel root);

    protected abstract ButtonRow createButtons();

    protected void relayoutPanel() {
        if (panelHost != null) {
            panelHost.invalidateHierarchy();
        }
        invalidateHierarchy();
        validate();
        if (panelHost != null) {
            panelHost.validate();
            panelHost.setOrigin(panelHost.getWidth() * 0.5f, panelHost.getHeight() * 0.5f);
        }
    }

    protected ButtonRow buildButtons(ButtonSpec left, ButtonSpec middle, ButtonSpec right) {
        return new ButtonRow(Arrays.asList(left, middle, right), layout.pause.buttons.actions);
    }

    public void present(Stage stage) {
        assemble();
        if (getStage() == null && stage != null) {
            stage.addActor(this);
        }
        PauseLayout pause = layout.pause;
        clearActions();
        panelHost.clearActions();
        setVisible(true);
        setTouchable(Touchable.enabled);
        toFront();
        validate();
        panelHost.setOrigin(panelHost.getWidth() * 0.5f, panelHost.getHeight() * 0.5f);
        getColor().a = 0f;
        panelHost.setScale(pause.animation.startScale * pause.size.scale);
        addAction(Actions.fadeIn(pause.animation.fadeIn));
        panelHost.addAction(Actions.scaleTo(
            pause.size.scale,
            pause.size.scale,
            pause.animation.scaleDuration,
            Interpolation.sineOut
        ));
    }

    @Override
    public void close() {
        PauseLayout pause = layout.pause;
        clearActions();
        if (panelHost != null) {
            panelHost.clearActions();
            panelHost.addAction(Actions.scaleTo(
                pause.animation.startScale * pause.size.scale,
                pause.animation.startScale * pause.size.scale,
                pause.animation.fadeOut,
                Interpolation.sineIn
            ));
        }
        addAction(Actions.sequence(
            Actions.fadeOut(pause.animation.fadeOut),
            Actions.removeActor()
        ));
    }

    public void showError(String message) {
        if (errorLabel == null) {
            return;
        }
        boolean has = message != null && !message.isBlank();
        errorLabel.setText(has ? message : "");
        errorLabel.setVisible(has);
    }

    public static String buyWinLabel() {
        return ResultBalanceConfigLoader.get().buyWinLabel();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (isVisible()) {
            toFront();
        }
    }

    @Override
    public void layout() {
        super.layout();
        if (panelHost != null) {
            panelHost.setOrigin(panelHost.getWidth() * 0.5f, panelHost.getHeight() * 0.5f);
        }
    }

    @Override
    protected Color overlayColor() {
        return overlayColor;
    }

    private static Image fitIcon(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        TextureRegion region = AssetManager.textureBank.region(id);
        if (region == null) {
            return null;
        }
        Image image = new Image(new TextureRegionDrawable(region));
        image.setScaling(Scaling.fit);
        image.setAlign(Align.center);
        return image;
    }
}
