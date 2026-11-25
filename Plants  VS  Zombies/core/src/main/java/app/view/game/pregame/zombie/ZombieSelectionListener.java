package app.view.game.pregame.zombie;

import java.util.List;

public interface ZombieSelectionListener {
    void onSelectionChanged(List<String> selected);
    default void onSelectionRejected(String alias, String reason){};
}
