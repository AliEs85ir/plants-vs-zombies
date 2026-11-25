package app.view.collection.plant;

import app.view.collection.CollectionIdlePreview;
import app.view.collection.CollectionSeedMeter;
import app.view.collection.CollectionStatTable;
import app.view.collection.CollectionTextureCache;
import app.view.collection.StatPreviewRow;
import app.view.collection.config.CollectionConfig;
import app.view.collection.data.CollectionData;
import app.view.ui.UiTheme;
import app.view.ui.button.ActionButton;
import app.view.ui.button.ButtonTheme;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import game.component.AssetManager;
import game.entity.plant.upgrade.PlantStatPreview;
import game.entity.plant.upgrade.PlantUpgradePreview;

import java.util.ArrayList;
import java.util.List;

public final class PlantDetailsView extends Table {
    public interface Listener {
        void onPrevious();

        void onNext();

        void onUpgrade();

        void onBuy();
    }

    private final CollectionConfig config;
    private final Listener listener;
    private final CollectionIdlePreview preview;
    private final CollectionSeedMeter progressMeter;
    private final Label nameLabel;
    private final Label descriptionLabel;
    private final Label plantFoodLabel;
    private final Table statsTable;
    private final ActionButton upgradeButton;
    private final ActionButton buyButton;
    private final Image buyGemIcon;
    private final Table buyRow;
    private CollectionData.PlantDetailsModel current;

    public PlantDetailsView(CollectionConfig config, CollectionTextureCache textures, Listener listener) {
        this.config = config;
        this.listener = listener;
        setFillParent(true);
        preview = new CollectionIdlePreview(config);
        progressMeter = new CollectionSeedMeter(
            config, config.details.previewWidth, config.details.progressBarHeight);

        nameLabel = TextSpec.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.CENTER)
            .color(config.colors.resolve(config.colors.title, UiTheme.ACCENT))
            .scale(config.details.titleScale)
            .build("");
        nameLabel.setAlignment(Align.center);

        descriptionLabel = TextSpec.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.TOP_LEFT)
            .color(config.colors.resolve(config.colors.body, UiTheme.TEXT_PRIMARY))
            .scale(config.details.bodyScale)
            .wrap(true)
            .build("");
        descriptionLabel.setAlignment(Align.topLeft);

        plantFoodLabel = TextSpec.of(UiTheme.LBL_DEFAULT)
            .align(TextAlign.TOP_LEFT)
            .color(config.colors.resolve(config.colors.muted, UiTheme.TEXT_MUTED))
            .scale(config.details.plantFoodScale)
            .wrap(true)
            .build("");
        plantFoodLabel.setAlignment(Align.topLeft);

        statsTable = new Table();
        statsTable.top().left();

        ButtonTheme upgradeTheme = new ButtonTheme();
        upgradeTheme.width = config.details.upgradeWidth;
        upgradeTheme.height = config.details.upgradeHeight;
        upgradeTheme.labelScale = 0.8f;
        upgradeButton = new ActionButton(config.labels.upgrade, config.details.upgradeStyle, upgradeTheme);
        upgradeButton.setName("menu-collection-upgrade");
        upgradeButton.onClick(() -> {
            if (listener != null) {
                listener.onUpgrade();
            }
        });

        ButtonTheme buyTheme = new ButtonTheme();
        buyTheme.width = config.details.buyWidth;
        buyTheme.height = config.details.buyHeight;
        buyTheme.labelScale = 0.8f;
        buyButton = new ActionButton(config.labels.buy, config.details.buyStyle, buyTheme);
        buyButton.setName("menu-collection-buy");
        buyButton.onClick(() -> {
            if (listener != null) {
                listener.onBuy();
            }
        });

        buyGemIcon = new Image();
        buyGemIcon.setScaling(Scaling.fit);
        buyGemIcon.setAlign(Align.center);
        Texture gemTexture = textures == null ? null : textures.get(config.details.buyGemIconPath);
        if (gemTexture != null) {
            buyGemIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(gemTexture)));
            buyGemIcon.setVisible(true);
        } else {
            buyGemIcon.setVisible(false);
        }

        buyRow = new Table();
        buyRow.center();
        buyRow.add(buyButton).size(config.details.buyWidth, config.details.buyHeight);
        buyRow.add(buyGemIcon)
            .size(config.details.buyGemIconSize, config.details.buyGemIconSize)
            .padLeft(config.details.buyGemGap);

        ImageButton previousButton = imageButton(config.details.arrowPreviousId, config.details.arrowPreviousDownId);
        ImageButton nextButton = imageButton(config.details.arrowNextId, config.details.arrowNextDownId);
        previousButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (listener != null) {
                    listener.onPrevious();
                }
            }
        });
        nextButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (listener != null) {
                    listener.onNext();
                }
            }
        });

        top().center();
        pad(config.details.padTop, config.details.padLeft, config.details.padBottom, config.details.padRight);
        add(nameLabel).expandX().center().padBottom(config.details.titlePad).row();

        Table left = new Table();
        left.top().center();
        left.add(preview)
            .size(config.details.previewWidth, config.details.previewHeight)
            .row();
        left.add(progressMeter)
            .width(config.details.previewWidth)
            .padTop(config.details.progressPadY)
            .padLeft(config.details.progressPadX)
            .center()
            .row();

        Table right = new Table();
        right.top().left();
        right.add(statsTable)
            .left()
            .width(config.details.infoWidth)
            .padTop(config.details.statsPadTop)
            .row();
        right.add(descriptionLabel)
            .left()
            .width(config.details.infoWidth)
            .height(descriptionHeight(config.details.plantDescriptionLines))
            .padTop(config.details.descriptionPadTop)
            .row();

        Table middle = new Table();
        middle.center().top();
        middle.add(previousButton).size(config.details.arrowSize).padRight(config.details.arrowPad);
        middle.add(left).top().padRight(config.details.columnGap);
        middle.add(right).top();
        middle.add(nextButton).size(config.details.arrowSize).padLeft(config.details.arrowPad);
        add(middle).expand().center().row();

        Stack actionStack = new Stack();
        Table upgradeWrap = new Table();
        upgradeWrap.add(upgradeButton)
            .size(config.details.upgradeWidth, config.details.upgradeHeight);
        actionStack.add(upgradeWrap);
        actionStack.add(buyRow);

        Table bottom = new Table();
        bottom.center();
        bottom.add(actionStack)
            .size(
                Math.max(config.details.upgradeWidth, config.details.buyWidth + config.details.buyGemGap + config.details.buyGemIconSize),
                Math.max(config.details.upgradeHeight, config.details.buyHeight)
            );
        bottom.add(plantFoodLabel)
            .width(config.details.infoWidth)
            .padLeft(config.details.bottomColumnGap)
            .left();
        add(bottom).expandX().center().padTop(config.details.bottomGap);
    }

    public void bind(CollectionData.PlantDetailsModel model) {
        current = model;
        if (model == null) {
            setVisible(false);
            return;
        }
        setVisible(true);
        preview.showPlant(model.plantName, model.unlocked, model.detailsBackgroundId);
        nameLabel.setText(model.displayName);
        descriptionLabel.setText(model.description);
        plantFoodLabel.setText(model.plantFoodDescription == null ? "" : model.plantFoodDescription);
        bindProgress(model);
        bindStats(model);
        bindActionButtons(model);
    }

    public void refresh() {
        bind(current);
    }

    private void bindActionButtons(CollectionData.PlantDetailsModel model) {
        if (model.unlocked) {
            boolean canUpgrade = model.progress != null && model.progress.canUpgrade();
            upgradeButton.setVisible(true);
            upgradeButton.getParent().setVisible(true);
            upgradeButton.setEnabledState(canUpgrade);
            upgradeButton.setText(model.progress != null && model.progress.atMaxLevel()
                ? config.labels.maxLevel
                : config.labels.upgrade);
            buyRow.setVisible(false);
            return;
        }
        upgradeButton.setVisible(false);
        if (upgradeButton.getParent() != null) {
            upgradeButton.getParent().setVisible(false);
        }
        buyRow.setVisible(true);
        String buyText = String.format(config.labels.buyFormat, model.unlockPrice);
        buyButton.setText(buyText);
        buyButton.setEnabledState(true);
        buyGemIcon.setVisible(buyGemIcon.getDrawable() != null);
    }

    private void bindProgress(CollectionData.PlantDetailsModel model) {
        progressMeter.setMeterWidth(config.details.previewWidth);
        progressMeter.setVisible(true);
        progressMeter.bind(model.progress);
    }

    private void bindStats(CollectionData.PlantDetailsModel model) {
        List<StatPreviewRow> rows = new ArrayList<>();
        if (!model.unlocked) {
            CollectionStatTable.fill(statsTable, config, rows);
            return;
        }
        PlantUpgradePreview previewModel = model.preview;
        List<CollectionConfig.StatConfig> configs = config.stats == null ? List.of() : config.stats;
        for (CollectionConfig.StatConfig statConfig : configs) {
            if (statConfig == null || statConfig.id == null || statConfig.id.isBlank()) {
                continue;
            }
            StatPreviewRow row = new StatPreviewRow(config);
            if ("category".equals(statConfig.id)) {
                row.bindPlain(statConfig, statConfig.title, model.categoryLabel, model.categoryIconId);
            } else if ("tags".equals(statConfig.id)) {
                row.bindPlain(statConfig, statConfig.title, model.tagsLabel, statConfig.icon);
            } else if (previewModel != null) {
                PlantStatPreview stat = previewModel.stat(statConfig.id);
                if (stat == null) {
                    continue;
                }
                row.bindPreview(statConfig, stat, statConfig.title);
            } else {
                continue;
            }
            rows.add(row);
        }
        CollectionStatTable.fill(statsTable, config, rows);
    }

    private float descriptionHeight(int lines) {
        return Math.max(1, lines) * config.details.descriptionLineHeight;
    }

    private static ImageButton imageButton(String normalId, String hoverId) {
        TextureRegion normal = AssetManager.textureBank == null ? null : AssetManager.textureBank.region(normalId);
        TextureRegion hover = AssetManager.textureBank == null ? null : AssetManager.textureBank.region(hoverId);
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        if (normal != null) {
            style.imageUp = new TextureRegionDrawable(normal);
        }
        if (hover != null) {
            style.imageOver = new TextureRegionDrawable(hover);
            style.imageDown = new TextureRegionDrawable(hover);
        }
        ImageButton button = new ImageButton(style);
        button.setName("menu-collection-nav");
        return button;
    }
}