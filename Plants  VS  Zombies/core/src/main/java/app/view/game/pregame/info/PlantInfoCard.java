package app.view.game.pregame.info;

import app.enums.plant.PlantName;
import app.service.plant.PlantBoostService;
import app.view.game.pregame.PregameLayout;
import app.view.game.pregame.visual.providers.PlantVisualProvider;
import app.view.ui.button.ButtonRow;
import app.view.ui.button.ButtonSpec;
import app.view.ui.ScrollPanes;
import app.view.ui.SkinPanel;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.entity.plant.PlantInfo;
import game.entity.plant.upgrade.EffectivePlantStats;
import game.entity.plant.upgrade.PlantUpgradeView;
import game.service.plant.PlantInfoRegistry;
import game.service.plant.PlantUpgradeService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlantInfoCard extends SkinPanel {

    public static final String ACTION_BOOST   = "boost";
    public static final String ACTION_UPGRADE = "upgrade";

    private static final String PLACEHOLDER_NAME = "Select a Plant";
    private static final String PLACEHOLDER_BODY =
        "Hover any plant card to preview its stats and upgrade progress.";

    private final PregameLayout layout;

    private final PlantVisualCard visualCard;
    private final PlantUpgradeProgressBar upgradeProgress;
    private final Label nameLabel;
    private final Label metaLabel;
    private final Label descriptionLabel;
    private final Label statsLabel;
    private final ButtonRow actions;

    private PlantName current;

    public PlantInfoCard(PregameLayout layout) {
        super(layout.backgrounds.infoPanel);
        this.layout = layout;

        visualCard = new PlantVisualCard(layout);
        upgradeProgress = new PlantUpgradeProgressBar(layout);
        upgradeProgress.setBarWidth(layout.selection.visualCardSize);

        nameLabel        = TextSpec.of(layout.text.plantName).build(PLACEHOLDER_NAME);
        metaLabel        = TextSpec.of(layout.text.plantMeta).build("");
        descriptionLabel = TextSpec.of(layout.text.plantDescription).build(PLACEHOLDER_BODY);
        statsLabel       = TextSpec.of(layout.text.plantStats).build("");

        actions = new ButtonRow(actionSpecs(), layout.buttons.infoActions);
        actions.left();

        buildLayout();
        showPlaceholder();
    }

    private List<ButtonSpec> actionSpecs() {
        return Arrays.asList(
            ButtonSpec.of(ACTION_BOOST, "Boost")
                .style(layout.buttons.tertiaryStyle)
                .fixedWidth(),
            ButtonSpec.of(ACTION_UPGRADE, "Upgrade")
                .style(layout.buttons.primaryStyle)
                .fixedWidth()
        );
    }

    private void buildLayout() {
        pad(layout.selection.infoPadding);
        top().left();

        Table cardColumn = new Table();
        cardColumn.top().left();
        cardColumn.add(visualCard)
            .size(layout.selection.visualCardSize, layout.selection.visualCardSize)
            .top()
            .row();
        cardColumn.add(upgradeProgress)
            .width(layout.selection.visualCardSize)
            .padTop(layout.selection.upgradeProgressGap)
            .top()
            .left();

        add(cardColumn)
            .top()
            .padRight(layout.selection.visualCardGap);

        Table details = new Table();
        details.top().left();

        Table titleRow = new Table();
        titleRow.add(nameLabel).left();
        titleRow.add(metaLabel).right().expandX()
                .padLeft(layout.spacing.innerPadding);
        details.add(titleRow).growX().row();

        Table body = new Table();
        body.top().left();
        body.add(descriptionLabel).growX().left().row();
        body.add(statsLabel).growX().left()
            .padTop(layout.spacing.tightPadding).row();

        ScrollPane bodyScroll = ScrollPanes.vertical(body, layout.cards.scrollbarWidth);
        details.add(bodyScroll).grow()
               .minHeight(layout.selection.descriptionMinHeight)
               .padTop(layout.spacing.tightPadding).row();

        details.add(actions).growX().left()
               .padTop(layout.spacing.innerPadding).row();

        add(details).grow().top().left();
    }

    public void setAction(String actionId, Runnable action) {
        if (actions.get(actionId) != null) {
            actions.get(actionId).onClick(action);
        }
    }

    public void setVisualProvider(PlantVisualProvider provider) {
        visualCard.setProvider(provider);
    }

    public void showPlant(PlantName plantName) {
        if (plantName == null) {
            showPlaceholder();
            return;
        }
        current = plantName;

        PlantInfo info = PlantInfoRegistry.get(plantName);
        PlantUpgradeView upgrade = PlantUpgradeService.getProgress(plantName);
        EffectivePlantStats stats = PlantUpgradeService.getEffectiveStats(plantName);

        nameLabel.setText(info.getDisplayName());
        metaLabel.setText(buildMeta(upgrade, info));
        descriptionLabel.setText(info.getDescription());
        statsLabel.setText(buildStats(stats, info));

        visualCard.show(plantName);
        upgradeProgress.bind(plantName);

        boolean canBoost = PlantBoostService.isEligible(plantName)
            && !PlantBoostService.isBoosted(plantName);
        actions.setEnabled(ACTION_BOOST, canBoost);
        actions.setEnabled(ACTION_UPGRADE, !upgrade.atMaxLevel());
        actions.setText(ACTION_UPGRADE, upgrade.atMaxLevel() ? "Max Level" : "Upgrade");

        invalidateHierarchy();
    }

    public void showPlaceholder() {
        current = null;
        nameLabel.setText(PLACEHOLDER_NAME);
        metaLabel.setText("");
        descriptionLabel.setText(PLACEHOLDER_BODY);
        statsLabel.setText("");
        visualCard.clear();
        upgradeProgress.bind(null);

        actions.setEnabled(ACTION_BOOST, false);
        actions.setEnabled(ACTION_UPGRADE, false);
        actions.setText(ACTION_UPGRADE, "Upgrade");

        invalidateHierarchy();
    }

    public void refresh() {
        if (current == null) {
            showPlaceholder();
            return;
        }
        showPlant(current);
    }

    public PlantName getCurrent() {
        return current;
    }

    private String buildMeta(PlantUpgradeView upgrade, PlantInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append("Lv.").append(upgrade.currentLevel())
            .append("/").append(upgrade.maxLevel());
        if (upgrade.atMaxLevel()) {
            builder.append("  ·  Max Level");
        } else if (!upgrade.coinsMet()) {
            builder.append("  ·  Need ").append(upgrade.requiredCoins()).append(" coins");
        }
        if (!info.getRarity().isEmpty()) {
            builder.append("  ·  ").append(info.getRarity());
        }
        builder.append("  ·  ").append(readableRole(info));
        return builder.toString();
    }

    private String readableRole(PlantInfo info) {
        String raw = info.getRole().name().toLowerCase();
        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
    }

    private String buildStats(EffectivePlantStats stats, PlantInfo info) {
        StringBuilder builder = new StringBuilder();
        if (stats != null) {
            append(builder, "Sun", String.valueOf(stats.sunCost()));
            append(builder, "HP", String.valueOf(stats.maxHealth()));
            if (stats.attackDamage() > 0) {
                append(builder, "DMG", String.valueOf(stats.attackDamage()));
            }
        }
        for (Map.Entry<String, String> entry : info.getExtras().entrySet()) {
            append(builder, entry.getKey(), entry.getValue());
        }
        return builder.toString();
    }

    private void append(StringBuilder builder, String key, String value) {
        if (value == null || value.isEmpty()) return;
        if (!builder.isEmpty()) builder.append("   ");
        builder.append(key).append(": ").append(value);
    }

    @Override
    public float getPrefHeight() {
        return layout.selection.topSectionHeight;
    }

    @Override
    public float getMinHeight() {
        return layout.selection.topSectionHeight;
    }

    @Override
    public float getMaxHeight() {
        return layout.selection.topSectionHeight;
    }
}
