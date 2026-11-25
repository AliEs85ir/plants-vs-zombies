package game.world.chapter.rule.speciallevelRules;

import app.enums.obstacle.ObstacleType;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.engine.system.sun.SunSimulationSystem;
import game.entity.obstacle.ObstacleFactory;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.GraphicsEngine;

import java.util.List;

public class BowlingRule implements ChapterRule {
    private final int redLineColumn;

    public BowlingRule(int redLineColumn) {
        this.redLineColumn = redLineColumn;
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        drawFlowerLine(world, graphicsEngine, physicService);
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {

    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
        engine.removeSystem(SunSimulationSystem.class);
    }

    private void drawFlowerLine(GameWorld world, GraphicsEngine engine, PhysicService service) {
        Grid grid = world.getGrid();
        for (int i = 0; i < grid.getRowCount(); i++) {
            Cell cell = grid.getCell(i, redLineColumn);
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

    public int getRedLineColumn() {
        return redLineColumn;
    }
}
