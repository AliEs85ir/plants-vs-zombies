package game.world.chapter.rule.speciallevelRules;

import app.enums.obstacle.ObstacleType;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.GoldTileAbility;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.GraphicsEngine;

import java.util.List;

public class DeadLineRules implements ChapterRule {

    private final int deadLineColumn;

    public DeadLineRules(int deadLineColumn) {
        this.deadLineColumn = deadLineColumn;
    }
    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        drawGoldTiles(world, graphicsEngine, physicService);
        drawFlowerLine(world, graphicsEngine, physicService);
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
    }
    @Override
    public void onEngineStart(GameSimulationEngine engine) {

    }

    public int getDeadLineColumn() {
        return deadLineColumn;
    }

    private void drawFlowerLine(GameWorld world, GraphicsEngine engine, PhysicService service) {
        Grid grid = world.getGrid();
        for (int i = 0; i < grid.getRowCount(); i++) {
            Cell cell = grid.getCell(i, deadLineColumn);
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.FLOWER_LINE,
                20,
                world.getGrid().getConfig().getCellHeight(),
                cell.getX() + grid.getConfig().getCellWidth(),
                cell.getY() + grid.getConfig().getCellHeight() / 2,
                List.of()
                );
        }
    }

    private void drawGoldTiles(GameWorld world, GraphicsEngine engine, PhysicService service) {
        Grid grid = world.getGrid();

        for (int row = 0; row < grid.getRowCount(); row++) {
            for (int col = 0; col < deadLineColumn + 1; col++) {
                if ((row + col) % 2 == 0) {
                    Cell cell = grid.getCell(row, col);
                    ObstacleFactory.getInstance().createRuntimeObstacle(
                        ObstacleType.GOLD_TILE,
                        grid.getConfig().getCellWidth() / 2,
                        grid.getConfig().getCellHeight() / 2,
                        cell.getWorldX() + 5,
                        cell.getWorldY() + 35,
                        List.of(new GoldTileAbility(cell))
                    );
                }
            }
        }
    }

}
