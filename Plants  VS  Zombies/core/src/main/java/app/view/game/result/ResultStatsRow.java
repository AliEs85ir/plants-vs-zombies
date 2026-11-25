package app.view.game.result;

import app.view.ui.SkinPanel;
import app.view.ui.UiTheme;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import game.component.AssetManager;
import game.world.levelConditions.result.StageRunStatistics;

public final class ResultStatsRow {
    private ResultStatsRow() {
    }

    public static Table build(ResultLayout layout, StageRunStatistics statistics) {
        ResultLayout.Stats stats = layout.stats;
        Table fields = new Table();
        fields.defaults().uniformX().growX().fill();
        addChip(fields, layout, stats.zombiesIcon, stats.zombiesLabel,
            String.valueOf(statistics.getZombiesKilled()), true);
        addChip(fields, layout, stats.plantsIcon, stats.plantsLabel,
            String.valueOf(statistics.getPlantsPlanted()), false);
        addChip(fields, layout, stats.sunsIcon, stats.sunsLabel,
            String.valueOf(statistics.getSunsCollected()), false);
        addChip(fields, layout, stats.timeIcon, stats.timeLabel,
            statistics.formattedPlayTime(), false);
        return fields;
    }

    private static void addChip(
        Table fields,
        ResultLayout layout,
        String iconId,
        String title,
        String value,
        boolean first
    ) {
        ResultLayout.Stats stats = layout.stats;
        SkinPanel chip = new SkinPanel(layout.pause.backgrounds.statField);
        chip.pad(stats.chipPadY, stats.chipPadX, stats.chipPadY, stats.chipPadX);
        chip.left();

        Image icon = fitIcon(iconId);
        if (icon != null) {
            chip.add(icon)
                .size(stats.iconBox, stats.iconBox)
                .padRight(stats.iconTextGap)
                .center();
        }

        Table text = new Table();
        text.left();
        Label label = TextSpec.of(layout.pause.text.statLabel)
            .style(UiTheme.LBL_DEFAULT)
            .color(stats.labelColor)
            .scale(stats.labelScale)
            .build(title);
        label.setAlignment(stats.labelAlign);
        Label valueLabel = TextSpec.of(layout.pause.text.statValue)
            .color(stats.valueColor)
            .scale(stats.valueScale)
            .build(value);
        valueLabel.setAlignment(stats.valueAlign);
        text.add(label).growX().left().row();
        text.add(valueLabel).growX().left().padTop(stats.labelValueGap);
        chip.add(text).growX().left();

        var cell = fields.add(chip).height(stats.chipHeight).left();
        if (!first) {
            cell.padLeft(stats.chipSpacing);
        }
    }

    private static Image fitIcon(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        TextureRegion region = AssetManager.textureBank.region(id);
        if (region == null) {
            return null;
        }
        Image image = new Image(new TextureRegionDrawable(region));
        image.setScaling(Scaling.fit);
        image.setAlign(Align.center);
        return image;
    }
}
