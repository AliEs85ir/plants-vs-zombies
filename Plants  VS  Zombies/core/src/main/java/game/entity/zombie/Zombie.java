package game.entity.zombie;

import app.enums.effect.EffectType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.audio.Sfx;
import game.component.contacts.ZombiePlantContact;
import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.entity.DamageContext;
import game.entity.DeathStyle;
import game.entity.Entity;
import game.entity.effect.Effect;
import game.entity.effect.TimedEffect;
import game.entity.effect.ZombieDeathEffectCatalog;
import game.entity.plant.Plant;
import game.entity.zombie.ability.ZombieAbility;
import game.entity.zombie.boss.boss.Zomboss;
import game.entity.zombie.data.properties.Property;
import game.entity.zombie.strategy.attack.AttackStrategy;
import game.entity.zombie.strategy.movement.MovementStrategy;
import game.entity.zombie.strategy.takedamage.TakingDamageStrategy;
import game.service.Zombie.ZombieService;
import game.service.drop.DropSpawner;
import game.service.quest.QuestSignals;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.EffectAnchor;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;
import graphics.general.modifier.ZombieEffectPresentation;
import graphics.zombie.ZombieRenderInstance;

import java.util.ArrayList;
import java.util.List;

public class Zombie extends Entity {
    private double currentHealth;
    private MovementStrategy movementStrategy;
    private AttackStrategy attackStrategy;
    private TakingDamageStrategy takingDamageStrategy;
    private Position position;
    private List<ZombieAbility> abilities;
    private GameWorld world;
    private Plant targetPlant;
    private Property propertyData;
    private final List<ZombieArmor> armors;
    private final List<Effect> effects;
    private boolean dead;
    private DeathStyle pendingDeathStyle = DeathStyle.NORMAL;
    private Entity lastAttacker;
    private boolean dropsPlantFood;

    public enum ZombieState {
        WALKING,
        EATING,
        USING_ABILITY,
        DYING
    }
    private ZombieState state;
    private boolean canMove;
    private double moveSpeedMultiplier;
    private double eatSpeedMultiplier;
    private long nextAttackTick;

    public Zombie(double currentHealth,
                  MovementStrategy movementStrategy,
                  AttackStrategy attackStrategy,
                  TakingDamageStrategy takingDamageStrategy,
                  Position position,
                  List<ZombieAbility> abilities,
                  GameWorld world,
                  Plant targetPlant,
                  Property propertyData) {

        this.currentHealth = currentHealth;
        this.movementStrategy = movementStrategy;
        this.attackStrategy = attackStrategy;
        this.takingDamageStrategy = takingDamageStrategy;
        this.position = position;
        this.abilities = abilities;
        this.world = world;
        this.targetPlant = targetPlant;
        this.propertyData = propertyData;
        armors = new ArrayList<>();
        effects = new ArrayList<>();
        this.canMove = true;
        this.moveSpeedMultiplier = 1f;
        this.eatSpeedMultiplier = 1f;
        this.dead = false;
        this.pendingDeathStyle = DeathStyle.NORMAL;
        this.nextAttackTick = 0;
        this.state = ZombieState.WALKING;
        this.dropsPlantFood = false;
    }

    public String getName() {
        return propertyData.getAlias();
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    public AttackStrategy getAttackStrategy() {
        return attackStrategy;
    }

    public TakingDamageStrategy getTakingDamageStrategy() {
        return takingDamageStrategy;
    }

    @Override
    public Position getPosition() {
        return position;
    }


    public List<ZombieAbility> getAbilities() {
        return abilities;
    }

    public GameWorld getWorld() {
        return world;
    }

    public Plant getTargetPlant() {
        return targetPlant;
    }

    public Property getPropertyData() {
        return propertyData;
    }

    public List<ZombieArmor> getArmors() {
        return armors;
    }
    public void addArmor(ZombieArmor armor) {
        armors.add(armor);
    }

    public List<Effect> getEffects() {
        return effects;
    }


    public void update(ZombieService zombieService, long currentTick) {
        updateLaneMask();
        if(dead){
            return;
        }
        if(currentHealth <= 0) {
            die();
            return;
        }
        updateEffects(zombieService,currentTick);
        syncPosition();
        if(!canAct()) {
            stopMovement();
            return;
        }
        for (ZombieAbility ability : abilities) {
            ability.update(this, zombieService, currentTick);
        }


        evaluateState();

        switch (state) {
            case EATING:
                stopMovement();
                retargetStackIfNeeded();
                attackStrategy.executeAttack(this, targetPlant, zombieService, currentTick);
                if (targetPlant != null && targetPlant.isDead()) {
                    retargetStackIfNeeded();
                    if (targetPlant != null && targetPlant.isDead()) {
                        targetPlant = null;
                    }
                }
                break;

            case WALKING:
                movementStrategy.move(this, zombieService, currentTick);
                break;

            case USING_ABILITY:
                break;
            case DYING:
                stopMovement();
                break;
        }

    }

    private void syncPosition() {
        Vector2 b2Pos = body.getPosition();
        setPosition(new Position(b2Pos.x * PhysicService.PIXEL_PER_METER,
                                 b2Pos.y * PhysicService.PIXEL_PER_METER));
    }

    private void evaluateState() {
        retargetStackIfNeeded();
        if (targetPlant == null || targetPlant.isDead()) {
            ZombiePlantContact.bindIfBlocking(this);
        }
        if (targetPlant != null && !targetPlant.isDead()) {
            this.state = ZombieState.EATING;
        } else {
            this.targetPlant = null;
            if (this.state != ZombieState.USING_ABILITY) {
                this.state = ZombieState.WALKING;
            }
        }
    }

    private void retargetStackIfNeeded() {
        if (targetPlant == null) {
            return;
        }
        if (!targetPlant.isDead()) {
            if (targetPlant.getCell() != null) {
                Plant top = targetPlant.getCell().getDamageTarget();
                if (top != null && top != targetPlant) {
                    targetPlant = top;
                }
            }
            return;
        }
        if (targetPlant.getCell() != null) {
            Plant next = targetPlant.getCell().getDamageTarget();
            if (next != null && !next.isDead()) {
                targetPlant = next;
            }
        }
    }


    private void updateEffects(ZombieService zombieService,long currentTick) {

        // Tick all effects (eg : for damage / freeze):
        for(Effect effect : effects) {
            effect.tick(this, zombieService, currentTick);
        }

        effects.removeIf(effect -> {
            if(effect.isExpired(currentTick)) {
                effect.onRemove(this);
                return true;
            }
            return false;
        });

        for(Effect effect : effects) {
            effect.applyModifier(this);
        }
        ZombieEffectPresentation.sync(this);
    }

    public void addEffect(Effect effect) {
        if (effect == null) {
            return;
        }
        if(this instanceof Zomboss) {
            return;
        }
        EffectType type = effect.getType();
        if (type == EffectType.FIRE || type == EffectType.BURN) {
            removeEffect(EffectType.ICE);
            removeEffect(EffectType.FREEZE);
        }
        if (type == EffectType.ICE || type == EffectType.FREEZE) {
            removeEffect(EffectType.FIRE);
            removeEffect(EffectType.BURN);
        }
        Effect existing = findEffect(type);
        if (existing instanceof TimedEffect current && effect instanceof TimedEffect incoming) {
            current.refresh(incoming);
            ZombieEffectPresentation.sync(this);
            return;
        }
        if (existing != null) {
            removeEffect(type);
        }
        effects.add(effect);
        effect.onApply(this);
        ZombieEffectPresentation.sync(this);
    }


    public void setMovementStrategy(MovementStrategy newStrategy) {
        this.movementStrategy = newStrategy;
    }

    private boolean canAct() {
        return canMove && !hasEffect(EffectType.FREEZE) && !hasEffect(EffectType.STUN);
    }


    public void setCurrentHealth(double currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void setAttackStrategy(AttackStrategy attackStrategy) {
        this.attackStrategy = attackStrategy;
    }

    public void setTakingDamageStrategy(TakingDamageStrategy takingDamageStrategy) {
        this.takingDamageStrategy = takingDamageStrategy;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public void die() {
        if (dead) {
            return;
        }
        world.increaseTotalDeadZombies();
        dead = true;
        Sfx.zombieDied(this);
        QuestSignals.zombieKilled(this, lastAttacker);
        DropSpawner.trySpawnFrom(this);

        for (Effect effect : effects) {
            effect.onRemove(this);
        }
        for (ZombieAbility ability : abilities) {
            ability.onDeath(this);
        }
        effects.clear();
        ZombieEffectPresentation.sync(this);

        world.addEvent("zombie die at position>>  X:" + position.getX() + " Y:" + position.getY());
        stopMovement();
        if (persistAfterDeath()) {
            destroyPhysics();
        } else if (pendingDeathStyle == DeathStyle.ASH) {
            spawnAshDeathEffect();
            destroyBox2d();
        } else {
            pushEvent(ZombieVisualEventType.DIED);
            if (App.getCurrentGameEngine() != null) {
                App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                    GameClock.currentTick,
                    20,
                    this::destroyBox2d
                );
            }
        }

        targetPlant = null;
    }

    public boolean persistAfterDeath() {
        return false;
    }

    private void spawnAshDeathEffect() {
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        String effectId = ZombieDeathEffectCatalog.ashEffectId(getName());
        App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(
            new EffectRequest<>(
                effectId,
                this,
                new WorldAnchor(position),
                EffectLayer.FRONT
            )
        );
    }

    private void destroyBox2d() {
        App.getRealTimeEngine().getGraphicsEngine().unregisterZombie(this);
        destroyPhysics();
    }

    private void destroyPhysics() {
        PhysicService physicService = App.getRealTimeEngine().getPhysicService();
        destroyBody(physicService);
    }

    public boolean isDead() {
        return dead || currentHealth <= 0;
    }

    public void setAbilities(List<ZombieAbility> abilities) {
        this.abilities = abilities;
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    public void setTargetPlant(Plant targetPlant) {
        this.targetPlant = targetPlant;
    }

    public void setPropertyData(Property propertyData) {
        this.propertyData = propertyData;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public void setMoveSpeedMultiplier(double moveSpeedMultiplier) {
        this.moveSpeedMultiplier = moveSpeedMultiplier;
    }

    public void addAbility(ZombieAbility ability) {
        abilities.add(ability);
    }

    public double getSpeed() {
        if (hasEffect(EffectType.FREEZE)) {
            return 0;
        }
        return propertyData.getSpeed() * moveSpeedMultiplier;
    }

    public double getMoveSpeedMultiplier() {
        return moveSpeedMultiplier;
    }

    public double getEatSpeedMultiplier() {
        if (hasEffect(EffectType.FREEZE)) {
            return 0;
        }
        return eatSpeedMultiplier;
    }

    public void setEatSpeedMultiplier(double eatSpeedMultiplier) {
        this.eatSpeedMultiplier = eatSpeedMultiplier;
    }

    public void removeEffect(EffectType effect) {
        effects.removeIf(element -> {
            if (element.getType() == effect) {
                element.onRemove(this);
                return true;
            }
            return false;
        });
        ZombieEffectPresentation.sync(this);
    }

    public boolean hasEffect(EffectType type) {
        return findEffect(type) != null;
    }

    public Effect findEffect(EffectType type) {
        if (type == null) {
            return null;
        }
        for (Effect effect : effects) {
            if (effect.getType() == type) {
                return effect;
            }
        }
        return null;
    }

    public long getNextAttackTick() {
        return nextAttackTick;
    }

    public void setNextAttackTick(long nextAttackTick) {
        this.nextAttackTick = nextAttackTick;
    }

    public String printInfo() {
        return propertyData.printInfo();
    }

    public Entity getLastAttacker() {
        return lastAttacker;
    }

    public void takeDamage(DamageContext context, long currentTick) {
        if (context != null && context.attacker() != null) {
            lastAttacker = context.attacker();
        }
        boolean applied = takingDamageStrategy.takeDamage(this, currentTick, context);
        if (applied) {
            Sfx.zombieHit(this);
        }
        if (currentHealth <= 0) {
            if (context != null && context.deathStyle() != null && context.deathStyle() != DeathStyle.NORMAL) {
                pendingDeathStyle = context.deathStyle();
            }
            die();
        }
    }

    public boolean dropsPlantFood() {
        return dropsPlantFood;
    }

    public void markPlantFoodDrop() {
        this.dropsPlantFood = true;
    }

    public void stopMovement() {
        if (body != null) {
            body.setLinearVelocity(0f, 0f);
        }
    }


    public void setState(ZombieState state) {
        this.state = state;
    }

    public void pushEvent(ZombieVisualEventType type) {
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        ZombieRenderInstance inst = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(this);
        if(inst == null) {
            return;
        }
        if (inst.getCurrentEvent() == null || inst.getCurrentEvent().getType() != type) {
            inst.pushEvent(new VisualEvent<>(type));
        }
    }
    public void pushEffect(String id, EffectAnchor anchor) {
        ZombieRenderInstance inst = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(this);
        if(inst == null) {
            return;
        }
        if (inst.getCurrentEffect() == null || !inst.getCurrentEffect().getEffectId().equals(id)) {
            inst.pushEffect(id, anchor);
        }
    }

    public void stopEffects() {
        ZombieRenderInstance inst = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(this);
        if (inst == null) {
            return;
        }
        inst.stopEffects();
    }

    public ZombieVisualEventType topEvent() {
        ZombieRenderInstance inst = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(this);

        VisualEvent<ZombieVisualEventType> currentEvent = inst.getCurrentEvent();
        if(currentEvent == null) {
            return ZombieVisualEventType.TOSS_OUT;
        }
        return currentEvent.getType();
    }

    public ZombieState getState() {
        return state;
    }

    public DeathStyle getPendingDeathStyle() {
        return pendingDeathStyle;
    }

    public void setPendingDeathStyle(DeathStyle pendingDeathStyle) {
        this.pendingDeathStyle = pendingDeathStyle;
    }
}
