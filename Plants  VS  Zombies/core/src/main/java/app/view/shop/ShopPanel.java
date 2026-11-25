package app.view.shop;

import app.controller.shop.ShopController;
import app.view.adventure.AdventureHud;
import app.view.screen.ScreenManager;
import app.view.shop.component.ShopAdvertisementBanner;
import app.view.shop.component.ShopCategoryPanel;
import app.view.shop.component.ShopDailyOfferSection;
import app.view.shop.component.ShopProductDetailPanel;
import app.view.shop.component.ShopProductGridSection;
import app.view.shop.config.ShopCatalogConfig;
import app.view.shop.config.ShopConfig;
import app.view.shop.config.ShopConfigLoader;
import app.view.shop.layout.ShopLayoutMetrics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import common.Result;
import plants_vs_zombies.PlantsVSZombies;

public final class ShopPanel extends com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup implements Disposable {
    private final PlantsVSZombies game;
    private final ShopConfig config;
    private final ShopController controller;
    private final ShopTextureCache textures = new ShopTextureCache();
    private final AdventureHud hud;
    private final ShopMessageToast messageToast;

    private Image titleImage;
    private Table mainContent;
    private Table rightShell;
    private Table rightScrollContent;
    private ShopCategoryPanel categoryPanel;
    private ShopDailyOfferSection dailyOfferSection;
    private ShopAdvertisementBanner advertisementBanner;
    private ShopProductGridSection productGridSection;
    private ScrollPane rightScrollPane;
    private ShopProductDetailPanel detailPanel;
    private float productScrollY;

    public ShopPanel(PlantsVSZombies game) {
        this.game = game;
        this.config = ShopConfigLoader.load();
        this.controller = new ShopController();
        this.hud = new AdventureHud(game, () -> ScreenManager.showGameScreen(game), true, false);
        BitmapFont messageFont = ShopUiFactory.resolveFont(config.messages.font, config.messages.fontFallback);
        this.messageToast = new ShopMessageToast(config.messages, messageFont);
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        build();
    }

    private void build() {
        Texture bgTexture = textures.get(config.backgroundPath);
        if (bgTexture != null) {
            Image background = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));
            background.setFillParent(true);
            background.setTouchable(Touchable.disabled);
            addActor(background);
        }

        Texture titleTexture = textures.get(config.titlePath);
        if (titleTexture != null) {
            titleImage = new Image(new TextureRegionDrawable(new TextureRegion(titleTexture)));
            titleImage.setTouchable(Touchable.disabled);
            addActor(titleImage);
        }

        mainContent = new Table();
        mainContent.setFillParent(true);
        mainContent.top().left();

        categoryPanel = new ShopCategoryPanel(config, controller, textures, new ShopCategoryPanel.Listener() {
            @Override
            public void onCategorySelected(String categoryId) {
            }

            @Override
            public void onApplyFilters() {
                refreshProductGrid();
                productScrollY = 0f;
                if (rightScrollPane != null) {
                    rightScrollPane.setScrollY(0f);
                }
            }

            @Override
            public void onCurrencyDraftChanged() {
            }
        });

        rightScrollContent = new Table();
        rightScrollContent.top().left();
        rightScrollContent.defaults().left().padBottom(config.rightPanel.contentSpacing);

        dailyOfferSection = new ShopDailyOfferSection(config, controller, textures, result -> {
            showResult(result);
            refreshRightColumn();
        });
        advertisementBanner = new ShopAdvertisementBanner(config, controller, textures);
        productGridSection = new ShopProductGridSection(config, controller, textures, new ShopProductGridSection.Listener() {
            @Override
            public void onOpenDetail(ShopCatalogConfig.ProductConfig product) {
                productScrollY = rightScrollPane == null ? 0f : rightScrollPane.getScrollY();
                mainContent.setVisible(false);
                detailPanel.open(product, productScrollY);
            }

            @Override
            public void onRefreshNeeded() {
                refreshRightColumn();
            }

            @Override
            public void onPurchaseResult(Result result) {
                showResult(result);
            }
        });

        rightScrollContent.add(dailyOfferSection).left().row();
        rightScrollContent.add(advertisementBanner).left().row();
        rightScrollContent.add(productGridSection).left().row();

        ScrollPane.ScrollPaneStyle scrollStyle = ShopUiFactory.thinScrollStyle(
            config.scrollPane.scrollBarWidth,
            ShopUiFactory.parseColor("C4A574", Color.TAN),
            ShopUiFactory.parseColor("00000000", Color.CLEAR)
        );
        rightScrollPane = new ScrollPane(rightScrollContent, scrollStyle);
        rightScrollPane.setFadeScrollBars(config.scrollPane.fadeScrollBars);
        rightScrollPane.setScrollingDisabled(true, false);
        rightScrollPane.setScrollBarPositions(false, true);
        rightScrollPane.setVariableSizeKnobs(true);
        rightScrollPane.setForceScroll(false, true);
        addScrollInputListener();

        rightShell = new Table();
        Drawable rightBackground = ShopUiFactory.tintedSkinDrawable(
            config.rightPanel.backgroundDrawable,
            "image_ui_generic_guaranteed_bg_10",
            config.rightPanel.backgroundAlpha
        );
        if (rightBackground != null) {
            rightShell.setBackground(rightBackground);
        }
        rightShell.pad(config.rightPanel.padding);
        rightShell.add(rightScrollPane).grow();

        Image separator = new Image();
        separator.setColor(ShopUiFactory.parseColor(config.leftPanel.separatorColor, Color.valueOf("6B5344")));

        mainContent.add(categoryPanel).top().left();
        mainContent.add(separator).width(config.leftPanel.separatorWidth).expandY().fillY().padLeft(config.leftPanel.separatorPadding).top();
        mainContent.add(rightShell).expand().fill().padLeft(config.leftPanel.separatorPadding).top();

        detailPanel = new ShopProductDetailPanel(config, controller, textures, new ShopProductDetailPanel.Listener() {
            @Override
            public void onBack() {
                mainContent.setVisible(true);
                if (rightScrollPane != null) {
                    rightScrollPane.setScrollY(detailPanel.savedScrollY());
                }
            }

            @Override
            public void onPurchaseResult(Result result) {
                showResult(result);
            }

            @Override
            public void onPurchased() {
                mainContent.setVisible(true);
                refreshRightColumn();
            }
        });

        addActor(mainContent);
        addActor(detailPanel);
        addActor(hud);
        addActor(messageToast);
    }

    private void refreshRightColumn() {
        dailyOfferSection.rebuild();
        advertisementBanner.rebuild(config, controller, textures);
        refreshProductGrid();
        invalidateHierarchy();
    }

    private void refreshProductGrid() {
        productGridSection.rebuild();
    }

    private void addScrollInputListener() {
        addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                if (rightScrollPane != null && mainContent.isVisible() && !detailPanel.isOpen()) {
                    rightScrollPane.setScrollY(rightScrollPane.getScrollY() + amountY * config.rightPanel.scrollSpeed);
                    return true;
                }
                return false;
            }
        });
    }

    private void showResult(Result result) {
        if (result == null || result.message() == null || result.message().isBlank()) {
            return;
        }
        if (result.success()) {
            messageToast.showSuccess(result.message());
        } else {
            messageToast.showError(result.message());
        }
    }

    @Override
    public void layout() {
        ShopLayoutMetrics base = new ShopLayoutMetrics(getWidth(), getHeight(), config);
        layoutTitle(base);
        float topMargin = base.marginTop();
        if (titleImage != null) {
            topMargin = getHeight() - titleImage.getY() + base.su(config.title.contentGap);
        }
        ShopLayoutMetrics metrics = new ShopLayoutMetrics(getWidth(), getHeight(), config, topMargin);

        mainContent.pad(
            metrics.marginTop(),
            metrics.marginLeft(),
            metrics.marginBottom(),
            metrics.marginRight()
        );
        categoryPanel.layoutWithMetrics(metrics);
        mainContent.getCell(categoryPanel).width(metrics.leftPanelWidth()).height(metrics.panelHeight());
        mainContent.getCell(rightShell).width(metrics.rightPanelWidth()).height(metrics.panelHeight());
        float innerPad = metrics.su(config.rightPanel.padding);
        rightShell.pad(innerPad);
        float contentWidth = Math.max(1f, metrics.rightPanelWidth() - innerPad * 2f);
        dailyOfferSection.fitWidth(contentWidth);
        advertisementBanner.fitWidth(contentWidth);
        rightScrollContent.getCell(dailyOfferSection).size(dailyOfferSection.getWidth(), dailyOfferSection.getHeight()).growX();
        rightScrollContent.getCell(advertisementBanner).size(advertisementBanner.getWidth(), advertisementBanner.getHeight()).growX();
        rightScrollContent.getCell(productGridSection).growX();
        productGridSection.layoutWithMetrics(metrics, contentWidth);

        detailPanel.layoutWithMetrics(metrics);
        messageToast.layoutInParent(getWidth(), getHeight());
        hud.setSize(getWidth(), getHeight());
        super.layout();
    }

    private void layoutTitle(ShopLayoutMetrics metrics) {
        if (titleImage == null) {
            return;
        }
        Texture titleTexture = textures.get(config.titlePath);
        float titleW = metrics.su(config.title.width * config.title.scale);
        float titleH = metrics.su(config.title.height * config.title.scale);
        if (config.title.keepAspect && titleTexture != null && titleTexture.getWidth() > 0) {
            titleH = titleW * (titleTexture.getHeight() / (float) titleTexture.getWidth());
        }
        titleImage.setSize(titleW, titleH);
        float x = (getWidth() - titleW) * config.title.anchorX + metrics.su(config.title.offsetX);
        float y = getHeight() - titleH + metrics.su(config.title.offsetY);
        titleImage.setPosition(x, y);
    }

    @Override
    public void dispose() {
        if (hud != null) {
            hud.dispose();
        }
        textures.dispose();
    }
}
