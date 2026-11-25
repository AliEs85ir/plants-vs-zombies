package app.view.hud.elements;

import app.controller.game.WorldListener;
import app.enums.plant.PlantName;
import app.view.hud.elements.plantcard.PlantCardConfigLoader;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import game.service.special_levels.ConveyorBeltItem;
import game.service.special_levels.ConveyorBeltListener;
import game.service.special_levels.ConveyorBeltService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConveyorBeltBar extends Table implements WorldListener, ConveyorBeltListener {

    private final ConveyorBeltLayout layout;
    private final Group contentGroup;
    private final ScrollPane scrollPane;
    private final List<ConveyorPlantCard> cards = new ArrayList<>();

    private ConveyorBeltService service;
    private WorldListener worldListener;
    private ConveyorPlantCard selectedCard;
    private boolean selectionEnabled = true;
    private float viewportHeight;
    private float barWidth;

    public ConveyorBeltBar(float width, float height, ConveyorBeltLayout layout) {
        this.layout = (layout != null) ? layout : ConveyorBeltLayout.defaults();
        this.barWidth = width;
        this.viewportHeight = height;
        this.layout.maxBarHeight = height;

        this.contentGroup = new Group();
        this.contentGroup.setSize(width, height);

        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
        this.scrollPane = new ScrollPane(contentGroup, style);
        this.scrollPane.setScrollingDisabled(true, false);
        this.scrollPane.setOverscroll(false, false);
        this.scrollPane.setSmoothScrolling(true);
        this.scrollPane.setFadeScrollBars(true);

        this.setSize(width, height);
        this.add(scrollPane).size(width, height).top();
        this.top();
        this.setTouchable(Touchable.childrenOnly);
        this.scrollPane.setTouchable(Touchable.childrenOnly);
        this.contentGroup.setTouchable(Touchable.childrenOnly);
    }

    public void bindService(ConveyorBeltService nextService) {
        if (this.service != null) {
            this.service.removeListener(this);
        }
        this.service = nextService;
        clearCards();
        if (this.service != null) {
            this.service.addListener(this);
        }
    }

    public void unbindService() {
        bindService(null);
    }

    @Override
    public void onItemArrived(ConveyorBeltItem item) {
        if (item == null || findCard(item.getId()) != null) return;

        float scale = resolveCardScale(item.getPlantName());
        ConveyorPlantCard card = new ConveyorPlantCard(item, scale, layout);
        card.setListener(this);
        cards.add(card);
        contentGroup.addActor(card);

        float height = contentHeight();
        contentGroup.setSize(barWidth, height);
        float x = (barWidth - card.getWidth()) / 2f;
        card.setPosition(x, visibleSpawnY());
        relayout(true);
    }

    @Override
    public void onItemRemoved(ConveyorBeltItem item) {
        if (item == null) return;
        ConveyorPlantCard card = findCard(item.getId());
        if (card == null) return;

        if (selectedCard == card) {
            selectedCard = null;
        }
        cards.remove(card);
        card.playExit(() -> relayout(true));
        relayout(true);
    }

    @Override
    public void onSelectionChanged(ConveyorBeltItem item) {
        long selectedId = (item != null) ? item.getId() : -1L;
        for (ConveyorPlantCard card : cards) {
            card.setSelected(card.getItemId() == selectedId);
        }
        selectedCard = (item != null) ? findCard(item.getId()) : null;
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {
        if (!selectionEnabled) {
            return;
        }
        ConveyorPlantCard clicked = cardByNameNearPointer(plantName);
        if (clicked == null) return;
        if (service != null) {
            service.select(clicked.getItemId());
        } else {
            selectCard(clicked);
        }
        if (worldListener != null) {
            worldListener.onPlantCardSelected(plantName);
        }
    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {
        if (service != null) {
            service.clearSelection();
        } else {
            deselectAll();
        }
        if (worldListener != null) {
            worldListener.onPlantCardDeselected(plantName);
        }
    }

    public void deselectAll() {
        if (service != null && service.getSelectedItem() != null) {
            service.clearSelection();
            return;
        }
        if (selectedCard != null) {
            selectedCard.setSelected(false);
            selectedCard = null;
        }
    }

    public void setWorldListener(WorldListener listener) {
        this.worldListener = listener;
    }

    public void setSelectionEnabled(boolean enabled) {
        this.selectionEnabled = enabled;
    }

    public List<ConveyorPlantCard> getCards() {
        return cards;
    }

    public void refreshBoostAppearances() {
        for (ConveyorPlantCard card : cards) {
            card.syncBoostAppearance();
        }
    }

    private void selectCard(ConveyorPlantCard card) {
        if (selectedCard != null && selectedCard != card) {
            selectedCard.setSelected(false);
        }
        selectedCard = card;
        if (selectedCard != null) {
            selectedCard.setSelected(true);
        }
    }

    private ConveyorPlantCard cardByNameNearPointer(PlantName plantName) {
        for (ConveyorPlantCard card : cards) {
            if (card.getPlantName() == plantName && card.isHovered()) {
                return card;
            }
        }
        for (ConveyorPlantCard card : cards) {
            if (card.getPlantName() == plantName) {
                return card;
            }
        }
        return null;
    }

    private void relayout(boolean animate) {
        pruneConsumed();
        float height = contentHeight();
        contentGroup.setSize(barWidth, height);

        for (int i = 0; i < cards.size(); i++) {
            ConveyorPlantCard card = cards.get(i);
            if (card.getPhase() == ConveyorPlantCard.Phase.CONSUMED) continue;
            float[] slot = slotForIndex(i);
            boolean atRest = Math.abs(card.getX() - slot[0]) < 0.5f
                && Math.abs(card.getY() - slot[1]) < 0.5f;
            if (card.getPhase() == ConveyorPlantCard.Phase.ENTERING) {
                card.playEnter(slot[0], slot[1]);
            } else if (animate && !atRest) {
                card.slideTo(slot[0], slot[1]);
            } else if (!animate) {
                card.setPosition(slot[0], slot[1]);
            }
        }

        getCell(scrollPane).size(barWidth, Math.min(height, viewportHeight));
        invalidateHierarchy();
    }

    private float[] slotForIndex(int index) {
        float height = contentHeight();
        float cursor = height - layout.padTop;
        for (int i = 0; i < cards.size(); i++) {
            ConveyorPlantCard card = cards.get(i);
            if (card.getPhase() == ConveyorPlantCard.Phase.CONSUMED) continue;
            if (i == index) {
                float x = (barWidth - card.getWidth()) / 2f;
                float y = cursor - card.getHeight();
                return new float[] { x, y };
            }
            cursor -= card.getHeight() + layout.cardSpacing;
        }
        return new float[] { layout.padLeft, layout.padBottom };
    }

    private float contentHeight() {
        float needed = layout.padTop + layout.padBottom;
        int visible = 0;
        for (ConveyorPlantCard card : cards) {
            if (card.getPhase() == ConveyorPlantCard.Phase.CONSUMED) continue;
            needed += card.getHeight();
            visible++;
        }
        if (visible > 1) {
            needed += layout.cardSpacing * (visible - 1);
        }
        return Math.max(viewportHeight, needed);
    }

    private float visibleSpawnY() {
        float height = Math.max(contentGroup.getHeight(), viewportHeight);
        float scrolled = (scrollPane != null) ? scrollPane.getScrollY() : 0f;
        return Math.max(0f, height - viewportHeight - scrolled);
    }

    private float resolveCardScale(PlantName plantName) {
        if (layout.cardScale != 1f) return layout.cardScale;
        PlantCardConfigLoader.PlantCardConfig config = PlantCardConfigLoader.getConfig(plantName);
        if (config == null || config.cachedBgRegion == null) return 1f;
        float nativeWidth = config.cachedBgRegion.getRegionWidth();
        if (nativeWidth <= 0f) return 1f;
        return Math.min(1f, (barWidth * layout.cardWidthRatio) / nativeWidth);
    }

    private ConveyorPlantCard findCard(long itemId) {
        for (ConveyorPlantCard card : cards) {
            if (card.getItemId() == itemId) {
                return card;
            }
        }
        return null;
    }

    private void pruneConsumed() {
        Iterator<ConveyorPlantCard> iterator = cards.iterator();
        while (iterator.hasNext()) {
            ConveyorPlantCard card = iterator.next();
            if (card.getPhase() == ConveyorPlantCard.Phase.CONSUMED && card.getParent() == null) {
                iterator.remove();
            }
        }
    }

    private void clearCards() {
        cards.clear();
        contentGroup.clearChildren();
        selectedCard = null;
    }
}
