package app.view.game.pregame.visual.providers;

import app.enums.plant.PlantName;
import app.view.game.pregame.visual.PlantIdlePreviewActor;
import app.view.game.pregame.visual.PlantIdlePreviewConfigLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import game.component.AssetManager;
import graphics.general.animation.PamClipResolver;
import graphics.general.animation.PamPathResolver;
import graphics.general.animation.loader.PlantAnimationProfileLoader;
import graphics.plant.PlantAnimationProfile;

public class IdlePlantVisualProvider implements PlantVisualProvider {

    private final StaticPlantVisualProvider staticFallback = new StaticPlantVisualProvider();

    @Override
    public Actor createVisual(PlantName plantName, float width, float height) {
        PlantIdlePreviewConfigLoader.PlantIdlePreviewConfig source =
            PlantIdlePreviewConfigLoader.getConfig(plantName);
        if (source == null) {
            return staticFallback.createVisual(plantName, width, height);
        }
        PlantIdlePreviewConfigLoader.PlantIdlePreviewConfig config = source.copy();

        PamPathResolver paths = PamPathResolver.get();
        if (config.pamPath != null && !config.pamPath.isEmpty()) {
            config.pamPath = paths.resolve(config.pamPath);
        }

        if (!paths.exists(config.pamPath)) {
            Gdx.app.error(
                "IdlePlantVisualProvider",
                "Missing PAM for " + plantName + " at " + config.pamPath
                    + ", using fallback animation profile"
            );
            if (!applyFallbackProfile(config, plantName) || !paths.exists(config.pamPath)) {
                return staticFallback.createVisual(plantName, width, height);
            }
        }

        try {
            AssetManager.player.loadSync(config.pamPath);
        } catch (RuntimeException loadFailed) {
            Gdx.app.error(
                "IdlePlantVisualProvider",
                "Failed to load PAM for " + plantName + " at " + config.pamPath
                    + ", using fallback animation profile",
                loadFailed
            );
            if (!applyFallbackProfile(config, plantName)) {
                return staticFallback.createVisual(plantName, width, height);
            }
            try {
                AssetManager.player.loadSync(config.pamPath);
            } catch (RuntimeException fallbackFailed) {
                Gdx.app.error(
                    "IdlePlantVisualProvider",
                    "Failed to load fallback PAM for " + plantName + " at " + config.pamPath,
                    fallbackFailed
                );
                return staticFallback.createVisual(plantName, width, height);
            }
        }

        String resolvedClip = PamClipResolver.resolve(
            AssetManager.player,
            config.pamPath,
            config.clip
        );
        if (resolvedClip == null) {
            Gdx.app.error(
                "IdlePlantVisualProvider",
                "No clips in PAM " + config.pamPath + " for " + plantName
            );
            return staticFallback.createVisual(plantName, width, height);
        }
        if (!resolvedClip.equals(config.clip)) {
            Gdx.app.error(
                "IdlePlantVisualProvider",
                "Clip '" + config.clip + "' is not in " + config.pamPath
                    + " for " + plantName + ", using '" + resolvedClip + "'"
            );
            config.clip = resolvedClip;
        }

        PlantIdlePreviewActor actor = new PlantIdlePreviewActor(config);
        actor.setSize(width, height);
        return actor;
    }

    private boolean applyFallbackProfile(
        PlantIdlePreviewConfigLoader.PlantIdlePreviewConfig config,
        PlantName plantName
    ) {
        PlantAnimationProfile fallback = PlantAnimationProfileLoader.fallbackFor(plantName);
        if (fallback == null || fallback.getPamPath() == null || fallback.getPamPath().isEmpty()) {
            return false;
        }
        config.pamPath = PamPathResolver.get().resolve(fallback.getPamPath());
        config.clip = PlantAnimationProfileLoader.idleClipOf(fallback);
        return true;
    }
}
