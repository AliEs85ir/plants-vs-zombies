package game.world.tide;

import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.service.plant.PlantPlacementPolicy;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.MapConfig;
import game.world.playground.celltypes.GrassCellType;
import game.world.playground.celltypes.WaterCellType;
import game.world.tide.ambush.LowTideAmbush;
import game.world.tide.effect.NoOpWaterEffectEmitter;
import game.world.tide.effect.WaterEffectEmitter;
import game.world.tide.effect.WaterEffectType;
import game.world.tide.visual.TideVisualConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies wave-driven tide to gameplay cells immediately, then eases the
 * visual water line. Animation is never the source of truth.
 */
public final class TideController {
    private final TideConfig config;
    private TideState state;
    private WaterEffectEmitter effectEmitter = NoOpWaterEffectEmitter.INSTANCE;

    public TideController(TideConfig config) {
        this.config = config;
    }

    public TideConfig getConfig() {
        return config;
    }

    public TideState getState() {
        return state;
    }

    public void setEffectEmitter(WaterEffectEmitter effectEmitter) {
        this.effectEmitter = effectEmitter != null ? effectEmitter : NoOpWaterEffectEmitter.INSTANCE;
    }

    public void initialize(Grid grid) {
        TideLevel initial = config.getInitialLevel();
        int startColumn = resolveWaterStart(initial, grid.getColCount());
        float visualX = waterWorldX(grid, startColumn);
        state = new TideState(
            config.getTideLine(),
            initial,
            new WaterLine(startColumn),
            visualX
        );
        applyCellTypes(grid, state.getWaterLine());
    }

    public void applyWave(GameWorld world, int waveNumber) {
        if (world == null || state == null) {
            return;
        }
        Grid grid = world.getGrid();
        WaveTideDefinition definition = config.getSchedule().forWave(waveNumber)
            .orElse(new WaveTideDefinition(waveNumber, config.getSchedule().levelForWave(waveNumber)));

        int previousStart = state.getWaterLine().getStartColumn();
        int nextStart = resolveWaterStart(definition.getTideLevel(), grid.getColCount());
        WaterLine nextLine = new WaterLine(nextStart);

        List<Cell> newlyExposed = collectNewlyExposedLand(grid, previousStart, nextStart);

        state.beginTransition(definition.getTideLevel(), nextLine);
        applyCellTypes(grid, nextLine);
        drownIllegalPlants(world, grid);
        effectEmitter.emit(
            WaterEffectType.WATER_SPLASH,
            waterWorldX(grid, nextStart),
            getWaterWorldY(grid)
        );

        if (definition.isLowTide() && nextStart > previousStart) {
            LowTideAmbush ambush = definition.getAmbush();
            if (ambush != null) {
                ambush.trigger(world, state, newlyExposed);
            }
        }
    }

    public void updateVisual(Grid grid, long currentTick) {
        if (state == null || !state.isTransitioning()) {
            return;
        }
        float targetX = Math.max(waterWorldX(grid, state.getWaterLine().getStartColumn()), getTideLineWorldX(grid));
        float currentX = Math.max(state.getVisualWaterX(), getTideLineWorldX(grid));
        TideVisualConfig visual = config.getVisualConfig();
        float step = visual.getWaterMoveSpeedPixelsPerSecond() * GameClock.SECONDS_PER_TICK;
        if (Math.abs(targetX - currentX) <= step) {
            state.setVisualWaterX(targetX);
            state.completeTransition();
            return;
        }
        state.setVisualWaterX(currentX + Math.signum(targetX - currentX) * step);
    }

    public float getTideLineWorldX(Grid grid) {
        MapConfig mapConfig = grid.getConfig();
        return mapConfig.getPlaygroundX()
            + config.getTideLine().getFirstFloodableColumn() * mapConfig.getCellWidth();
    }

    public float getTideLineWorldY(Grid grid) {
        MapConfig mapConfig = grid.getConfig();
        return mapConfig.getPlaygroundY() + mapConfig.getPlaygroundHeight() / 2f;
    }

    public float getWaterWorldX(Grid grid) {
        float waterX;
        if (state == null) {
            waterX = waterWorldX(grid, resolveWaterStart(config.getInitialLevel(), grid.getColCount()));
        } else {
            waterX = state.getVisualWaterX();
        }
        return Math.max(waterX, getTideLineWorldX(grid));
    }

    public float getWaterWorldY(Grid grid) {
        return getTideLineWorldY(grid);
    }

    private int resolveWaterStart(TideLevel level, int columnCount) {
        return config.getMapping().toWaterStartColumn(level, columnCount, config.getTideLine());
    }

    private float waterWorldX(Grid grid, int startColumn) {
        MapConfig mapConfig = grid.getConfig();
        float x = mapConfig.getPlaygroundX() + startColumn * mapConfig.getCellWidth();
        return Math.max(x, getTideLineWorldX(grid));
    }

    private void applyCellTypes(Grid grid, WaterLine waterLine) {
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColCount(); col++) {
                Cell cell = grid.getCell(row, col);
                if (waterLine.isWater(col)) {
                    cell.setCellType(new WaterCellType());
                } else {
                    cell.setCellType(new GrassCellType());
                }
            }
        }
    }

    private List<Cell> collectNewlyExposedLand(Grid grid, int previousStart, int nextStart) {
        List<Cell> exposed = new ArrayList<>();
        if (nextStart <= previousStart) {
            return exposed;
        }
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = previousStart; col < nextStart && col < grid.getColCount(); col++) {
                exposed.add(grid.getCell(row, col));
            }
        }
        return exposed;
    }

    private void drownIllegalPlants(GameWorld world, Grid grid) {
        List<Plant> doomed = new ArrayList<>();
        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < grid.getColCount(); col++) {
                Cell cell = grid.getCell(row, col);
                for (Plant plant : new ArrayList<>(cell.getPlants())) {
                    if (!PlantPlacementPolicy.isPlantStillLegal(cell, plant)) {
                        doomed.add(plant);
                    }
                }
            }
        }
        for (Plant plant : doomed) {
            plant.markDead(world);
        }
    }
}
