package app.view.game.pregame.zombie;

import app.service.plantselection.SelectionHelper;
import game.entity.zombie.data.properties.Property;
import game.world.chapter.Chapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZombieSelectionState {
    public enum Phase { EMPTY, PARTIAL, FULL }

    private final Chapter chapter;
    private final List<String> allowedZombies;
    private final List<String> selected = new ArrayList<>();
    private final List<ZombieSelectionListener> listeners = new ArrayList<>();

    public ZombieSelectionState(Chapter chapter) {
        if (chapter == null) throw new IllegalArgumentException("Chapter must not be null.");
        this.chapter = chapter;
        this.allowedZombies = extract(chapter.getAvailableZombies());
    }

    private List<String> extract(List<Property> zombies) {
        if (zombies == null || zombies.isEmpty()) {
            return List.of();
        }
        List<String> aliases = new ArrayList<>();
        for (Property property : zombies) {
            if (property != null && property.getAlias() != null) {
                aliases.add(property.getAlias());
            }
        }
        return aliases;
    }

    public void addListener(ZombieSelectionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            listener.onSelectionChanged(getSelected());
        }
    }

    public void removeListener(ZombieSelectionListener listener) {
        listeners.remove(listener);
    }

    public Chapter getChapter() {
        return chapter;
    }

    public List<String> getAllowedZombies() {
        return allowedZombies;
    }

    public int getCapacity() {
        return Math.min(chapter.getSelectionCount(), Math.max(allowedZombies.size(), 0));
    }

    public int getSelectedCount() {
        return selected.size();
    }

    public boolean isSelected(String zombie) {
        return selected.contains(zombie);
    }

    public boolean isFull() {
        return selected.size() >= getCapacity();
    }

    public boolean isComplete() {
        return getCapacity() > 0 && selected.size() == getCapacity();
    }

    public List<String> getSelected() {
        return Collections.unmodifiableList(new ArrayList<>(selected));
    }

    public ZombieSelectionState.Phase getPhase() {
        if (selected.isEmpty()) return ZombieSelectionState.Phase.EMPTY;
        return isComplete() ? ZombieSelectionState.Phase.FULL : ZombieSelectionState.Phase.PARTIAL;
    }

    public boolean isSelectable(String zombie) {
        return zombie != null
            && allowedZombies.contains(zombie)
            && !selected.contains(zombie)
            && !isFull();
    }

    public void toggle(String zombie) {
        if (zombie == null) return;
        if (selected.contains(zombie)) {
            deselect(zombie);
        } else {
            select(zombie);
        }
    }

    public void select(String zombie) {
        if (zombie == null) return;
        if (!allowedZombies.contains(zombie)) {
            reject(zombie, zombie + " is not allowed in this stage.");
            return;
        }
        if (selected.contains(zombie)) {
            reject(zombie, zombie + " is already selected.");
            return;
        }
        if (isFull()) {
            reject(zombie, "Selection limit of " + getCapacity() + " reached.");
            return;
        }
        selected.add(zombie);
        notifyChanged();
    }

    public void deselect(String zombie) {
        if (selected.remove(zombie)) {
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
        if (capacity <= 0 || allowedZombies.isEmpty()) return;

        selected.clear();
        try {
            selected.addAll(SelectionHelper.randomZombieSelect(allowedZombies, capacity));
        } catch (IllegalArgumentException e) {
            List<String> pool = new ArrayList<>(allowedZombies);
            Collections.shuffle(pool);
            selected.addAll(pool);
        }
        notifyChanged();
    }

    public String validateForStart() {
        if (getCapacity() <= 0) {
            return "This stage has no selectable plants.";
        }
        if (selected.size() != getCapacity()) {
            return "Select " + getCapacity() + " plants to start (" + selected.size() + " chosen).";
        }
        for (String zombie : selected) {
            if (!allowedZombies.contains(zombie)) {
                return zombie + " is not allowed in this stage.";
            }
        }
        return null;
    }

    private void reject(String alias, String reason) {
        for (ZombieSelectionListener listener : new ArrayList<>(listeners)) {
            listener.onSelectionRejected(alias, reason);
        }
    }

    private void notifyChanged() {
        List<String> snapshot = getSelected();
        for (ZombieSelectionListener listener : new ArrayList<>(listeners)) {
            listener.onSelectionChanged(snapshot);
        }
    }


}
