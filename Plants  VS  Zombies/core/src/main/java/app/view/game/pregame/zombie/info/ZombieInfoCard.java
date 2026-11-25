package app.view.game.pregame.zombie.info;

import app.model.App;
import app.view.game.pregame.PregameLayout;
import app.view.game.pregame.zombie.visual.providers.ZombieVisualProvider;
import app.view.ui.ScrollPanes;
import app.view.ui.SkinPanel;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.data.properties.Property;
import game.service.Zombie.ZombieInfo;
import game.service.Zombie.ZombieInfoRegistry;

import java.util.Map;

public class ZombieInfoCard extends SkinPanel {

    private static final String PLACEHOLDER_NAME = "Select a zombie";
    private static final String PLACEHOLDER_BODY =
        "Hover any zombie card to preview its stats.";

    private final PregameLayout layout;

    private final ZombieVisualCard visualCard;
    private final Label nameLabel;
   // private final Label metaLabel;
    private final Label descriptionLabel;
    private final Label statsLabel;

    private String current;

    public ZombieInfoCard(PregameLayout layout) {
        super(layout.backgrounds.infoPanel);
        this.layout = layout;

        visualCard = new ZombieVisualCard(layout);

        nameLabel        = TextSpec.of(layout.text.plantName).build(PLACEHOLDER_NAME);
       // metaLabel        = TextSpec.of(layout.text.plantMeta).build("");
        descriptionLabel = TextSpec.of(layout.text.plantDescription).build(PLACEHOLDER_BODY);
        statsLabel       = TextSpec.of(layout.text.plantStats).build("");


        buildLayout();
        showPlaceholder();
    }

    private void buildLayout() {
        pad(layout.selection.infoPadding);
        top().left();

        add(visualCard)
            .size(layout.selection.visualCardSize, layout.selection.visualCardSize)
            .top()
            .padRight(layout.selection.visualCardGap);

        Table details = new Table();
        details.top().left();

        Table titleRow = new Table();
        titleRow.add(nameLabel).left();
       // titleRow.add(metaLabel).right().expandX()
        //    .padLeft(layout.spacing.innerPadding);
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

        add(details).grow().top().left();
    }

    public void setVisualProvider(ZombieVisualProvider provider) {
        visualCard.setProvider(provider);
    }

    public void showZombie(String alias) {
        if (alias == null) {
            showPlaceholder();
            return;
        }
        current = alias;
        ZombieInfo info = ZombieInfoRegistry.get(alias);
        Property property = ZombieRegistry.getZombie(alias);

        nameLabel.setText(info.getDisplayName());
       // metaLabel.setText(buildMeta(alias, info));
        descriptionLabel.setText(info.getDescription());
        statsLabel.setText(buildStats(property, info));

        visualCard.show(alias);

        invalidateHierarchy();
    }

    public void showPlaceholder() {
        current = null;
        nameLabel.setText(PLACEHOLDER_NAME);
        descriptionLabel.setText(PLACEHOLDER_BODY);
        statsLabel.setText("");
        visualCard.clear();

        invalidateHierarchy();
    }

    public String getCurrent() {
        return current;
    }

//    private String buildMeta(String alias, ZombieInfo info) {
//        StringBuilder builder = new StringBuilder();
//        if (!info.getRarity().isEmpty()) {
//            builder.append("  ·  ").append(info.getRarity());
//        }
//        builder.append("  ·  ").append(readableRole(info));
//        return builder.toString();
//    }


    private String buildStats(Property property, ZombieInfo info) {
        StringBuilder builder = new StringBuilder();
        if (property != null) {
            append(builder, "Sun", String.valueOf(property.getZombieWaveCost()));
            append(builder, "HP", String.valueOf(property.getInitialHealth()));
            append(builder, "EatDPS", String.valueOf(property.getEatDPS()));
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
