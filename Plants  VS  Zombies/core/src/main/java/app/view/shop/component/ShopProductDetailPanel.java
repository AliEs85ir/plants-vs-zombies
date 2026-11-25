package app.view.shop.component;

import app.controller.shop.ShopController;
import app.enums.plant.PlantName;
import app.model.App;
import app.model.shop.purchase.ShopPriceQuote;
import app.model.shop.purchase.ShopPurchaseContext;
import app.model.user.User;
import app.service.shop.ShopCatalog;
import app.service.shop.ShopProductEffects;
import app.view.shop.ShopPurchaseConfirmPopup;
import app.view.shop.ShopCompositeButton;
import app.view.shop.ShopTextureCache;
import app.view.shop.ShopUiFactory;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopConfig;
import app.view.shop.layout.ShopLayoutMetrics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import common.Result;

import java.util.ArrayList;
import java.util.List;

public final class ShopProductDetailPanel extends Table {
    public interface Listener {
        void onBack();

        void onPurchaseResult(Result result);

        void onPurchased();
    }

    private final ShopConfig config;
    private final ShopController controller;
    private final ShopTextureCache textures;
    private final Listener listener;
    private final BitmapFont font;

    private ShopCatalogConfig.ProductConfig product;
    private ShopPurchaseContext context = new ShopPurchaseContext(1);
    private int quantity = 1;
    private float savedScrollY;
    private final List<String> plantNames = new ArrayList<>();

    private Table leftShell;
    private Table leftBody;
    private Table rightShell;
    private ShopCompositeButton titleButton;
    private ShopCompositeButton buyButton;
    private SelectBox<String> optionBox;
    private SelectBox<String> plantBox;
    private ShopValueStepper quantityStepper;
    private TextButton backButton;
    private Label previewTitle;
    private Label descriptionLabel;
    private Image productImage;
    private Image separator;

    public ShopProductDetailPanel(
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
        setFillParent(true);
        setVisible(false);
        setTouchable(Touchable.childrenOnly);
        buildShell();
    }

    private void buildShell() {
        clearChildren();
        top().left();
        Color bodyColor = ShopUiFactory.parseColor(config.typography.bodyColor, Color.WHITE);
        ShopConfig.ProductDetailConfig detail = config.productDetail;
        ShopConfig.CompositeButtonStyleConfig titleStyle = detail.titleButton;
        ShopConfig.CompositeButtonStyleConfig buyStyle = detail.buyButton;

        leftShell = new Table();
        leftShell.top();
        leftShell.pad(config.leftPanel.padding);
        Drawable leftBackground = ShopUiFactory.tintedSkinDrawable(
            config.leftPanel.backgroundDrawable,
            "image_ui_dialog_asset_inner_bkgd_10",
            config.leftPanel.backgroundAlpha
        );
        if (leftBackground != null) {
            leftShell.setBackground(leftBackground);
        }

        String titlePath = titleStyle.backgroundPath.isBlank()
            ? config.productGrid.titleButtonPath
            : titleStyle.backgroundPath;
        String buyPath = buyStyle.backgroundPath.isBlank()
            ? config.productGrid.priceButtonPath
            : buyStyle.backgroundPath;
        titleButton = new ShopCompositeButton(
            textures.get(titlePath),
            null,
            "",
            font,
            titleStyle.fontSize,
            titleStyle.width,
            titleStyle.height,
            titleStyle.iconSize,
            titleStyle.iconOffsetX,
            titleStyle.iconOffsetY,
            titleStyle.textPadLeft,
            titleStyle.textPadRight,
            titleStyle.keepAspect,
            ShopUiFactory.parseColor(titleStyle.fontColor, Color.WHITE)
        );
        titleButton.setTouchable(Touchable.disabled);
        titleButton.setTextAlignment(Align.center);

        Color selectColor = ShopUiFactory.parseColor(detail.selectBox.fontColor, bodyColor);
        optionBox = ShopUiFactory.styledSelectBox(font, selectColor, detail.selectBox);
        plantBox = ShopUiFactory.styledSelectBox(font, selectColor, detail.selectBox);
        optionBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applySelectedOption();
            }
        });
        plantBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                applySelectedPlant();
            }
        });

        Color stepperColor = ShopUiFactory.parseColor(detail.quantityStepper.fontColor, bodyColor);
        quantityStepper = new ShopValueStepper(
            config,
            textures,
            font,
            stepperColor,
            () -> adjustQuantity(-1),
            () -> adjustQuantity(1)
        );

        buyButton = new ShopCompositeButton(
            textures.get(buyPath),
            null,
            "0",
            font,
            buyStyle.fontSize,
            buyStyle.width,
            buyStyle.height,
            buyStyle.iconSize,
            buyStyle.iconOffsetX,
            buyStyle.iconOffsetY,
            buyStyle.textPadLeft,
            buyStyle.textPadRight,
            buyStyle.keepAspect,
            ShopUiFactory.parseColor(buyStyle.fontColor, Color.WHITE)
        );
        buyButton.setName("shop-detail-buy");
        buyButton.setTextAlignment(Align.center);
        ShopUiFactory.attachHoverScale(buyButton, config.interaction);
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                requestPurchaseConfirm();
            }
        });

        leftBody = new Table();
        leftBody.top().center();
        float spacing = detail.sectionSpacing;
        leftBody.add(titleButton).center().padBottom(spacing).row();
        leftBody.add(optionBox).center().padBottom(spacing).row();
        leftBody.add(plantBox).center().padBottom(spacing).row();
        leftBody.add(quantityStepper).center().padBottom(detail.purchaseSectionSpacing).row();
        leftBody.add(buyButton).center().row();

        backButton = ShopUiFactory.styledButton(config.leftPanel.backButton, font, textures, config.interaction);
        backButton.setName("shop-detail-back");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        leftShell.add(leftBody).grow().top().row();
        leftShell.add(backButton).size(backButton.getWidth(), backButton.getHeight()).center().expandX().padTop(spacing);

        rightShell = new Table();
        rightShell.center();
        rightShell.defaults().center();
        Drawable rightBackground = ShopUiFactory.tintedSkinDrawable(
            config.rightPanel.backgroundDrawable,
            "image_ui_generic_guaranteed_bg_10",
            config.rightPanel.backgroundAlpha
        );
        if (rightBackground != null) {
            rightShell.setBackground(rightBackground);
        }
        rightShell.pad(config.rightPanel.padding);

        productImage = new Image();
        productImage.setScaling(Scaling.fit);
        productImage.setAlign(Align.center);
        Color titleColor = ShopUiFactory.parseColor(detail.previewTitleColor, Color.GOLD);
        Color descriptionColor = ShopUiFactory.parseColor(detail.descriptionColor, bodyColor);
        previewTitle = ShopUiFactory.label("", font, titleColor, Align.center);
        previewTitle.setFontScale(detail.titleFontSize);
        descriptionLabel = ShopUiFactory.label("", font, descriptionColor, Align.center);
        descriptionLabel.setWrap(true);
        descriptionLabel.setFontScale(detail.descriptionFontSize);
        descriptionLabel.setAlignment(Align.center);

        rightShell.add(productImage).size(detail.rightImageSize).padTop(detail.rightImagePadTop).row();
        rightShell.add(previewTitle).padTop(detail.sectionSpacing).growX().row();
        rightShell.add(descriptionLabel).width(detail.descriptionWidth).padTop(8f).row();

        separator = new Image();
        separator.setColor(ShopUiFactory.parseColor(config.leftPanel.separatorColor, Color.BROWN));

        add(leftShell).top().left();
        add(separator).width(config.leftPanel.separatorWidth).expandY().fillY().padLeft(config.leftPanel.separatorPadding).top();
        add(rightShell).expand().fill().padLeft(config.leftPanel.separatorPadding).top();
    }

    public void open(ShopCatalogConfig.ProductConfig productConfig, float scrollY) {
        this.product = productConfig;
        this.savedScrollY = scrollY;
        this.quantity = productConfig.defaultQuantity;
        this.context = new ShopPurchaseContext(quantity);
        setVisible(true);

        titleButton.setLabelText(productConfig.title);
        ShopCatalogConfig.CategoryConfig category = ShopCatalog.current().category(productConfig.categoryId);
        titleButton.setIcon(category == null ? null : textures.get(category.iconPath));
        previewTitle.setText(productConfig.title);
        descriptionLabel.setText(productConfig.description);

        Texture image = textures.get(productConfig.imagePath);
        if (image != null) {
            productImage.setDrawable(new TextureRegionDrawable(new TextureRegion(image)));
            productImage.setVisible(true);
        } else {
            productImage.setDrawable(null);
            productImage.setVisible(false);
        }

        populateSelectors();
        updatePrice();
        invalidateHierarchy();
    }

    public void hide() {
        setVisible(false);
        product = null;
    }

    public float savedScrollY() {
        return savedScrollY;
    }

    public boolean isOpen() {
        return isVisible();
    }

    private void populateSelectors() {
        boolean hasOptions = product.options != null && !product.options.isEmpty();
        if (hasOptions) {
            Array<String> labels = new Array<>();
            int selected = 0;
            for (int i = 0; i < product.options.size(); i++) {
                ShopCatalogConfig.ProductOptionConfig option = product.options.get(i);
                labels.add(option.label);
                if (option.defaultOption) {
                    selected = i;
                }
            }
            optionBox.setItems(labels);
            optionBox.setSelectedIndex(selected);
            context.setSelectedOptionId(product.options.get(selected).id);
        } else {
            optionBox.setItems(new Array<>());
            context.setSelectedOptionId(null);
        }
        setCellVisible(optionBox, hasOptions, config.productDetail.selectBox.width, config.productDetail.selectBox.height);

        plantNames.clear();
        boolean needsPlant = product.requiresPlantSelection;
        if (needsPlant) {
            User user = App.getCurrentUser();
            if (user != null) {
                for (PlantName plantName : user.getUnlockedPlants()) {
                    plantNames.add(plantName.name());
                }
            }
            Array<String> plants = new Array<>();
            for (String name : plantNames) {
                plants.add(name);
            }
            plantBox.setItems(plants);
            if (!plantNames.isEmpty()) {
                plantBox.setSelectedIndex(0);
                context.setTargetPlant(PlantName.valueOf(plantNames.get(0)));
            } else {
                context.setTargetPlant(null);
            }
        } else {
            plantBox.setItems(new Array<>());
            context.setTargetPlant(null);
        }
        setCellVisible(plantBox, needsPlant && !plantNames.isEmpty(), config.productDetail.selectBox.width, config.productDetail.selectBox.height);

        boolean variableQuantity = !"fixed".equalsIgnoreCase(product.quantityMode);
        if (!variableQuantity) {
            quantity = 1;
            context.setQuantity(1);
        }
        quantityStepper.setValueText(String.valueOf(quantity));
        setCellVisible(quantityStepper, variableQuantity, quantityStepper.getWidth(), quantityStepper.getHeight());
    }

    private void setCellVisible(Actor actor, boolean visible, float width, float height) {
        actor.setVisible(visible);
        Cell cell = leftBody.getCell(actor);
        if (cell == null) {
            return;
        }
        if (visible) {
            cell.size(width, height).padBottom(config.productDetail.sectionSpacing).center();
        } else {
            cell.size(0f, 0f).padBottom(0f);
        }
    }

    private void applySelectedOption() {
        if (product == null || product.options == null || product.options.isEmpty() || !optionBox.isVisible()) {
            return;
        }
        int index = optionBox.getSelectedIndex();
        if (index < 0 || index >= product.options.size()) {
            return;
        }
        context.setSelectedOptionId(product.options.get(index).id);
        updatePrice();
    }

    private void applySelectedPlant() {
        if (product == null || plantNames.isEmpty() || !plantBox.isVisible()) {
            return;
        }
        int index = plantBox.getSelectedIndex();
        if (index < 0 || index >= plantNames.size()) {
            return;
        }
        context.setTargetPlant(PlantName.valueOf(plantNames.get(index)));
        updatePrice();
    }

    private void adjustQuantity(int delta) {
        if (product == null || "fixed".equalsIgnoreCase(product.quantityMode)) {
            return;
        }
        User user = App.getCurrentUser();
        int max = ShopProductEffects.maxQuantity(user, product, context);
        int step = Math.max(1, product.quantityStep);
        quantity = Math.max(product.minQuantity, Math.min(max, quantity + delta * step));
        context.setQuantity(quantity);
        quantityStepper.setValueText(String.valueOf(quantity));
        updatePrice();
    }

    private void updatePrice() {
        if (product == null) {
            return;
        }
        context.setQuantity(quantity);
        ShopPriceQuote quote = ShopProductEffects.calculatePrice(product, context);
        buyButton.setLabelText(String.valueOf(quote.getTotalAmount()));
        buyButton.setIcon(textures.get(config.currencyIcon(quote.getCurrencyType().name())));
    }

    private void requestPurchaseConfirm() {
        ShopPurchaseConfirmPopup.show(getStage(), config, this::purchase);
    }

    private void purchase() {
        if (product == null) {
            return;
        }
        context.setQuantity(quantity);
        Result result = controller.buy(product, context);
        if (listener != null) {
            listener.onPurchaseResult(result);
        }
        if (result.success()) {
            hide();
            if (listener != null) {
                listener.onPurchased();
            }
        }
    }

    private void applyComposite(
        ShopCompositeButton button,
        ShopConfig.CompositeButtonStyleConfig style,
        float maxWidth
    ) {
        button.applyLayout(
            Math.min(style.width, maxWidth),
            style.height,
            style.iconSize,
            style.iconOffsetX,
            style.iconOffsetY,
            style.textPadLeft,
            style.textPadRight,
            style.fontSize,
            ShopUiFactory.parseColor(style.fontColor, Color.WHITE),
            style.keepAspect
        );
        Cell cell = leftBody.getCell(button);
        if (cell != null) {
            cell.size(button.getWidth(), button.getHeight())
                .padLeft(style.offsetX)
                .padBottom(config.productDetail.sectionSpacing + style.offsetY)
                .center();
        }
    }

    public void layoutWithMetrics(ShopLayoutMetrics metrics) {
        setSize(metrics.stageWidth(), metrics.stageHeight());
        pad(
            metrics.marginTop(),
            metrics.marginLeft(),
            metrics.marginBottom(),
            metrics.marginRight()
        );
        getCell(leftShell).width(metrics.leftPanelWidth()).height(metrics.panelHeight());
        getCell(rightShell).width(metrics.rightPanelWidth()).height(metrics.panelHeight());
        getCell(separator).width(config.leftPanel.separatorWidth).padLeft(config.leftPanel.separatorPadding);
        getCell(rightShell).padLeft(config.leftPanel.separatorPadding);

        float leftInner = Math.max(40f, metrics.leftPanelWidth() - config.leftPanel.padding * 2f);
        float rightInner = Math.max(
            40f,
            metrics.rightPanelWidth() - metrics.su(config.rightPanel.padding) * 2f
        );
        rightShell.pad(metrics.su(config.rightPanel.padding));

        ShopConfig.ProductDetailConfig detail = config.productDetail;
        applyComposite(titleButton, detail.titleButton, leftInner);
        applyComposite(buyButton, detail.buyButton, leftInner);

        quantityStepper.applySize();
        if (quantityStepper.getWidth() > leftInner) {
            quantityStepper.fitTo(leftInner);
        }
        if (quantityStepper.isVisible()) {
            setCellVisible(quantityStepper, true, quantityStepper.getWidth(), quantityStepper.getHeight());
        }

        float selectW = Math.min(detail.selectBox.width, leftInner);
        float selectH = detail.selectBox.height;
        if (optionBox.isVisible()) {
            optionBox.setSize(selectW, selectH);
            setCellVisible(optionBox, true, selectW, selectH);
            leftBody.getCell(optionBox).padLeft(detail.selectBox.offsetX).padBottom(detail.sectionSpacing + detail.selectBox.offsetY);
        }
        if (plantBox.isVisible()) {
            plantBox.setSize(selectW, selectH);
            setCellVisible(plantBox, true, selectW, selectH);
            leftBody.getCell(plantBox).padLeft(detail.selectBox.offsetX).padBottom(detail.sectionSpacing + detail.selectBox.offsetY);
        }

        leftShell.getCell(backButton)
            .size(backButton.getWidth(), backButton.getHeight())
            .center()
            .expandX();

        float imageSize = detail.rightImageSize;
        float descWidth = Math.min(detail.descriptionWidth, rightInner);
        rightShell.getCell(productImage).size(imageSize, imageSize).padTop(detail.rightImagePadTop).center();
        previewTitle.setFontScale(detail.titleFontSize);
        previewTitle.setColor(ShopUiFactory.parseColor(detail.previewTitleColor, Color.GOLD));
        descriptionLabel.setFontScale(detail.descriptionFontSize);
        descriptionLabel.setColor(ShopUiFactory.parseColor(detail.descriptionColor, Color.WHITE));
        rightShell.getCell(previewTitle).growX().center();
        rightShell.getCell(descriptionLabel).width(descWidth).center();
        descriptionLabel.setWidth(descWidth);
    }
}
