package app.view.hud.elements.zombiecard;

import app.controller.game.WorldListener;
import app.view.game.pregame.zombie.ZombieSelectionListener;
import app.view.game.pregame.zombie.ZombieSelectionState;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;
import java.util.List;

public class ZombieCardBar extends Table implements WorldListener, ZombieSelectionListener {
    private final Table contentTable;
    private final ScrollPane scrollPane;
    private final List<ZombieCard> zombieCards = new ArrayList<>();

    private float cardScale = 1.0f;
    private float cardSpacing = 6f;
    private float maxBarHeight = 500f;

    private ZombieCard selectedCard = null;
    private WorldListener worldListener;
    private boolean selectionEnabled = true;

    private ZombieSelectionState selectionState;
    private int emptySlotCount = 0;
    private int lastSelectedCount = -1;

    public ZombieCardBar() {
        this(1.0f, 6f, 500f);
    }

    public void update(float deltaTime) {
    }

    public ZombieCardBar(float cardScale, float cardSpacing, float maxBarHeight) {
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

    public void bindSelectionState(ZombieSelectionState state) {
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
    public void onSelectionChanged(List<String> selected) {
        int capacity = (selectionState != null) ? selectionState.getCapacity() : selected.size();
        emptySlotCount = Math.max(0, capacity - selected.size());

        boolean grew = selected.size() > lastSelectedCount;
        setZombies(selected);
        lastSelectedCount = selected.size();

        if (grew && !zombieCards.isEmpty()) {
            ZombieCard newest = zombieCards.get(zombieCards.size() - 1);
            newest.getColor().a = 0f;
            newest.addAction(Actions.fadeIn(0.2f));
        }
    }

    public void setZombies(List<String> zombies) {
        clearCards();
        if (zombies != null) {
            for (String name : zombies) {
                ZombieCard card = new ZombieCard(name, cardScale);
                card.setListener(this);
                zombieCards.add(card);
            }
        }
        rebuildLayout();
    }

    public void addZombieCard(String name) {
        ZombieCard card = new ZombieCard(name, cardScale);
        card.setListener(this);
        zombieCards.add(card);
        rebuildLayout();
    }

    @Override
    public void onZombieCardSelected(String alias) {
        if (!selectionEnabled) {
            return;
        }
        ZombieCard clickedCard = getCardByName(alias);
        if (clickedCard != null) {
            selectCard(clickedCard);
        }
        if (worldListener != null) {
            worldListener.onZombieCardSelected(alias);
        }
    }

    @Override
    public void onZombieCardDeselected(String alias) {
        deselectAll();
    }

    public void selectCard(ZombieCard card) {
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
            String deselectedZombie = selectedCard.getZombieName();
            selectedCard.setSelected(false);
            selectedCard = null;
            if (worldListener != null) {
                worldListener.onZombieCardDeselected(deselectedZombie);
            }
        }
    }

    public void setCardScale(float scale) {
        this.cardScale = scale;
        for (ZombieCard card : zombieCards) {
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

        for (ZombieCard card : zombieCards) {
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
                Actor slot = new EmptyZombieSlot(slotWidth, slotHeight);
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
        String reference = null;
        if (selectionState != null && !selectionState.getAllowedZombies().isEmpty()) {
            reference = selectionState.getAllowedZombies().get(0);
        }
        if (reference == null) {
            return new float[] { 60f * cardScale, 80f * cardScale };
        }

        ZombieCardConfigLoader.ZombieCardConfig config =
            ZombieCardConfigLoader.getConfig(reference);
        if (config == null || config.cachedBgRegion == null) {
            return new float[] { 60f * cardScale, 80f * cardScale };
        }
        return new float[] {
            config.cachedBgRegion.getRegionWidth() * cardScale,
            config.cachedBgRegion.getRegionHeight() * cardScale
        };
    }

    public void clearCards() {
        zombieCards.clear();
        contentTable.clearChildren();
        selectedCard = null;
    }

    public ZombieCard getCardByName(String alias) {
        for (ZombieCard card : zombieCards) {
            if (card.getZombieName().equals(alias)) {
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

    public List<ZombieCard> getZombieCards() {
        return zombieCards;
    }

    public ZombieCard getSelectedCard() {
        return selectedCard;
    }

    public int getCardCount() {
        return zombieCards.size();
    }

    public void activateCardAt(int index) {
        if (index < 0 || index >= zombieCards.size()) {
            return;
        }
        ZombieCard card = zombieCards.get(index);
        if (card == null) {
            return;
        }
        if (card.isSelected()) {
            onZombieCardDeselected(card.getZombieName());
        } else {
            onZombieCardSelected(card.getZombieName());
        }
        scrollPane.scrollTo(card.getX(), card.getY(), card.getWidth(), card.getHeight());
    }

    public void selectCardAt(int index) {
        if (index < 0 || index >= zombieCards.size()) {
            return;
        }
        onZombieCardSelected(zombieCards.get(index).getZombieName());
    }
}
