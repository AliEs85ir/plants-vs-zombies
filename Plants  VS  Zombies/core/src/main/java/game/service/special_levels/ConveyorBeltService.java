package game.service.special_levels;

import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.world.GameWorld;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public class ConveyorBeltService {

    private final GameWorld world;
    private final ConveyorBeltConfig config;
    private final List<PlantName> plantPool;
    private final List<ConveyorBeltItem> items;
    private final List<ConveyorBeltListener> listeners;
    private final Random random;

    private long nextItemId = 1L;
    private long nextSpawnTick = 0L;
    private int currentIntervalTicks;
    private Long selectedItemId;
    private boolean started;

    public ConveyorBeltService(GameWorld world, List<PlantName> plantPool) {
        this(world, plantPool, ConveyorBeltConfig.defaults());
    }

    public ConveyorBeltService(GameWorld world, List<PlantName> plantPool, ConveyorBeltConfig config) {
        this.world = world;
        this.config = (config != null) ? config : ConveyorBeltConfig.defaults();
        this.plantPool = new ArrayList<>();
        this.items = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.random = new Random();
        setPlantPool(plantPool);
        this.currentIntervalTicks = rollIntervalTicks();
    }

    public void start() {
        startAt(0);
    }

    public void startAt(long currentTick) {
        if (started) return;
        started = true;
        scheduleNextSpawn(currentTick);
    }

    public boolean isStarted() {
        return started;
    }

    public void update(long currentTick) {
        if (!started) return;
        if (currentTick < nextSpawnTick) return;
        if (rollPlant()) {
            scheduleNextSpawn(currentTick);
        } else {
            nextSpawnTick = currentTick + 1;
        }
    }

    public boolean rollPlant() {
        if (isFull()) {
            return false;
        }
        if (plantPool.isEmpty()) {
            return false;
        }

        PlantName rolledPlant = plantPool.get(random.nextInt(plantPool.size()));
        ConveyorBeltItem item = new ConveyorBeltItem(nextItemId++, rolledPlant);
        items.add(item);
        if (world != null) {
            world.addEvent("🎁 [Conveyor Belt] A new " + rolledPlant + " has arrived!");
        }
        notifyArrived(item);
        return true;
    }

    public void consumePlant(PlantName plantName) {
        consumeFirst(plantName);
    }

    public boolean consumeSelected(PlantName plantName) {
        ConveyorBeltItem selected = getSelectedItem();
        if (selected != null && selected.getPlantName() == plantName) {
            return removeItem(selected);
        }
        return consumeFirst(plantName);
    }

    public boolean consumeFirst(PlantName plantName) {
        if (plantName == null) return false;
        for (ConveyorBeltItem item : items) {
            if (item.getPlantName() == plantName) {
                return removeItem(item);
            }
        }
        return false;
    }

    public boolean hasPlant(PlantName plantName) {
        if (plantName == null) return false;
        ConveyorBeltItem selected = getSelectedItem();
        if (selected != null) {
            return selected.getPlantName() == plantName;
        }
        for (ConveyorBeltItem item : items) {
            if (item.getPlantName() == plantName) {
                return true;
            }
        }
        return false;
    }

    public void select(long itemId) {
        ConveyorBeltItem item = findItem(itemId);
        selectedItemId = (item != null) ? itemId : null;
        notifySelectionChanged(item);
    }

    public void clearSelection() {
        if (selectedItemId == null) return;
        selectedItemId = null;
        notifySelectionChanged(null);
    }

    public ConveyorBeltItem getSelectedItem() {
        return selectedItemId == null ? null : findItem(selectedItemId);
    }

    public ConveyorBeltItem findItem(long itemId) {
        for (ConveyorBeltItem item : items) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public List<PlantName> getBeltPlants() {
        List<PlantName> names = new ArrayList<>(items.size());
        for (ConveyorBeltItem item : items) {
            names.add(item.getPlantName());
        }
        return names;
    }

    public List<ConveyorBeltItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public int getCapacity() {
        return config.getMaxCapacity();
    }

    public boolean isFull() {
        return config.hasCapacityLimit() && items.size() >= config.getMaxCapacity();
    }

    public int getTickInterval() {
        return currentIntervalTicks;
    }

    public void setTickInterval(int tickInterval) {
        int clamped = Math.max(1, tickInterval);
        currentIntervalTicks = clamped;
        float seconds = clamped / (float) GameClock.TICKS_PER_SECOND;
        config.setIntervalSeconds(seconds, seconds);
    }

    public ConveyorBeltConfig getConfig() {
        return config;
    }

    public List<PlantName> getPlantPool() {
        return Collections.unmodifiableList(plantPool);
    }

    public void setPlantPool(List<PlantName> pool) {
        plantPool.clear();
        if (pool == null) return;
        plantPool.addAll(new LinkedHashSet<>(pool));
        plantPool.removeIf(name -> name == null);
    }

    public void addListener(ConveyorBeltListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            for (ConveyorBeltItem item : getItems()) {
                listener.onItemArrived(item);
            }
            listener.onSelectionChanged(getSelectedItem());
        }
    }

    public void removeListener(ConveyorBeltListener listener) {
        listeners.remove(listener);
    }

    private boolean removeItem(ConveyorBeltItem item) {
        if (!items.remove(item)) return false;
        if (selectedItemId != null && selectedItemId == item.getId()) {
            selectedItemId = null;
            notifySelectionChanged(null);
        }
        notifyRemoved(item);
        return true;
    }

    private void scheduleNextSpawn(long currentTick) {
        currentIntervalTicks = rollIntervalTicks();
        nextSpawnTick = currentTick + currentIntervalTicks;
    }

    private int rollIntervalTicks() {
        int min = config.getMinIntervalTicks();
        int max = config.getMaxIntervalTicks();
        if (max <= min) return min;
        return min + random.nextInt(max - min + 1);
    }

    private void notifyArrived(ConveyorBeltItem item) {
        for (ConveyorBeltListener listener : snapshotListeners()) {
            listener.onItemArrived(item);
        }
    }

    private void notifyRemoved(ConveyorBeltItem item) {
        for (ConveyorBeltListener listener : snapshotListeners()) {
            listener.onItemRemoved(item);
        }
    }

    private void notifySelectionChanged(ConveyorBeltItem item) {
        for (ConveyorBeltListener listener : snapshotListeners()) {
            listener.onSelectionChanged(item);
        }
    }

    private List<ConveyorBeltListener> snapshotListeners() {
        return new ArrayList<>(listeners);
    }
}
