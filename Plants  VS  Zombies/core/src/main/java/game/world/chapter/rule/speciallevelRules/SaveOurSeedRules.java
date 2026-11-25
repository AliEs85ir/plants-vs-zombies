package game.world.chapter.rule.speciallevelRules;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.entity.obstacle.ObstacleFactory;
import game.entity.plant.Plant;
import game.entity.plant.PlantDefinition;
import game.entity.registry.PlantRegistry;
import game.service.plant.PlantFactory;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.MapConfig;
import graphics.GraphicsEngine;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SaveOurSeedRules implements ChapterRule {
    private final int protectTileCount;
    private static final Random RANDOM = new Random();
    private Set<Cell> selectedCells;

    public SaveOurSeedRules(int protectTileCount) {
        this.protectTileCount = protectTileCount;
        this.selectedCells = new HashSet<>();
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        Grid grid = world.getGrid();
        int randomCol = RANDOM.nextInt(grid.getColCount());
        for (int i = 0; i < protectTileCount; i++) {
            int randomRow = RANDOM.nextInt(grid.getRowCount());
            Cell cell = grid.getCell(randomRow, randomCol);
            if(selectedCells.contains(cell)) {
                i--;
                continue;
            }

            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.PROTECT_TILE,
                grid.getConfig().getCellWidth() / 2,
                grid.getConfig().getCellHeight() / 2,
                cell.getWorldX(),
                cell.getWorldY(),
                List.of()
            );
            placeRandomPlant(cell, world, graphicsEngine);
            selectedCells.add(cell);
        }
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {

    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {

    }

    private void placeRandomPlant(Cell cell, GameWorld world, GraphicsEngine graphicsEngine) {
        List<PlantName> plants = world.getCurrentChapter().getAvailablePlants();
        PlantName randomPlant = plants.get(RANDOM.nextInt(plants.size()));
        MapConfig config = world.getMap().getMapConfig();

        Vector2 center = config.gridToWorld(
            cell.getRow(),
            cell.getCol()
        );

        long currentTick = App.getCurrentGameEngine().getGameClock().getCurrentTick();

        Plant plant = PlantFactory.create(
            randomPlant,
            new Position(center.x, center.y),
            currentTick ,
            cell
        );

        cell.setPlant(plant);
        world.addPlant(plant);
        graphicsEngine.registerPlant(plant);
    }

    public Set<Cell> getProtectedCells() {
        return selectedCells;
    }
}
