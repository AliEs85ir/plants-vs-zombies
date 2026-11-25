package app.view.collection;

import app.view.collection.config.CollectionConfig;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public final class CollectionGridLayout {
    private CollectionGridLayout() {
    }

    public static void begin(Table table, CollectionConfig.GridConfig grid) {
        table.clearChildren();
        table.center().top();
        table.pad(grid.padTop, grid.padLeft, grid.padBottom, grid.padRight);
    }

    public static Table newRow() {
        Table row = new Table();
        row.center();
        return row;
    }

    public static void addCell(Table row, Actor cell, float width, float height, int column, int columns, float gapX) {
        var added = row.add(cell);
        if (width > 0f && height > 0f) {
            added.size(width, height);
        }
        if (column + 1 < columns) {
            added.padRight(gapX);
        }
    }

    public static void finishRow(Table table, Table row, float gapY) {
        table.add(row).center().padBottom(gapY).row();
    }
}
