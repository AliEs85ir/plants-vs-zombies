package app.view.mainmenu.profile;

import app.view.ui.ScrollPanes;
import app.view.ui.Solid;
import app.view.ui.UiTheme;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import game.audio.AudioId;
import game.audio.AudioManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class ProfilePicturePanel extends Table {
    private final ProfileMenuConfig.PictureConfig config;
    private final Consumer<String> onSelect;
    private final Image previewImage;
    private final Image previewBorder;
    private final Image previewSelected;
    private final List<PictureCell> cells = new ArrayList<>();
    private String selectedId = ProfileAvatars.DEFAULT_ID;
    private boolean pickingEnabled = true;

    public ProfilePicturePanel(ProfileMenuConfig menuConfig, Consumer<String> onSelect) {
        this.config = menuConfig.picture;
        this.onSelect = onSelect;
        top();
        setBackground(Solid.drawable(
            ProfileMenuConfig.color(config.panelColor, Color.valueOf("1A140ED4")),
            config.panelWidth,
            200f
        ));
        pad(config.panelPadY, config.panelPadX, config.panelPadY, config.panelPadX);

        Label title = new Label(config.title, new Label.LabelStyle(
            ProfileUiFonts.resolve(config.titleFont, config.titleFontFallback),
            ProfileMenuConfig.color(config.titleColor, Color.valueOf("F4E7B0"))
        ));
        title.setFontScale(config.titleFontSize);
        title.setAlignment(Align.center);
        add(title).growX().center();
        row();

        Label subtitle = new Label(config.subtitle, new Label.LabelStyle(
            ProfileUiFonts.resolve(config.subtitleFont, config.subtitleFontFallback),
            ProfileMenuConfig.color(config.subtitleColor, Color.valueOf("E4DCC4"))
        ));
        subtitle.setFontScale(config.subtitleFontSize);
        subtitle.setAlignment(Align.center);
        add(subtitle).growX().center().padBottom(8f);
        row();

        previewImage = framedImage();
        previewBorder = framedImage();
        previewSelected = framedImage();
        applyRegion(previewBorder, ProfileAvatars.overlay(ProfileAvatars.BORDER_ID));
        applyRegion(previewSelected, ProfileAvatars.overlay(ProfileAvatars.SELECTED_OVERLAY_ID));
        previewSelected.setVisible(false);

        Stack preview = new Stack();
        Table previewSlot = new Table();
        previewSlot.setBackground(selectionBackground(config.previewSize));
        previewSlot.add(previewImage).size(config.previewSize);
        preview.add(previewSlot);
        preview.add(previewBorder);
        preview.add(previewSelected);
        add(preview).size(config.previewSize).center().padBottom(config.previewBottomPadding);
        row();

        Table grid = new Table();
        grid.top();
        int column = 0;
        for (String pictureId : ProfileAvatars.suggestions()) {
            PictureCell cell = new PictureCell(pictureId);
            cells.add(cell);
            grid.add(cell).size(config.cellSize).pad(config.cellGap * 0.5f);
            column++;
            if (column >= config.gridColumns) {
                grid.row();
                column = 0;
            }
        }
        ScrollPane scroller = ScrollPanes.verticalHidden(grid);
        add(scroller).grow().top();
        setSelected(ProfileAvatars.DEFAULT_ID, false);
    }

    public void setSelected(String pictureId) {
        setSelected(pictureId, false);
    }

    public void setPickingEnabled(boolean enabled) {
        pickingEnabled = enabled;
        setTouchable(enabled ? Touchable.childrenOnly : Touchable.disabled);
        Color tint = enabled ? Color.WHITE : new Color(1f, 1f, 1f, 0.45f);
        setColor(tint);
    }

    private void setSelected(String pictureId, boolean announce) {
        selectedId = ProfileAvatars.normalize(pictureId);
        applyRegion(previewImage, ProfileAvatars.region(selectedId));
        previewSelected.setVisible(true);
        for (PictureCell cell : cells) {
            cell.setChosen(cell.pictureId.equals(selectedId));
        }
        if (announce && onSelect != null) {
            onSelect.accept(selectedId);
        }
    }

    private Image framedImage() {
        Image image = new Image();
        image.setScaling(Scaling.fit);
        image.setAlign(Align.center);
        image.setFillParent(true);
        return image;
    }

    private Drawable selectionBackground(float size) {
        Drawable skinBg = UiTheme.tintedDrawable(
            config.selectionBackgroundDrawable,
            UiTheme.BG_GUARANTEE,
            config.selectionBackgroundAlpha
        );
        if (skinBg != null) {
            return skinBg;
        }
        return Solid.drawable(
            ProfileMenuConfig.color(config.slotColor, new Color(0f, 0f, 0f, 0.4f)),
            size,
            size
        );
    }

    private static void applyRegion(Image image, TextureRegion region) {
        if (region == null) {
            image.setDrawable(null);
            image.setVisible(false);
            return;
        }
        image.setDrawable(new TextureRegionDrawable(region));
        image.setVisible(true);
    }

    private final class PictureCell extends Table {
        private final String pictureId;
        private final Image portrait;
        private final Image selectedOverlay;
        private boolean chosen;

        private PictureCell(String pictureId) {
            this.pictureId = pictureId;
            setTransform(true);
            setOrigin(Align.center);
            setTouchable(Touchable.enabled);
            setBackground(selectionBackground(config.cellSize));

            portrait = new Image();
            portrait.setScaling(Scaling.fit);
            portrait.setFillParent(true);
            applyRegion(portrait, ProfileAvatars.region(pictureId));

            selectedOverlay = new Image();
            selectedOverlay.setScaling(Scaling.fill);
            selectedOverlay.setFillParent(true);
            applyRegion(selectedOverlay, ProfileAvatars.overlay(ProfileAvatars.SELECTED_OVERLAY_ID));
            selectedOverlay.setVisible(false);

            Stack stack = new Stack();
            stack.add(portrait);
            stack.add(selectedOverlay);
            add(stack).grow();
            pad(config.selectedBorderWidth);

            addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer != -1 || !pickingEnabled) {
                        return;
                    }
                    clearActions();
                    addAction(Actions.scaleTo(config.hoverScale, config.hoverScale, config.hoverDuration));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if (pointer != -1) {
                        return;
                    }
                    clearActions();
                    addAction(Actions.scaleTo(1f, 1f, config.hoverDuration));
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!pickingEnabled || chosen) {
                        return;
                    }
                    AudioManager.play(AudioId.UI_CLICK);
                    ProfilePicturePanel.this.setSelected(pictureId, true);
                }
            });
        }

        @Override
        protected void sizeChanged() {
            super.sizeChanged();
            setOrigin(getWidth() * 0.5f, getHeight() * 0.5f);
        }

        private void setChosen(boolean chosen) {
            this.chosen = chosen;
            selectedOverlay.setVisible(chosen && selectedOverlay.getDrawable() != null);
            if (chosen) {
                Color border = ProfileMenuConfig.color(config.selectedBorderColor, Color.valueOf("F4C84A"));
                setBackground(Solid.drawable(border, config.cellSize, config.cellSize));
            } else {
                setBackground(selectionBackground(config.cellSize));
            }
        }
    }
}
