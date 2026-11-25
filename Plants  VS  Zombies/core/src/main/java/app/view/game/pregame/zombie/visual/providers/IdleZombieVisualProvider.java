package app.view.game.pregame.zombie.visual.providers;

import app.view.game.pregame.zombie.visual.ZombieIdlePreviewActor;
import app.view.game.pregame.zombie.visual.ZombieIdlePreviewConfigLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.component.AssetManager;
import graphics.general.animation.PamClipResolver;

public class IdleZombieVisualProvider implements ZombieVisualProvider{
    private final StaticZombieVisualProvider staticFallback = new StaticZombieVisualProvider();

    @Override
    public Actor createVisual(String alias, float width, float height) {
        ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig source =
            ZombieIdlePreviewConfigLoader.getConfig(alias);
        if (source == null
            || source.pamPath == null
            || source.pamPath.isEmpty()
            || source.clip == null
            || source.clip.isEmpty()) {
            return staticFallback.createVisual(alias, width, height);
        }
        ZombieIdlePreviewConfigLoader.ZombieIdlePreviewConfig config = source.copy();

        try {
            AssetManager.player.loadSync(config.pamPath);
        } catch (RuntimeException loadFailed) {
            Gdx.app.error(
                "IdleZombieVisualProvider",
                "Failed to load PAM for " + alias + " at " + config.pamPath + ", using static image",
                loadFailed
            );
            return staticFallback.createVisual(alias, width, height);
        }

        String resolvedClip = PamClipResolver.resolve(
            AssetManager.player,
            config.pamPath,
            config.clip
        );
        if (resolvedClip == null) {
            Gdx.app.error(
                "IdleZombieVisualProvider",
                "No clips in PAM " + config.pamPath + " for " + alias
            );
            return staticFallback.createVisual(alias, width, height);
        }
        if (!resolvedClip.equals(config.clip)) {
            Gdx.app.error(
                "IdleZombieVisualProvider",
                "Clip '" + config.clip + "' is not in " + config.pamPath
                    + " for " + alias + ", using '" + resolvedClip + "'"
            );
            config.clip = resolvedClip;
        }

        ZombieIdlePreviewActor actor = new ZombieIdlePreviewActor(config);
        actor.setSize(width, height);
        return actor;
    }
}
