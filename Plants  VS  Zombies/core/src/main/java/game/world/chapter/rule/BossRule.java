package game.world.chapter.rule;

import app.enums.SeasonType;
import app.enums.obstacle.ObstacleType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.engine.system.sun.SunSimulationSystem;
import game.engine.system.wave.WaveSimulationSystem;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.GlacierAbility;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.zombie.Zombie;
import game.entity.zombie.data.properties.BossProperty;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.chapter.ChapterType;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.GraphicsEngine;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.modifier.visualModifiers.RedModifier;
import graphics.general.modifier.visualModifiers.WhiteModifier;
import graphics.zombie.ZombieRenderInstance;

import java.util.ArrayList;
import java.util.List;

public class BossRule implements ChapterRule{
    private Zombie boss;

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        if(world.getCurrentChapter().getChapterType() != ChapterType.BOSS) {
            return;
        }
        ZombieService service = new ZombieService(world);
        Vector2 bossSpawn = world.getMap().getZombieSpawns().get(0);
        SeasonType seasonType = world.getSeason().getSeasonType();

        spawnBoss(bossSpawn, seasonType, service);
        spawnObstacles(world, graphicsEngine, physicService);
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
        engine.removeSystem(WaveSimulationSystem.class);
        engine.removeSystem(SunSimulationSystem.class);
    }

    private void spawnBoss(Vector2 pos, SeasonType seasonType, ZombieService service) {
        switch (seasonType) {
            case ANCIENT_EGYPT -> this.boss = service.spawnZombie("ZombieBossEgypt", new Position(pos.x, pos.y));
            case DARK_AGES -> this.boss = service.spawnZombie("ZombieBossDragon", new Position(pos.x, pos.y));
            case BIG_WAVE_BEACH -> this.boss = service.spawnZombie("ZombieBossBeach", new Position(pos.x, pos.y));
            case FROSTBITE_CAVES -> this.boss = service.spawnZombie("ZombieBossMammoth", new Position(pos.x, pos.y));
        }
    }

    private void spawnObstacles(GameWorld world,
                                GraphicsEngine graphicsEngine,
                                PhysicService physicService) {
        switch (world.getSeason().getSeasonType()) {
            case DARK_AGES -> {
                handleDarkAgesObstacles(world, graphicsEngine, physicService);
            }
            case FROSTBITE_CAVES -> {
                handleIceAgeObstacles(world, graphicsEngine, physicService);
            }
            case BIG_WAVE_BEACH -> {
                handleBeachObstacles(world, graphicsEngine, physicService);
            }
        }
    }

    private void handleDarkAgesObstacles(GameWorld world,
                                         GraphicsEngine graphicsEngine,
                                         PhysicService physicService) {
        var grid = world.getGrid();

        int rowCount = grid.getRowCount();
        int colCount = grid.getColCount();

        List<Cell> obstacleCells = new ArrayList<>();

        int secondLastColumn = colCount - 3;
        int thirdLastColumn = colCount - 4;

        for (int row = 0; row < rowCount; row++) {
            Cell secondLastCell = grid.getCell(row, secondLastColumn);
            Cell thirdLastCell = grid.getCell(row, thirdLastColumn);

            if (secondLastCell != null) {
                obstacleCells.add(secondLastCell);
            }

            if (thirdLastCell != null) {
                obstacleCells.add(thirdLastCell);
            }
        }

        for(Cell cell : obstacleCells) {
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.TUTORIAL_GRAVE,
                30,
                50,
                cell.getWorldX(),
                cell.getWorldY(),
                List.of(new TakeDamageAbility(400))
            );
        }
    }
    private void handleIceAgeObstacles(GameWorld world,
                                         GraphicsEngine graphicsEngine,
                                         PhysicService physicService) {
        Vector2 position = world.getMap().getZombieSpawns().get(0);
        ObstacleFactory.getInstance().createRuntimeObstacle(
            ObstacleType.MAMMOTH_GLACIER,
            100,
            3 * (world.getGrid().getConfig().getCellHeight() / 2),
            position.x,
            position.y,
            List.of(new GlacierAbility(300))
        );
    }

    private void handleBeachObstacles(GameWorld world,
                                      GraphicsEngine graphicsEngine,
                                      PhysicService physicService) {
        Grid grid = world.getGrid();
        for(Cell cell : grid.getColumnCells(grid.getColCount() - 2)) {
            EffectRequest<Object> request = new EffectRequest<>(
                "shark idle",
                null,
                new WorldAnchor(cell.getWorldPosition()),
                EffectLayer.BEHIND
            );
            graphicsEngine.getEffectManager().spawn(request);
        }
    }

    public Zombie getBoss() {
        return boss;
    }


}
