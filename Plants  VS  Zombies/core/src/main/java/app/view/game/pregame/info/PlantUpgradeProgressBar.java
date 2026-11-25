package app.view.game.pregame.info;

import app.enums.plant.PlantName;
import app.view.game.pregame.PregameLayout;
import app.view.ui.UiTheme;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import game.component.AssetManager;
import game.entity.plant.upgrade.PlantUpgradeView;
import game.service.plant.PlantUpgradeService;

public final class PlantUpgradeProgressBar extends Table {
    public static final String ICON_YELLOW = "IMAGE_UI_GENERIC_XP_PROGRESS_ICON_YELLOW_LARGE";
    public static final String ICON_GREEN = "IMAGE_UI_GENERIC_XP_PROGRESS_ICON_GREEN_LARGE";
    public static final String STYLE_YELLOW = "xp_yellow";
    public static final String STYLE_GREEN = "xp_green";

    private final Metrics metrics;
    private final ProgressBar progressBar;
    private final Image icon;
    private final Label seedLabel;
    private final Label coinLabel;
    private float barWidth;

    public static final class Metrics {
        public final float barWidth;
        public final float barHeight;
        public final float gap;
        public final float iconHeight;
        public final TextSpec seedText;
        public final TextSpec coinText;

        public Metrics(
            float barWidth,
            float barHeight,
            float gap,
            float iconHeight,
            TextSpec seedText,
            TextSpec coinText
        ) {
            this.barWidth = barWidth;
            this.barHeight = barHeight;
            this.gap = gap;
            this.iconHeight = iconHeight;
            this.seedText = seedText;
            this.coinText = coinText;
        }

        public static Metrics from(PregameLayout layout) {
            return new Metrics(
                layout.selection.visualCardSize,
                layout.selection.upgradeProgressHeight,
                layout.selection.upgradeProgressGap,
                layout.selection.upgradeIconHeight,
                TextSpec.of(layout.text.upgradeProgress),
                TextSpec.of(layout.text.upgradeCoins)
            );
        }
    }

    public PlantUpgradeProgressBar(PregameLayout layout) {
        this(Metrics.from(layout));
    }

    public PlantUpgradeProgressBar(Metrics metrics) {
        this.metrics = metrics;
        this.barWidth = metrics.barWidth;
        progressBar = createProgressBar(STYLE_YELLOW);
        icon = new Image();
        icon.setScaling(Scaling.fit);
        seedLabel = metrics.seedText.build("0 / 0");
        coinLabel = metrics.coinText.build("");

        top().left();
        add(progressBar).width(barWidth).height(metrics.barHeight).growX().row();

        Table seedRow = new Table();
        seedRow.left();
        seedRow.add(icon).padRight(4f);
        seedRow.add(seedLabel).left().padRight(8f);
        seedRow.add(coinLabel).left().expandX();
        add(seedRow).width(barWidth).left().padTop(metrics.gap);
    }

    public void bind(PlantName plantName) {
        if (plantName == null) {
            setVisible(false);
            return;
        }
        setVisible(true);
        PlantUpgradeView view = PlantUpgradeService.getProgress(plantName);
        boolean ready = view.seedsMet();
        applyStyle(ready);
        applyIcon(ready);
        progressBar.setRange(0f, 1f);
        progressBar.setValue(view.seedProgress());
        seedLabel.setText(view.seedProgressText());
        if (view.atMaxLevel()) {
            coinLabel.setText("");
        } else {
            coinLabel.setText(view.requiredCoins() + " coins");
            coinLabel.setColor(view.coinsMet() ? UiTheme.TEXT_SUCCESS : UiTheme.TEXT_ERROR);
        }
        getCell(progressBar).width(barWidth);
    }

    public void setBarWidth(float width) {
        this.barWidth = Math.max(1f, width);
        getCell(progressBar).width(barWidth);
        invalidateHierarchy();
    }

    private void applyStyle(boolean seedsMet) {
        Skin skin = AssetManager.skin;
        String requested = seedsMet ? STYLE_GREEN : STYLE_YELLOW;
        String style = resolveProgressStyle(skin, requested);
        if (skin != null && hasProgressStyle(skin, style)) {
            progressBar.setStyle(skin.get(style, ProgressBar.ProgressBarStyle.class));
        }
    }

    private void applyIcon(boolean seedsMet) {
        String id = seedsMet ? ICON_GREEN : ICON_YELLOW;
        TextureRegion region = AssetManager.textureBank == null ? null : AssetManager.textureBank.region(id);
        if (region == null) {
            icon.setDrawable(null);
            icon.setVisible(false);
            return;
        }
        icon.setVisible(true);
        icon.setDrawable(new TextureRegionDrawable(region));
        float height = metrics.iconHeight;
        float aspect = (float) region.getRegionWidth() / (float) Math.max(1, region.getRegionHeight());
        float width = height * aspect;
        icon.setSize(width, height);
        Table seedRow = (Table) icon.getParent();
        if (seedRow != null && seedRow.getCell(icon) != null) {
            seedRow.getCell(icon).size(width, height);
        }
    }

    private ProgressBar createProgressBar(String styleName) {
        Skin skin = AssetManager.skin;
        String style = resolveProgressStyle(skin, styleName);
        ProgressBar bar;
        try {
            bar = new ProgressBar(0f, 1f, 0.001f, false, skin, style);
        } catch (Exception ignored) {
            bar = new ProgressBar(0f, 1f, 0.001f, false, skin);
        }
        bar.setAnimateDuration(0f);
        return bar;
    }

    private static String resolveProgressStyle(Skin skin, String requested) {
        if (hasProgressStyle(skin, requested)) {
            return requested;
        }
        if (hasProgressStyle(skin, "xp_green")) {
            return "xp_green";
        }
        if (hasProgressStyle(skin, "default-horizontal")) {
            return "default-horizontal";
        }
        return "default-horizontal";
    }

    private static boolean hasProgressStyle(Skin skin, String name) {
        return skin != null && name != null && !name.isBlank()
            && skin.has(name, ProgressBar.ProgressBarStyle.class);
    }
}
