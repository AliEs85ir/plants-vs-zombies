package app.view.shop.component;

import app.controller.shop.ShopController;
import app.model.shop.purchase.ShopPriceQuote;
import app.model.shop.purchase.ShopPurchaseContext;
import app.service.shop.ShopCatalog;
import app.service.shop.ShopProductEffects;
import app.view.shop.ShopProductCard;
import app.view.shop.ShopTextureCache;
import app.view.shop.ShopUiFactory;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopConfig;
import app.view.shop.layout.ShopLayoutMetrics;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import common.Result;

import java.util.List;
import java.util.function.Consumer;

public final class ShopProductGridSection extends Table {
    public interface Listener {
        void onOpenDetail(ShopCatalogConfig.ProductConfig product);

        void onRefreshNeeded();

        void onPurchaseResult(Result result);
    }

    private final ShopConfig config;
    private final ShopController controller;
    private final ShopTextureCache textures;
    private final Listener listener;

    public ShopProductGridSection(
        ShopConfig config,
        ShopController controller,
        ShopTextureCache textures,
        Listener listener
    ) {
        this.config = config;
        this.controller = controller;
        this.textures = textures;
        this.listener = listener;
        top().left();
        rebuild();
    }

    public void rebuild() {
        clearChildren();
        ShopConfig.ProductGridConfig gridConfig = config.productGrid;
        top().left();
        defaults().pad(0f);
        List<ShopCatalogConfig.ProductConfig> products = controller.filteredProducts();
        BitmapFont font = ShopUiFactory.resolveFont(config.leftPanel.font, config.leftPanel.fontFallback);
        int column = 0;
        for (ShopCatalogConfig.ProductConfig product : products) {
            ShopCatalogConfig.CategoryConfig category = ShopCatalog.current().category(product.categoryId);
            Texture categoryIcon = category == null ? null : textures.get(category.iconPath);
            Texture productImage = textures.get(product.imagePath);
            Texture cardBg = textures.get(product.cardBackgroundPath != null ? product.cardBackgroundPath : gridConfig.cardBackgroundPath);
            Texture titleButtonTexture = textures.get(gridConfig.titleButtonPath);
            Texture priceButtonTexture = textures.get(gridConfig.priceButtonPath);
            ShopPurchaseContext context = new ShopPurchaseContext(1);
            ShopPriceQuote quote = ShopProductEffects.calculatePrice(product, context);
            Texture currencyIcon = textures.get(config.currencyIcon(quote.getCurrencyType().name()));

            ShopProductCard card = new ShopProductCard(
                product,
                config,
                controller,
                font,
                categoryIcon,
                productImage,
                cardBg,
                titleButtonTexture,
                priceButtonTexture,
                currencyIcon,
                () -> {
                    if (listener != null) {
                        listener.onRefreshNeeded();
                    }
                },
                () -> {
                    if (listener != null) {
                        listener.onOpenDetail(product);
                    }
                },
                result -> {
                    if (listener != null) {
                        listener.onPurchaseResult(result);
                    }
                }
            );
            add(card).size(gridConfig.productWidth, gridConfig.productHeight);
            column++;
            if (column >= gridConfig.columns) {
                row();
                column = 0;
            }
        }
        if (column > 0) {
            row();
        }
    }

    public void layoutWithMetrics(ShopLayoutMetrics metrics, float availableWidth) {
        ShopConfig.ProductGridConfig grid = config.productGrid;
        int cols = Math.max(1, grid.columns);
        float gap = grid.columnSpacing;
        float cardW = grid.productWidth;
        float cardH = grid.productHeight;
        if (grid.stretchToPanel && availableWidth > 1f) {
            cardW = (availableWidth - gap * (cols - 1)) / cols;
            cardH = grid.productHeight * (cardW / Math.max(1f, grid.productWidth));
        }
        int column = 0;
        for (Cell cell : getCells()) {
            boolean lastInRow = column == cols - 1;
            cell.size(cardW, cardH);
            cell.padRight(lastInRow ? 0f : gap);
            cell.padBottom(grid.rowSpacing);
            column++;
            if (column >= cols) {
                column = 0;
            }
        }
        setWidth(availableWidth);
    }
}
