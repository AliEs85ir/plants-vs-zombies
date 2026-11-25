package app.view.game.pregame.zombie.visual.providers;

import com.badlogic.gdx.scenes.scene2d.Actor;

public interface ZombieVisualProvider {
    Actor createVisual(String alias, float width, float height);

    static ZombieVisualProvider staticImage() {
        return new StaticZombieVisualProvider();
    }

    static ZombieVisualProvider idleAnimation() {
        return new IdleZombieVisualProvider();
    }
}
