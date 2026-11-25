package app.view.game.pregame.zombie;

import app.view.game.pregame.PregameLayout;
import app.view.hud.elements.zombiecard.SelectionZombieCard;
import app.view.hud.elements.zombiecard.ZombieCardConfigLoader;
import app.view.ui.ScrollPanes;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZombieGridPanel extends Table {
    private final ZombieSelectionState state;
    private final PregameLayout layout;
    private final SelectionZombieCard.HoverListener hoverListener;

    private final Map<String, SelectionZombieCard> cards = new HashMap<>();
    private final Table grid = new Table();
    private final ScrollPane scrollPane;

    public ZombieGridPanel(ZombieSelectionState state, PregameLayout layout,
                           SelectionZombieCard.HoverListener hoverListener) {
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

        List<String> zombies = state.getAllowedZombies();
        if (zombies.isEmpty()) return;

        float cellWidth = layout.gridCellWidth();
        float scale = resolveScale(zombies, cellWidth);
        float cellHeight = referenceHeight(zombies) * scale;

        int column = 0;
        for (String zombie : zombies) {
            SelectionZombieCard card =
                new SelectionZombieCard(zombie, scale, state, layout.cards);
            card.setHoverListener(hoverListener);
            cards.put(zombie, card);

            Container<SelectionZombieCard> cell = new Container<>(card);
            cell.size(card.getWidth(), card.getHeight());
            cell.center();

            Cell<Container<SelectionZombieCard>> gridCell =
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

    private float resolveScale(List<String> zombies, float cellWidth) {
        float nativeWidth = referenceWidth(zombies);
        if (nativeWidth <= 0f) return layout.cards.gridMinScale;

        float scale = cellWidth / nativeWidth;
        return Math.max(layout.cards.gridMinScale,
            Math.min(layout.cards.gridMaxScale, scale));
    }

    private float referenceWidth(List<String> zombies) {
        ZombieCardConfigLoader.ZombieCardConfig config = referenceConfig(zombies);
        return (config != null && config.cachedBgRegion != null)
            ? config.cachedBgRegion.getRegionWidth() : 0f;
    }

    private float referenceHeight(List<String> zombies) {
        ZombieCardConfigLoader.ZombieCardConfig config = referenceConfig(zombies);
        return (config != null && config.cachedBgRegion != null)
            ? config.cachedBgRegion.getRegionHeight() : 1f;
    }

    private ZombieCardConfigLoader.ZombieCardConfig referenceConfig(List<String> zombies) {
        if (zombies == null || zombies.isEmpty()) return null;
        return ZombieCardConfigLoader.getConfig(zombies.get(0));
    }

    public void refresh() {
        for (SelectionZombieCard card : cards.values()) {
            card.setColor(card.isDisabled() ? 0.7f : 1f, 1f, 1f, 1f);
        }
    }

    public SelectionZombieCard getCard(String zombieName) {
        return cards.get(zombieName);
    }

    public int getCardCount() {
        return state.getAllowedZombies().size();
    }

    public void setKeyboardFocusIndex(int index) {
        List<String> zombies = state.getAllowedZombies();
        if (zombies.isEmpty()) {
            return;
        }
        int clamped = Math.max(0, Math.min(index, zombies.size() - 1));
        for (int i = 0; i < zombies.size(); i++) {
            SelectionZombieCard card = cards.get(zombies.get(i));
            if (card != null) {
                card.setKeyboardFocused(i == clamped);
            }
        }
        if (hoverListener != null) {
            hoverListener.onZombieHovered(zombies.get(clamped));
        }
        scrollCardIntoView(zombies.get(clamped));
    }

    public void scrollBy(float amountY) {
        if (scrollPane == null) {
            return;
        }
        scrollPane.setScrollY(scrollPane.getScrollY() - amountY * 48f);
    }

    private void scrollCardIntoView(String alias) {
        SelectionZombieCard card = cards.get(alias);
        if (card == null || scrollPane == null) {
            return;
        }
        Actor target = card.getParent() != null ? card.getParent() : card;
        Vector2 pos = target.localToAscendantCoordinates(grid, new Vector2());
        scrollPane.scrollTo(pos.x, pos.y, target.getWidth(), target.getHeight());
    }

    public void clearKeyboardFocus() {
        for (SelectionZombieCard card : cards.values()) {
            card.setKeyboardFocused(false);
        }
    }

    public void setColumns(int columns) {
        layout.withColumns(columns);
        buildCards();
    }
}
