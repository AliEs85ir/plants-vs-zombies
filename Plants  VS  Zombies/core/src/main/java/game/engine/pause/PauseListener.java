package game.engine.pause;

import java.util.Set;

public interface PauseListener {

    void onPauseStateChanged(boolean paused, Set<PauseReason> activeReasons);
}
