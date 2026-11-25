package app.view.shop.component;

import app.controller.shop.ShopController;
import app.model.shop.DailyOffer;
import app.model.shop.Shop;
import app.model.shop.purchase.ShopPriceQuote;
import app.model.shop.purchase.ShopPurchaseContext;
import app.service.shop.ShopProductEffects;
import app.view.shop.ShopPurchaseConfirmPopup;
import app.view.shop.ShopTextureCache;
import app.view.shop.ShopTimerGroup;
import app.view.shop.ShopUiFactory;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopConfig;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import common.Result;

import java.time.Duration;

public final class ShopDailyOfferSection extends WidgetGroup {
    public interface Listener {
        void onPurchased(Result result);
    }

    private final ShopConfig config;
    private final ShopController controller;
    private final ShopTextureCache textures;
    private final Listener listener;
    private ShopTimerGroup timerGroup;
    private Image background;
    private Image productImage;
    private Label discountLabel;
    private TextButton buyButton;
    private float fittedWidth;

    public ShopDailyOfferSection(
        ShopConfig config,
        ShopController controller,
        ShopTextureCache textures,
        Listener listener
    ) {
        this.config = config;
        this.controller = controller;
        this.textures = textures;
        this.listener = listener;
        rebuild();
    }

    public void rebuild() {
        clearChildren();
        timerGroup = null;
        background = null;
        productImage = null;
        discountLabel = null;
        buyButton = null;
        DailyOffer offer = controller.getDailyOffer();
        if (offer == null || offer.isSold()) {
            setVisible(false);
            setSize(0f, 0f);
            return;
        }
        ShopCatalogConfig.ProductConfig product = controller.product(offer.getProductId());
        if (product == null) {
            setVisible(false);
            setSize(0f, 0f);
            return;
        }
        setVisible(true);
        ShopConfig.DailyOfferUiConfig ui = config.dailyOffer;
        Texture bgTexture = textures.get(ui.backgroundPath);
        background = new Image();
        if (bgTexture != null) {
            background.setDrawable(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        }
        addActor(background);

        Texture productTexture = textures.get(product.imagePath);
        if (productTexture != null) {
            productImage = new Image(new TextureRegionDrawable(new TextureRegion(productTexture)));
            addActor(productImage);
        }

        BitmapFont font = ShopUiFactory.resolveFont(ui.font, ui.fontFallback);
        timerGroup = new ShopTimerGroup(ui.timer, config.formats, textures.get(ui.timerIconPath), font);
        timerGroup.refresh(Shop.remainingDailyTime().duration(), false);
        addActor(timerGroup);

        discountLabel = ShopUiFactory.label(
            config.labels.discountPrefix + offer.getDiscountPercent() + config.labels.discountSuffix,
            font,
            ShopUiFactory.parseColor(ui.discountColor, Color.GOLD),
            Align.center
        );
        discountLabel.setFontScale(ui.discountFontSize);
        addActor(discountLabel);

        ShopPurchaseContext context = new ShopPurchaseContext(1);
        ShopPriceQuote base = ShopProductEffects.calculatePrice(product, context);
        int discounted = Shop.applyDiscount(base.getTotalAmount(), offer.getDiscountPercent());
        ShopConfig.ButtonStyleConfig buyStyle = ui.buyButton.copy();
        buyStyle.label = config.labels.buyPrefix + discounted;
        buyButton = ShopUiFactory.styledButton(buyStyle, font, textures, config.interaction);
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ShopPurchaseConfirmPopup.show(getStage(), config, () -> {
                    Result result = controller.buyDaily();
                    if (listener != null) {
                        listener.onPurchased(result);
                    }
                    rebuild();
                });
            }
        });
        addActor(buyButton);
        layoutBanner(fittedWidth > 0f ? fittedWidth : ui.width);
    }

    public void fitWidth(float width) {
        fittedWidth = Math.max(1f, width);
        if (isVisible()) {
            layoutBanner(fittedWidth);
        }
    }

    private void layoutBanner(float width) {
        ShopConfig.DailyOfferUiConfig ui = config.dailyOffer;
        Texture bgTexture = textures.get(ui.backgroundPath);
        float height = ui.height;
        if (ui.keepAspect && bgTexture != null && bgTexture.getWidth() > 0) {
            height = width * (bgTexture.getHeight() / (float) bgTexture.getWidth());
        }
        setSize(width, height);
        if (background != null) {
            background.setSize(width, height);
            background.setPosition(0f, 0f);
        }
        float refW = ui.width > 0f ? ui.width : width;
        float refH = ui.height > 0f ? ui.height : height;
        float sx = width / refW;
        float sy = height / refH;
        if (productImage != null) {
            float size = ui.productImageSize * Math.min(sx, sy);
            productImage.setSize(size, size);
            productImage.setPosition(ui.productImageOffsetX * sx, ui.productImageOffsetY * sy);
        }
        if (timerGroup != null) {
            timerGroup.setPosition(ui.timerOffsetX * sx, ui.timerOffsetY * sy);
        }
        if (discountLabel != null) {
            discountLabel.pack();
            discountLabel.setPosition(ui.discountOffsetX * sx, ui.discountOffsetY * sy);
        }
        if (buyButton != null) {
            buyButton.setPosition(ui.buyOffsetX * sx, ui.buyOffsetY * sy);
        }
    }

    public void refreshTimer() {
        if (timerGroup != null && isVisible()) {
            Duration remaining = Shop.remainingDailyTime().duration();
            timerGroup.refresh(remaining, remaining.isZero() || remaining.isNegative());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        refreshTimer();
    }
}
