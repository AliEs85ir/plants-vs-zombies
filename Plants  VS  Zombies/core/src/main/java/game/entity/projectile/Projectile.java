package game.entity.projectile;

import app.enums.ProjectilleEffect.ProjectileEffect;
import app.enums.effect.EffectType;
import app.enums.plant.PlantName;
import app.enums.projectile.ProjectileType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.DamageContext;
import game.entity.Entity;
import game.entity.effect.CharmEffect;
import game.entity.effect.FireEffect;
import game.entity.effect.PoisonEffect;
import game.entity.effect.SlowEffect;
import game.entity.effect.StunEffect;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.GraphicsEngine;
import graphics.general.event.VisualEvent;
import graphics.general.modifier.HitFlashEffect;
import graphics.projectile.ProjectileRenderInstance;
import graphics.projectile.ProjectileVisualEventType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Projectile extends Entity {
    protected Plant owner;
    protected int damage;
    protected double speed;
    protected ProjectileType type;
    protected boolean active;
    protected boolean launched;
    protected boolean canBypassObstacles = false;
    protected int maxTargets = 1;
    protected Set<Zombie> hitZombies;
    protected Set<Plant> hitPlants;
    protected Set<Obstacle> hitObstacles;
    protected boolean ignoresArmor = false;
    protected int delayOffset;
    protected int maxLifetimeTicks = -1;
    protected int ticksActive = 0;
    protected double maxRange = -1;
    protected boolean infinitePierce = false;
    protected Vector2 startPhysicsPosition;
    protected List<ProjectileEffect> effects;
    protected boolean isSplash;
    protected double splashDamageMultiplier = 0.5;
    protected int splashRadius = 1;
    protected int fireDamageMultiplier = 1;
    protected boolean alreadyFireTransformed;
    protected boolean unstoppable;
    protected boolean instantKill;
    protected boolean enemyOnly;
    protected boolean plantFoodVisual;
    protected String hitEffectId;
    protected Position spawnWorld;
    protected Position lastHitEffectWorld;
    protected PlantName animationPlantName;
    protected boolean hasLastPosition;
    protected double lastX;
    protected double lastY;

    public Projectile(Plant owner, int damage, double speed, ProjectileType type, int delayOffset) {
        this.owner = owner;
        this.damage = damage;
        this.speed = speed;
        this.type = type;
        this.active = true;
        this.delayOffset = delayOffset;
        this.launched = (delayOffset <= 0);
        this.hitZombies = new HashSet<>();
        this.hitObstacles = new HashSet<>();
        this.hitPlants = new HashSet<>();
        this.effects = new ArrayList<>();
    }

    protected Projectile() {
    }

    @Override
    public Position getPosition() {
        if (body == null) {
            if (hasLastPosition) {
                return new Position(lastX, lastY);
            }
            return spawnWorld;
        }
        Vector2 pos = body.getPosition();
        return new Position(pos.x * PhysicService.PIXEL_PER_METER, pos.y * PhysicService.PIXEL_PER_METER);
    }

    public void update(GameWorld gameWorld, long currentTick) {
        updateLaneMask();
        if (!active) {return;}

        if (delayOffset > 0) {
            delayOffset--;
            return;
        }

        if (!launched) {
            launched = true;
            launch();
        }

        if (maxLifetimeTicks != -1 &&
            ticksActive >= maxLifetimeTicks) {
            destroy();
            return;
        }

        ticksActive++;
        checkRange();
        checkOutOfBounds(gameWorld);
    }

    public void rememberPosition() {
        Position current = getPosition();
        if (current == null) {
            return;
        }
        lastX = current.getX();
        lastY = current.getY();
        hasLastPosition = true;
    }

    public boolean hasLastPosition() {
        return hasLastPosition;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    protected abstract void launch();

    protected void checkRange() {
        if (maxRange != -1 && body != null && startPhysicsPosition != null) {
            float distance = body.getPosition().dst(startPhysicsPosition) * PhysicService.PIXEL_PER_METER;
            if (distance >= maxRange) {
                destroy();
            }
        }
    }

    protected void checkOutOfBounds(GameWorld gameWorld) {
        if (body == null || gameWorld == null) {
            return;
        }
        Vector2 pos = getPhysicsPosition();
        if (gameWorld.getMap() != null && gameWorld.getMap().getMapConfig() != null) {
            var config = gameWorld.getMap().getMapConfig();
            if (pos.x < 0 || pos.x > config.getPlaygroundX() + config.getPlaygroundWidth() + 200 ||
                pos.y < 0 || pos.y > config.getPlaygroundY() + config.getPlaygroundHeight() + 200) {
                destroy();
            }
        }
    }

    public abstract void handleHit(Zombie zombie, long currentTick);
    public abstract void handleHit(Obstacle obstacle, long currentTick);


    public void setOwner(Plant owner) {
        this.owner = owner;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public ProjectileType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isLaunched() {
        return launched;
    }

    public void setLaunched(boolean launched) {
        this.launched = launched;
    }

    public boolean isCanBypassObstacles() {
        return canBypassObstacles;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public Set<Obstacle> getHitObstacles() {
        return hitObstacles;
    }

    public Set<Zombie> getHitZombies() {
        return hitZombies;
    }

    public void destroy() {
        System.out.println("called destroy()");
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
            ProjectileRenderInstance instance = graphicsEngine.getProjectileInstance(this);
            if (instance != null) {
                instance.pushEvent(new VisualEvent<>(ProjectileVisualEventType.DESTROY));
            }
        }
        this.active = false;
    }

    public Plant getOwner() {
        return owner;
    }

    public boolean isIgnoresArmor() {
        return ignoresArmor;
    }

    public void enableIgnoresArmor() {
        this.ignoresArmor = true;
    }

    public void enableObstacleBypass() {
        this.canBypassObstacles = true;
    }

    public int getDelayOffset() {
        return delayOffset;
    }

    public void setDelayOffset(int delayOffset) {
        this.delayOffset = delayOffset;
        if (delayOffset <= 0 && !launched) {
            launched = true;
            launch();
        }
    }

    public int getMaxLifetimeTicks() {
        return maxLifetimeTicks;
    }

    public void setMaxLifetimeTicks(int maxLifetimeTicks) {
        this.maxLifetimeTicks = maxLifetimeTicks;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    public boolean isInfinitePierce() {
        return infinitePierce;
    }

    public void enableInfinitePierce() {
        this.infinitePierce = true;
    }

    public PlantName getPlantName() {
        return owner.getDefinition().getName();
    }

    public PlantName getAnimationPlantName() {
        return animationPlantName != null ? animationPlantName : getPlantName();
    }

    public void setAnimationPlantName(PlantName animationPlantName) {
        this.animationPlantName = animationPlantName;
    }

    public int getFireDamageMultiplier() {
        return fireDamageMultiplier;
    }

    public void setFireDamageMultiplier(int fireDamageMultiplier) {
        this.fireDamageMultiplier = Math.max(1, fireDamageMultiplier);
    }

    public boolean isFireTransformed() {
        return alreadyFireTransformed || fireDamageMultiplier > 1;
    }

    public void markFireTransformed() {
        this.alreadyFireTransformed = true;
    }

    public void setSpawnWorld(Position spawnWorld) {
        this.spawnWorld = spawnWorld;
    }

    public Position getSpawnWorld() {
        return spawnWorld;
    }

    public boolean isUnstoppable() {
        return unstoppable;
    }

    public boolean isInstantKill() {
        return instantKill;
    }

    public boolean isEnemyOnly() {
        return enemyOnly;
    }

    public boolean isPlantFoodVisual() {
        return plantFoodVisual;
    }

    public String getHitEffectId() {
        return hitEffectId;
    }

    public Position getLastHitEffectWorld() {
        return lastHitEffectWorld;
    }

    public void applyCapabilities(ProjectileCapabilities capabilities) {
        if (capabilities == null) {
            return;
        }
        if (capabilities.isUnstoppable()) {
            this.unstoppable = true;
            enableIgnoresArmor();
        }
        if (capabilities.isPierceAll()) {
            enableInfinitePierce();
        }
        this.instantKill = capabilities.isInstantKill();
        this.enemyOnly = capabilities.isEnemyOnly();
        this.plantFoodVisual = capabilities.isPlantFoodVisual();
        if (capabilities.getHitEffectId() != null && !capabilities.getHitEffectId().isBlank()) {
            this.hitEffectId = capabilities.getHitEffectId();
        }
    }

    public Set<Plant> getHitPlants() {
        return hitPlants;
    }

    public void addEffect(ProjectileEffect effect) {
        if (effect != null && !effects.contains(effect)) {
            effects.add(effect);
            if (effect == ProjectileEffect.FIRE) {
                game.audio.Sfx.playRandom(game.audio.AudioId.SFX_IGNITE, game.audio.AudioId.SFX_IGNITE2);
            }
        }
    }

    public void removeEffect(ProjectileEffect effect) {
        effects.remove(effect);
    }

    public List<ProjectileEffect> getEffects() {
        return effects;
    }

    public float getVisualHeight() {
        return 0f;
    }

    public void syncFrame(float interpolationAlpha) {
    }

    protected void applyEffects(Zombie zombie, long currentTick) {
        if (zombie == null) {
            return;
        }
        if (effects.contains(ProjectileEffect.FIRE)) {
            zombie.removeEffect(EffectType.ICE);
            zombie.addEffect(new FireEffect(4f));
        }
        if (effects.contains(ProjectileEffect.ICE)) {
            zombie.addEffect(new SlowEffect(4f, 0.5f, EffectType.ICE));
        }
        if (effects.contains(ProjectileEffect.SLOWER)) {
            zombie.addEffect(new SlowEffect(4f, 0.5f, EffectType.SLOW));
        }
        if (effects.contains(ProjectileEffect.STUN)) {
            zombie.addEffect(new StunEffect(4f));
        }
        if (effects.contains(ProjectileEffect.HYPNOSIS)) {
            zombie.addEffect(new CharmEffect(1));
        }
        if (effects.contains(ProjectileEffect.POISONOUS)) {
            zombie.addEffect(new PoisonEffect(1f, 5));
        }
    }

    protected int resolveDamage() {
        if (fireDamageMultiplier > 1) {
            return damage * fireDamageMultiplier;
        }
        int actualDamage = damage;
        if (effects.contains(ProjectileEffect.FIRE)) {
            actualDamage *= 2;
        }
        return actualDamage;
    }

    protected void applyDamage(Zombie zombie, int damageAmount, long currentTick) {
        if (zombie == null || zombie.isDead() || hitZombies.contains(zombie)) {
            return;
        }
        if (enemyOnly && zombie.hasEffect(app.enums.effect.EffectType.CHARM)) {
            return;
        }
        applyEffects(zombie, currentTick);
        if (instantKill) {
            zombie.takeDamage(new DamageContext(this, Math.max(damageAmount, 1), true), currentTick);
            if (!zombie.isDead()) {
                zombie.die();
            }
        } else {
            DamageContext context = new DamageContext(this, damageAmount, ignoresArmor);
            zombie.takeDamage(context, currentTick);
        }
        hitZombies.add(zombie);
        HitFlashEffect.apply(zombie);
    }
    protected void applyDamage(Obstacle obstacle) {
        if (obstacle == null || obstacle.isDead() || hitObstacles.contains(obstacle) || !obstacle.isDamageable()) {
            return;
        }
        obstacle.takeDamage(resolveDamage());
        hitObstacles.add(obstacle);
        HitFlashEffect.apply(obstacle);
    }

    public boolean isSplash() {
        return isSplash;
    }

    public void setSplash(boolean splash) {
        isSplash = splash;
    }

    public double getSplashDamageMultiplier() {
        return splashDamageMultiplier;
    }

    public void setSplashDamageMultiplier(double splashDamageMultiplier) {
        this.splashDamageMultiplier = splashDamageMultiplier;
    }

    public int getSplashRadius() {
        return splashRadius;
    }

    public void setSplashRadius(int splashRadius) {
        this.splashRadius = splashRadius;
    }

    protected void applySplash(GameWorld gameWorld, Position center, Zombie primaryTarget, long currentTick) {
        if (!isSplash || gameWorld == null || center == null) {
            return;
        }
        Grid grid = gameWorld.getGrid();
        if (grid == null) {
            return;
        }
        Cell impactCell = grid.getCellAt((float) center.getX(), (float) center.getY());
        if (impactCell == null) {
            return;
        }
        int splashDamage = Math.max(1, (int) (resolveDamage() * splashDamageMultiplier));
        for (Zombie zombie : gameWorld.getZombies()) {
            if (zombie == null || zombie.isDead() || zombie == primaryTarget || hitZombies.contains(zombie)) {
                continue;
            }
            if (zombie.getPosition() == null) {
                continue;
            }
            Cell cell = grid.getCellAt(
                (float) zombie.getPosition().getX(),
                (float) zombie.getPosition().getY()
            );
            if (cell == null) {
                continue;
            }
            if (Math.abs(cell.getRow() - impactCell.getRow()) <= splashRadius
                && Math.abs(cell.getCol() - impactCell.getCol()) <= splashRadius) {
                applyDamage(zombie, splashDamage, currentTick);
            }
        }
    }
}
