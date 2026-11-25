package app.view.mainmenu.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.audio.AudioCategory;

import java.util.ArrayList;
import java.util.List;

public final class SettingsAudioSection extends Table {
    private final List<SettingsAudioRow> rows = new ArrayList<>();

    public SettingsAudioSection(
        SettingsMenuConfig.AudioConfig config,
        SettingsMenuConfig.ResetAudioConfig resetConfig,
        Runnable onReset
    ) {
        top().left();
        Label title = new Label(config.titleText, new Label.LabelStyle(
            SettingsUiFonts.resolve(config.titleFont, config.titleFontFallback),
            SettingsMenuConfig.color(config.titleColor, Color.valueOf("F4E7B0"))
        ));
        title.setFontScale(config.titleFontSize);
        title.setAlignment(SettingsMenuConfig.textAlign(config.titleAlignment));
        add(title)
            .left()
            .padLeft(config.titleOffsetX)
            .padBottom(config.titleBottomPadding + config.titleOffsetY);
        row();

        Table grid = new Table();
        grid.left().top();
        AudioCategory[] categories = AudioCategory.values();
        int columns = Math.max(1, config.columns);
        for (int i = 0; i < categories.length; i++) {
            AudioCategory category = categories[i];
            SettingsAudioRow row = new SettingsAudioRow(
                config,
                config.titleOf(category),
                SettingsAudioRow.categoryBinding(category)
            );
            rows.add(row);
            var cell = grid.add(row).left().height(config.rowHeight).expandX();
            if (i % columns != 0) {
                cell.padLeft(config.columnSpacing);
            }
            if (i >= columns) {
                cell.padTop(config.rowSpacing);
            }
            if ((i + 1) % columns == 0) {
                grid.row();
            }
        }
        add(grid).left().growX();
        row();
        add(new SettingsResetSection(resetConfig, onReset))
            .left()
            .padTop(config.resetTopPadding);
    }

    public void refresh() {
        for (SettingsAudioRow row : rows) {
            row.refresh();
        }
    }
}
