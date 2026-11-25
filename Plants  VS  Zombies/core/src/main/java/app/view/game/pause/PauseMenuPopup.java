package app.view.game.pause;

import app.view.game.pause.section.ActionButtonsSection;
import app.view.game.pause.section.AudioSettingsSection;
import app.view.game.pause.section.GameInformationSection;
import app.view.game.pause.section.GameplayTogglesSection;
import app.view.ui.ModalOverlay;
import app.view.ui.SkinPanel;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import game.component.AssetManager;
import game.world.GameWorld;

public class PauseMenuPopup extends ModalOverlay {
    private final PauseLayout layout;
    private final Color overlayColor;
    private final GameInformationSection informationSection;
    private final AudioSettingsSection audioSection;
    private final ActionButtonsSection actionsSection;
    private final Table panelHost;

    public PauseMenuPopup(PauseLayout layout, PauseMenuActions actions) {
        this.layout = layout == null ? PauseLayout.defaults() : layout;
        this.overlayColor = new Color(
            this.layout.overlay.color.r,
            this.layout.overlay.color.g,
            this.layout.overlay.color.b,
            this.layout.overlay.opacity
        );
        informationSection = new GameInformationSection(this.layout);
        audioSection = new AudioSettingsSection(this.layout);
        GameplayTogglesSection togglesSection = new GameplayTogglesSection(this.layout);
        actionsSection = new ActionButtonsSection(
            this.layout,
            actions::onContinue,
            actions::onSaveAndExit,
            actions::onRestart
        );
        panelHost = build(togglesSection);
        setVisible(false);
        setTouchable(Touchable.disabled);
        getColor().a = 0f;
        clearActions();
    }

    private Table build(GameplayTogglesSection togglesSection) {
        SkinPanel root = new SkinPanel(layout.backgrounds.popupRoot);
        root.pad(
            layout.spacing.contentPadding,
            layout.spacing.contentPadding,
            layout.spacing.bottomPadding,
            layout.spacing.contentPadding
        );
        root.top();

        SkinPanel header = new SkinPanel(layout.backgrounds.header);
        header.pad(layout.spacing.tightPadding);
        Image icon = icon(layout.icons.header, layout.header.sectionIconSize);
        if (icon != null) {
            header.add(icon)
                .size(layout.header.sectionIconSize)
                .padRight(layout.header.sectionIconGap);
        }
        header.add(TextSpec.of(layout.text.header).build(layout.header.title)).grow();
        root.add(header).growX().height(layout.size.headerHeight).row();

        root.add(informationSection).growX().top()
            .padTop(layout.spacing.headerSpacing).row();
        root.add(audioSection).growX().top()
            .padTop(layout.spacing.sectionSpacing).row();
        root.add(togglesSection).growX().top()
            .padTop(layout.spacing.sectionSpacing).row();
        root.add(actionsSection).growX()
            .padTop(layout.spacing.sectionSpacing);

        Table scaled = new Table();
        scaled.setTransform(true);
        scaled.setOrigin(Align.center);
        var panel = scaled.add(root).width(layout.size.width);
        if (layout.size.height > 0f) {
            panel.height(layout.size.height);
        }
        add(scaled);
        return scaled;
    }

    public void refresh(GameWorld world) {
        informationSection.refresh(world);
        audioSection.refresh();
        actionsSection.clearError();
    }

    public void showError(String message) {
        actionsSection.showError(message);
    }

    public void present(Stage stage) {
        if (getStage() == null && stage != null) {
            stage.addActor(this);
        }
        clearActions();
        panelHost.clearActions();
        setVisible(true);
        setTouchable(Touchable.enabled);
        toFront();
        validate();
        panelHost.setOrigin(panelHost.getWidth() * 0.5f, panelHost.getHeight() * 0.5f);
        getColor().a = 0f;
        panelHost.setScale(layout.animation.startScale * layout.size.scale);
        addAction(Actions.fadeIn(layout.animation.fadeIn));
        panelHost.addAction(Actions.scaleTo(
            layout.size.scale,
            layout.size.scale,
            layout.animation.scaleDuration,
            Interpolation.sineOut
        ));
    }

    public void dismiss() {
        dismiss(null);
    }

    public void dismiss(Runnable after) {
        clearActions();
        panelHost.clearActions();
        addAction(Actions.sequence(
            Actions.fadeOut(layout.animation.fadeOut),
            Actions.run(() -> {
                setVisible(false);
                setTouchable(Touchable.disabled);
                if (after != null) {
                    after.run();
                }
            })
        ));
        panelHost.addAction(Actions.scaleTo(
            layout.animation.startScale * layout.size.scale,
            layout.animation.startScale * layout.size.scale,
            layout.animation.fadeOut,
            Interpolation.sineIn
        ));
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
        panelHost.setOrigin(panelHost.getWidth() * 0.5f, panelHost.getHeight() * 0.5f);
    }

    @Override
    protected Color overlayColor() {
        return overlayColor;
    }

    private static Image icon(String id, float size) {
        if (id == null || id.isBlank()) {
            return null;
        }
        TextureRegion region = AssetManager.textureBank.region(id);
        if (region == null) {
            return null;
        }
        Image image = new Image(new TextureRegionDrawable(region));
        image.setSize(size, size);
        return image;
    }

    public interface PauseMenuActions {
        void onContinue();
        void onSaveAndExit();
        void onRestart();
    }
}
