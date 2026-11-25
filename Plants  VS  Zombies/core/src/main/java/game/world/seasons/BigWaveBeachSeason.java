package game.world.seasons;

import app.enums.SeasonType;
import app.enums.obstacle.ObstacleType;
import app.model.App;
import app.view.hud.notification.GameplayNotifications;
import game.engine.GameSimulationEngine;
import game.entity.obstacle.Obstacle;
import game.service.season.SeasonService;
import game.world.GameWorld;
import game.world.playground.Grid;
import game.world.tide.TideConfig;
import game.world.tide.TideController;

public class BigWaveBeachSeason extends Season {
    private final TideController tideController;

    public BigWaveBeachSeason() {
        this(TideConfig.bigWaveBeachDefault());
    }

    public BigWaveBeachSeason(TideConfig tideConfig) {
        seasonName = "Big Wave Beach";
        this.tideController = new TideController(tideConfig);
    }

    @Override
    public String getGrassDisplay() {
        return Color.GREEN + "░" + Color.RESET;
    }

    @Override
    public String getWaterDisplay() {
        return Color.CYAN + "≈" + Color.RESET;
    }

    @Override
    public void setupMap(Grid grid) {
        tideController.initialize(grid);
    }


    @Override
    public void onWaveStart(GameWorld world, int waveNumber) {
        GameplayNotifications.showSeasonHazard("Quick Sand!");
        game.audio.Sfx.play(game.audio.AudioId.VOX_DOLPHIN_JUMP);
        game.audio.Sfx.play(game.audio.AudioId.SFX_ZOMBIE_SPLASH);
        GameSimulationEngine engine = App.getCurrentGameEngine();
        if (engine == null) {
            return;
        }
        for(Obstacle obstacle : world.getObstacles()) {
            obstacle.onWaveStart(world, waveNumber);
        }
        tideController.applyWave(world, waveNumber);
    }

    @Override
    public void updateEnvironment(long currentTick, SeasonService service) {
        if (service == null || service.getWorld() == null) {
            return;
        }
        tideController.updateVisual(service.getWorld().getGrid(), currentTick);
    }

    @Override
    public TideController getTideController() {
        return tideController;
    }

    @Override
    public SeasonType getSeasonType() {
        return SeasonType.BIG_WAVE_BEACH;
    }
}
