package app.view.collection.zombie;

import app.view.collection.CollectionGridLayout;
import app.view.collection.config.CollectionConfig;
import app.view.collection.data.CollectionData;
import app.view.ui.UiTheme;
import app.view.ui.text.TextAlign;
import app.view.ui.text.TextSpec;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class CollectionZombieGrid extends Table {
    private final CollectionConfig config;
    private final Consumer<CollectionData.ZombieCardModel> onSelect;
    private List<CollectionData.ZombieCardModel> models = new ArrayList<>();
    private float availableWidth = 1f;

    public CollectionZombieGrid(CollectionConfig config, Consumer<CollectionData.ZombieCardModel> onSelect) {
        this.config = config;
        this.onSelect = onSelect;
        center().top();
    }

    public void rebuild(List<CollectionData.ZombieCardModel> cards, float width) {
        this.models = cards == null ? new ArrayList<>() : cards;
        this.availableWidth = Math.max(1f, width);
        relayout();
    }

    public void relayoutToWidth(float width) {
        if (Math.abs(width - availableWidth) < 0.5f && getChildren().size > 0) {
            return;
        }
        this.availableWidth = Math.max(1f, width);
        relayout();
    }

    @Override
    public float getPrefWidth() {
        return availableWidth;
    }

    @Override
    public float getMinWidth() {
        return availableWidth;
    }

    private void relayout() {
        CollectionConfig.GridConfig grid = config.zombieGrid;
        CollectionGridLayout.begin(this, grid);
        if (models.isEmpty()) {
            Label empty = TextSpec.of(UiTheme.LBL_MEDIUM_OUTLINE)
                .align(TextAlign.CENTER)
                .color(UiTheme.TEXT_MUTED)
                .build(config.labels.filterEmpty);
            empty.setAlignment(Align.center);
            empty.setWrap(true);
            add(empty).width(Math.max(120f, availableWidth - 24f)).padTop(36f).center();
            return;
        }
        float cardWidth = config.cardWidth(grid, availableWidth);
        Table row = CollectionGridLayout.newRow();
        int column = 0;
        for (CollectionData.ZombieCardModel model : models) {
            ZombieCollectionCard card = new ZombieCollectionCard(
                model.alias,
                1f,
                model.seen,
                config,
                () -> {
                    if (onSelect != null) {
                        onSelect.accept(model);
                    }
                }
            );
            float scale = cardWidth / Math.max(1f, card.nativeWidth());
            card.setCardScale(scale);
            CollectionGridLayout.addCell(
                row,
                card,
                card.getWidth(),
                card.getHeight(),
                column,
                grid.columns,
                grid.gapX
            );
            column++;
            if (column >= grid.columns) {
                CollectionGridLayout.finishRow(this, row, grid.gapY);
                row = CollectionGridLayout.newRow();
                column = 0;
            }
        }
        if (column > 0) {
            CollectionGridLayout.finishRow(this, row, grid.gapY);
        }
    }
}
