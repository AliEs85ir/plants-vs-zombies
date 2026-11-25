package app.view.shop.component;

import app.view.mainmenu.config.MainMenuEasings;
import app.view.shop.ShopUiFactory;
import app.view.shop.config.ShopConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

public final class ShopCategoryRow extends WidgetGroup {
    private final ShopConfig.CategoryUiConfig ui;
    private final String categoryId;
    private final Image highlight;
    private final Image selectedBar;
    private final Image icon;
    private final Label label;
    private final Color selectedColor;
    private final Color hoverColor;
    private final Color normalColor;
    private boolean selected;
    private boolean hovered;

    public ShopCategoryRow(
        ShopConfig config,
        String title,
        String categoryId,
        Texture iconTexture,
        BitmapFont font,
        float rowWidth,
        Runnable onSelect
    ) {
        this.ui = config.leftPanel.category;
        this.categoryId = categoryId;
        this.selectedColor = ShopUiFactory.parseColor(ui.selectedColor, Color.GOLD);
        this.hoverColor = ShopUiFactory.parseColor(ui.hoverColor, Color.WHITE);
        this.normalColor = ShopUiFactory.parseColor(ui.normalColor, Color.WHITE);
        setTransform(true);
        setName("shop-category");
        setSize(rowWidth, ui.rowHeight);
        setOrigin(Align.left);
        setTouchable(Touchable.enabled);

        highlight = new Image();
        highlight.setFillParent(true);
        highlight.setColor(selectedColor.r, selectedColor.g, selectedColor.b, 0f);
        highlight.setTouchable(Touchable.disabled);
        addActor(highlight);

        selectedBar = new Image();
        selectedBar.setSize(ui.selectedBarWidth, ui.rowHeight);
        selectedBar.setPosition(0f, 0f);
        selectedBar.setColor(selectedColor.r, selectedColor.g, selectedColor.b, 0f);
        selectedBar.setTouchable(Touchable.disabled);
        addActor(selectedBar);

        icon = new Image();
        if (iconTexture != null) {
            icon.setDrawable(new TextureRegionDrawable(new TextureRegion(iconTexture)));
        }
        icon.setSize(ui.iconSize, ui.iconSize);
        icon.setPosition(ui.selectedBarWidth + 6f, (ui.rowHeight - ui.iconSize) * 0.5f);
        icon.setOrigin(Align.center);
        icon.setTouchable(Touchable.disabled);
        addActor(icon);

        label = ShopUiFactory.label(title, font, normalColor, Align.left);
        label.setFontScale(config.typography.bodyScale);
        float textX = ui.selectedBarWidth + 6f + ui.iconSize + ui.iconPadding;
        label.setSize(Math.max(8f, rowWidth - textX - 8f), ui.rowHeight);
        label.setPosition(textX, 0f);
        label.setTouchable(Touchable.disabled);
        addActor(label);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onSelect != null) {
                    onSelect.run();
                }
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1) {
                    return;
                }
                hovered = true;
                refreshVisuals();
                icon.clearActions();
                icon.addAction(Actions.scaleTo(ui.hoverScale, ui.hoverScale, ui.hoverDuration, MainMenuEasings.parse(config.interaction.easing)));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) {
                    return;
                }
                if (toActor != null && toActor.isDescendantOf(ShopCategoryRow.this)) {
                    return;
                }
                hovered = false;
                refreshVisuals();
                icon.clearActions();
                icon.addAction(Actions.scaleTo(1f, 1f, ui.hoverDuration, MainMenuEasings.parse(config.interaction.easing)));
            }
        });
        refreshVisuals();
    }

    public String categoryId() {
        return categoryId;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        refreshVisuals();
    }

    private void refreshVisuals() {
        float alpha = selected ? ui.selectedBackgroundAlpha : hovered ? ui.hoverBackgroundAlpha : 0f;
        Color tint = selected ? selectedColor : hoverColor;
        highlight.setColor(tint.r, tint.g, tint.b, alpha);
        selectedBar.setColor(selectedColor.r, selectedColor.g, selectedColor.b, selected ? 1f : 0f);
        label.setColor(selected ? selectedColor : hovered ? hoverColor : normalColor);
    }
}
