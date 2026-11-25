package app.view.shop;

import app.controller.shop.ShopController;
import app.model.shop.purchase.ShopPriceQuote;
import app.model.shop.purchase.ShopPurchaseContext;
import app.service.shop.ShopProductEffects;
import app.view.mainmenu.config.MainMenuEasings;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopConfig;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import common.Result;

public final class ShopProductCard extends WidgetGroup {
    private final ShopCatalogConfig.ProductConfig product;
    private final ShopConfig config;
    private final ShopController controller;
    private final Runnable onRefresh;
    private final Runnable onOpenDetail;
    private final java.util.function.Consumer<Result> onResult;
    private final Image productImageActor;
    private float baseTitleY;
    private float basePriceY;
    private final ShopCompositeButton titleButton;
    private final ShopCompositeButton priceButton;
    private boolean hovered;

    public ShopProductCard(
        ShopCatalogConfig.ProductConfig product,
        ShopConfig config,
        ShopController controller,
        BitmapFont font,
        Texture categoryIcon,
        Texture productImage,
        Texture cardBackground,
        Texture titleButtonTexture,
        Texture priceButtonTexture,
        Texture currencyIcon,
        Runnable onRefresh,
        Runnable onOpenDetail,
        java.util.function.Consumer<Result> onResult
    ) {
        this.product = product;
        this.config = config;
        this.controller = controller;
        this.onRefresh = onRefresh;
        this.onOpenDetail = onOpenDetail;
        this.onResult = onResult;

        ShopConfig.ProductGridConfig grid = config.productGrid;
        ShopConfig.CardLayoutConfig card = grid.card;
        setSize(grid.productWidth, grid.productHeight);
        setTransform(true);
        setOrigin(Align.center);
        setTouchable(Touchable.enabled);
        setName("shop-product");

        if (cardBackground != null) {
            Image bg = new Image(new TextureRegionDrawable(new TextureRegion(cardBackground)));
            bg.setFillParent(true);
            bg.setTouchable(Touchable.disabled);
            addActor(bg);
        }

        float imageSize = grid.imageSize;
        if (productImage != null) {
            float imageScale = product.imageScale > 0f ? product.imageScale : 1f;
            productImageActor = new Image(new TextureRegionDrawable(new TextureRegion(productImage)));
            float scaledSize = imageSize * imageScale;
            productImageActor.setScaling(Scaling.fit);
            productImageActor.setAlign(Align.center);
            productImageActor.setSize(scaledSize, scaledSize);
            productImageActor.setTouchable(Touchable.disabled);
            addActor(productImageActor);
        } else {
            productImageActor = null;
        }

        float buttonWidth = grid.productWidth - card.titleMarginX * 2f;
        titleButton = new ShopCompositeButton(
            titleButtonTexture,
            categoryIcon,
            product.title,
            font,
            config.typography.bodyScale * card.titleFontScale,
            buttonWidth * card.titleButtonSizeScale,
            grid.titleButtonHeight * card.titleButtonSizeScale,
            card.buttonIconSize * card.titleIconScale,
            card.buttonIconPadLeft,
            card.buttonIconPadY,
            card.buttonTextPadLeft,
            card.buttonTextPadRight
        );
        priceButton = new ShopCompositeButton(
            priceButtonTexture,
            currencyIcon,
            formatPrice(),
            font,
            config.typography.priceScale * card.priceFontScale,
            buttonWidth * card.priceButtonSizeScale,
            grid.priceButtonHeight * card.priceButtonSizeScale,
            card.buttonIconSize * card.priceIconScale,
            card.buttonIconPadLeft,
            card.buttonIconPadY,
            card.buttonTextPadLeft,
            card.buttonTextPadRight
        );
        priceButton.setVisible(false);
        priceButton.getColor().a = 0f;

        baseTitleY = card.titleMarginY;
        basePriceY = baseTitleY - grid.priceButtonHeight - card.titlePriceGap;
        titleButton.setPosition(card.titleMarginX, baseTitleY);
        priceButton.setPosition(card.titleMarginX, basePriceY);
        addActor(titleButton);
        addActor(priceButton);
        addListeners();
        layoutCard();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        layoutCard();
    }

    private void layoutCard() {
        if (titleButton == null || priceButton == null) {
            return;
        }
        ShopConfig.ProductGridConfig grid = config.productGrid;
        ShopConfig.CardLayoutConfig card = grid.card;
        float width = getWidth();
        float height = getHeight();
        if (width <= 1f || height <= 1f) {
            return;
        }
        float sx = width / Math.max(1f, grid.productWidth);
        float sy = height / Math.max(1f, grid.productHeight);
        float uniform = Math.min(sx, sy);
        setOrigin(Align.center);
        if (productImageActor != null) {
            float imageScale = product.imageScale > 0f ? product.imageScale : 1f;
            float box = grid.imageSize * imageScale * uniform;
            productImageActor.setSize(box, box);
            productImageActor.setPosition(
                (width - box) * 0.5f + card.imageOffsetX + product.imageOffsetX,
                height - box - card.imageOffsetY - product.imageOffsetY
            );
        }
        float fullButtonWidth = width - card.titleMarginX * 2f * sx;
        float titleW = fullButtonWidth * card.titleButtonSizeScale;
        float priceW = fullButtonWidth * card.priceButtonSizeScale;
        float titleH = grid.titleButtonHeight * sy * card.titleButtonSizeScale;
        float priceH = grid.priceButtonHeight * sy * card.priceButtonSizeScale;
        baseTitleY = card.titleMarginY * sy;
        basePriceY = baseTitleY - priceH - card.titlePriceGap * sy;
        titleButton.applyLayout(
            titleW,
            titleH,
            card.buttonIconSize * card.titleIconScale * uniform,
            config.typography.bodyScale * card.titleFontScale
        );
        priceButton.applyLayout(
            priceW,
            priceH,
            card.buttonIconSize * card.priceIconScale * uniform,
            config.typography.priceScale * card.priceFontScale
        );
        titleButton.setPosition((width - titleW) * 0.5f, baseTitleY);
        priceButton.setPosition((width - priceW) * 0.5f, basePriceY);
    }

    private String formatPrice() {
        ShopPurchaseContext context = new ShopPurchaseContext(1);
        ShopPriceQuote quote = ShopProductEffects.calculatePrice(product, context);
        return String.valueOf(quote.getTotalAmount());
    }

    private void addListeners() {
        ClickListener hoverListener = new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1 || hovered) {
                    return;
                }
                hovered = true;
                clearActions();
                addAction(Actions.scaleTo(
                    config.productGrid.card.cardHoverScale,
                    config.productGrid.card.cardHoverScale,
                    config.productGrid.card.cardHoverDuration,
                    MainMenuEasings.parse(config.interaction.easing)
                ));
                ShopHoverAnimator.animateHoverEnter(
                    titleButton,
                    priceButton,
                    baseTitleY,
                    config.productGrid.hover
                );
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) {
                    return;
                }
                if (toActor != null && toActor.isDescendantOf(ShopProductCard.this)) {
                    return;
                }
                hovered = false;
                clearActions();
                addAction(Actions.scaleTo(
                    1f,
                    1f,
                    config.productGrid.card.cardHoverDuration,
                    MainMenuEasings.parse(config.interaction.easing)
                ));
                ShopHoverAnimator.animateHoverExit(
                    titleButton,
                    priceButton,
                    baseTitleY,
                    basePriceY,
                    config.productGrid.hover
                );
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onOpenDetail != null) {
                    onOpenDetail.run();
                }
            }
        };
        addListener(hoverListener);
        priceButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                ShopPurchaseConfirmPopup.show(getStage(), config, () -> {
                    ShopPurchaseContext context = new ShopPurchaseContext(1);
                    Result result = controller.buy(product, context);
                    if (onResult != null) {
                        onResult.accept(result);
                    }
                    if (result.success() && onRefresh != null) {
                        onRefresh.run();
                    }
                });
            }
        });
    }

    public ShopCatalogConfig.ProductConfig product() {
        return product;
    }
}
