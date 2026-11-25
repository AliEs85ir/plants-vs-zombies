package app.view.game.pregame;

import app.enums.plant.PlantName;
import app.model.App;
import app.model.user.User;
import app.service.plantselection.SelectionHelper;
import app.service.progression.ProgressionService;
import game.world.chapter.Chapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class PlantSelectionState {

    public enum Phase { EMPTY, PARTIAL, FULL }

    private final Chapter chapter;
    private final List<PlantName> allowedPlants;
    private final List<PlantName> selected = new ArrayList<>();
    private final List<PlantSelectionListener> listeners = new ArrayList<>();

    public PlantSelectionState(Chapter chapter) {
        if (chapter == null) throw new IllegalArgumentException("Chapter must not be null.");
        this.chapter = chapter;
        this.allowedPlants = distinct(chapter.getAvailablePlants());
    }

    private static List<PlantName> distinct(List<PlantName> source) {
        if (source == null) return Collections.emptyList();
        return Collections.unmodifiableList(new ArrayList<>(new LinkedHashSet<>(source)));
    }

    private User currentUser() {
        return App.getCurrentUser();
    }

    public boolean isPlantUnlocked(PlantName plant) {
        User user = currentUser();
        return plant != null && (user == null || user.isUnlocked(plant));
    }

    public void addListener(PlantSelectionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            listener.onSelectionChanged(getSelected());
        }
    }

    public void removeListener(PlantSelectionListener listener) {
        listeners.remove(listener);
    }

    public Chapter getChapter() {
        return chapter;
    }

    public List<PlantName> getAllowedPlants() {
        return allowedPlants;
    }

    public int getCapacity() {
        return Math.min(chapter.getSelectionCount(), Math.max(allowedPlants.size(), 0));
    }

    public int getSelectedCount() {
        return selected.size();
    }

    public List<PlantName> getSelected() {
        return Collections.unmodifiableList(new ArrayList<>(selected));
    }

    public boolean isSelected(PlantName plant) {
        return selected.contains(plant);
    }

    public boolean isFull() {
        return selected.size() >= getCapacity();
    }

    public boolean isComplete() {
        return getCapacity() > 0 && selected.size() == getCapacity();
    }

    public Phase getPhase() {
        if (selected.isEmpty()) return Phase.EMPTY;
        return isComplete() ? Phase.FULL : Phase.PARTIAL;
    }

    public boolean isSelectable(PlantName plant) {
        return plant != null
            && allowedPlants.contains(plant)
            && isPlantUnlocked(plant)
            && !selected.contains(plant)
            && !isFull();
    }

    public void toggle(PlantName plant) {
        if (plant == null) return;
        if (selected.contains(plant)) {
            deselect(plant);
        } else {
            select(plant);
        }
    }

    public void select(PlantName plant) {
        if (plant == null) return;
        if (!allowedPlants.contains(plant)) {
            reject(plant, plant.getLabel() + " is not allowed in this stage.");
            return;
        }
        if (!isPlantUnlocked(plant)) {
            reject(plant, plant.getLabel() + " is locked.");
            return;
        }
        if (selected.contains(plant)) {
            reject(plant, plant.getLabel() + " is already selected.");
            return;
        }
        if (isFull()) {
            reject(plant, "Selection limit of " + getCapacity() + " reached.");
            return;
        }
        selected.add(plant);
        notifyChanged();
    }

    public void deselect(PlantName plant) {
        if (selected.remove(plant)) {
            notifyChanged();
        }
    }

    public void clear() {
        if (selected.isEmpty()) return;
        selected.clear();
        notifyChanged();
    }

    public void applyRandom() {
        int capacity = getCapacity();
        List<PlantName> unlockedPool = unlockedPool();
        if (capacity <= 0 || unlockedPool.isEmpty()) return;

        selected.clear();
        try {
            selected.addAll(SelectionHelper.randomPlantSelect(unlockedPool, capacity));
        } catch (IllegalArgumentException e) {
            List<PlantName> pool = new ArrayList<>(unlockedPool);
            Collections.shuffle(pool);
            selected.addAll(pool.subList(0, Math.min(capacity, pool.size())));
        }
        notifyChanged();
    }

    public String applyRecommended() {
        List<PlantName> recommended = ProgressionService.suggestedPlants(chapter, currentUser());

        if (recommended.isEmpty()) {
            return "No recommended plants are available for this stage.";
        }
        int capacity = getCapacity();
        if (recommended.size() > capacity) {
            recommended = new ArrayList<>(recommended.subList(0, capacity));
        }
        if (recommended.size() < capacity) {
            List<PlantName> filler = new ArrayList<>(unlockedPool());
            filler.removeAll(recommended);
            for (PlantName plant : filler) {
                if (recommended.size() >= capacity) {
                    break;
                }
                recommended.add(plant);
            }
        }
        if (recommended.size() != capacity) {
            return "Not enough unlocked plants to fill the recommended lineup.";
        }

        selected.clear();
        selected.addAll(recommended);
        notifyChanged();
        return null;
    }

    public String validateForStart() {
        if (getCapacity() <= 0) {
            return "This stage has no selectable plants.";
        }
        if (selected.size() != getCapacity()) {
            return "Select " + getCapacity() + " plants to start (" + selected.size() + " chosen).";
        }
        for (PlantName plant : selected) {
            if (!allowedPlants.contains(plant)) {
                return plant.getLabel() + " is not allowed in this stage.";
            }
            if (!isPlantUnlocked(plant)) {
                return plant.getLabel() + " is locked.";
            }
        }
        return null;
    }

    private List<PlantName> unlockedPool() {
        List<PlantName> pool = new ArrayList<>();
        for (PlantName plant : allowedPlants) {
            if (isPlantUnlocked(plant)) {
                pool.add(plant);
            }
        }
        return pool;
    }

    private void reject(PlantName plant, String reason) {
        for (PlantSelectionListener listener : new ArrayList<>(listeners)) {
            listener.onSelectionRejected(plant, reason);
        }
    }

    private void notifyChanged() {
        List<PlantName> snapshot = getSelected();
        for (PlantSelectionListener listener : new ArrayList<>(listeners)) {
            listener.onSelectionChanged(snapshot);
        }
    }
}
