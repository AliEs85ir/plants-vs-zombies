package graphics.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import game.component.AssetManager;
import game.world.playground.Grid;
import game.world.tide.TideController;
import game.world.tide.visual.BeachOverlayTuning;
import game.world.tide.visual.TideVisualConfig;
import graphics.general.animation.AnimationLibrary;

/**
 * Renders the fixed Tide Line and the moving Water Line overlay.
 * The visible left edge of WAVE_UPPERLAYER is the Water Line and is
 * clamped so it never travels left of the Tide Line.
 *
 * Tune {@link BeachOverlayTuning} for scale and offset.
 */
public final class BeachEnvironmentRenderer {
    private final AnimationLibrary animationLibrary;
    private final TideController tideController;
    private final ScaledPamOverlay tideLine;
    private final ScaledPamOverlay water;

    public BeachEnvironmentRenderer(AnimationLibrary animationLibrary, TideController tideController) {
        this.animationLibrary = animationLibrary;
        this.tideController = tideController;
        TideVisualConfig visual = tideController.getConfig().getVisualConfig();
        this.tideLine = new ScaledPamOverlay(visual.getTideLinePamPath(), visual.getTideLineClip(), true);
        this.water = new ScaledPamOverlay(visual.getWaterPamPath(), visual.getWaterClip(), true);
    }

    public void load() {
        tideLine.ensureLoaded(animationLibrary);
        water.ensureLoaded(animationLibrary);
    }

    public void render(SpriteBatch batch, Grid grid, float delta) {
        if (grid == null || tideController.getState() == null) {
            return;
        }

        float tideLineX = tideController.getTideLineWorldX(grid) + BeachOverlayTuning.TIDE_LINE_OFFSET_X;
        float tideLineY = tideController.getTideLineWorldY(grid) + BeachOverlayTuning.TIDE_LINE_OFFSET_Y;
        tideLine.setPosition(tideLineX, tideLineY);
        tideLine.setScale(BeachOverlayTuning.TIDE_LINE_SCALE_X, BeachOverlayTuning.TIDE_LINE_SCALE_Y);
        tideLine.update(delta);
        tideLine.render(batch, AssetManager.player);

        float waterScaleX = BeachOverlayTuning.WATER_SCALE_X;
        float originToLeft = BeachOverlayTuning.WATER_ORIGIN_TO_LEFT_EDGE * waterScaleX;
        float waterLineX = Math.max(tideController.getWaterWorldX(grid), tideController.getTideLineWorldX(grid));
        float visualLeft = waterLineX + BeachOverlayTuning.WATER_OFFSET_X;
        if (visualLeft < tideController.getTideLineWorldX(grid)) {
            visualLeft = tideController.getTideLineWorldX(grid);
        }
        float drawX = visualLeft - originToLeft;
        float drawY = tideController.getWaterWorldY(grid) + BeachOverlayTuning.WATER_OFFSET_Y;

        water.setPosition(drawX, drawY);
        water.setScale(waterScaleX, BeachOverlayTuning.WATER_SCALE_Y);
        water.update(delta);
        water.render(batch, AssetManager.player);
    }
}
