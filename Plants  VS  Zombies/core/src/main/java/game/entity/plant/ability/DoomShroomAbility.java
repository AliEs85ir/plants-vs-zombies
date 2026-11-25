package game.entity.plant.ability;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantGameplayPhase;
import game.engine.time.GameClock;
import game.entity.DeathStyle;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.CellOccupancyAbility;
import game.entity.obstacle.ability.TimerdLifeAbility;
import game.entity.plant.Plant;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.playground.Cell;
import graphics.general.event.PlantVisualEventType;
import graphics.general.effects.GameplayEffects;
import graphics.plant.PlantVisualState;

import java.util.List;

public class DoomShroomAbility extends AbstractPlantAbility {
    private static final long STAGE_DURATION_TICKS = 10L * GameClock.TICKS_PER_SECOND;
    private static final long MIN_TRANSFORM_TICKS = 4;
    private static final long MAX_TRANSFORM_TICKS = 40;
    private static final float STAGE_DAMAGE_BONUS = 0.20f;
    private static final float CRATER_DURATION_SECONDS = 180f;
    private static final int IMPACT_DELAY_TICKS = 9;

    private int stage = 1;
    private long lastStageTick = -1;
    private boolean transforming;
    private long transformStartTick;
    private boolean exploding;
    private boolean detonated;

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null) {
            return;
        }
        if (lastStageTick < 0) {
            lastStageTick = plant.getPlantedTick();
            plant.setGrowthStage(stage);
        }
        if (exploding) {
            return;
        }
        if (transforming) {
            long elapsed = currentTick - transformStartTick;
            boolean finished = elapsed >= MIN_TRANSFORM_TICKS
                && PlantAbilitySupport.isClipFinished(plant, PlantVisualState.ATTACK_2);
            if (finished || elapsed >= MAX_TRANSFORM_TICKS) {
                transforming = false;
                stage = Math.min(3, stage + 1);
                plant.setGrowthStage(stage);
                plant.setGameplayPhase(PlantGameplayPhase.DEFAULT);
                lastStageTick = currentTick;
            }
            return;
        }
        if (stage < 3 && currentTick - lastStageTick >= STAGE_DURATION_TICKS) {
            transforming = true;
            transformStartTick = currentTick;
            plant.setGameplayPhase(PlantGameplayPhase.TRANSFORMING);
            PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_2_TRIGGERED);
        }
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        if (detonated) {
            return false;
        }
        if (exploding) {
            return true;
        }
        exploding = true;
        plant.setGameplayPhase(PlantGameplayPhase.ATTACKING);
        int restore = Math.max(1, plant.getHealth().getMaxHealth());
        plant.getHealth().heal(restore);
        PlantAbilitySupport.play(plant, PlantVisualEventType.ATTACK_TRIGGERED);
        PlantAbilitySupport.schedule(currentTick, IMPACT_DELAY_TICKS, () -> detonate(plant, plantService));
        return true;
    }

    private void detonate(Plant plant, PlantService plantService) {
        if (detonated || plant == null) {
            return;
        }
        detonated = true;
        long currentTick = GameClock.currentTick;
        Cell cell = PlantTargeting.cellOf(plantService.getGameWorld(), plant);
        GameplayEffects.spawnExplosionPair(plant.getPosition());
        PlantAbilitySupport.notifyExploded(plant);
        AttackService attackService = PlantAbilitySupport.attacks(plantService.getGameWorld());
        int damage = scaledDamage(plant);
        if (stage >= 3) {
            attackService.damageAll(damage, currentTick, DeathStyle.ASH);
        } else if (cell != null) {
            attackService.damageArea(cell.getRow(), cell.getCol(), explosionRadius(), damage, currentTick, DeathStyle.ASH);
        }
        createCrater(cell);
        plant.markDead(plantService.getGameWorld());
    }

    private int explosionRadius() {
        return switch (stage) {
            case 1 -> 1;
            case 2 -> 2;
            default -> -1;
        };
    }

    private int scaledDamage(Plant plant) {
        return Math.round(plant.getAttackDamage() * (1f + STAGE_DAMAGE_BONUS * (stage - 1)));
    }

    private void createCrater(Cell cell) {
        if (cell == null) {
            return;
        }
        Obstacle crater = ObstacleFactory.getInstance().createRuntimeObstacle(
            ObstacleType.TEMPORARY_SCORCH,
            cell.getWidth() / 2,
            cell.getHeight() / 2,
            cell.getWorldX(),
            cell.getWorldY(),
            List.of(new TimerdLifeAbility(CRATER_DURATION_SECONDS))
        );
        cell.setObstacle(crater);
    }

    public int getStage() {
        return stage;
    }
}
