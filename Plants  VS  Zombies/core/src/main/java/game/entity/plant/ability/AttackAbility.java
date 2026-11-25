package game.entity.plant.ability;

import app.enums.ProjectilleEffect.ProjectileEffect;
import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.model.App;
import common.model.Position;
import game.entity.plant.combat.PlantFoodProjectileLaunch;
import game.entity.plant.combat.StarfruitCombat;
import game.entity.plant.combat.PlantProjectileKinematics;
import game.entity.plant.Plant;
import game.entity.plant.plantfood.PlantFoodShot;
import game.entity.projectile.LobbedProjectile;
import game.entity.projectile.ShooterProjectile;
import game.entity.zombie.Zombie;
import game.service.plant.PlantService;
import game.service.plant.PlantUpgradeService;
import game.service.projectile.ProjectileFactory;
import game.world.GameWorld;
import game.world.playground.Grid;
import graphics.GraphicsEngine;
import graphics.general.event.PlantVisualEventType;
import graphics.general.event.VisualEvent;

import java.util.function.Consumer;

public class AttackAbility extends AbstractPlantAbility {
    private static final double BUTTER_CHANCE = 0.25;

    private final long attackCooldown;
    private long lastAttackTick;

    public AttackAbility(long intervalTicks) {
        this.attackCooldown = intervalTicks;
        this.lastAttackTick = 0;
    }

    @Override
    public void resetCycle(long currentTick) {
        super.resetCycle(currentTick);
        lastAttackTick = currentTick;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive()) return;

        if (currentTick - lastAttackTick < attackCooldown) return;

        PlantCategory category = plant.getDefinition().getCategory();
        if (category == PlantCategory.SUN_PRODUCER ||
            category == PlantCategory.WALL_NUT ||
            category == PlantCategory.MODIFIER) return;

        boolean attacked = performAttack(plant, currentTick, plantService);
        if (attacked) lastAttackTick = currentTick;
    }

    private boolean performAttack(Plant plant, long currentTick, PlantService plantService) {
        PlantName name = plant.getDefinition().getName();
        int damage = plant.getAttackDamage();
        double speed = PlantProjectileKinematics.speedOf(name);

        GameWorld gameWorld = App.getCurrentGameEngine().getGameWorld();
        Grid grid = gameWorld.getGrid();

        switch (name) {
            case PEASHOOTER: {
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane())) {
                    return false;
                }
                playAttack(plant);
                Position pos = new Position(plant.getPosition().getX() + 60, plant.getPosition().getY() + 30);
                scheduleShooter(plantService, currentTick, 5, plant, damage, pos, speed, 0);
                return true;
            }

            case REPEATER: {
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane())) {
                    return false;
                }
                playAttack(plant);
                Position pos1 = new Position(plant.getPosition().getX() + 60, plant.getPosition().getY() + 30);
                Position pos2 = new Position(plant.getPosition().getX() + 60, plant.getPosition().getY() + 30);
                scheduleShooter(plantService, currentTick, 5, plant, damage, pos1, speed, 0);
                scheduleShooter(plantService, currentTick, 7, plant, damage, pos2, speed, 0);
                return true;
            }

            case THREEPEATER: {
                boolean mainLane = PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane());
                boolean topLane = PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane(1));
                boolean bottomLane = PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane(-1));
                if (!mainLane && !topLane && !bottomLane) {
                    return false;
                }
                playAttack(plant);
                Position mainPos = new Position(plant.getPosition().getX() + 60, plant.getPosition().getY() + 30);
                Position topPos = new Position(mainPos.getX(), mainPos.getY() + grid.getConfig().getCellHeight() - 40);
                Position bottomPos = new Position(mainPos.getX(), mainPos.getY() - grid.getConfig().getCellHeight() + 10);
                if (mainLane) {
                    scheduleShooter(plantService, currentTick, 5, plant, damage, mainPos, speed, 0);
                }
                if (topLane) {
                    scheduleShooter(plantService, currentTick, 4, plant, damage, topPos, speed, 0);
                }
                if (bottomLane) {
                    scheduleShooter(plantService, currentTick, 5, plant, damage, bottomPos, speed, 0);
                }
                return true;
            }

            case SNOW_PEA: {
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane())) {
                    return false;
                }
                playAttack(plant);
                Position pos = new Position(plant.getPosition().getX() + 60, plant.getPosition().getY() + 30);
                scheduleShooter(plantService, currentTick, 5, plant, damage, pos, speed, 0,
                    projectile -> projectile.addEffect(ProjectileEffect.ICE));
                return true;
            }

            case ROTOBAGA: {
                boolean d1 = PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.cone(40));
                boolean d2 = PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.cone(-40));
                boolean d3 = PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.cone(140));
                boolean d4 = PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.cone(220));
                if (!d1 && !d2 && !d3 && !d4) {
                    return false;
                }
                playAttack(plant);
                Position pos1 = new Position(plant.getPosition().getX() + 50, plant.getPosition().getY() + 70);
                Position pos2 = new Position(plant.getPosition().getX() + 45, plant.getPosition().getY() - 25);
                Position pos3 = new Position(plant.getPosition().getX() - 55, plant.getPosition().getY() + 70);
                Position pos4 = new Position(plant.getPosition().getX() - 45, plant.getPosition().getY() - 20);
                if (d1) {
                    scheduleShooter(plantService, currentTick, 7, plant, damage, pos1, speed, 40);
                    scheduleShooter(plantService, currentTick, 8, plant, damage, pos1, speed, 40);
                    scheduleShooter(plantService, currentTick, 9, plant, damage, pos1, speed, 40);
                }
                if (d2) {
                    scheduleShooter(plantService, currentTick, 7, plant, damage, pos2, speed, -40);
                    scheduleShooter(plantService, currentTick, 8, plant, damage, pos2, speed, -40);
                    scheduleShooter(plantService, currentTick, 9, plant, damage, pos2, speed, -40);
                }
                if (d3) {
                    scheduleShooter(plantService, currentTick, 7, plant, damage, pos3, speed, 140);
                    scheduleShooter(plantService, currentTick, 8, plant, damage, pos3, speed, 140);
                    scheduleShooter(plantService, currentTick, 9, plant, damage, pos3, speed, 140);
                }
                if (d4) {
                    scheduleShooter(plantService, currentTick, 7, plant, damage, pos4, speed, 220);
                    scheduleShooter(plantService, currentTick, 8, plant, damage, pos4, speed, 220);
                    scheduleShooter(plantService, currentTick, 9, plant, damage, pos4, speed, 220);
                }
                return true;
            }

            case SPLIT_PEA: {
                boolean forward = PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane());
                boolean backward = PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.backwardLane());
                if (!forward && !backward) {
                    return false;
                }
                playAttack(plant);
                if (forward) {
                    Position pos1 = new Position(plant.getPosition().getX() + 75, plant.getPosition().getY() + 22);
                    scheduleShooter(plantService, currentTick, 5, plant, damage, pos1, speed, 0);
                }
                if (backward) {
                    Position pos2 = new Position(plant.getPosition().getX() - 64, plant.getPosition().getY() + 28);
                    Position pos3 = new Position(plant.getPosition().getX() - 60, plant.getPosition().getY() + 28);
                    scheduleShooter(plantService, currentTick, 7, plant, damage, pos2, speed, 180);
                    scheduleShooter(plantService, currentTick, 8, plant, damage, pos3, speed, 180);
                }
                return true;
            }

            case CITRON: {
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane())) {
                    return false;
                }
                playAttack(plant);
                Position pos = new Position(plant.getPosition().getX() + 4, plant.getPosition().getY() + 0);
                scheduleShooter(plantService, currentTick, 4, plant, damage, pos, speed, 0);
                return true;
            }

            case FIRE_PEASHOOTER: {
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane())) {
                    return false;
                }
                playAttack(plant);
                Position pos = new Position(plant.getPosition().getX() + 85, plant.getPosition().getY() + 30);
                scheduleShooter(plantService, currentTick, 6, plant, damage, pos, speed, 0,
                    projectile -> projectile.addEffect(ProjectileEffect.FIRE));
                return true;
            }

            case GOO_PEASHOOTER: {
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane())) {
                    return false;
                }
                playAttack(plant);
                Position pos = new Position(plant.getPosition().getX() + 120, plant.getPosition().getY() + 21);
                scheduleShooter(plantService, currentTick, 4, plant, damage, pos, speed, 0,
                    ShooterProjectile::enableIgnoresArmor);
                return true;
            }

            case MEGA_GATLING_PEA: {
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane())) {
                    return false;
                }
                playAttack(plant);
                Position pos1 = new Position(plant.getPosition().getX() + 90, plant.getPosition().getY() + 30);
                Position pos2 = new Position(plant.getPosition().getX() + 90, plant.getPosition().getY() + 30);
                Position pos3 = new Position(plant.getPosition().getX() + 90, plant.getPosition().getY() + 30);
                Position pos4 = new Position(plant.getPosition().getX() + 90, plant.getPosition().getY() + 30);
                scheduleShooter(plantService, currentTick, 5, plant, damage, pos1, speed, 0);
                scheduleShooter(plantService, currentTick, 6, plant, damage, pos2, speed, 0);
                scheduleShooter(plantService, currentTick, 7, plant, damage, pos3, speed, 0);
                scheduleShooter(plantService, currentTick, 8, plant, damage, pos4, speed, 0);
                return true;
            }

            case PEA_POD:
                return false;

            case BOWLING_BULB:
                return false;

            case STARFRUIT: {
                boolean any = false;
                boolean[] visible = new boolean[StarfruitCombat.SHOTS.length];
                for (int i = 0; i < StarfruitCombat.SHOTS.length; i++) {
                    visible[i] = PlantTargeting.hasVisibleZombie(
                        gameWorld,
                        plant,
                        FieldOfView.cone(StarfruitCombat.SHOTS[i].angleDegrees())
                    );
                    any |= visible[i];
                }
                if (!any) {
                    return false;
                }
                playAttack(plant);
                for (int i = 0; i < StarfruitCombat.SHOTS.length; i++) {
                    if (!visible[i]) {
                        continue;
                    }
                    StarfruitCombat.Shot shot = StarfruitCombat.SHOTS[i];
                    Position pos = new Position(
                        plant.getPosition().getX() + shot.offsetX(),
                        plant.getPosition().getY() + shot.offsetY()
                    );
                    scheduleShooter(plantService, currentTick, 7, plant, damage, pos, speed, shot.angleDegrees());
                }
                return true;
            }

            case SEA_SHROOM: {
                double range = grid.getPlaygroundWidth() / 2.5;
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane(0, range))) {
                    return false;
                }
                playAttack(plant);
                Position pos = new Position(plant.getPosition().getX() + 50, plant.getPosition().getY() + 12);
                scheduleShooter(plantService, currentTick, 5, plant, damage, pos, speed, 0, projectile -> {
                    projectile.setMaxLifetimeTicks(600);
                    projectile.setMaxRange(range);
                });
                return true;
            }

            case PUFF_SHROOM: {
                double range = grid.getPlaygroundWidth() / 2.5;
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane(0, range))) {
                    return false;
                }
                playAttack(plant);
                Position pos = new Position(plant.getPosition().getX() + 30, plant.getPosition().getY() - 25);
                scheduleShooter(plantService, currentTick, 4, plant, damage, pos, speed, 0, projectile -> {
                    projectile.setMaxLifetimeTicks(600);
                    projectile.setMaxRange(range);
                });
                return true;
            }

            case CABBAGE_PULT: {
                return fireLobber(plant, plantService, currentTick, gameWorld, damage, speed, false, null);
            }

            case KERNEL_PULT: {
                Zombie target = PlantTargeting.selectTarget(gameWorld, plant, FieldOfView.forwardLane());
                if (target == null) {
                    return false;
                }
                boolean butter = Math.random() < BUTTER_CHANCE + PlantUpgradeService.getModifiers(name).extraButterChance();
                if (butter) {
                    playAttack2(plant);
                } else {
                    playAttack(plant);
                }
        Position pos = lobberSpawn(plant);
        scheduleLobbed(plantService, currentTick, 6, plant, damage, pos, speed, target, projectile -> {
            if (butter) {
                projectile.addEffect(ProjectileEffect.STUN);
            }
        });
                return true;
            }

            case MELON_PULT: {
                return fireLobber(plant, plantService, currentTick, gameWorld, damage, speed, true, null);
            }

            case WINTER_MELON: {
                return fireLobber(plant, plantService, currentTick, gameWorld, damage, speed, true, ProjectileEffect.ICE);
            }

            case PEPPER_PULT: {
                return fireLobber(plant, plantService, currentTick, gameWorld, damage, speed, true, ProjectileEffect.FIRE);
            }

            case CACTUS: {
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane())) {
                    return false;
                }
                playAttack(plant);
                PlantFoodShot empowered = PlantFoodProjectileLaunch.empoweredAttackShot(plant);
                if (empowered != null) {
                    int delay = Math.max(0, empowered.getDelayTicks());
                    App.getCurrentGameEngine().getTaskManager().scheduleRelative(currentTick, delay, () ->
                        PlantFoodProjectileLaunch.spawnEmpoweredShot(plant, plantService, empowered, damage, speed)
                    );
                    return true;
                }
                Position pos = new Position(plant.getPosition().getX() + 59, plant.getPosition().getY() + 59);
                scheduleShooter(plantService, currentTick, 5, plant, damage, pos, speed, 0,
                    projectile -> projectile.setMaxTargets(3));
                return true;
            }

            case CAULIPOWER: {
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane())) {
                    return false;
                }
                playAttack(plant);
                Position pos = new Position(plant.getPosition().getX() - 30, plant.getPosition().getY() + 100);
                scheduleShooter(plantService, currentTick, 6, plant, damage, pos, speed, 0,
                    ShooterProjectile::enableInfinitePierce);
                return true;
            }

            case FUME_SHROOM: {
                double range = grid.getPlaygroundWidth() / 1.5;
                if (!PlantTargeting.hasVisibleZombie(gameWorld, plant, FieldOfView.forwardLane(0, range))) {
                    return false;
                }
                playAttack(plant);
                Position pos = new Position(plant.getPosition().getX() + 90, plant.getPosition().getY() + 35);
                scheduleShooter(plantService, currentTick, 6, plant, damage, pos, speed, 0, projectile -> {
                    projectile.enableInfinitePierce();
                    projectile.setMaxRange(range);
                });
                return true;
            }

            default:
                return false;
        }
    }

    private boolean fireLobber(Plant plant,PlantService plantService,long currentTick,GameWorld gameWorld,int damage,double speed,boolean splash,ProjectileEffect effect
    ) {
        int delay = 0;
        switch (plant.getDefinition().getName()) {
            case KERNEL_PULT:
                delay = 6;
                break;
            case MELON_PULT:
                delay = 7;
                break;
            case WINTER_MELON:
                delay = 7;
                break;
            case PEPPER_PULT:
                delay = 4;
                break;
            case CABBAGE_PULT:
                delay = 6;
                break;
            default:
                delay = 5;
                break;
        }
        Zombie target = PlantTargeting.selectTarget(gameWorld, plant, FieldOfView.forwardLane());
        if (target == null) {
            return false;
        }
        playAttack(plant);
        Position pos = lobberSpawn(plant);
        scheduleLobbed(plantService, currentTick, delay, plant, damage, pos, speed, target, projectile -> {
            if (splash) {
                projectile.setSplash(true);
                projectile.setSplashRadius(1);
            }
            if (effect != null) {
                projectile.addEffect(effect);
            }
        });
        return true;
    }

    private Position lobberSpawn(Plant plant) {
        int x = 0;
        int y = 0;
        switch (plant.getDefinition().getName()) {
            case KERNEL_PULT:
                x = 80;
                y = 155;
                break;
            case MELON_PULT:
                x = 100;
                y = 100;
                break;
            case WINTER_MELON:
                x = 100;
                y = 100;
                break;
            case PEPPER_PULT:
                x = 95;
                y = 95;
                break;
            case CABBAGE_PULT:
                x = 115;
                y = 110;
                break;
            default:
                break;
        }
        return new Position(plant.getPosition().getX() + x, plant.getPosition().getY() + y);
    }

    private void playAttack(Plant plant) {
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
        if (plant.getDefinition() != null) {
            game.audio.Sfx.plantAttack(plant.getDefinition().getName());
        }
    }

    private void playAttack2(Plant plant) {
        GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
        graphicsEngine.getPlantInstance(plant).pushEvent(new VisualEvent<>(PlantVisualEventType.ATTACK_2_TRIGGERED));
        game.audio.Sfx.playRandom(game.audio.AudioId.SFX_THROW, game.audio.AudioId.SFX_THROW2);
    }

    private void scheduleShooter(
        PlantService plantService,
        long currentTick,
        int delay,
        Plant plant,
        int damage,
        Position position,
        double speed,
        double angle
    ) {
        scheduleShooter(plantService, currentTick, delay, plant, damage, position, speed, angle, null);
    }

    private void scheduleShooter(
        PlantService plantService,
        long currentTick,
        int delay,
        Plant plant,
        int damage,
        Position position,
        double speed,
        double angle,
        Consumer<ShooterProjectile> customizer
    ) {
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(currentTick, delay, () -> {
            if (plant == null || plant.isDead()) {
                return;
            }
            ShooterProjectile projectile = ProjectileFactory.createShooterProjectile(plant, damage, position, speed, angle);
            if (customizer != null) {
                customizer.accept(projectile);
            }
            plantService.addProjectile(projectile);
        });
    }

    private void scheduleLobbed(
        PlantService plantService,
        long currentTick,
        int delay,
        Plant plant,
        int damage,
        Position position,
        double speed,
        Zombie target,
        Consumer<LobbedProjectile> customizer
    ) {
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(currentTick, delay, () -> {
            LobbedProjectile projectile = ProjectileFactory.createLobbedProjectile(plant, damage, position, speed, target);
            if (customizer != null) {
                customizer.accept(projectile);
            }
            
            plantService.addProjectile(projectile);
        });
    }
}
