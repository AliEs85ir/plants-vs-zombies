package app.view.hud.elements.plantcard;

import app.controller.game.WorldListener;
import app.enums.plant.PlantName;
import app.view.game.pregame.PlantSelectionListener;
import app.view.game.pregame.PlantSelectionState;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

public class PlantCardBar extends Table implements WorldListener, PlantSelectionListener {

    private final Table contentTable;
    private final ScrollPane scrollPane;
    private final List<PlantCard> plantCards = new ArrayList<>();

    private float cardScale = 1.0f;
    private float cardSpacing = 6f;
    private float maxBarHeight = 500f;

    private PlantCard selectedCard = null;
    private WorldListener worldListener;
    private boolean selectionEnabled = true;

    private PlantSelectionState selectionState;
    private int emptySlotCount = 0;
    private int lastSelectedCount = -1;

    public PlantCardBar() {
        this(1.0f, 6f, 500f);
    }

    public void update(float deltaTime) {
    }

    public PlantCardBar(float cardScale, float cardSpacing, float maxBarHeight) {
        this.cardScale = cardScale;
        this.cardSpacing = cardSpacing;
        this.maxBarHeight = maxBarHeight;

        this.contentTable = new Table();
        this.contentTable.top();

        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
        this.scrollPane = new ScrollPane(contentTable, style);
        this.scrollPane.setScrollingDisabled(true, false);
        this.scrollPane.setOverscroll(false, false);
        this.scrollPane.setSmoothScrolling(true);
        this.scrollPane.setFadeScrollBars(true);

        this.add(scrollPane).top().expandY().fillY();
        this.top();
        this.setTouchable(Touchable.childrenOnly);
        this.scrollPane.setTouchable(Touchable.childrenOnly);
        this.contentTable.setTouchable(Touchable.childrenOnly);
    }

    public void bindSelectionState(PlantSelectionState state) {
        if (selectionState != null) {
            selectionState.removeListener(this);
        }
        selectionState = state;
        if (selectionState != null) {
            selectionState.addListener(this);
        }
    }

    public void unbindSelectionState() {
        if (selectionState != null) {
            selectionState.removeListener(this);
            selectionState = null;
        }
    }

    @Override
    public void onSelectionChanged(List<PlantName> selected) {
        int capacity = (selectionState != null) ? selectionState.getCapacity() : selected.size();
        emptySlotCount = Math.max(0, capacity - selected.size());

        boolean grew = selected.size() > lastSelectedCount;
        setPlants(selected);
        lastSelectedCount = selected.size();

        if (grew && !plantCards.isEmpty()) {
            PlantCard newest = plantCards.get(plantCards.size() - 1);
            newest.getColor().a = 0f;
            newest.addAction(Actions.fadeIn(0.2f));
        }
    }

    public void setPlants(List<PlantName> plantNames) {
        clearCards();
        if (plantNames != null) {
            for (PlantName name : plantNames) {
                PlantCard card = new PlantCard(name, cardScale);
                card.setListener(this);
                plantCards.add(card);
            }
        }
        rebuildLayout();
    }

    public void addPlantCard(PlantName plantName) {
        PlantCard card = new PlantCard(plantName, cardScale);
        card.setListener(this);
        plantCards.add(card);
        rebuildLayout();
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {
        if (!selectionEnabled) {
            return;
        }
        PlantCard clickedCard = getCardByName(plantName);
        if (clickedCard != null) {
            selectCard(clickedCard);
        }
        if (worldListener != null) {
            worldListener.onPlantCardSelected(plantName);
        }
    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {
        deselectAll();
    }

    public void selectCard(PlantCard card) {
        if (selectedCard != null && selectedCard != card) {
            selectedCard.setSelected(false);
        }
        selectedCard = card;
        if (selectedCard != null) {
            selectedCard.setSelected(true);
        }
    }

    public void deselectAll() {
        if (selectedCard != null) {
            PlantName deselectedPlant = selectedCard.getPlantName();
            selectedCard.setSelected(false);
            selectedCard = null;
            if (worldListener != null) {
                worldListener.onPlantCardDeselected(deselectedPlant);
            }
        }
    }

    public void setCardScale(float scale) {
        this.cardScale = scale;
        for (PlantCard card : plantCards) {
            card.setCardScale(scale);
        }
        rebuildLayout();
    }

    public void setCardSpacing(float spacing) {
        this.cardSpacing = spacing;
        rebuildLayout();
    }

    public void setMaxBarHeight(float height) {
        this.maxBarHeight = height;
        rebuildLayout();
    }

    public void setEmptySlotCount(int count) {
        this.emptySlotCount = Math.max(0, count);
        rebuildLayout();
    }

    private void rebuildLayout() {
        contentTable.clearChildren();

        float maxCardWidth = 0f;
        float slotWidth = 0f;
        float slotHeight = 0f;

        for (PlantCard card : plantCards) {
            contentTable.add(card).padBottom(cardSpacing).row();
            maxCardWidth = Math.max(maxCardWidth, card.getWidth());
            slotWidth = Math.max(slotWidth, card.getWidth());
            slotHeight = Math.max(slotHeight, card.getHeight());
        }

        if (emptySlotCount > 0) {
            if (slotWidth <= 0f || slotHeight <= 0f) {
                float[] fallback = fallbackSlotSize();
                slotWidth = fallback[0];
                slotHeight = fallback[1];
            }
            for (int i = 0; i < emptySlotCount; i++) {
                Actor slot = new EmptyPlantSlot(slotWidth, slotHeight);
                contentTable.add(slot).size(slotWidth, slotHeight)
                    .padBottom(cardSpacing).row();
            }
            maxCardWidth = Math.max(maxCardWidth, slotWidth);
        }

        contentTable.pack();

        float calculatedHeight = Math.min(contentTable.getHeight(), maxBarHeight);
        getCell(scrollPane).width(maxCardWidth + 10f).height(calculatedHeight);
        invalidateHierarchy();
    }

    private float[] fallbackSlotSize() {
        PlantName reference = null;
        if (selectionState != null && !selectionState.getAllowedPlants().isEmpty()) {
            reference = selectionState.getAllowedPlants().get(0);
        }
        if (reference == null) {
            return new float[] { 60f * cardScale, 80f * cardScale };
        }

        PlantCardConfigLoader.PlantCardConfig config =
            PlantCardConfigLoader.getConfig(reference);
        if (config == null || config.cachedBgRegion == null) {
            return new float[] { 60f * cardScale, 80f * cardScale };
        }
        return new float[] {
            config.cachedBgRegion.getRegionWidth() * cardScale,
            config.cachedBgRegion.getRegionHeight() * cardScale
        };
    }

    public void clearCards() {
        plantCards.clear();
        contentTable.clearChildren();
        selectedCard = null;
    }

    public PlantCard getCardByName(PlantName plantName) {
        for (PlantCard card : plantCards) {
            if (card.getPlantName() == plantName) {
                return card;
            }
        }
        return null;
    }

    public void setWorldListener(WorldListener listener) {
        this.worldListener = listener;
    }

    public void setSelectionEnabled(boolean enabled) {
        this.selectionEnabled = enabled;
    }

    public List<PlantCard> getPlantCards() {
        return plantCards;
    }

    public PlantCard getSelectedCard() {
        return selectedCard;
    }

    public void refreshBoostAppearances() {
        for (PlantCard card : plantCards) {
            card.syncBoostAppearance();
        }
    }
}
