package game.entity.plant.ability.plantfood;

import app.enums.ProjectilleEffect.ProjectileEffect;
import common.model.Position;
import game.component.AssetManager;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.plant.ability.PlantAbilitySupport;
import game.entity.plant.combat.PlantFoodProjectileLaunch;
import game.entity.plant.combat.PlantProjectileKinematics;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.plantfood.PlantFoodShot;
import game.entity.projectile.ProjectileCapabilities;
import game.entity.projectile.ShooterProjectile;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.service.projectile.ProjectileFactory;
import game.world.playground.Grid;

import java.util.HashMap;
import java.util.Map;

public final class RapidFirePlantFoodAbility extends AbstractPlantFoodAbility {
    private static final String SNOW_PEA_FREEZE_PAM = "768/INITIAL/EFFECTS/SNOWPEA_PLANTFOOD/SNOWPEA_PLANTFOOD.PAM";
    private final Map<PlantFoodShot, Integer> shotsFired = new HashMap<>();
    private boolean freezeStarted;
    private boolean finishingFired;
    private boolean freezeEnded;

    public RapidFirePlantFoodAbility(PlantFoodDefinition definition) {
        super(definition);
    }

    @Override
    protected void onStart(Plant plant, PlantService plantService, long currentTick) {
        shotsFired.clear();
        freezeStarted = false;
        finishingFired = false;
        freezeEnded = false;
        if (definition.isLaneFreeze()) {
            freezeLane(plant, plantService, currentTick);
            freezeStarted = true;
            float[] laneScale = computeLaneEffectScale(plant, plantService);
            spawnNamedEffect(
                definition.getFreezeEffectId(),
                plant.getPosition(),
                definition.getFreezeHoldSeconds(),
                laneScale[0] * definition.getFreezeScale(),
                laneScale[1] * definition.getFreezeScale()
            );
            spawnNamedEffect(
                definition.getParticleEffectId(),
                plant.getPosition(),
                definition.getFreezeHoldSeconds(),
                laneScale[0] * definition.getParticleScale(),
                laneScale[1] * definition.getParticleScale()
            );
        }
    }

    @Override
    protected void onTick(Plant plant, PlantService plantService, long currentTick, long elapsedTicks) {
        fireStreams(plant, plantService, currentTick, elapsedTicks);
        fireFinishingShot(plant, plantService, currentTick, elapsedTicks);
        endFreeze(plant, plantService, elapsedTicks);
        if (isGameplayComplete(elapsedTicks) && isAnimationLifecycleFinished(plant, elapsedTicks)) {
            complete();
        }
    }

    private void fireStreams(Plant plant, PlantService plantService, long currentTick, long elapsedTicks) {
        int interval = Math.max(1, definition.getFireIntervalTicks());
        int globalDelay = definition.getFireStartDelayTicks();
        int maxShots = definition.getMaxShotsPerStream();
        int duration = definition.getDurationTicks();
        for (PlantFoodShot stream : definition.getStreams()) {
            int delay = Math.max(globalDelay, stream.getDelayTicks());
            int fired = shotsFired.getOrDefault(stream, 0);
            if (stream.hasCombatFlags()) {
                if (plant != null && plant.isDead()) {
                    continue;
                }
                if (!PlantFoodProjectileLaunch.shouldLaunchOnce(elapsedTicks, delay, fired > 0)) {
                    continue;
                }
            } else {
                if (elapsedTicks < delay) {
                    continue;
                }
                if (duration > 0 && elapsedTicks >= duration) {
                    continue;
                }
                long fireElapsed = elapsedTicks - delay;
                if (fireElapsed % interval != 0) {
                    continue;
                }
                if (maxShots > 0 && fired >= maxShots) {
                    continue;
                }
            }
            spawnShot(plant, plantService, currentTick, stream);
            shotsFired.put(stream, fired + 1);
            if (fired == 0 && stream.getAnimationStage() >= 0) {
                requestAnimationStage(plant, stream.getAnimationStage());
            }
        }
    }

    private void fireFinishingShot(Plant plant, PlantService plantService, long currentTick, long elapsedTicks) {
        PlantFoodShot finishingShot = definition.getFinishingShot();
        if (finishingShot == null || finishingFired) {
            return;
        }
        if (elapsedTicks < finishingShot.getDelayTicks()) {
            return;
        }
        spawnShot(plant, plantService, currentTick, finishingShot);
        finishingFired = true;
        if (finishingShot.getAnimationStage() >= 0) {
            requestAnimationStage(plant, finishingShot.getAnimationStage());
        }
    }

    private void spawnShot(Plant plant, PlantService plantService, long currentTick, PlantFoodShot shot) {
        if (plant == null || plant.isDead()) {
            return;
        }
        if (shot.isAllLanes()) {
            Grid grid = plantService.getGameWorld().getGrid();
            int plantRow = plant.getCell() == null ? 0 : plant.getCell().getRow();
            float cellHeight = grid.getConfig().getCellHeight();
            double baseX = plant.getPosition().getX() + shot.getOffsetX();
            double baseY = plant.getPosition().getY() + shot.getOffsetY();
            for (int row = 0; row < grid.getRowCount(); row++) {
                fireAt(
                    plant,
                    plantService,
                    currentTick,
                    shot,
                    baseX,
                    baseY + (row - plantRow) * cellHeight
                );
            }
            return;
        }
        fireAt(
            plant,
            plantService,
            currentTick,
            shot,
            plant.getPosition().getX() + shot.getOffsetX(),
            plant.getPosition().getY() + shot.getOffsetY()
        );
    }

    private void fireAt(
        Plant plant,
        PlantService plantService,
        long currentTick,
        PlantFoodShot shot,
        double x,
        double y
    ) {
        double speed = PlantProjectileKinematics.speedOf(plant.getDefinition().getName()) * shot.getSpeedMultiplier();
        int damage = Math.max(1, Math.round(plant.getAttackDamage() * shot.getDamageMultiplier()));
        Position computed = shot.hasCombatFlags() || shot.isFromPamVisualCenter() || shot.isApplyFacing()
            ? PlantFoodProjectileLaunch.spawnPosition(plant, shot)
            : new Position(x, y);
        final Position position = computed != null ? computed : new Position(x, y);
        double angle = PlantFoodProjectileLaunch.angleDegrees(plant, shot);
        Runnable spawn = () -> {
            ShooterProjectile projectile = ProjectileFactory.createShooterProjectile(
                plant,
                damage,
                position,
                speed,
                angle
            );
            if (shot.isIce()) {
                projectile.addEffect(ProjectileEffect.ICE);
            }
            ProjectileCapabilities capabilities = shot.getCapabilities();
            if (capabilities != null) {
                projectile.applyCapabilities(capabilities);
            }
            plantService.addProjectile(projectile);
            spawnNamedEffect(shot.getEffectId(), position, 0f, 1f);
        };
        if (shot.isImmediateSpawn()) {
            spawn.run();
        } else {
            PlantAbilitySupport.schedule(currentTick, 0, spawn);
        }
    }

    private void freezeLane(Plant plant, PlantService plantService, long currentTick) {
        if (plant.getCell() == null) {
            return;
        }
        int durationTicks = definition.getDurationTicks() > 0
            ? definition.getDurationTicks()
            : 4 * GameClock.TICKS_PER_SECOND;
        new AttackService(plantService.getGameWorld())
            .slowZombiesInRow(currentTick, durationTicks, plant.getCell().getRow(), 0.5);
    }

    private void endFreeze(Plant plant, PlantService plantService, long elapsedTicks) {
        if (!freezeStarted || freezeEnded) {
            return;
        }
        if (elapsedTicks < definition.getDurationTicks()) {
            return;
        }
        freezeEnded = true;
        float[] laneScale = computeLaneEffectScale(plant, plantService);
        spawnNamedEffect(
            definition.getFreezeOffEffectId(),
            plant.getPosition(),
            0f,
            laneScale[0] * definition.getFreezeScale(),
            laneScale[1] * definition.getFreezeScale()
        );
    }

    private float[] computeLaneEffectScale(Plant plant, PlantService plantService) {
        float[] scale = {1f, 1f};
        if (plant == null || plant.getPosition() == null || plantService == null || plantService.getGameWorld() == null) {
            return scale;
        }
        Grid grid = plantService.getGameWorld().getGrid();
        if (grid == null || grid.getConfig() == null) {
            return scale;
        }
        com.badlogic.gdx.math.Rectangle bounds = AssetManager.player.bounds(SNOW_PEA_FREEZE_PAM, "plantfood_on");
        float cellHeight = grid.getConfig().getCellHeight();
        float nativeHeight = bounds != null && bounds.height > 1f ? bounds.height : cellHeight;
        float nativeWidth = bounds != null && bounds.width > 1f ? bounds.width : grid.getConfig().getPlaygroundWidth();
        scale[1] = cellHeight / nativeHeight;
        float remaining = (float) (grid.getConfig().getPlaygroundX()
            + grid.getConfig().getPlaygroundWidth()
            - plant.getPosition().getX());
        scale[0] = remaining / Math.max(1f, nativeWidth * 0.5f);
        if (scale[0] <= 0f) {
            scale[0] = 1f;
        }
        return scale;
    }

    private boolean isGameplayComplete(long elapsedTicks) {
        if (definition.getMaxShotsPerStream() > 0) {
            return allStreamsFinished();
        }
        int duration = definition.getDurationTicks();
        if (duration <= 0) {
            return false;
        }
        if (definition.getFinishingShot() != null && !finishingFired) {
            return false;
        }
        return elapsedTicks >= duration;
    }

    private boolean allStreamsFinished() {
        int maxShots = definition.getMaxShotsPerStream();
        if (maxShots <= 0 || definition.getStreams().isEmpty()) {
            return false;
        }
        for (PlantFoodShot stream : definition.getStreams()) {
            if (shotsFired.getOrDefault(stream, 0) < maxShots) {
                return false;
            }
        }
        return true;
    }

    private void spawnNamedEffect(String effectId, Position position, float holdSeconds, float scale) {
        spawnNamedEffect(effectId, position, holdSeconds, scale, scale);
    }

    private void spawnNamedEffect(String effectId, Position position, float holdSeconds, float scaleX, float scaleY) {
        if (effectId == null || effectId.isBlank() || position == null) {
            return;
        }
        PlantAbilitySupport.spawnEffect(effectId, position, false, holdSeconds, scaleX, scaleY);
    }
}
