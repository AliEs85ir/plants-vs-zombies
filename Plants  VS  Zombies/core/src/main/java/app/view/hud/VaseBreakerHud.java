package app.view.hud;

import app.controller.game.WorldListener;
import app.enums.plant.PlantName;
import app.view.hud.elements.VaseBreakerPlantCard;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import game.engine.RealTimeGameEngine;
import game.world.chapter.rule.speciallevelRules.VaseBreakerRule;
import game.world.minigame.vasebreaker.VaseBreakerInventoryListener;
import game.world.minigame.vasebreaker.VaseBreakerInventoryPlant;
import game.world.minigame.vasebreaker.VaseBreakerWorld;

import java.util.ArrayList;
import java.util.List;

public class VaseBreakerHud extends AbstractHud implements WorldListener, VaseBreakerInventoryListener {

    private VaseBreakerRule rule;
    private VaseBreakerWorld vaseWorld;
    private Stage worldStage;
    private final Group cardLayer = new Group();
    private final List<VaseBreakerPlantCard> plantCards = new ArrayList<>();
    private VaseBreakerPlantCard selectedCard;

    public VaseBreakerHud() {
        this(null);
    }

    public VaseBreakerHud(VaseBreakerRule rule) {
        this.rule = rule;
    }

    public void attachWorldStage(Stage worldStage) {
        this.worldStage = worldStage;
        if (worldStage == null || cardLayer.getParent() != null) {
            return;
        }
        cardLayer.setTouchable(Touchable.childrenOnly);
        worldStage.addActor(cardLayer);
    }

    @Override
    public void initialize(RealTimeGameEngine engine) {
        super.initialize(engine);
        if (this.rule == null && world != null) {
            this.rule = world.getVaseBreakerRule();
        }
        if (stage != null) {
            stage.getRoot().setTouchable(Touchable.childrenOnly);
        }
        bindWorld();
    }

    @Override
    protected void buildUI() {
        if (stage != null) {
            stage.getRoot().setTouchable(Touchable.childrenOnly);
        }
    }

    @Override
    public void finalizePlantCardBar(List<PlantName> selectedPlants) {
        if (rule == null && world != null) {
            rule = world.getVaseBreakerRule();
        }
        if (rule != null) {
            rule.applyPlantPool(selectedPlants);
        }
    }

    @Override
    public void deselectAllCards() {
        if (vaseWorld != null && vaseWorld.getSelectedItem() != null) {
            vaseWorld.clearSelection();
            return;
        }
        if (selectedCard != null) {
            selectedCard.setSelected(false);
            selectedCard = null;
        }
    }

    @Override
    protected void applySeedSelectionEnabled(boolean enabled) {
        cardLayer.setTouchable(enabled ? Touchable.childrenOnly : Touchable.disabled);
    }

    @Override
    public void onItemArrived(VaseBreakerInventoryPlant item) {
        if (item == null || findCard(item.getId()) != null) {
            return;
        }
        VaseBreakerPlantCard card = new VaseBreakerPlantCard(item, VaseBreakerPlantCard.WORLD_CARD_SCALE);
        card.setListener(this);
        plantCards.add(card);
        cardLayer.addActor(card);
        card.playSpawnOnVase(item.getWorldX(), item.getWorldY());
    }

    @Override
    public void onItemRemoved(VaseBreakerInventoryPlant item) {
        if (item == null) {
            return;
        }
        VaseBreakerPlantCard card = findCard(item.getId());
        if (card == null) {
            return;
        }
        if (selectedCard == card) {
            selectedCard = null;
        }
        plantCards.remove(card);
        card.playConsume(null);
    }

    @Override
    public void onSelectionChanged(VaseBreakerInventoryPlant item) {
        long selectedId = (item != null) ? item.getId() : -1L;
        selectedCard = null;
        for (VaseBreakerPlantCard card : plantCards) {
            boolean isSelected = card.getItemId() == selectedId;
            card.setSelected(isSelected);
            if (isSelected) {
                selectedCard = card;
            }
        }
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {
        if (!seedSelectionEnabled) {
            return;
        }
        VaseBreakerPlantCard clicked = cardByNameNearPointer(plantName);
        if (clicked == null) {
            return;
        }
        if (vaseWorld != null) {
            vaseWorld.select(clicked.getItemId());
        } else {
            selectCard(clicked);
        }
        if (forwardListener != null) {
            forwardListener.onPlantCardSelected(plantName);
        }
    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {
        if (vaseWorld != null) {
            vaseWorld.clearSelection();
        } else {
            deselectAllCards();
        }
        if (forwardListener != null) {
            forwardListener.onPlantCardDeselected(plantName);
        }
    }

    @Override
    public void onPlantCardPlaceAt(float worldX, float worldY) {
        if (forwardListener != null) {
            forwardListener.onPlantCardPlaceAt(worldX, worldY);
        }
    }

    @Override
    public boolean trySelectWorldPlantCard(float worldX, float worldY) {
        if (!seedSelectionEnabled) {
            return false;
        }
        VaseBreakerPlantCard clicked = findCardAt(worldX, worldY);
        if (clicked == null) {
            return false;
        }
        if (clicked.isSelected()) {
            onPlantCardPlaceAt(clicked.getItem().getWorldX(), clicked.getItem().getWorldY());
        } else {
            if (vaseWorld != null) {
                vaseWorld.select(clicked.getItemId());
            } else {
                selectCard(clicked);
            }
            if (forwardListener != null) {
                forwardListener.onPlantCardSelected(clicked.getPlantName());
            }
        }
        return true;
    }

    @Override
    public HudType getType() {
        return HudType.VASE_BREAKER;
    }

    private void bindWorld() {
        if (this.vaseWorld != null) {
            this.vaseWorld.removeListener(this);
        }
        clearCards();
        if (world instanceof VaseBreakerWorld nextWorld) {
            this.vaseWorld = nextWorld;
            this.vaseWorld.addListener(this);
        } else {
            this.vaseWorld = null;
        }
    }

    private void selectCard(VaseBreakerPlantCard card) {
        if (selectedCard != null && selectedCard != card) {
            selectedCard.setSelected(false);
        }
        selectedCard = card;
        if (selectedCard != null) {
            selectedCard.setSelected(true);
        }
    }

    private VaseBreakerPlantCard cardByNameNearPointer(PlantName plantName) {
        for (VaseBreakerPlantCard card : plantCards) {
            if (card.getPlantName() == plantName && card.isHovered()) {
                return card;
            }
        }
        for (VaseBreakerPlantCard card : plantCards) {
            if (card.getPlantName() == plantName) {
                return card;
            }
        }
        return null;
    }

    private VaseBreakerPlantCard findCardAt(float worldX, float worldY) {
        VaseBreakerPlantCard best = null;
        float bestDist = Float.MAX_VALUE;
        for (VaseBreakerPlantCard card : plantCards) {
            if (!card.containsWorldPoint(worldX, worldY)) {
                continue;
            }
            float cx = card.getX() + card.getWidth() / 2f;
            float cy = card.getY() + card.getHeight() / 2f;
            float dx = worldX - cx;
            float dy = worldY - cy;
            float dist = dx * dx + dy * dy;
            if (dist < bestDist) {
                bestDist = dist;
                best = card;
            }
        }
        return best;
    }

    private VaseBreakerPlantCard findCard(long itemId) {
        for (VaseBreakerPlantCard card : plantCards) {
            if (card.getItemId() == itemId) {
                return card;
            }
        }
        return null;
    }

    private void clearCards() {
        plantCards.clear();
        selectedCard = null;
        cardLayer.clearChildren();
    }

    @Override
    public void refreshPlantCardBoostStates() {
        for (VaseBreakerPlantCard card : plantCards) {
            card.syncBoostAppearance();
        }
    }
}
