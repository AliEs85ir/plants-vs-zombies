package app.view.minigame;

import java.util.List;

public final class MiniGameHitTester {
    private final MiniGameMapSpace space;
    private final List<MiniGameDefinition> games;

    public MiniGameHitTester(MiniGameMapSpace space, List<MiniGameDefinition> games) {
        this.space = space;
        this.games = games == null ? List.of() : games;
    }

    public MiniGameDefinition at(float mapX, float mapY) {
        if (space != null && !space.containsMapPoint(mapX, mapY)) {
            return null;
        }
        MiniGameDefinition found = null;
        for (MiniGameDefinition game : games) {
            if (game != null && game.contains(mapX, mapY)) {
                found = game;
            }
        }
        return found;
    }
}
