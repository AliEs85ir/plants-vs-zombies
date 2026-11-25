package app.view.adventure;

import app.view.screen.ScreenManager;
import com.badlogic.gdx.Gdx;
import plants_vs_zombies.PlantsVSZombies;

public final class AdventureChapterRouter {
    private final PlantsVSZombies game;
    private boolean navigating;

    public AdventureChapterRouter(PlantsVSZombies game) {
        this.game = game;
    }

    public void activate(AdventureMenuConfig.ChapterConfig chapter) {
        if (navigating || chapter == null || !chapter.interactive()) {
            return;
        }
        navigating = true;
        switch (chapter.action) {
            case "open_level_select", "level_select", "select" -> {
                if (chapter.seasonType() == null) {
                    navigating = false;
                    Gdx.app.error("AdventureChapterRouter", "Missing season for chapter " + chapter.id);
                    return;
                }
                ScreenManager.showLevelSelectForSeason(game, chapter.seasonType());
            }
            case "none" -> navigating = false;
            default -> {
                navigating = false;
                Gdx.app.log("AdventureChapterRouter", "Unknown action: " + chapter.action);
            }
        }
    }
}
