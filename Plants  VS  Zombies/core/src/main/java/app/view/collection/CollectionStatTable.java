package app.view.collection;

import app.view.collection.config.CollectionConfig;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.List;

public final class CollectionStatTable {
    private CollectionStatTable() {
    }

    public static void fill(Table table, CollectionConfig config, List<StatPreviewRow> rows) {
        table.clearChildren();
        if (rows == null || rows.isEmpty()) {
            return;
        }
        int columns = Math.max(1, config.details.statColumns);
        int column = 0;
        for (StatPreviewRow row : rows) {
            if (row == null) {
                continue;
            }
            table.add(row).growX().top()
                .padRight(config.details.statColGap)
                .padBottom(config.details.statRowGap);
            column++;
            if (column >= columns) {
                table.row();
                column = 0;
            }
        }
        if (column > 0) {
            table.row();
        }
    }
}
