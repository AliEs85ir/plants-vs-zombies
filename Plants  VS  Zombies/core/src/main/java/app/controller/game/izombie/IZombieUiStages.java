package app.controller.game.izombie;

import app.view.game.PlayScreen;
import app.view.screen.BaseScreen;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import plants_vs_zombies.PlantsVSZombies;

public final class IZombieUiStages {
    private IZombieUiStages() {
    }

    public static Stage current() {
        PlantsVSZombies game = PlantsVSZombies.getPvz();
        if (game == null) {
            return null;
        }
        Screen screen = game.getScreen();
        if (screen instanceof BaseScreen baseScreen) {
            return baseScreen.getStage();
        }
        if (screen instanceof PlayScreen playScreen) {
            if (playScreen.getHud() != null && playScreen.getHud().getStage() != null) {
                return playScreen.getHud().getStage();
            }
            return playScreen.getStage();
        }
        return null;
    }
}
