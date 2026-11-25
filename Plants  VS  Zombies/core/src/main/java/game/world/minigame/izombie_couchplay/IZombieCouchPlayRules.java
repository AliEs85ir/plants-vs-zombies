package game.world.minigame.izombie_couchplay;

import app.controller.game.IZombieCouchPlayController;
import app.view.hud.HudType;
import app.view.hud.IZombieCouchPlayHud;
import game.world.chapter.Chapter;

public final class IZombieCouchPlayRules {
    private IZombieCouchPlayRules() {
    }

    public static boolean matches(Chapter chapter) {
        if (chapter == null) {
            return false;
        }
        if (chapter.getMinigameLogic() instanceof IZombieCouchPlayMode) {
            return true;
        }
        if (chapter.getScreenController() instanceof IZombieCouchPlayController) {
            return true;
        }
        return chapter.getHud() instanceof IZombieCouchPlayHud
            || (chapter.getHud() != null && chapter.getHud().getType() == HudType.I_ZOMBIE_COUCH_PLAY);
    }
}
