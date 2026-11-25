package app.view.shop.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShopConfig {
    public String backgroundPath = "menu/bg/shop.png";
    public String titlePath = "menu/image/shop_title.png";
    public LayoutConfig layout = new LayoutConfig();
    public TitleConfig title = new TitleConfig();
    public LabelsConfig labels = new LabelsConfig();
    public PurchaseConfirmConfig purchaseConfirm = new PurchaseConfirmConfig();
    public TypographyConfig typography = new TypographyConfig();
    public InteractionConfig interaction = new InteractionConfig();
    public LeftPanelConfig leftPanel = new LeftPanelConfig();
    public RightPanelConfig rightPanel = new RightPanelConfig();
    public ScrollPaneConfig scrollPane = new ScrollPaneConfig();
    public DailyOfferUiConfig dailyOffer = new DailyOfferUiConfig();
    public AdvertisementUiConfig advertisement = new AdvertisementUiConfig();
    public ProductGridConfig productGrid = new ProductGridConfig();
    public PriceFilterConfig priceFilter = new PriceFilterConfig();
    public ProductDetailConfig productDetail = new ProductDetailConfig();
    public Map<String, String> currencyIcons = new HashMap<>();
    public MessagesOverlayConfig messages = new MessagesOverlayConfig();
    public LayersConfig layers = new LayersConfig();
    public FormatsConfig formats = new FormatsConfig();

    public ShopConfig sanitize() {
        backgroundPath = text(backgroundPath, "menu/bg/shop.png");
        titlePath = text(titlePath, "menu/image/shop_title.png");
        if (layout == null) layout = new LayoutConfig();
        if (title == null) title = new TitleConfig();
        if (labels == null) labels = new LabelsConfig();
        if (purchaseConfirm == null) purchaseConfirm = new PurchaseConfirmConfig();
        if (typography == null) typography = new TypographyConfig();
        if (interaction == null) interaction = new InteractionConfig();
        if (leftPanel == null) leftPanel = new LeftPanelConfig();
        if (rightPanel == null) rightPanel = new RightPanelConfig();
        if (scrollPane == null) scrollPane = new ScrollPaneConfig();
        if (dailyOffer == null) dailyOffer = new DailyOfferUiConfig();
        if (advertisement == null) advertisement = new AdvertisementUiConfig();
        if (productGrid == null) productGrid = new ProductGridConfig();
        if (priceFilter == null) priceFilter = new PriceFilterConfig();
        if (productDetail == null) productDetail = new ProductDetailConfig();
        if (currencyIcons == null) currencyIcons = new HashMap<>();
        if (messages == null) messages = new MessagesOverlayConfig();
        if (layers == null) layers = new LayersConfig();
        if (formats == null) formats = new FormatsConfig();
        layout.sanitize();
        title.sanitize();
        labels.sanitize();
        purchaseConfirm.sanitize();
        typography.sanitize();
        interaction.sanitize();
        leftPanel.sanitize();
        rightPanel.sanitize();
        scrollPane.sanitize();
        dailyOffer.sanitize();
        advertisement.sanitize();
        productGrid.sanitize();
        priceFilter.sanitize();
        productDetail.sanitize();
        messages.sanitize();
        layers.sanitize();
        formats.sanitize();
        putDefaultIcon("COIN", "menu/icon/coin2.png");
        putDefaultIcon("GEM", "menu/icon/gem.png");
        putDefaultIcon("REAL_MONEY", "menu/icon/gem2.png");
        return this;
    }

    private void putDefaultIcon(String key, String path) {
        if (!currencyIcons.containsKey(key) || currencyIcons.get(key) == null || currencyIcons.get(key).isBlank()) {
            currencyIcons.put(key, path);
        }
    }

    public String currencyIcon(String currency) {
        if (currency == null) {
            return currencyIcons.getOrDefault("COIN", "menu/icon/coin2.png");
        }
        return currencyIcons.getOrDefault(currency.toUpperCase(Locale.ROOT), "menu/icon/coin2.png");
    }

    public static class LayoutConfig {
        public float referenceWidth = 1280f;
        public float referenceHeight = 720f;
        public float marginLeft = 20f;
        public float marginRight = 20f;
        public float marginTop = 96f;
        public float marginBottom = 16f;
        public float leftPanelWidthRatio = 0.22f;
        public float leftPanelMaxWidth = 240f;

        public void sanitize() {
            referenceWidth = positive(referenceWidth, 1280f);
            referenceHeight = positive(referenceHeight, 720f);
            marginLeft = Math.max(0f, marginLeft);
            marginRight = Math.max(0f, marginRight);
            marginTop = Math.max(0f, marginTop);
            marginBottom = Math.max(0f, marginBottom);
            leftPanelWidthRatio = clamp(leftPanelWidthRatio, 0.12f, 0.35f, 0.22f);
            leftPanelMaxWidth = positive(leftPanelMaxWidth, 240f);
        }
    }

    public static class LabelsConfig {
        /** Shop UI locale: "en" or "fa". */
        public String locale = "en";
        public String categories = "Categories";
        public String allCategories = "All";
        public String priceFilter = "Price Filter";
        public String currencyFilter = "Currency";
        public String quantityPrefix = "Qty: ";
        public String minus = "-";
        public String plus = "+";
        public String buyPrefix = "Buy ";
        public String discountPrefix = "-";
        public String discountSuffix = "%";
        public String filtersPending = "Filters not applied";
        public String filtersApplied = "Filters applied";

        public void sanitize() {
            locale = text(locale, "en");
            categories = text(categories, "Categories");
            allCategories = text(allCategories, "All");
            priceFilter = text(priceFilter, "Price Filter");
            currencyFilter = text(currencyFilter, "Currency");
            quantityPrefix = text(quantityPrefix, "Qty: ");
            minus = text(minus, "-");
            plus = text(plus, "+");
            buyPrefix = text(buyPrefix, "Buy ");
            discountPrefix = text(discountPrefix, "-");
            discountSuffix = text(discountSuffix, "%");
            filtersPending = text(filtersPending, "Filters not applied");
            filtersApplied = text(filtersApplied, "Filters applied");
        }
    }

    public static class TypographyConfig {
        public float sectionTitleScale = 0.95f;
        public float bodyScale = 0.85f;
        public float priceScale = 0.9f;
        public float detailTitleScale = 1.35f;
        public float detailDescriptionScale = 0.88f;
        public float detailPriceScale = 1.05f;
        public float lineSpacing = 1.15f;
        public String sectionTitleColor = "FFD700";
        public String bodyColor = "FFFDF5";
        public String mutedColor = "C8B8A0";

        public void sanitize() {
            sectionTitleScale = positive(sectionTitleScale, 0.95f);
            bodyScale = positive(bodyScale, 0.85f);
            priceScale = positive(priceScale, 0.9f);
            detailTitleScale = positive(detailTitleScale, 1.35f);
            detailDescriptionScale = positive(detailDescriptionScale, 0.88f);
            detailPriceScale = positive(detailPriceScale, 1.05f);
            lineSpacing = positive(lineSpacing, 1.15f);
            sectionTitleColor = text(sectionTitleColor, "FFD700");
            bodyColor = text(bodyColor, "FFFDF5");
            mutedColor = text(mutedColor, "C8B8A0");
        }
    }

    public static class InteractionConfig {
        public float hoverScale = 1.04f;
        public float hoverDuration = 0.15f;
        public float pressedScale = 0.96f;
        public String easing = "sineOut";

        public void sanitize() {
            hoverScale = positive(hoverScale, 1.04f);
            hoverDuration = Math.max(0f, hoverDuration);
            pressedScale = positive(pressedScale, 0.96f);
            easing = text(easing, "sineOut");
        }
    }

    public static class ScrollPaneConfig {
        public boolean fadeScrollBars = false;
        public float scrollBarWidth = 4f;
        public float scrollBarPadRight = 6f;
        public float overscrollDistance = 0f;

        public void sanitize() {
            scrollBarWidth = positive(scrollBarWidth, 4f);
            scrollBarPadRight = Math.max(0f, scrollBarPadRight);
            overscrollDistance = Math.max(0f, overscrollDistance);
        }
    }

    public static class TimerUiConfig {
        public float width = 140f;
        public float height = 36f;
        public float iconSize = 28f;
        public float iconOffsetX = 0f;
        public float iconOffsetY = 0f;
        public float textOffsetX = 6f;
        public float textOffsetY = 0f;
        public float fontSize = 0.9f;
        public String color = "FFFDF5";
        public String finishedColor = "A8A8A8";

        public void sanitize() {
            width = positive(width, 140f);
            height = positive(height, 36f);
            iconSize = positive(iconSize, 28f);
            fontSize = positive(fontSize, 0.9f);
            color = text(color, "FFFDF5");
            finishedColor = text(finishedColor, "A8A8A8");
        }
    }

    public static class CategoryUiConfig {
        public String allIconPath = "menu/icon/shop.png";
        public float iconSize = 40f;
        public float iconPadding = 10f;
        public float rowHeight = 48f;
        public float rowSpacing = 4f;
        public float selectedBarWidth = 5f;
        public String selectedColor = "FFE566";
        public String hoverColor = "FFFFFF";
        public String normalColor = "FFFDF5";
        public float selectedBackgroundAlpha = 0.32f;
        public float hoverBackgroundAlpha = 0.16f;
        public float hoverScale = 1.04f;
        public float hoverDuration = 0.12f;

        public void sanitize() {
            allIconPath = text(allIconPath, "menu/icon/shop.png");
            iconSize = positive(iconSize, 40f);
            iconPadding = Math.max(0f, iconPadding);
            rowHeight = positive(rowHeight, 48f);
            rowSpacing = Math.max(0f, rowSpacing);
            selectedBarWidth = positive(selectedBarWidth, 5f);
            selectedColor = text(selectedColor, "FFE566");
            hoverColor = text(hoverColor, "FFFFFF");
            normalColor = text(normalColor, "FFFDF5");
            selectedBackgroundAlpha = clamp(selectedBackgroundAlpha, 0f, 1f, 0.32f);
            hoverBackgroundAlpha = clamp(hoverBackgroundAlpha, 0f, 1f, 0.16f);
            hoverScale = positive(hoverScale, 1.04f);
            hoverDuration = Math.max(0f, hoverDuration);
        }
    }

    public static class CardLayoutConfig {
        public float imageOffsetX = 0f;
        public float imageOffsetY = 44f;
        public float imagePaddingBottom = 8f;
        public float titleMarginX = 10f;
        public float titleMarginY = 12f;
        public float titlePriceGap = 4f;
        public float buttonIconSize = 34f;
        public float buttonIconPadLeft = 8f;
        public float buttonIconPadY = 0f;
        public float buttonTextPadLeft = 6f;
        public float buttonTextPadRight = 8f;
        public float categoryIconSize = 34f;
        public float categoryIconOffsetX = 8f;
        public float categoryIconOffsetY = 0f;
        public float cardHoverScale = 1.02f;
        public float cardHoverDuration = 0.15f;
        public float titleButtonSizeScale = 0.9f;
        public float priceButtonSizeScale = 0.9f;
        public float titleFontScale = 1.15f;
        public float priceFontScale = 1.15f;
        public float titleIconScale = 1.14f;
        public float priceIconScale = 1.14f;
        public boolean titleKeepAspect = true;
        public boolean priceKeepAspect = true;
        public String titleFontColor = "fffbed";
        public String priceFontColor = "fffbed";
        public float titleIconOffsetX = 0f;
        public float titleIconOffsetY = 0f;
        public float priceIconOffsetX = 0f;
        public float priceIconOffsetY = 0f;

        public void sanitize() {
            imagePaddingBottom = Math.max(0f, imagePaddingBottom);
            titleMarginX = Math.max(0f, titleMarginX);
            titleMarginY = Math.max(0f, titleMarginY);
            titlePriceGap = Math.max(0f, titlePriceGap);
            buttonIconSize = positive(buttonIconSize, 34f);
            buttonIconPadLeft = Math.max(0f, buttonIconPadLeft);
            buttonTextPadLeft = Math.max(0f, buttonTextPadLeft);
            buttonTextPadRight = Math.max(0f, buttonTextPadRight);
            categoryIconSize = positive(categoryIconSize, 34f);
            cardHoverScale = positive(cardHoverScale, 1.02f);
            cardHoverDuration = Math.max(0f, cardHoverDuration);
            titleButtonSizeScale = positive(titleButtonSizeScale, 0.9f);
            priceButtonSizeScale = positive(priceButtonSizeScale, 0.9f);
            titleFontScale = positive(titleFontScale, 1.15f);
            priceFontScale = positive(priceFontScale, 1.15f);
            titleIconScale = positive(titleIconScale, 1.14f);
            priceIconScale = positive(priceIconScale, 1.14f);
            titleFontColor = text(titleFontColor, "fffbed");
            priceFontColor = text(priceFontColor, "fffbed");
        }
    }

    public static class TitleConfig {
        public float anchorX = 0.5f;
        public float anchorY = 1f;
        public float offsetX = 0f;
        public float offsetY = -10f;
        public float x = 640f;
        public float y = 640f;
        public float width = 120f;
        public float height = 120f;
        public float scale = 1f;
        public boolean keepAspect = true;
        public float contentGap = 10f;

        public void sanitize() {
            width = positive(width, 120f);
            height = positive(height, 120f);
            scale = positive(scale, 1f);
            anchorX = clamp(anchorX, 0f, 1f, 0.5f);
            anchorY = clamp(anchorY, 0f, 1f, 1f);
            contentGap = Math.max(0f, contentGap);
        }
    }

    public static class ButtonStyleConfig {
        public String backgroundPath = "menu/image/btn5.png";
        public float width = 220f;
        public float height = 48f;
        public String label = "";
        public float fontSize = 0.9f;
        public String fontColor = "FFFFFF";
        public boolean keepAspect = true;

        public void sanitize() {
            backgroundPath = text(backgroundPath, "menu/image/btn5.png");
            width = positive(width, 220f);
            height = positive(height, 48f);
            label = text(label, "");
            fontSize = positive(fontSize, 0.9f);
            fontColor = text(fontColor, "FFFFFF");
        }

        public ButtonStyleConfig copy() {
            ButtonStyleConfig copy = new ButtonStyleConfig();
            copy.backgroundPath = backgroundPath;
            copy.width = width;
            copy.height = height;
            copy.label = label;
            copy.fontSize = fontSize;
            copy.fontColor = fontColor;
            copy.keepAspect = keepAspect;
            return copy;
        }
    }

    public static class CompositeButtonStyleConfig {
        public String backgroundPath = "";
        public float width = 180f;
        public float height = 40f;
        public boolean keepAspect = true;
        public float iconSize = 34f;
        public float iconOffsetX = 8f;
        public float iconOffsetY = 0f;
        public float textPadLeft = 6f;
        public float textPadRight = 8f;
        public float fontSize = 1f;
        public String fontColor = "fffbed";
        public float offsetX = 0f;
        public float offsetY = 0f;

        public void sanitize() {
            backgroundPath = backgroundPath == null ? "" : backgroundPath.trim();
            width = positive(width, 180f);
            height = positive(height, 40f);
            iconSize = positive(iconSize, 34f);
            fontSize = positive(fontSize, 1f);
            fontColor = text(fontColor, "fffbed");
            textPadLeft = Math.max(0f, textPadLeft);
            textPadRight = Math.max(0f, textPadRight);
        }
    }

    public static class SelectBoxStyleConfig {
        public float width = 180f;
        public float height = 36f;
        public float fontSize = 0.9f;
        public String fontColor = "fffbed";
        public float offsetX = 0f;
        public float offsetY = 0f;

        public void sanitize() {
            width = positive(width, 180f);
            height = positive(height, 36f);
            fontSize = positive(fontSize, 0.9f);
            fontColor = text(fontColor, "fffbed");
        }
    }

    public static class LeftPanelConfig {
        public float x = 24f;
        public float y = 24f;
        public float width = 220f;
        public float height = 672f;
        public float padding = 8f;
        public float spacing = 6f;
        public String backgroundDrawable = "image_ui_dialog_asset_inner_bkgd_10";
        public float backgroundAlpha = 0.8f;
        public float separatorWidth = 2f;
        public String separatorColor = "6B5344";
        public float separatorPadding = 10f;
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.85f;
        public String fontColor = "FFFDF5";
        public float categorySpacing = 8f;
        public float filterSpacing = 8f;
        public float categoryIconSize = 40f;
        public CategoryUiConfig category = new CategoryUiConfig();
        public ButtonStyleConfig applyButton = new ButtonStyleConfig();
        public ButtonStyleConfig backButton = new ButtonStyleConfig();

        public void sanitize() {
            width = positive(width, 220f);
            height = positive(height, 672f);
            padding = Math.max(0f, padding);
            spacing = Math.max(0f, spacing);
            separatorWidth = Math.max(1f, separatorWidth);
            separatorColor = text(separatorColor, "6B5344");
            separatorPadding = Math.max(0f, separatorPadding);
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, font);
            fontSize = positive(fontSize, 0.85f);
            fontColor = text(fontColor, "FFFDF5");
            backgroundDrawable = text(backgroundDrawable, "image_ui_dialog_asset_inner_bkgd_10");
            backgroundAlpha = clamp(backgroundAlpha, 0f, 1f, 0.8f);
            if (category == null) category = new CategoryUiConfig();
            category.sanitize();
            if (applyButton == null) applyButton = new ButtonStyleConfig();
            if (backButton == null) backButton = new ButtonStyleConfig();
            applyButton.sanitize();
            backButton.sanitize();
            if (applyButton.label.isBlank()) applyButton.label = "Apply Filter";
            if (backButton.label.isBlank()) backButton.label = "Back";
        }
    }

    public static class RightPanelConfig {
        public float x = 320f;
        public float y = 24f;
        public float width = 936f;
        public float height = 672f;
        public float padding = 16f;
        public float scrollSpeed = 40f;
        public float contentSpacing = 16f;
        public String backgroundDrawable = "image_ui_generic_guaranteed_bg_10";
        public float backgroundAlpha = 0.85f;

        public void sanitize() {
            width = positive(width, 936f);
            height = positive(height, 672f);
            padding = Math.max(0f, padding);
            scrollSpeed = positive(scrollSpeed, 40f);
            contentSpacing = Math.max(0f, contentSpacing);
            backgroundDrawable = text(backgroundDrawable, "image_ui_generic_guaranteed_bg_10");
            backgroundAlpha = clamp(backgroundAlpha, 0f, 1f, 0.85f);
        }
    }

    public static class DailyOfferUiConfig {
        public String backgroundPath = "menu/image/off.png";
        public float width = 904f;
        public float height = 120f;
        public float padding = 12f;
        public float productImageSize = 88f;
        public float productImagePadLeft = 8f;
        public float productImageOffsetX = 18f;
        public float productImageOffsetY = 16f;
        public float timerOffsetX = 118f;
        public float timerOffsetY = 40f;
        public float discountOffsetX = 280f;
        public float discountOffsetY = 34f;
        public float buyOffsetX = 740f;
        public float buyOffsetY = 32f;
        public boolean keepAspect = true;
        public float timerPadLeft = 16f;
        public float discountPadLeft = 12f;
        public float buyPadLeft = 12f;
        public String timerIconPath = "menu/icon/timer.png";
        public TimerUiConfig timer = new TimerUiConfig();
        public float discountFontSize = 1.35f;
        public String discountColor = "FFD700";
        public String discountOutlineColor = "8B4513";
        public ButtonStyleConfig buyButton = new ButtonStyleConfig();
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.9f;
        public String fontColor = "FFFDF5";

        public void sanitize() {
            backgroundPath = text(backgroundPath, "menu/image/off.png");
            width = positive(width, 904f);
            height = positive(height, 120f);
            productImageSize = positive(productImageSize, 88f);
            timerIconPath = text(timerIconPath, "menu/icon/timer.png");
            if (timer == null) timer = new TimerUiConfig();
            timer.sanitize();
            discountFontSize = positive(discountFontSize, 1.35f);
            discountColor = text(discountColor, "FFD700");
            discountOutlineColor = text(discountOutlineColor, "8B4513");
            if (buyButton == null) buyButton = new ButtonStyleConfig();
            buyButton.sanitize();
            if (buyButton.backgroundPath == null || buyButton.backgroundPath.isBlank()) {
                buyButton.backgroundPath = "menu/image/bg_greengouse.png";
            }
            if (buyButton.label.isBlank()) buyButton.label = "Buy";
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, font);
            fontSize = positive(fontSize, 0.9f);
            fontColor = text(fontColor, "FFFDF5");
        }
    }

    public static class AdvertisementUiConfig {
        public float width = 904f;
        public float height = 110f;
        public float padding = 0f;
        public boolean keepAspect = true;
        public float hoverScale = 1.02f;
        public float hoverDuration = 0.15f;
        public float hoverAlpha = 0.9f;

        public void sanitize() {
            width = positive(width, 904f);
            height = positive(height, 110f);
            padding = Math.max(0f, padding);
            hoverScale = positive(hoverScale, 1.02f);
            hoverDuration = Math.max(0f, hoverDuration);
            hoverAlpha = clamp(hoverAlpha, 0f, 1f, 0.9f);
        }
    }

    public static class HoverConfig {
        public float duration = 0.2f;
        public float titleOffsetY = 52f;
        public float priceRevealOffsetY = 4f;
        public String easing = "sineOut";

        public void sanitize() {
            duration = Math.max(0f, duration);
            easing = text(easing, "sineOut");
        }
    }

    public static class ProductGridConfig {
        public int columns = 4;
        public float productWidth = 200f;
        public float productHeight = 230f;
        public float columnSpacing = 12f;
        public float rowSpacing = 12f;
        public boolean stretchToPanel = true;
        public String cardBackgroundPath = "menu/image/product_card_bg.png";
        public String titleButtonPath = "menu/image/btn1.png";
        public String priceButtonPath = "menu/image/bg_greengouse.png";
        public float imageSize = 101f;
        public float titleButtonHeight = 42f;
        public float priceButtonHeight = 38f;
        public CardLayoutConfig card = new CardLayoutConfig();
        public HoverConfig hover = new HoverConfig();

        public void sanitize() {
            columns = Math.max(1, columns);
            productWidth = positive(productWidth, 200f);
            productHeight = positive(productHeight, 230f);
            columnSpacing = Math.max(0f, columnSpacing);
            rowSpacing = Math.max(0f, rowSpacing);
            cardBackgroundPath = text(cardBackgroundPath, "menu/image/product_card_bg.png");
            titleButtonPath = text(titleButtonPath, "menu/image/btn1.png");
            priceButtonPath = text(priceButtonPath, "menu/image/bg_greengouse.png");
            imageSize = positive(imageSize, 101f);
            titleButtonHeight = positive(titleButtonHeight, 42f);
            priceButtonHeight = positive(priceButtonHeight, 38f);
            if (card == null) card = new CardLayoutConfig();
            card.sanitize();
            if (hover == null) hover = new HoverConfig();
            hover.sanitize();
        }
    }

    public static class PriceFilterConfig {
        public int minDefault = 0;
        public int maxDefault = 10000;
        public float sliderWidth = 220f;
        public float sliderHeight = 16f;
        public float knobSize = 24f;

        public void sanitize() {
            maxDefault = Math.max(minDefault, maxDefault);
            sliderWidth = positive(sliderWidth, 220f);
            sliderHeight = positive(sliderHeight, 16f);
            knobSize = positive(knobSize, 24f);
        }
    }

    public static class QuantityStepperConfig {
        public String backgroundPath = "menu/image/btn2.png";
        public String plusPath = "menu/image/plus.png";
        public String minusPath = "menu/image/negative.png";
        public float width = 196f;
        public float height = 48f;
        public boolean keepAspect = true;
        public float buttonSize = 34f;
        public float buttonPadX = 8f;
        public float minusOffsetX = 0f;
        public float minusOffsetY = 0f;
        public float plusOffsetX = 0f;
        public float plusOffsetY = 0f;
        public float fontScale = 1.15f;
        public String fontColor = "fffbed";
        public float textOffsetY = 0f;
        public float iconHoverScale = 1.12f;

        public void sanitize() {
            backgroundPath = text(backgroundPath, "menu/image/btn2.png");
            plusPath = text(plusPath, "menu/image/plus.png");
            minusPath = text(minusPath, "menu/image/negative.png");
            width = positive(width, 196f);
            height = positive(height, 48f);
            buttonSize = positive(buttonSize, 34f);
            buttonPadX = Math.max(0f, buttonPadX);
            fontScale = positive(fontScale, 1.15f);
            fontColor = text(fontColor, "fffbed");
            iconHoverScale = positive(iconHoverScale, 1.12f);
        }
    }

    public static class ProductDetailConfig {
        public float leftPanelWidth = 260f;
        public float rightImageSize = 220f;
        public float rightImagePadTop = 16f;
        public float titleFontSize = 1.35f;
        public float descriptionFontSize = 0.88f;
        public float descriptionWidth = 620f;
        public float descriptionLineSpacing = 1.2f;
        public float sectionSpacing = 12f;
        public float purchaseSectionSpacing = 16f;
        public String previewTitleColor = "ffdb6f";
        public String descriptionColor = "fffbed";
        public CompositeButtonStyleConfig titleButton = new CompositeButtonStyleConfig();
        public CompositeButtonStyleConfig buyButton = new CompositeButtonStyleConfig();
        public SelectBoxStyleConfig selectBox = new SelectBoxStyleConfig();
        public QuantityStepperConfig quantityStepper = new QuantityStepperConfig();

        public void sanitize() {
            leftPanelWidth = positive(leftPanelWidth, 260f);
            rightImageSize = positive(rightImageSize, 220f);
            titleFontSize = positive(titleFontSize, 1.35f);
            descriptionFontSize = positive(descriptionFontSize, 0.88f);
            descriptionWidth = positive(descriptionWidth, 620f);
            descriptionLineSpacing = positive(descriptionLineSpacing, 1.2f);
            sectionSpacing = Math.max(0f, sectionSpacing);
            purchaseSectionSpacing = Math.max(0f, purchaseSectionSpacing);
            previewTitleColor = text(previewTitleColor, "ffdb6f");
            descriptionColor = text(descriptionColor, "fffbed");
            if (titleButton == null) titleButton = new CompositeButtonStyleConfig();
            if (buyButton == null) buyButton = new CompositeButtonStyleConfig();
            if (selectBox == null) selectBox = new SelectBoxStyleConfig();
            if (quantityStepper == null) quantityStepper = new QuantityStepperConfig();
            titleButton.sanitize();
            buyButton.sanitize();
            selectBox.sanitize();
            quantityStepper.sanitize();
            if (titleButton.backgroundPath.isBlank()) {
                titleButton.backgroundPath = "menu/image/btn1.png";
            }
            if (buyButton.backgroundPath.isBlank()) {
                buyButton.backgroundPath = "menu/image/bg_greengouse.png";
            }
        }
    }

    public static class MessagesOverlayConfig {
        public float anchorX = 0.5f;
        public float anchorY = 0.08f;
        public float offsetX = 0f;
        public float offsetY = 0f;
        public float width = 520f;
        public float height = 48f;
        public String font = "BRIANNETOD";
        public String fontFallback = "BRIANNETOD";
        public float fontSize = 0.85f;
        public String color = "FF6B5A";
        public String successColor = "7CFF7A";
        public float alpha = 1f;
        public float duration = 2.5f;
        public String alignment = "center";

        public void sanitize() {
            width = positive(width, 520f);
            height = positive(height, 48f);
            font = text(font, "BRIANNETOD");
            fontFallback = text(fontFallback, font);
            fontSize = positive(fontSize, 0.85f);
            color = text(color, "FF6B5A");
            successColor = text(successColor, "7CFF7A");
            alpha = clamp(alpha, 0f, 1f, 1f);
            duration = positive(duration, 2.5f);
            alignment = text(alignment, "center");
        }
    }

    public static class LayersConfig {
        public int backgroundDepth = 0;
        public int panelDepth = 10;
        public int contentDepth = 20;
        public int hudDepth = 100;
        public int messageDepth = 200;

        public void sanitize() {
        }
    }

    public static class FormatsConfig {
        public String hoursMinutesSeconds = "%02d:%02d:%02d";
        public String minutesSeconds = "%02d:%02d";

        public void sanitize() {
            hoursMinutesSeconds = text(hoursMinutesSeconds, "%02d:%02d:%02d");
            minutesSeconds = text(minutesSeconds, "%02d:%02d");
        }
    }

    public static class PurchaseConfirmConfig {
        public String overlayColor = "000000A8";
        public float dimAlpha = 0.66f;
        public float panelWidth = 480f;
        public float panelHeight = 220f;
        public float panelPadX = 28f;
        public float panelPadY = 24f;
        public String panelBackgroundDrawable = "image_ui_generic_guaranteed_bg_10";
        public String titleEn = "Confirm Purchase";
        public String titleFa = "\u062A\u0627\u06CC\u06CC\u062F\u0020\u062E\u0631\u06CC\u062F";
        public String titleFont = "BRIANNETOD";
        public String titleFontFallback = "BRIANNETOD";
        public float titleFontSize = 1.05f;
        public String messageEn = "Do you want to purchase this item?";
        public String messageFa = "\u0622\u06CC\u0627\u0020\u0627\u0632\u0020\u062E\u0631\u06CC\u062F\u0020\u0627\u06CC\u0646\u0020\u0622\u06CC\u062A\u0645\u0020\u0645\u0637\u0645\u0626\u0646\u0020\u0647\u0633\u062A\u06CC\u062F\u061F";
        public String messageFont = "BRIANNETOD";
        public String messageFontFallback = "BRIANNETOD";
        public float messageFontSize = 0.92f;
        public float buttonWidth = 160f;
        public float buttonHeight = 40f;
        public float buttonGap = 16f;
        public float buttonTopPadding = 20f;
        public String cancelLabelEn = "Cancel";
        public String cancelLabelFa = "\u06A9\u0646\u0633\u0644";
        public String confirmLabelEn = "Confirm Purchase";
        public String confirmLabelFa = "\u0627\u0646\u062C\u0627\u0645\u0020\u062E\u0631\u06CC\u062F";
        public String buttonStyle = "purple";
        public float buttonLabelScale = 0.74f;
        public float buttonHoverScale = 1.05f;
        public float buttonHoverDuration = 0.10f;
        public float offsetX;
        public float offsetY;

        public PurchaseConfirmConfig sanitize() {
            overlayColor = text(overlayColor, "000000A8");
            if (dimAlpha < 0f || dimAlpha > 1f) {
                dimAlpha = 0.66f;
            }
            if (panelWidth <= 0f) {
                panelWidth = 480f;
            }
            if (panelHeight <= 0f) {
                panelHeight = 220f;
            }
            panelPadX = Math.max(0f, panelPadX);
            panelPadY = Math.max(0f, panelPadY);
            panelBackgroundDrawable = text(panelBackgroundDrawable, "image_ui_generic_guaranteed_bg_10");
            titleEn = text(titleEn, "Confirm Purchase");
            titleFa = text(titleFa, "\u062A\u0627\u06CC\u06CC\u062F\u0020\u062E\u0631\u06CC\u062F");
            titleFont = text(titleFont, "BRIANNETOD");
            titleFontFallback = text(titleFontFallback, "BRIANNETOD");
            if (titleFontSize <= 0f) {
                titleFontSize = 1.05f;
            }
            messageEn = text(messageEn, "Do you want to purchase this item?");
            messageFa = text(messageFa, "\u0622\u06CC\u0627\u0020\u0627\u0632\u0020\u062E\u0631\u06CC\u062F\u0020\u0627\u06CC\u0646\u0020\u0622\u06CC\u062A\u0645\u0020\u0645\u0637\u0645\u0626\u0646\u0020\u0647\u0633\u062A\u06CC\u062F\u061F");
            messageFont = text(messageFont, "BRIANNETOD");
            messageFontFallback = text(messageFontFallback, "BRIANNETOD");
            if (messageFontSize <= 0f) {
                messageFontSize = 0.92f;
            }
            if (buttonWidth <= 0f) {
                buttonWidth = 160f;
            }
            if (buttonHeight <= 0f) {
                buttonHeight = 40f;
            }
            if (buttonGap < 0f) {
                buttonGap = 16f;
            }
            if (buttonTopPadding < 0f) {
                buttonTopPadding = 20f;
            }
            cancelLabelEn = text(cancelLabelEn, "Cancel");
            cancelLabelFa = text(cancelLabelFa, "\u06A9\u0646\u0633\u0644");
            confirmLabelEn = text(confirmLabelEn, "Confirm Purchase");
            confirmLabelFa = text(confirmLabelFa, "\u0627\u0646\u062C\u0627\u0645\u0020\u062E\u0631\u06CC\u062F");
            buttonStyle = text(buttonStyle, "purple");
            if (buttonLabelScale <= 0f) {
                buttonLabelScale = 0.74f;
            }
            if (buttonHoverScale <= 0f) {
                buttonHoverScale = 1.05f;
            }
            if (buttonHoverDuration < 0f) {
                buttonHoverDuration = 0.10f;
            }
            return this;
        }
    }
    protected static String text(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    protected static float positive(float value, float fallback) {
        if (Float.isNaN(value) || value <= 0f) {
            return fallback;
        }
        return value;
    }

    protected static float clamp(float value, float min, float max, float fallback) {
        if (Float.isNaN(value)) {
            return fallback;
        }
        return Math.max(min, Math.min(max, value));
    }
}

