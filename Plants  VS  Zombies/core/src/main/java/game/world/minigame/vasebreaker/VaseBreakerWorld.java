package game.world.minigame.vasebreaker;

import app.enums.plant.PlantName;
import game.entity.zombie.Zombie;
import game.world.minigame.Minigame;
import game.world.minigame.MinigameWorld;
import game.world.playground.Cell;
import game.world.seasons.Season;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VaseBreakerWorld extends MinigameWorld {
    private final List<Vase> vases = new ArrayList<>();
    private final List<VaseBreakerInventoryPlant> plantInventory;
    private final List<VaseBreakerInventoryListener> listeners = new ArrayList<>();
    private long nextItemId = 1L;
    private Long selectedItemId;

    public VaseBreakerWorld(Minigame logic, Season season) {
        super(logic, season);
        plantInventory = new ArrayList<>();
    }

    public List<Vase> getVases() {
        return vases;
    }

    public void addPlantToInventory(PlantName plantName, long currentTick) {
        addPlantToInventory(plantName, currentTick, 0f, 0f);
    }

    public void addPlantToInventory(PlantName plantName, long currentTick, float worldX, float worldY) {
        if (plantName == null) {
            return;
        }
        VaseBreakerInventoryPlant entry =
            new VaseBreakerInventoryPlant(nextItemId++, plantName, currentTick, worldX, worldY);
        plantInventory.add(entry);
        notifyArrived(entry);
    }

    public List<VaseBreakerInventoryPlant> getPlantInventory() {
        return plantInventory;
    }

    public boolean hasPlantInInventory(PlantName name) {
        if (name == null) {
            return false;
        }
        VaseBreakerInventoryPlant selected = getSelectedItem();
        if (selected != null) {
            return selected.getPlantName() == name;
        }
        for (VaseBreakerInventoryPlant entry : plantInventory) {
            if (entry.getPlantName() == name) {
                return true;
            }
        }
        return false;
    }

    public boolean consumeSelected(PlantName plantName) {
        VaseBreakerInventoryPlant selected = getSelectedItem();
        if (selected != null && selected.getPlantName() == plantName) {
            return removeEntry(selected);
        }
        return removePlantFromInventory(plantName);
    }

    public boolean removePlantFromInventory(PlantName name) {
        for (VaseBreakerInventoryPlant entry : plantInventory) {
            if (entry.getPlantName() == name) {
                return removeEntry(entry);
            }
        }
        return false;
    }

    public void select(long itemId) {
        VaseBreakerInventoryPlant item = findItem(itemId);
        selectedItemId = (item != null) ? itemId : null;
        notifySelectionChanged(item);
    }

    public void clearSelection() {
        if (selectedItemId == null) {
            return;
        }
        selectedItemId = null;
        notifySelectionChanged(null);
    }

    public VaseBreakerInventoryPlant getSelectedItem() {
        return selectedItemId == null ? null : findItem(selectedItemId);
    }

    public VaseBreakerInventoryPlant findItem(long itemId) {
        for (VaseBreakerInventoryPlant entry : plantInventory) {
            if (entry.getId() == itemId) {
                return entry;
            }
        }
        return null;
    }

    public void addListener(VaseBreakerInventoryListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            for (VaseBreakerInventoryPlant entry : new ArrayList<>(plantInventory)) {
                listener.onItemArrived(entry);
            }
            listener.onSelectionChanged(getSelectedItem());
        }
    }

    public void removeListener(VaseBreakerInventoryListener listener) {
        listeners.remove(listener);
    }

    public void removeExpiredPlants(long currentTick) {
        Iterator<VaseBreakerInventoryPlant> iterator = plantInventory.iterator();

        while (iterator.hasNext()) {
            VaseBreakerInventoryPlant entry = iterator.next();

            if (entry.isExpired(currentTick)) {
                iterator.remove();
                if (selectedItemId != null && selectedItemId == entry.getId()) {
                    selectedItemId = null;
                    notifySelectionChanged(null);
                }
                notifyRemoved(entry);
                addEvent(entry.getPlantName().name() + " disappeared from inventory after 50 ticks.");
            }
        }
    }

    public void addVase(Vase vase) {
        vases.add(vase);
        Cell vaseCell = getGrid().getCellAt(vase.getPosition());
        vaseCell.setObstacle(vase);
    }

    private boolean removeEntry(VaseBreakerInventoryPlant entry) {
        if (!plantInventory.remove(entry)) {
            return false;
        }
        if (selectedItemId != null && selectedItemId == entry.getId()) {
            selectedItemId = null;
            notifySelectionChanged(null);
        }
        notifyRemoved(entry);
        return true;
    }

    private void notifyArrived(VaseBreakerInventoryPlant item) {
        for (VaseBreakerInventoryListener listener : snapshotListeners()) {
            listener.onItemArrived(item);
        }
    }

    private void notifyRemoved(VaseBreakerInventoryPlant item) {
        for (VaseBreakerInventoryListener listener : snapshotListeners()) {
            listener.onItemRemoved(item);
        }
    }

    private void notifySelectionChanged(VaseBreakerInventoryPlant item) {
        for (VaseBreakerInventoryListener listener : snapshotListeners()) {
            listener.onSelectionChanged(item);
        }
    }

    private List<VaseBreakerInventoryListener> snapshotListeners() {
        return new ArrayList<>(listeners);
    }
}
