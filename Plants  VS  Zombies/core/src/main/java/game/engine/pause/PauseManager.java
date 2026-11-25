package game.engine.pause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PauseManager {

    private final EnumSet<PauseReason> activeReasons = EnumSet.noneOf(PauseReason.class);
    private final List<PauseListener> listeners = new ArrayList<>();

    public boolean pause(PauseReason reason) {
        if (reason == null) return false;
        boolean wasPaused = isPaused();
        if (!activeReasons.add(reason)) {
            return false;
        }
        notifyIfChanged(wasPaused);
        return true;
    }

    public boolean resume(PauseReason reason) {
        if (reason == null) return false;
        boolean wasPaused = isPaused();
        if (!activeReasons.remove(reason)) {
            return false;
        }
        notifyIfChanged(wasPaused);
        return true;
    }

    public void resumeAll() {
        if (activeReasons.isEmpty()) return;
        boolean wasPaused = isPaused();
        activeReasons.clear();
        notifyIfChanged(wasPaused);
    }

    public boolean isPaused() {
        return !activeReasons.isEmpty();
    }

    public boolean isPausedBy(PauseReason reason) {
        return activeReasons.contains(reason);
    }

    public Set<PauseReason> getActiveReasons() {
        return Collections.unmodifiableSet(EnumSet.copyOf(
            activeReasons.isEmpty() ? EnumSet.noneOf(PauseReason.class) : activeReasons));
    }

    public int getHoldCount() {
        return activeReasons.size();
    }

    public boolean canPlayerResume() {
        if (activeReasons.isEmpty()) return false;
        for (PauseReason reason : activeReasons) {
            if (!reason.isDismissibleByPlayer()) {
                return false;
            }
        }
        return true;
    }

    public void addListener(PauseListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(PauseListener listener) {
        listeners.remove(listener);
    }

    private void notifyIfChanged(boolean wasPaused) {
        boolean nowPaused = isPaused();
        if (wasPaused == nowPaused) return;

        Set<PauseReason> snapshot = getActiveReasons();
        for (PauseListener listener : new ArrayList<>(listeners)) {
            listener.onPauseStateChanged(nowPaused, snapshot);
        }
    }
}
