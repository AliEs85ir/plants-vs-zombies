package app.view.shop.component;

import app.controller.shop.ShopController;
import app.service.shop.ShopFilterState;
import app.view.shop.ShopTextureCache;
import app.view.shop.ShopUiFactory;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopConfig;
import app.view.shop.layout.ShopLayoutMetrics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import game.component.AssetManager;

public final class ShopCategoryPanel extends Table {
    public interface Listener {
        void onCategorySelected(String categoryId);

        void onApplyFilters();

        void onCurrencyDraftChanged();
    }

    private final ShopConfig config;
    private final ShopController controller;
    private final ShopTextureCache textures;
    private final Listener listener;
    private final BitmapFont font;
    private final Color sectionColor;
    private final Color bodyColor;
    private final Color mutedColor;

    private Slider minPriceSlider;
    private Slider maxPriceSlider;
    private Label minPriceLabel;
    private Label maxPriceLabel;
    private SelectBox<String> currencyFilterBox;
    private Label filterStatusLabel;
    private String selectedCategoryId;
    private Table categoryRows;

    public ShopCategoryPanel(
        ShopConfig config,
        ShopController controller,
        ShopTextureCache textures,
        Listener listener
    ) {
        this.config = config;
        this.controller = controller;
        this.textures = textures;
        this.listener = listener;
        this.font = ShopUiFactory.resolveFont(config.leftPanel.font, config.leftPanel.fontFallback);
        this.sectionColor = ShopUiFactory.parseColor(config.typography.sectionTitleColor, Color.GOLD);
        this.bodyColor = ShopUiFactory.parseColor(config.typography.bodyColor, Color.WHITE);
        this.mutedColor = ShopUiFactory.parseColor(config.typography.mutedColor, Color.LIGHT_GRAY);
        top().left();
        Drawable background = ShopUiFactory.tintedSkinDrawable(
            config.leftPanel.backgroundDrawable,
            "image_ui_dialog_asset_inner_bkgd_10",
            config.leftPanel.backgroundAlpha
        );
        if (background != null) {
            setBackground(background);
        }
        rebuild();
    }

    public void rebuild() {
        clearChildren();
        ShopConfig.LeftPanelConfig panel = config.leftPanel;
        pad(panel.padding);
        defaults().left().padBottom(panel.spacing);

        Label categoriesTitle = ShopUiFactory.label(config.labels.categories, font, sectionColor, Align.left);
        categoriesTitle.setFontScale(config.typography.sectionTitleScale);
        add(categoriesTitle).row();

        categoryRows = new Table();
        categoryRows.defaults().left().padBottom(panel.category.rowSpacing);
        addCategoryRow(config.labels.allCategories, null, textures.get(panel.category.allIconPath));
        for (ShopCatalogConfig.CategoryConfig category : controller.categories()) {
            addCategoryRow(category.title, category.id, textures.get(category.iconPath));
        }
        add(categoryRows).row();

        Label priceTitle = ShopUiFactory.label(config.labels.priceFilter, font, sectionColor, Align.left);
        priceTitle.setFontScale(config.typography.sectionTitleScale);
        add(priceTitle).padTop(panel.filterSpacing).row();

        minPriceSlider = new Slider(0f, config.priceFilter.maxDefault, 1f, false, AssetManager.skin);
        maxPriceSlider = new Slider(0f, config.priceFilter.maxDefault, 1f, false, AssetManager.skin);
        minPriceSlider.setName("shop-slider");
        maxPriceSlider.setName("shop-slider");
        minPriceSlider.setValue(config.priceFilter.minDefault);
        maxPriceSlider.setValue(config.priceFilter.maxDefault);
        minPriceLabel = ShopUiFactory.label(String.valueOf(config.priceFilter.minDefault), font, bodyColor);
        maxPriceLabel = ShopUiFactory.label(String.valueOf(config.priceFilter.maxDefault), font, bodyColor);

        minPriceSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = Math.round(minPriceSlider.getValue());
                controller.filterState().setDraftMinPrice(value);
                minPriceLabel.setText(String.valueOf(value));
                if (value > maxPriceSlider.getValue()) {
                    maxPriceSlider.setValue(value);
                }
                refreshFilterStatus();
            }
        });
        maxPriceSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int value = Math.round(maxPriceSlider.getValue());
                controller.filterState().setDraftMaxPrice(value);
                maxPriceLabel.setText(String.valueOf(value));
                if (value < minPriceSlider.getValue()) {
                    minPriceSlider.setValue(value);
                }
                refreshFilterStatus();
            }
        });

        add(minPriceLabel).row();
        add(minPriceSlider).width(config.priceFilter.sliderWidth).row();
        add(maxPriceLabel).row();
        add(maxPriceSlider).width(config.priceFilter.sliderWidth).row();

        Label currencyTitle = ShopUiFactory.label(config.labels.currencyFilter, font, sectionColor, Align.left);
        currencyTitle.setFontScale(config.typography.sectionTitleScale);
        add(currencyTitle).padTop(panel.filterSpacing).row();

        currencyFilterBox = new SelectBox<>(AssetManager.skin);
        Array<String> currencyItems = new Array<>();
        for (ShopCatalogConfig.CurrencyFilterConfig filter : controller.currencyFilters()) {
            if (filter.enabled) {
                currencyItems.add(filter.label);
            }
        }
        currencyFilterBox.setItems(currencyItems);
        currencyFilterBox.setSelected(currencyItems.first());
        currencyFilterBox.setName("shop-select");
        currencyFilterBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applyCurrencyDraft();
                refreshFilterStatus();
            }
        });
        add(currencyFilterBox).width(config.priceFilter.sliderWidth).row();

        filterStatusLabel = ShopUiFactory.label(config.labels.filtersPending, font, mutedColor);
        filterStatusLabel.setFontScale(config.typography.bodyScale * 0.9f);
        add(filterStatusLabel).padTop(4f).row();

        TextButton applyButton = ShopUiFactory.styledButton(
            panel.applyButton,
            font,
            textures,
            config.interaction
        );
        applyButton.setName("shop-apply");
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.applyFilters();
                refreshFilterStatus();
                refreshCategorySelection();
                if (listener != null) {
                    listener.onApplyFilters();
                }
            }
        });
        ShopUiFactory.addCenteredButton(this, applyButton, panel.spacing);
        selectedCategoryId = controller.filterState().getDraftCategoryId();
        refreshFilterStatus();
        refreshCategorySelection();
    }

    private void addCategoryRow(String title, String categoryId, Texture icon) {
        ShopCategoryRow row = new ShopCategoryRow(
            config,
            title,
            categoryId,
            icon,
            font,
            config.leftPanel.width - config.leftPanel.padding * 2f,
            () -> {
                selectedCategoryId = categoryId;
                controller.filterState().setCategoryId(categoryId);
                refreshCategorySelection();
                refreshFilterStatus();
            }
        );
        categoryRows.add(row).size(row.getWidth(), row.getHeight()).left().row();
    }

    public void refreshCategorySelection() {
        for (Actor actor : categoryRows.getChildren()) {
            if (actor instanceof ShopCategoryRow row) {
                String categoryId = row.categoryId();
                boolean selectedRow = (categoryId == null && selectedCategoryId == null)
                    || (categoryId != null && categoryId.equals(selectedCategoryId));
                row.setSelected(selectedRow);
            }
        }
    }

    public void refreshFilterStatus() {
        ShopFilterState state = controller.filterState();
        if (state.hasPendingChanges()) {
            filterStatusLabel.setText(config.labels.filtersPending);
            filterStatusLabel.setColor(mutedColor);
        } else {
            filterStatusLabel.setText(config.labels.filtersApplied);
            filterStatusLabel.setColor(ShopUiFactory.parseColor("7CFF7A", Color.GREEN));
        }
    }

    private void applyCurrencyDraft() {
        String selected = currencyFilterBox.getSelected();
        for (ShopCatalogConfig.CurrencyFilterConfig filter : controller.currencyFilters()) {
            if (filter.label.equals(selected)) {
                if (filter.currency == null || filter.currency.isBlank()) {
                    controller.filterState().setCurrencyFilter(null);
                } else {
                    controller.filterState().setCurrencyFilter(
                        app.service.shop.ShopProductEffects.parseCurrency(filter.currency)
                    );
                }
                return;
            }
        }
    }

    public void layoutWithMetrics(ShopLayoutMetrics metrics) {
        setSize(metrics.leftPanelWidth(), metrics.panelHeight());
    }
}
