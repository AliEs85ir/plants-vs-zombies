package game.entity.obstacle;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import common.model.Position;
import game.component.contacts.ContactBits;
import game.component.physic.BodyFactory;
import game.entity.obstacle.ability.*;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.GraphicsEngine;

import java.util.ArrayList;
import java.util.List;

public class ObstacleFactory {
    private static ObstacleFactory instance;
    private final GraphicsEngine graphicsEngine;
    private final GameWorld world;

    public static void initialize(GameWorld world, GraphicsEngine engine) {
        instance = new ObstacleFactory(world, engine);
    }

    private ObstacleFactory(GameWorld world, GraphicsEngine graphicsEngine) {
        this.world = world;
        this.graphicsEngine = graphicsEngine;
    }

    public static ObstacleFactory getInstance() {
        if(instance == null) {
            throw new IllegalArgumentException("OBSTACLE FACTORY SHOULD BE INITIALIZED FIRST");
        }
        return instance;
    }

    public Obstacle createRuntimeObstacle(ObstacleType type, float width, float height, float x, float y, List<ObstacleAbility> abilities) {
        BodyFactory bodyFactory = BodyFactory.getInstance();
        Body body = bodyFactory.createObstacleBody(x, y, width, height);
        Obstacle obstacle = new Obstacle(new Position(x,y), type);
        obstacle.setBody(body);
        obstacle.setAbilities(abilities != null ? new ArrayList<>(abilities) : new ArrayList<>());
        setFilterData(obstacle);
        world.getObstacles().add(obstacle);
        body.setUserData(obstacle);
        graphicsEngine.registerObstacle(obstacle);
        return obstacle;
    }
    private void setFilterData(Obstacle obstacle) {
        ObstacleType type = obstacle.getType();
        Filter filter = new Filter();
        filter.categoryBits = ContactBits.OBSTACLE;

        switch (type) {
            case EGYPT_GRAVE, ICE_BLOCK, PLANTFOOD_GRAVE,
                 SUN_GRAVE, TUTORIAL_GRAVE, BOSS_ICE -> {
                filter.maskBits = ContactBits.PROJECTILE;
            }
            case SNOWBALL, PEASHOOTER, SNOW_PEA -> {
                obstacle.getBody().setType(BodyDef.BodyType.DynamicBody);
                filter.maskBits = ContactBits.PLANT;
               // obstacle.setHasAnimationOnDeath(true);
            }
            case MISSILE, FIREBALL, SLINGSHOT -> {
                obstacle.getBody().setType(BodyDef.BodyType.DynamicBody);
                obstacle.setHasAnimationOnDeath(true);
            }
            case PERMANENT_SCORCHED, CRATER,
                 PROTECT_TILE, FLOWER_LINE, GOLD_TILE,
                 SLIDING_TILE_DOWN, SLIDING_TILE_UP,
                 NECROMANCY_TILE, PUDDLE, GARGANTUAR_PUDDLE-> {
                obstacle.setBackGround(true);
            }
            case PLANT_VASE, RANDOM_VASE, GARGANTUAR_VASE -> {
                obstacle.setHasAnimationOnDeath(true);
            }
            case WALLNUT, TALLNUT , EXPLODE_O_NUT -> {
                obstacle.getBody().setType(BodyDef.BodyType.DynamicBody);
                filter.maskBits = ContactBits.ZOMBIE;
            }
            case BRAIN -> {
                filter.maskBits = ContactBits.ZOMBIE;
                obstacle.setHasAnimationOnDeath(true);
            }
            case OCTOPUS -> {
                obstacle.getBody().setType(BodyDef.BodyType.DynamicBody);
                obstacle.setHasAnimationOnDeath(true);
                filter.maskBits = ContactBits.PROJECTILE;
            }case HOOK -> {
                obstacle.getBody().setType(BodyDef.BodyType.DynamicBody);
                filter.maskBits = 0;
            }
            case TEMPORARY_SCORCH -> {
                obstacle.setBackGround(true);
                obstacle.setHasAnimationOnDeath(true);
            }
            case MAMMOTH_GLACIER -> {
                filter.maskBits = ContactBits.PROJECTILE;
                obstacle.setHasAnimationOnDeath(true);
            }
            case CABINET, PUSHABLE_ICE -> {
                filter.maskBits = ContactBits.PROJECTILE | ContactBits.PLANT;
                obstacle.setHasAnimationOnDeath(true);
                Fixture fixture = obstacle.getBody().getFixtureList().first();
                fixture.setFriction(0.8f);
                obstacle.getBody().setLinearDamping(0.6f);
                obstacle.getBody().setType(BodyDef.BodyType.DynamicBody);
            }
            case SQUASH -> {
                obstacle.getBody().setType(BodyDef.BodyType.DynamicBody);
                filter.maskBits = ContactBits.PLANT;
            }
        }

        if(type == ObstacleType.EGYPT_GRAVE ||
            type == ObstacleType.SLIDING_TILE_DOWN ||
            type == ObstacleType.SLIDING_TILE_UP ||
            type == ObstacleType.ICE_BLOCK ||
            type == ObstacleType.SUN_GRAVE ||
            type == ObstacleType.TUTORIAL_GRAVE ||
            type == ObstacleType.PLANTFOOD_GRAVE ||
            type == ObstacleType.PUDDLE ||
            type == ObstacleType.GARGANTUAR_PUDDLE ||
            type == ObstacleType.CRATER ||
            type == ObstacleType.PERMANENT_SCORCHED ||
            type == ObstacleType.TEMPORARY_SCORCH ||
            type == ObstacleType.BOSS_ICE
        )   {
            Cell cell = world.getGrid().getCellAt(obstacle.getPosition());
            cell.setObstacle(obstacle);
           obstacle.addAbility(new CellOccupancyAbility(cell));
        }
        obstacle.getBody().getFixtureList().first().setFilterData(filter);
    }

    public Obstacle restoreCellObstacle(ObstacleType type, Cell cell, double health) {
        if (type == null || cell == null || !isPersistentMapObstacle(type)) {
            return null;
        }
        if (cell.hasObstacle()) {
            return cell.getObstacle();
        }
        List<ObstacleAbility> abilities = restoreAbilities(type, cell, health);
        float[] size = restoreSize(type, cell);
        return createRuntimeObstacle(
            type,
            size[0],
            size[1],
            cell.getWorldX(),
            cell.getWorldY(),
            abilities
        );
    }

    public static boolean isPersistentMapObstacle(ObstacleType type) {
        if (type == null) {
            return false;
        }
        return switch (type) {
            case EGYPT_GRAVE, TUTORIAL_GRAVE, SUN_GRAVE, PLANTFOOD_GRAVE,
                 PUDDLE, GARGANTUAR_PUDDLE, NECROMANCY_TILE,
                 SLIDING_TILE_UP, SLIDING_TILE_DOWN,
                 PROTECT_TILE, FLOWER_LINE, GOLD_TILE,
                 PERMANENT_SCORCHED, ICE_BLOCK,
                 PLANT_VASE, RANDOM_VASE, GARGANTUAR_VASE, VASE -> true;
            default -> false;
        };
    }

    private List<ObstacleAbility> restoreAbilities(ObstacleType type, Cell cell, double health) {
        List<ObstacleAbility> abilities = new ArrayList<>();
        double hp = health > 0 ? health : 400;
        switch (type) {
            case EGYPT_GRAVE, TUTORIAL_GRAVE ->
                abilities.add(new TakeDamageAbility(hp));
            case SUN_GRAVE -> {
                abilities.add(new TakeDamageAbility(hp));
                abilities.add(new SpawnSunAbility());
            }
            case PLANTFOOD_GRAVE -> {
                abilities.add(new TakeDamageAbility(hp));
                abilities.add(new SpawnPlantFoodAbility());
            }
            case ICE_BLOCK -> {
                Zombie frozen = zombieOnCell(cell);
                if (frozen != null) {
                    abilities.add(new IceBlockAbility(hp, frozen));
                } else {
                    abilities.add(new TakeDamageAbility(hp));
                }
            }
            case PUDDLE, GARGANTUAR_PUDDLE -> abilities.add(new PuddleAbility(cell));
            case NECROMANCY_TILE -> abilities.add(new NecromancyAbility(cell));
            case SLIDING_TILE_UP, SLIDING_TILE_DOWN -> abilities.add(new SlideAbility(cell));
            case GOLD_TILE -> abilities.add(new GoldTileAbility(cell));
            case PLANT_VASE, RANDOM_VASE, GARGANTUAR_VASE, VASE -> abilities.add(new VaseAbility(cell));
            default -> {
            }
        }
        return abilities;
    }

    private float[] restoreSize(ObstacleType type, Cell cell) {
        return switch (type) {
            case EGYPT_GRAVE, TUTORIAL_GRAVE, SUN_GRAVE, PLANTFOOD_GRAVE -> new float[]{20f, 40f};
            case ICE_BLOCK -> new float[]{60f, 80f};
            case SLIDING_TILE_UP, SLIDING_TILE_DOWN -> new float[]{40f, 40f};
            case FLOWER_LINE -> new float[]{20f, cell.getHeight()};
            default -> new float[]{cell.getWidth() / 2f, cell.getHeight() / 2f};
        };
    }

    private Zombie zombieOnCell(Cell cell) {
        if (cell == null) {
            return null;
        }
        for (Zombie zombie : world.getZombies()) {
            if (zombie == null || zombie.isDead() || zombie.getPosition() == null) {
                continue;
            }
            Cell zombieCell = world.getGrid().getCellAt(zombie.getPosition());
            if (zombieCell == cell) {
                return zombie;
            }
        }
        return null;
    }
}
