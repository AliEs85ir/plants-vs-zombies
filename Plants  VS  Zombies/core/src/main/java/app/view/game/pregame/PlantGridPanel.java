package app.view.game.pregame;

import app.enums.plant.PlantName;
import app.view.hud.elements.plantcard.PlantCardConfigLoader;
import app.view.hud.elements.plantcard.SelectionPlantCard;
import app.view.ui.ScrollPanes;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PlantGridPanel extends Table {

    private final PlantSelectionState state;
    private final PregameLayout layout;
    private final SelectionPlantCard.HoverListener hoverListener;

    private final Map<PlantName, SelectionPlantCard> cards = new EnumMap<>(PlantName.class);
    private final Table grid = new Table();
    private final ScrollPane scrollPane;

    public PlantGridPanel(PlantSelectionState state, PregameLayout layout,
                          SelectionPlantCard.HoverListener hoverListener) {
        this.state = state;
        this.layout = layout;
        this.hoverListener = hoverListener;

        grid.top().left();
        scrollPane = ScrollPanes.vertical(grid, layout.cards.scrollbarWidth);
        add(scrollPane).grow();

        buildCards();
    }

    private void buildCards() {
        grid.clearChildren();
        cards.clear();

        List<PlantName> plants = state.getAllowedPlants();
        if (plants.isEmpty()) return;

        float cellWidth = layout.gridCellWidth();
        float scale = resolveScale(plants, cellWidth);
        float cellHeight = referenceHeight(plants) * scale;

        int column = 0;
        for (PlantName plant : plants) {
            SelectionPlantCard card =
                new SelectionPlantCard(plant, scale, state, layout.cards);
            card.setHoverListener(hoverListener);
            cards.put(plant, card);

            Container<SelectionPlantCard> cell = new Container<>(card);
            cell.size(card.getWidth(), card.getHeight());
            cell.center();

            Cell<Container<SelectionPlantCard>> gridCell =
                grid.add(cell)
                    .width(cellWidth)
                    .height(cellHeight);
            if (column > 0) {
                gridCell.padLeft(layout.cards.cellGapX);
            }
            gridCell.padBottom(layout.cards.cellGapY);

            if (++column >= layout.cards.gridColumns) {
                grid.row();
                column = 0;
            }
        }
    }

    private float resolveScale(List<PlantName> plants, float cellWidth) {
        float nativeWidth = referenceWidth(plants);
        if (nativeWidth <= 0f) return layout.cards.gridMinScale;

        float scale = cellWidth / nativeWidth;
        return Math.max(layout.cards.gridMinScale,
               Math.min(layout.cards.gridMaxScale, scale));
    }

    private float referenceWidth(List<PlantName> plants) {
        PlantCardConfigLoader.PlantCardConfig config = referenceConfig(plants);
        return (config != null && config.cachedBgRegion != null)
            ? config.cachedBgRegion.getRegionWidth() : 0f;
    }

    private float referenceHeight(List<PlantName> plants) {
        PlantCardConfigLoader.PlantCardConfig config = referenceConfig(plants);
        return (config != null && config.cachedBgRegion != null)
            ? config.cachedBgRegion.getRegionHeight() : 1f;
    }

    private PlantCardConfigLoader.PlantCardConfig referenceConfig(List<PlantName> plants) {
        if (plants == null || plants.isEmpty()) return null;
        return PlantCardConfigLoader.getConfig(plants.get(0));
    }

    public void refresh() {
        for (SelectionPlantCard card : cards.values()) {
            if (card.isLocked()) {
                card.setColor(0.55f, 0.55f, 0.55f, 1f);
            } else {
                card.setColor(card.isDisabled() ? 0.7f : 1f, 1f, 1f, 1f);
            }
            card.syncBoostAppearance();
            card.refreshUpgradeAppearance();
        }
    }

    public void refreshBoostAppearances() {
        for (SelectionPlantCard card : cards.values()) {
            card.syncBoostAppearance();
        }
    }

    public SelectionPlantCard getCard(PlantName plantName) {
        return cards.get(plantName);
    }

    public void setColumns(int columns) {
        layout.withColumns(columns);
        buildCards();
    }
}
