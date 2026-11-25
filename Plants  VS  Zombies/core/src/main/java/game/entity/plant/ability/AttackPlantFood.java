package game.entity.plant.ability;

import app.enums.ProjectilleEffect.ProjectileEffect;
import app.enums.plant.PlantName;
import app.model.App;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.projectile.LobbedProjectile;
import game.entity.projectile.ShooterProjectile;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Grid;

import java.util.ArrayList;
import java.util.List;

public class AttackPlantFood extends AbstractPlantAbility {

    private long attackCooldown = 10;
    private long lastAttackTick = 0;
    private AttackService attackService;
    private boolean flag = false;

    public AttackPlantFood() {
        super();
    }

    public AttackPlantFood(long intervalTicks) {
        super();
        this.attackCooldown = intervalTicks;
        this.lastAttackTick = 0;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive()) return;
        if (!plant.getPlantFoodState().isActive()) return;

        GameWorld gameWorld = App.getCurrentGameEngine().getGameWorld();
        if (this.attackService == null) {
            this.attackService = new AttackService(gameWorld);
        }

        Grid grid = gameWorld.getGrid();
        if (!hasTargetInLanes(plant, grid, gameWorld)) return;

        long actualCooldown = (long) getAttackCooldown(plant.getDefinition().getName());
        if (currentTick - lastAttackTick < actualCooldown && lastAttackTick != 0) return;

        boolean attacked = performAttack(plant, currentTick, plantService);
        if (attacked) {
            lastAttackTick = currentTick;
        }
    }

    private boolean performAttack(Plant plant, long currentTick, PlantService plantService) {
        PlantName name = plant.getDefinition().getName();
        Position position = new Position(plant.getPosition().getX(), plant.getPosition().getY());
        int damage = plant.getAttackDamage();

        GameWorld gameWorld = App.getCurrentGameEngine().getGameWorld();
        Grid grid = gameWorld.getGrid();

        int plantRow = (int) (plant.getPosition().getY() / grid.getConfig().getCellHeight());

        switch (name) {
//            // ====== SHOOTERS ======
//            case PEASHOOTER -> {
//                plantService.addProjectile(new ShooterProjectile(plant, damage, position, getSpeed(name), 0));
//            }
//            case REPEATER -> {
//                plantService.addProjectile(new ShooterProjectile(plant, damage, position, getSpeed(name), 0));
//                if (!flag) {
//                    plantService.addProjectile(new ShooterProjectile(plant, damage * 20, position, getSpeed(name), 0));
//                    flag = true;
//                }
//            }
//            case THREEPEATER -> {
//                for (int i = 0; i < grid.getRowCount(); i++) {
//                    Position rowPos = new Position(position.getX(), i * grid.getConfig().getCellHeight());
//                    plantService.addProjectile(new ShooterProjectile(plant, damage, rowPos, getSpeed(name), 0));
//                }
//            }
//            case SNOW_PEA -> {
//                ShooterProjectile snowPeaProjectile = new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 0);
//                snowPeaProjectile.addEffect(ProjectileEffect.ICE);
//                plantService.addProjectile(snowPeaProjectile);
//                attackService.damageZombiesInRowWithIce(plantRow, plant.getAttackDamage(), currentTick, 0.0, (int) plant.getPlantFoodState().getDurationTicks());
//            }
//            case FIRE_PEASHOOTER -> {
//                ShooterProjectile firePeaShooterProjectile = new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 0);
//                firePeaShooterProjectile.addEffect(ProjectileEffect.FIRE);
//                plantService.addProjectile(firePeaShooterProjectile);
//            }
//            case GOO_PEASHOOTER -> {
//                ShooterProjectile gooProjectile = new ShooterProjectile(plant, damage, position, getSpeed(name), 0);
//                gooProjectile.enableIgnoresArmor();
//                plantService.addProjectile(gooProjectile);
//            }
//            case MEGA_GATLING_PEA -> {
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 0));
//                if (!flag) {
//                    plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 0, 3));
//                    plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 0, 6));
//                    plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 0, 9));
//                    plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 0, 12));
//                    flag = true;
//                }
//            }
//            case ROTOBAGA -> {
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 40));
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), -40));
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 180 + 40));
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 180 - 40));
//            }
//            case SPLIT_PEA -> {
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 0));
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 180));
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 180));
//            }
//            case CITRON -> {
//                if (!flag) {
//                    plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage() * 50, position, getSpeed(name), 0));
//                    flag = true;
//                }
//            }
//            case STARFRUIT -> {
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 180));
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 180 + 72));
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 180 + 144));
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 180 + 216));
//                plantService.addProjectile(new ShooterProjectile(plant, plant.getAttackDamage(), position, getSpeed(name), 180 + 288));
//            }
//            case SEA_SHROOM -> {
//                ShooterProjectile seaShroomProjectile = new ShooterProjectile(plant, damage, position, getSpeed(name), 0);
//                seaShroomProjectile.setMaxLifetimeTicks(600);
//                seaShroomProjectile.setMaxRange(grid.getPlaygroundWidth() / 2.5);
//                plantService.addProjectile(seaShroomProjectile);
//            }
//            case PUFF_SHROOM -> {
//                ShooterProjectile puffShroomProjectile = new ShooterProjectile(plant, damage, position, getSpeed(name), 0);
//                puffShroomProjectile.setMaxLifetimeTicks(600);
//                puffShroomProjectile.setMaxRange(grid.getPlaygroundWidth() / 2.5);
//                plantService.addProjectile(puffShroomProjectile);
//            }
//
//            // ====== LOBBERS ======
//            case CABBAGE_PULT -> {
//                Zombie targetZombie = findFrontmostActiveZombie(gameWorld, grid, plantRow);
//                if (targetZombie != null) {
//                    plantService.addProjectile(new LobbedProjectile(plant, damage, position, getSpeed(name), targetZombie));
//                }
//            }
//            case KERNEL_PULT -> {
//                Zombie targetZombie = findFrontmostActiveZombie(gameWorld, grid, plantRow);
//                if (targetZombie != null) {
//                    if (Math.random() < 0.5) {
//                        LobbedProjectile butter = new LobbedProjectile(plant, damage, position, getSpeed(name), targetZombie);
//                        butter.addEffect(ProjectileEffect.SLOWER);
//                        plantService.addProjectile(butter);
//                    } else {
//                        plantService.addProjectile(new LobbedProjectile(plant, damage / 2, position, getSpeed(name), targetZombie));
//                    }
//                }
//            }
//            case MELON_PULT -> {
//                Zombie targetZombie = findFrontmostActiveZombie(gameWorld, grid, plantRow);
//                if (targetZombie != null) {
//                    LobbedProjectile melon = new LobbedProjectile(plant, damage, position, getSpeed(name), targetZombie);
//                    melon.setSplash(true);
//                    plantService.addProjectile(melon);
//                }
//            }
//            case WINTER_MELON -> {
//                Zombie targetZombie = findFrontmostActiveZombie(gameWorld, grid, plantRow);
//                if (targetZombie != null) {
//                    LobbedProjectile winterMelon = new LobbedProjectile(plant, damage, position, getSpeed(name), targetZombie);
//                    winterMelon.setSplash(true);
//                    winterMelon.addEffect(ProjectileEffect.ICE);
//                    plantService.addProjectile(winterMelon);
//                }
//            }
//            case PEPPER_PULT -> {
//                Zombie targetZombie = findFrontmostActiveZombie(gameWorld, grid, plantRow);
//                if (targetZombie != null) {
//                    LobbedProjectile pepper = new LobbedProjectile(plant, damage, position, getSpeed(name), targetZombie);
//                    pepper.setSplash(true);
//                    pepper.addEffect(ProjectileEffect.FIRE);
//                    plantService.addProjectile(pepper);
//                }
//            }
//
//            // ====== STRIKE-THROUGH ======
//            case CACTUS -> {
//                ShooterProjectile cactusProjectile = new ShooterProjectile(plant, damage * 5, position, getSpeed(name), 0);
//                cactusProjectile.enableInfinitePierce();
//                plantService.addProjectile(cactusProjectile);
//            }
//            case FUME_SHROOM -> {
//                ShooterProjectile fumeShroomProjectile = new ShooterProjectile(plant, damage, position, getSpeed(name), 0);
//                fumeShroomProjectile.enableInfinitePierce();
//                fumeShroomProjectile.setMaxRange(grid.getPlaygroundWidth() / 1.5);
//                plantService.addProjectile(fumeShroomProjectile);
//            }

            default -> {
                return false;
            }
        }
        //return true;
    }

    private double getSpeed(PlantName plantName) {
        return 1.6 * switch (plantName) {
            case PEASHOOTER -> 0.4;
            case REPEATER -> 0.35;
            case THREEPEATER -> 0.4;
            case SNOW_PEA -> 0.5;
            case ROTOBAGA -> 0.3;
            case SPLIT_PEA -> 0.4;
            case CITRON -> 0.25;
            case FIRE_PEASHOOTER -> 0.4;
            case CACTUS -> 0.6;
            case STARFRUIT -> 0.37;
            case GOO_PEASHOOTER -> 0.35;
            case MEGA_GATLING_PEA -> 1.0;
            case SEA_SHROOM -> 0.3;
            case PUFF_SHROOM -> 0.32;
            case FUME_SHROOM -> 0.4;
            case CABBAGE_PULT -> 0.3;
            case KERNEL_PULT -> 0.3;
            case MELON_PULT -> 0.25;
            case WINTER_MELON -> 0.25;
            case PEPPER_PULT -> 0.25;
            default -> 0.5;
        };
    }

    private boolean hasTargetInLanes(Plant plant, Grid grid, GameWorld gameWorld) {
        int plantRow = (int) (plant.getPosition().getY() / grid.getConfig().getCellHeight());
        PlantName name = plant.getDefinition().getName();

        if (name == PlantName.THREEPEATER) {
            return hasActiveZombieInRow(gameWorld, grid, plantRow) ||
                    hasActiveZombieInRow(gameWorld, grid, plantRow - 1) ||
                    hasActiveZombieInRow(gameWorld, grid, plantRow + 1);
        }

        if (name == PlantName.ROTOBAGA || name == PlantName.STARFRUIT) {
            return true;
        }

        return hasActiveZombieInRow(gameWorld, grid, plantRow);
    }

    private boolean hasActiveZombieInRow(GameWorld gameWorld, Grid grid, int row) {
        List<Zombie> zombies = getSafeZombiesInRow(gameWorld, grid, row);
        for (Zombie zombie : zombies) {
            if (zombie != null && !zombie.isDead()) {
                return true;
            }
        }
        return false;
    }

    private List<Zombie> getSafeZombiesInRow(GameWorld gameWorld, Grid grid, int row) {
        Position temporaryPosition = new Position(0, row * grid.getConfig().getCellHeight());
        if (grid.isInsideGrid(temporaryPosition)) {
            List<Zombie> zombies = gameWorld.getZombiesInRow(row);
            return zombies != null ? zombies : new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private Zombie findFrontmostActiveZombie(GameWorld gameWorld, Grid grid, int row) {
        Zombie frontmostZombie = null;
        for (Zombie zombie : getSafeZombiesInRow(gameWorld, grid, row)) {
            if (zombie != null && !zombie.isDead()) {
                if (frontmostZombie == null || zombie.getPosition().getX() < frontmostZombie.getPosition().getX()) {
                    frontmostZombie = zombie;
                }
            }
        }
        return frontmostZombie;
    }

    private double getAttackCooldown(PlantName name) {
        return switch (name) {
            case PEASHOOTER -> attackCooldown / 3.0;
            case REPEATER -> attackCooldown / 5.0;
            case THREEPEATER -> attackCooldown / 3.0;
            case ROTOBAGA -> attackCooldown / 3.0;
            case SNOW_PEA -> attackCooldown / 4.0;
            case SPLIT_PEA -> attackCooldown / 3.0;
            case CITRON -> attackCooldown;
            case FIRE_PEASHOOTER -> attackCooldown / 4.0;
            case CACTUS -> attackCooldown / 2.0;
            case STARFRUIT -> attackCooldown / 3.0;
            case GOO_PEASHOOTER -> attackCooldown / 2.5;
            case MEGA_GATLING_PEA -> attackCooldown / 3.0;
            case SEA_SHROOM, PUFF_SHROOM -> attackCooldown / 5.0;
            case FUME_SHROOM, CABBAGE_PULT, KERNEL_PULT, MELON_PULT, WINTER_MELON, PEPPER_PULT -> attackCooldown;
            default -> 5.0;
        };
    }
}
