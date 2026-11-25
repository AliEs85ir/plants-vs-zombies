package app.view.collection;

import app.view.collection.config.CollectionConfig;
import app.view.ui.UiTheme;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import game.component.AssetManager;
import game.entity.plant.upgrade.PlantUpgradeView;

public final class CollectionSeedMeter extends WidgetGroup {
    private final CollectionConfig config;
    private final ProgressBar bar;
    private final Image icon;
    private final Label countLabel;
    private float barHeight;

    public CollectionSeedMeter(CollectionConfig config, float width, float barHeight) {
        this.config = config;
        this.barHeight = barHeight;
        bar = createBar(config.progress.yellowStyle);
        icon = new Image();
        icon.setScaling(Scaling.fit);
        countLabel = TextSpec.of(UiTheme.LBL_SECONDARY)
            .align(TextAlign.CENTER)
            .color(UiTheme.TEXT_PRIMARY)
            .scale(config.progress.textScale)
            .build("0 / 0");
        countLabel.setAlignment(Align.center);
        addActor(bar);
        addActor(icon);
        addActor(countLabel);
        setSize(width, Math.max(barHeight, config.progress.iconSize));
    }

    public void setMeterWidth(float width) {
        setWidth(Math.max(1f, width));
        invalidate();
    }

    public void bind(PlantUpgradeView view) {
        boolean ready = view != null && (view.atMaxLevel() || view.seedsMet());
        applyStyle(ready);
        applyIcon(ready);
        bar.setRange(0f, 1f);
        bar.setValue(view == null ? 0f : Math.min(1f, view.seedProgress()));
        countLabel.setText(formatCount(view));
    }

    @Override
    public void layout() {
        float width = getWidth();
        float height = Math.max(barHeight, config.progress.iconSize);
        setHeight(height);
        float iconSize = config.progress.iconSize;
        bar.setBounds(0f, (height - barHeight) / 2f, width, barHeight);
        icon.setBounds(config.progress.iconGap, (height - iconSize) / 2f, iconSize, iconSize);
        countLabel.setBounds(0f, 0f, width, height);
    }

    @Override
    public float getPrefWidth() {
        return getWidth();
    }

    @Override
    public float getPrefHeight() {
        return Math.max(barHeight, config.progress.iconSize);
    }

    private void applyStyle(boolean ready) {
        Skin skin = AssetManager.skin;
        String requested = ready ? config.progress.greenStyle : config.progress.yellowStyle;
        String style = resolveStyle(skin, requested);
        if (skin != null && hasStyle(skin, style)) {
            bar.setStyle(skin.get(style, ProgressBar.ProgressBarStyle.class));
        }
    }

    private void applyIcon(boolean ready) {
        String id = ready ? config.progress.greenIcon : config.progress.yellowIcon;
        TextureRegion region = AssetManager.textureBank == null ? null : AssetManager.textureBank.region(id);
        if (region == null) {
            icon.setDrawable(null);
            icon.setVisible(false);
            return;
        }
        icon.setVisible(true);
        icon.setDrawable(new TextureRegionDrawable(region));
    }

    private String formatCount(PlantUpgradeView view) {
        if (view == null) {
            return String.format(config.progress.countFormat, 0, 0);
        }
        if (view.atMaxLevel()) {
            return config.progress.maxText;
        }
        return String.format(config.progress.countFormat, view.currentSeeds(), view.requiredSeeds());
    }

    private ProgressBar createBar(String styleName) {
        Skin skin = AssetManager.skin;
        String style = resolveStyle(skin, styleName);
        ProgressBar created;
        try {
            created = new ProgressBar(0f, 1f, 0.001f, false, skin, style);
        } catch (Exception ignored) {
            created = new ProgressBar(0f, 1f, 0.001f, false, skin);
        }
        created.setAnimateDuration(0f);
        return created;
    }

    private static String resolveStyle(Skin skin, String requested) {
        if (hasStyle(skin, requested)) {
            return requested;
        }
        if (hasStyle(skin, "xp_green")) {
            return "xp_green";
        }
        return "default-horizontal";
    }

    private static boolean hasStyle(Skin skin, String name) {
        return skin != null && name != null && !name.isBlank()
            && skin.has(name, ProgressBar.ProgressBarStyle.class);
    }
}
