package app.view.collection;

import app.controller.collection.CollectionController;
import app.view.adventure.AdventureHud;
import app.view.collection.config.CollectionAnchors;
import app.view.collection.config.CollectionConfig;
import app.view.collection.config.CollectionConfigLoader;
import app.view.collection.data.CollectionData;
import app.view.collection.plant.CollectionPlantGrid;
import app.view.collection.plant.PlantDetailsView;
import app.view.collection.zombie.CollectionZombieGrid;
import app.view.collection.zombie.ZombieDetailsView;
import app.view.screen.ScreenManager;
import app.view.shop.ShopUiFactory;
import app.view.ui.ScrollPanes;
import app.view.ui.UiTheme;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import common.Result;
import plants_vs_zombies.PlantsVSZombies;

public final class CollectionPanel extends com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup implements Disposable {
    private final PlantsVSZombies game;
    private final CollectionConfig config;
    private final CollectionController controller;
    private final CollectionTextureCache textures = new CollectionTextureCache();

    private Image titleImage;
    private Image overlay;
    private Texture overlayTexture;
    private CollectionTabActor plantsTab;
    private CollectionTabActor zombiesTab;
    private AdventureHud hud;
    private Table contentShell;
    private Table gridRoot;
    private ScrollPane plantScroll;
    private ScrollPane zombieScroll;
    private CollectionPlantGrid plantGrid;
    private CollectionZombieGrid zombieGrid;
    private PlantDetailsView plantDetails;
    private ZombieDetailsView zombieDetails;
    private Label toastLabel;
    private CollectionFilterPanel filterPanel;
    private Actor shownContent;
    private float lastGridWidth = -1f;

    public CollectionPanel(PlantsVSZombies game) {
        this.game = game;
        CollectionConfig loaded = CollectionConfigLoader.load();
        this.config = loaded == null ? new CollectionConfig().sanitize() : loaded;
        this.controller = new CollectionController(config);
        setFillParent(true);
        setTransform(false);
        setTouchable(Touchable.childrenOnly);
        build();
        showHub(false);
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
        }

        hud = new AdventureHud(game, this::onBack, true, true, false);

        plantsTab = new CollectionTabActor(
            config,
            config.tabs.plants,
            textures.get(config.tabs.plants.path),
            "menu-collection-tab-plants",
            false,
            this::showPlantGrid
        );
        zombiesTab = new CollectionTabActor(
            config,
            config.tabs.zombies,
            textures.get(config.tabs.zombies.path),
            "menu-collection-tab-zombies",
            true,
            this::showZombieGrid
        );
        addActor(plantsTab);
        addActor(zombiesTab);

        overlay = createOverlay();
        addActor(overlay);

        plantGrid = new CollectionPlantGrid(config, model -> {
            controller.selectPlant(model.plantName);
            openPlantDetails();
        });
        zombieGrid = new CollectionZombieGrid(config, model -> {
            controller.selectZombie(model.alias);
            openZombieDetails();
        });
        plantScroll = ScrollPanes.vertical(plantGrid, config.scrollPane.scrollBarWidth);
        zombieScroll = ScrollPanes.vertical(zombieGrid, config.scrollPane.scrollBarWidth);
        plantScroll.setFadeScrollBars(config.scrollPane.fadeScrollBars);
        zombieScroll.setFadeScrollBars(config.scrollPane.fadeScrollBars);
        plantScroll.setScrollbarsOnTop(true);
        zombieScroll.setScrollbarsOnTop(true);

        gridRoot = new Table();
        gridRoot.setFillParent(true);
        gridRoot.center().top();
        gridRoot.add(plantScroll).grow();

        plantDetails = new PlantDetailsView(config, textures, new PlantDetailsView.Listener() {
            @Override
            public void onPrevious() {
                controller.cyclePlant(-1);
                openPlantDetails();
            }

            @Override
            public void onNext() {
                controller.cyclePlant(1);
                openPlantDetails();
            }

            @Override
            public void onUpgrade() {
                Result result = controller.upgradeSelectedPlant();
                showToast(result);
                if (result != null && result.success()) {
                    openPlantDetails();
                    rebuildGrids();
                }
            }

            @Override
            public void onBuy() {
                Result result = controller.purchaseSelectedPlant();
                showToast(result);
                if (result != null && result.success()) {
                    openPlantDetails();
                    rebuildGrids();
                }
            }
        });

        zombieDetails = new ZombieDetailsView(config, new ZombieDetailsView.Listener() {
            @Override
            public void onPrevious() {
                controller.cycleZombie(-1);
                openZombieDetails();
            }

            @Override
            public void onNext() {
                controller.cycleZombie(1);
                openZombieDetails();
            }
        });
        plantDetails.setVisible(false);
        zombieDetails.setVisible(false);
        plantDetails.getColor().a = 0f;
        zombieDetails.getColor().a = 0f;

        contentShell = new Table();
        Drawable panel = ShopUiFactory.tintedSkinDrawable(
            config.panel.drawable,
            config.panel.fallback,
            config.panel.alpha
        );
        if (panel != null) {
            contentShell.setBackground(panel);
        }
        contentShell.pad(
            config.panel.padTop,
            config.panel.padLeft,
            config.panel.padBottom,
            config.panel.padRight
        );
        Stack contentStack = new Stack();
        contentStack.add(gridRoot);
        contentStack.add(plantDetails);
        contentStack.add(zombieDetails);
        contentShell.add(contentStack).grow();
        addActor(contentShell);
        if (titleImage != null) {
            addActor(titleImage);
        }
        addActor(hud);

        toastLabel = TextSpec.of(UiTheme.LBL_MEDIUM_OUTLINE)
            .align(TextAlign.CENTER)
            .color(UiTheme.TEXT_PRIMARY)
            .build("");
        toastLabel.setAlignment(Align.center);
        toastLabel.setVisible(false);
        toastLabel.setTouchable(Touchable.disabled);
        addActor(toastLabel);

        filterPanel = new CollectionFilterPanel(config, controller, textures, this::onFiltersApplied);
        filterPanel.setVisible(false);
        addActor(filterPanel);
    }

    private void onFiltersApplied() {
        rebuildGrids();
        if (controller.tab() == CollectionData.Tab.ZOMBIES) {
            zombieScroll.setScrollY(0f);
        } else {
            plantScroll.setScrollY(0f);
        }
    }

    private void onBack() {
        CollectionData.View view = controller.view();
        if (view == CollectionData.View.DETAILS) {
            hud.setLeaving(false);
            if (controller.tab() == CollectionData.Tab.ZOMBIES) {
                showZombieGrid();
            } else {
                showPlantGrid();
            }
            return;
        }
        if (view == CollectionData.View.GRID) {
            hud.setLeaving(false);
            showHub(true);
            return;
        }
        ScreenManager.showGameScreen(game);
    }

    private void showHub(boolean animate) {
        controller.openHub();
        applyHubChrome(animate);
    }

    private void showPlantGrid() {
        CollectionData.View previous = controller.view();
        controller.openGrid(CollectionData.Tab.PLANTS);
        rebuildGrids();
        gridRoot.clearChildren();
        gridRoot.center().top();
        gridRoot.add(plantScroll).grow();
        showInPanel(gridRoot);
        showFilterPanel(CollectionData.Tab.PLANTS);
        syncStatueBrowse();
        if (previous == CollectionData.View.HUB) {
            applyBrowseChrome();
            plantScroll.setScrollY(0f);
        }
    }

    private void showZombieGrid() {
        CollectionData.View previous = controller.view();
        controller.openGrid(CollectionData.Tab.ZOMBIES);
        rebuildGrids();
        gridRoot.clearChildren();
        gridRoot.center().top();
        gridRoot.add(zombieScroll).grow();
        showInPanel(gridRoot);
        showFilterPanel(CollectionData.Tab.ZOMBIES);
        syncStatueBrowse();
        if (previous == CollectionData.View.HUB) {
            applyBrowseChrome();
            zombieScroll.setScrollY(0f);
        }
    }

    private void openPlantDetails() {
        controller.openDetails();
        plantDetails.bind(controller.plantDetails(controller.selectedPlant()));
        hideFilterPanel();
        showInPanel(plantDetails);
    }

    private void openZombieDetails() {
        controller.openDetails();
        zombieDetails.bind(controller.zombieDetails(controller.selectedZombie()));
        hideFilterPanel();
        showInPanel(zombieDetails);
    }

    private void applyHubChrome(boolean animate) {
        float duration = animate ? config.interaction.fadeDuration : 0f;
        fadeActor(overlay, false, 0f, duration);
        fadeActor(contentShell, false, 0f, duration);
        hideFilterPanel();
        plantsTab.showHub(!animate);
        zombiesTab.showHub(!animate);
        contentShell.setTouchable(Touchable.disabled);
    }

    private void applyBrowseChrome() {
        float duration = config.interaction.fadeDuration;
        Color tint = config.overlay.tint();
        overlay.setColor(tint.r, tint.g, tint.b, overlay.getColor().a);
        fadeActor(overlay, true, config.overlay.alpha, duration);
        fadeActor(contentShell, true, 1f, duration);
        syncStatueBrowse();
        contentShell.setTouchable(Touchable.enabled);
    }

    private void syncStatueBrowse() {
        boolean zombiesOpen = controller.tab() == CollectionData.Tab.ZOMBIES;
        plantsTab.showBrowse(zombiesOpen, false);
        zombiesTab.showBrowse(zombiesOpen, false);
    }

    private void fadeActor(Actor actor, boolean show, float targetAlpha, float duration) {
        if (actor == null) {
            return;
        }
        actor.clearActions();
        if (show) {
            actor.setVisible(true);
            if (duration <= 0f) {
                actor.getColor().a = targetAlpha;
                return;
            }
            actor.addAction(Actions.alpha(targetAlpha, duration));
            return;
        }
        if (duration <= 0f) {
            actor.getColor().a = 0f;
            actor.setVisible(false);
            return;
        }
        actor.addAction(Actions.sequence(
            Actions.alpha(0f, duration),
            Actions.visible(false)
        ));
    }

    private void showInPanel(Actor incoming) {
        float duration = config.interaction.fadeDuration;
        Actor[] contents = {gridRoot, plantDetails, zombieDetails};
        for (Actor actor : contents) {
            actor.clearActions();
            boolean show = actor == incoming;
            if (show) {
                actor.setVisible(true);
                actor.setTouchable(Touchable.enabled);
                if (duration <= 0f || shownContent == incoming) {
                    actor.getColor().a = 1f;
                } else {
                    actor.addAction(Actions.fadeIn(duration));
                }
            } else if (actor.isVisible()) {
                actor.setTouchable(Touchable.disabled);
                if (duration <= 0f) {
                    actor.getColor().a = 0f;
                    actor.setVisible(false);
                } else {
                    actor.addAction(Actions.sequence(
                        Actions.fadeOut(duration),
                        Actions.visible(false)
                    ));
                }
            }
        }
        shownContent = incoming;
    }

    private void rebuildGrids() {
        float width = gridInnerWidth();
        plantGrid.rebuild(controller.plantCards(), width);
        zombieGrid.rebuild(controller.zombieCards(), width);
        lastGridWidth = width;
    }

    private float gridInnerWidth() {
        float width = contentShell == null ? 0f : contentShell.getWidth();
        if (width <= 1f) {
            width = getWidth();
        }
        return Math.max(1f, width - config.panel.padLeft - config.panel.padRight);
    }

    private void showToast(Result result) {
        if (result == null || result.message() == null || result.message().isBlank()) {
            return;
        }
        Color color = result.success()
            ? config.colors.resolve(config.colors.nextValue, UiTheme.TEXT_SUCCESS)
            : UiTheme.TEXT_ERROR;
        toastLabel.setColor(color);
        toastLabel.setText(result.message());
        toastLabel.setVisible(true);
        toastLabel.clearActions();
        toastLabel.getColor().a = 1f;
        toastLabel.addAction(Actions.sequence(
            Actions.delay(2.2f),
            Actions.fadeOut(0.25f),
            Actions.run(() -> toastLabel.setVisible(false))
        ));
    }

    @Override
    public void layout() {
        float width = getWidth();
        float height = getHeight();
        if (titleImage != null) {
            CollectionAnchors.applyImage(
                titleImage, config.title, textures.get(config.titlePath), width, height);
        }
        plantsTab.place(width, height, textures.get(config.tabs.plants.path));
        zombiesTab.place(width, height, textures.get(config.tabs.zombies.path));
        CollectionAnchors.applyPanel(contentShell, config.panel, width, height);
        contentShell.pad(
            config.panel.padTop,
            config.panel.padLeft,
            config.panel.padBottom,
            config.panel.padRight
        );
        if (controller.view() != CollectionData.View.HUB) {
            float inner = gridInnerWidth();
            if (Math.abs(inner - lastGridWidth) >= 0.5f) {
                plantGrid.relayoutToWidth(inner);
                zombieGrid.relayoutToWidth(inner);
                lastGridWidth = inner;
            }
        }
        if (filterPanel != null && filterPanel.isVisible()) {
            filterPanel.place(width, height);
            float filterClearance = config.filterPanel.height + config.filterPanel.bottomOffset;
            contentShell.pad(
                config.panel.padTop,
                config.panel.padLeft,
                Math.max(config.panel.padBottom, filterClearance * 0.35f),
                config.panel.padRight
            );
            filterPanel.toFront();
        }
        toastLabel.setSize(Math.min(520f, Math.max(200f, contentShell.getWidth())), 40f);
        toastLabel.setPosition(
            (width - toastLabel.getWidth()) / 2f,
            Math.max(12f, contentShell.getY() + 12f)
        );
        if (toastLabel.isVisible()) {
            toastLabel.toFront();
        }
        super.layout();
    }


    private void showFilterPanel(CollectionData.Tab tab) {
        if (filterPanel == null) {
            return;
        }
        filterPanel.bindTab(tab);
        filterPanel.setVisible(true);
        filterPanel.toFront();
        if (toastLabel != null) {
            toastLabel.toFront();
        }
    }

    private void hideFilterPanel() {
        if (filterPanel != null) {
            filterPanel.setVisible(false);
        }
    }

    private Image createOverlay() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        overlayTexture = new Texture(pixmap);
        pixmap.dispose();
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(overlayTexture)));
        image.setFillParent(true);
        image.setTouchable(Touchable.disabled);
        Color tint = config.overlay.tint();
        image.setColor(tint.r, tint.g, tint.b, 0f);
        image.setVisible(false);
        return image;
    }

    @Override
    public void dispose() {
        textures.dispose();
        if (hud != null) {
            hud.dispose();
        }
        if (overlayTexture != null) {
            overlayTexture.dispose();
            overlayTexture = null;
        }
    }
}
