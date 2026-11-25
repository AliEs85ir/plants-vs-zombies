package game.world.chapter.rule;

import app.enums.SeasonType;
import app.enums.obstacle.ObstacleType;
import app.model.App;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.*;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.chapter.ChapterType;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.GraphicsEngine;
import graphics.general.modifier.visualModifiers.RedModifier;
import graphics.zombie.ZombieRenderInstance;

import java.util.*;

public class NormalWorldRule implements ChapterRule{
    private final Random RANDOM = new Random();
    private int obstaclesCount;

    public NormalWorldRule(int obstaclesCount) {
        this.obstaclesCount = obstaclesCount;
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        if (world.getCurrentChapter() == null) {
            return;
        }
        ChapterType type = world.getCurrentChapter().getChapterType();
        if (type == ChapterType.BOSS || type == ChapterType.MINIGAME) {
            return;
        }
        SeasonType seasonType = world.getSeason().getSeasonType();
        List<Cell> cells = world.getGrid().getLastColumns(6);

        switch (seasonType) {
            case ANCIENT_EGYPT -> handleEgyptObstacles(world, graphicsEngine, cells);
            case FROSTBITE_CAVES -> handleIceAgeObstacles(world, graphicsEngine, cells);
            case DARK_AGES -> handleDarkAgeObstacles(world, graphicsEngine, cells);
            case BIG_WAVE_BEACH -> handleBeachObstacles(world, graphicsEngine, cells);
        }

    }

    private void handleEgyptObstacles
        (GameWorld world,
         GraphicsEngine graphicsEngine,
         List<Cell> cells)
    {
        List<Cell> shuffledCells = new ArrayList<>(cells);
        Collections.shuffle(shuffledCells);
        int count = Math.min(obstaclesCount, shuffledCells.size());
        List<Cell> randomCells = shuffledCells.subList(0, count);

        for (Cell cell : randomCells) {
            if(cell.hasPlant() || cell.hasObstacle()) {
                continue;
            }
            Obstacle obstacle = ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.EGYPT_GRAVE,
                20,
                40,
                cell.getWorldX(),
                cell.getWorldY(),
                List.of(new TakeDamageAbility(700))
            );
            cell.setObstacle(obstacle);
        }
    }
    private void handleBeachObstacles
        (GameWorld world,
         GraphicsEngine graphicsEngine,
         List<Cell> cells)
    {
        List<Cell> shuffledCells = new ArrayList<>(cells);
        Collections.shuffle(shuffledCells);
        int count1 = Math.min(obstaclesCount / 2 + 2, shuffledCells.size());
        List<Cell> randomCells = shuffledCells.subList(0, count1);

        for (Cell cell : randomCells) {
            if(cell.hasPlant() || cell.hasObstacle()) {
                continue;
            }
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.PUDDLE,
                cell.getWidth() / 2,
                cell.getHeight() / 2,
                cell.getWorldX(),
                cell.getWorldY(),
                List.of(new PuddleAbility(cell))
            );
        }
        List<Cell> shuffledCells2 = new ArrayList<>(world.getGrid().getLastColumns(2));
        Collections.shuffle(shuffledCells2);
        int count2 = obstaclesCount - count1;
        int placed = 0;
        for(Cell cell : shuffledCells2) {
            if(placed >= count2) {
                break;
            }
            if(cell.hasPlant() || cell.hasObstacle()) {
                continue;
            }
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.GARGANTUAR_PUDDLE,
                cell.getWidth() / 2,
                cell.getHeight() / 2,
                cell.getWorldX(),
                cell.getWorldY(),
                List.of(new PuddleAbility(cell))
            );
            placed++;
        }
    }
    private void handleDarkAgeObstacles
        (GameWorld world,
         GraphicsEngine graphicsEngine,
         List<Cell> cells)
    {
        List<Cell> shuffledCells = new ArrayList<>(cells);
        Collections.shuffle(shuffledCells);
        int count = Math.min(obstaclesCount, shuffledCells.size());
        List<Cell> randomCells = shuffledCells.subList(0, count);
        for (Cell cell : randomCells) {
            if(cell.hasPlant() || cell.hasObstacle()) {
                continue;
            }
            ObstacleType type = chooseRandomType();
            List<ObstacleAbility> abilities = new ArrayList<>();
            abilities.add(new TakeDamageAbility(400));
            if (type == ObstacleType.SUN_GRAVE) {
                abilities.add(new SpawnSunAbility());
            } else if (type == ObstacleType.PLANTFOOD_GRAVE) {
                abilities.add(new SpawnPlantFoodAbility());
            }
            Obstacle obstacle = ObstacleFactory.getInstance().createRuntimeObstacle(
                type,
                20,
                60,
                cell.getWorldX(),
                cell.getWorldY(),
                abilities
            );
            cell.setObstacle(obstacle);
        }

        List<Cell> necroCells = new ArrayList<>(cells);
        Collections.shuffle(necroCells);
        List<Cell> randomNecroCells = necroCells.subList(0, count);

        for (Cell cell : randomNecroCells) {
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.NECROMANCY_TILE,
                cell.getWidth() / 2,
                cell.getHeight() / 2,
                cell.getWorldX(),
                cell.getWorldY(),
                List.of(new NecromancyAbility(cell))
            );
        }

    }

    private ObstacleType chooseRandomType() {
        int chance = RANDOM.nextInt(100);
        if(chance < 33) {
            return ObstacleType.TUTORIAL_GRAVE;
        } else if (chance < 66) {
            return ObstacleType.SUN_GRAVE;
        }else {
            return ObstacleType.PLANTFOOD_GRAVE;
        }
    }


    private void handleIceAgeObstacles
        (GameWorld world,
         GraphicsEngine graphicsEngine,
         List<Cell> cells)
    {
        ZombieService service = new ZombieService(world);
        Grid grid = world.getGrid();

        List<Cell> shuffledCells = new ArrayList<>(cells);
        Collections.shuffle(shuffledCells);

        int count = Math.min(obstaclesCount, shuffledCells.size());
        int placed = 0;

        for (Cell cell : shuffledCells) {
            if (placed >= count) {
                break;
            }

            int row = cell.getRow();
            int col = cell.getCol();

            boolean hasCellAbove = grid.getCell(row + 1, col) != null;
            boolean hasCellBelow = grid.getCell(row - 1, col) != null;

            if (!hasCellAbove && !hasCellBelow) {
                continue;
            }

            ObstacleType obstacleType;

            if (hasCellAbove && hasCellBelow) {
                obstacleType = Math.random() < 0.5
                    ? ObstacleType.SLIDING_TILE_UP
                    : ObstacleType.SLIDING_TILE_DOWN;
            } else if (hasCellAbove) {
                obstacleType = ObstacleType.SLIDING_TILE_UP;
            } else {
                obstacleType = ObstacleType.SLIDING_TILE_DOWN;
            }
            ObstacleFactory.getInstance().createRuntimeObstacle(
                obstacleType,
                40,
                40,
                cell.getWorldX(),
                cell.getWorldY(),
                List.of(new SlideAbility(cell))
            );
            placed++;
        }

        int extraCount = count / 2;
        List<Cell> availableCells = new ArrayList<>();
        for (Cell candidate : cells) {
            if (!candidate.hasObstacle()) {
                availableCells.add(candidate);
            }
        }
        Collections.shuffle(availableCells);

        int extraPlaced = 0;
        for (Cell extraCell : availableCells) {
            if (extraPlaced >= extraCount) {
                break;
            }
            Zombie zombie = service.spawnZombie(
                "ZombieIceAgeHunter",
                extraCell.getWorldPosition()
            );
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.ICE_BLOCK,
                60,
                80,
                extraCell.getWorldX(),
                extraCell.getWorldY(),
                List.of(new IceBlockAbility(600, zombie))
            );
            extraPlaced++;
        }

    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        for(Zombie zombie : world.getZombies()) {
            ZombieRenderInstance instance = App.getRealTimeEngine().
                getGraphicsEngine().getZombieInstance(zombie);
            if (instance != null) {
                instance.getModifierController().add(new RedModifier(zombie));
            }
        }
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {

    }
}
