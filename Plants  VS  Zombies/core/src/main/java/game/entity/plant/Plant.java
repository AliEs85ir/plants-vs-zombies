package game.entity.plant;

import app.enums.plant.PlantGameplayPhase;
import app.enums.plant.PlantState;
import app.enums.effect.EffectType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.Entity;
import game.entity.effect.Effect;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.SunProductionAbility;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.entity.zombie.Zombie;
import game.service.plant.PlantService;
import game.service.plant.PlantUpgradeService;
import game.world.GameWorld;
import game.world.playground.Cell;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.PlantVisualEventType;
import graphics.general.event.VisualEvent;
import graphics.plant.PlantDamageFlash;

import java.util.ArrayList;
import java.util.List;

// >>> Represents a planted plant inside game world.
public class Plant extends Entity {
    // -> Properties
    private PlantDefinition definition; // Static plant data
    private PlantHealth health; // Health container
    private PlantLevel level; // Upgrade progress
    private PlantFoodState plantFoodState; // Plant food status
    private Position position; // Current position
    private PlantState state; // Current life state
    private long plantedTick; // Tick when plant was planted
    private boolean isBoosted;
    private final List<Effect> effects;
    private final PlantDamage plantDamage;
    private Cell cell;
    private final PlantFreezeController freezeController;

    private boolean canAct = true;
    private PlantGameplayPhase gameplayPhase = PlantGameplayPhase.DEFAULT;
    private int growthStage = 1;
    private int facing = 1;

    private List<PlantAbility> abilities; // Attached abilities
    // -> Constructor
    public Plant() {
        abilities = new ArrayList<>();
        effects = new ArrayList<>();
        this.plantDamage = new PlantDamage();
        this.plantFoodState = new PlantFoodState();
        this.freezeController = new PlantFreezeController();
    }

    public void addAbility(PlantAbility ability) {
        abilities.add(ability);
    }

    public PlantDefinition getDefinition()
    {
        return definition;
    }

    @Override
    public Position getPosition()
    {
        return position;
    }

    // Changes plant position.
    public void setPosition(Position position)
    {
        this.position = position;
    }

    // Returns current health.
    public PlantHealth getCurrentHealth()
    {
        return health;
    }

    // Applies damage to plant.
    public void takeDamage(double damage , Zombie attacker , long currentTick)
    {
        if (cell != null) {
            Plant top = cell.getDamageTarget();
            if (top != null && top != this) {
                top.takeDamage(damage, attacker, currentTick);
                return;
            }
        }
        if (abilities != null) {
            for (PlantAbility ability : abilities) {
                if (ability != null && !ability.acceptsDamageFrom(attacker)) {
                    if (attacker != null && attacker.getTargetPlant() == this) {
                        attacker.setTargetPlant(null);
                    }
                    return;
                }
            }
        }
        this.health.takeDamage(damage , plantDamage , attacker , currentTick);
        if (damage > 0) {
            PlantDamageFlash.apply(this);
        }
        game.audio.Sfx.plantDamaged(this);
        if (damage > 0 && abilities != null) {
            for (PlantAbility ability : abilities) {
                if (ability != null) {
                    ability.onDamaged(this, attacker, currentTick);
                }
            }
        }
    }

    // Restores health.
    public void heal(int amount)
    {
        this.health.heal(amount);
    }

    // Checks whether plant is alive.
    public boolean isAlive()
    {
        return state != PlantState.DEAD && !this.health.isDead();
    }

    // Returns current level.
    public int getLevel()
    {
        if (definition == null || definition.getName() == null) {
            return 1;
        }
        return PlantUpgradeService.getCurrentLevel(definition.getName());
    }

    // Upgrades plant level.
    public void levelUp()
    {
        if (definition == null || definition.getName() == null) {
            return;
        }
        PlantUpgradeService.upgrade(definition.getName());
    }

    // Returns plant state.
    public PlantState getState()
    {
        return state;
    }

    // Changes plant state.
    public void setState(PlantState state)
    {
        this.state = state;
    }

    // Returns creation tick.
    public long getPlantTick()
    {
        return plantedTick;
    }

    // Returns all abilities.
    public List<PlantAbility> getAbilities()
    {
        return abilities;
    }


    public int getAttackDamage() {
        if (definition == null || definition.getName() == null) return 0;
        return PlantUpgradeService.getEffectiveStats(definition.getName()).attackDamage();
    }

    public void setDefinition(PlantDefinition deginition) {
        this.definition = deginition;
        abilities.addAll(this.definition.getAbilities());
    }

    public float getActionInterval() {
        if (definition == null || definition.getName() == null) {
            return 1f;
        }
        return PlantUpgradeService.getEffectiveStats(definition.getName()).actionIntervalTicks();
    }

    public int getSunCost()
    {
        if (definition == null || definition.getName() == null) {
            return 0;
        }
        return PlantUpgradeService.getEffectiveStats(definition.getName()).sunCost();
    }

    public void setHealth(PlantHealth health) {
        this.health = health;
    }

    public String getName() {
        return this.definition.getName().toString();
    }


    public PlantHealth getHealth() {
        return health;
    }

    public PlantFoodState getPlantFoodState() {
        return plantFoodState;
    }

    public long getPlantedTick() {
        return plantedTick;
    }

    public void setPlantedTick(long plantedTick) {
        this.plantedTick = plantedTick;
    }

    public boolean isBoosted() {
        return isBoosted;
    }

    public void setBoosted(boolean boosted) {
        isBoosted = boosted;
    }

    public void update(PlantService plantService , long currentTick )
    {
        updateLaneMask();
        updateEffects(plantService , currentTick);
        syncPosition();
        if(canAct) {
            boolean plantFoodActive = plantFoodState.isActive();
            boolean keepNormalCombat = false;
            if (plantFoodActive && getDefinition() != null) {
                PlantFoodDefinition plantFoodDefinition = PlantFoodDefinitionRegistry.get(getDefinition().getName());
                keepNormalCombat = plantFoodDefinition != null && plantFoodDefinition.keepsNormalCombat();
            }
            for (PlantAbility ability : abilities) {
                if (ability == null) {
                    continue;
                }
                if (ability.isPlantFoodAbility()) {
                    if (plantFoodActive) {
                        ability.update(this, plantService, currentTick);
                    }
                } else if (!plantFoodActive || keepNormalCombat) {
                    ability.update(this, plantService, currentTick);
                }
            }
            plantFoodState.update(currentTick);
        }
        if (health != null && health.isDead() && state != PlantState.DEAD) {
            boolean handled = false;
            for (PlantAbility ability : abilities) {
                if (ability != null && ability.handleDeath(this, plantService, currentTick)) {
                    handled = true;
                }
            }
            if (!handled) {
                markDead(plantService.getGameWorld());
            }
        }
    }

    private void syncPosition() {
        if (body != null) {
            Vector2 b2Pos = body.getPosition();
            setPosition(new Position(b2Pos.x * PhysicService.PIXEL_PER_METER,
                b2Pos.y * PhysicService.PIXEL_PER_METER));
        }
    }

    private void updateEffects(PlantService plantService, long currentTick) {

        // Tick all effects (eg : for damage / freeze):
        for(Effect effect : effects) {
            effect.tick(this, plantService, currentTick);
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
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public void setAbilities(List<PlantAbility> abilities) {
        this.abilities = abilities;
    }

    public boolean isDead() {
        return state == PlantState.DEAD || (health != null && health.isDead());
    }

    public boolean canBeShoveled() {
        return !isDead();
    }

    public void addEffect(Effect effect) {
        effects.add(effect);
        effect.onApply(this);
    }

    public void markDead(GameWorld world) {
        if (state == PlantState.DEAD) {
            return;
        }
        clearFreezeVisuals();
        var plantInstance = App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null
            ? null
            : App.getRealTimeEngine().getGraphicsEngine().getPlantInstance(this);
        if (plantInstance != null) {
            plantInstance.getAnimationController().setTimeScale(1f);
            plantInstance.pushEvent(new VisualEvent<>(PlantVisualEventType.DIED));
        }
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(
                new EffectRequest<>(
                    "impact plant death",
                    null,
                    new WorldAnchor(position),
                    EffectLayer.FRONT
                )
            );
            App.getRealTimeEngine().getGraphicsEngine().unregisterPlant(this);
        }
        state = PlantState.DEAD;
        if (this.health != null) {
            this.health.die();
        }
        game.audio.Sfx.plantDied();
        if (world != null) {
            world.increaseTotalDeadPlants();
            if (plantDamage != null && plantDamage.getZombie() != null && world.getObserver() != null) {
                world.getObserver().addLostPlantsCount(1);
                world.notifyPlantEaten(this, cell);
            }
            String plantName = definition == null || definition.getName() == null ? "plant" : getName();
            String where = position == null ? "unknown" : position.toString();
            world.addEvent(">>> Plant " + plantName + " died (!) at position : " + where);
        }
        if(cell != null) {
            cell.removePlant(this);
        }
        if (App.getRealTimeEngine() != null) {
            PhysicService physicService = App.getRealTimeEngine().getPhysicService();
            destroyBody(physicService);
        }
    }

    private void clearFreezeVisuals() {
        freezeController.reset();
        effects.removeIf(effect -> {
            if (effect != null && effect.getType() == EffectType.FREEZE) {
                effect.onRemove(this);
                return true;
            }
            return false;
        });
    }

    public int getRow()
    {
        return (int) position.getY();
    }

    public int getCol()
    {
        return (int) position.getX();
    }

    public PlantDamage getPlantDamage() {
        return plantDamage;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public boolean isCanAct() {
        return canAct;
    }

    public void setCanAct(boolean canAct) {
        this.canAct = canAct;
    }

    public boolean isFrozen() {
        for (Effect effect : effects) {
            if (effect != null && effect.getType() == EffectType.FREEZE) {
                return true;
            }
        }
        return false;
    }

    public boolean isSleeping() {
        return false;
    }

    public int getStage() {
        if (growthStage > 1) {
            return growthStage;
        }
        for (PlantAbility ability : abilities) {
            if (ability instanceof SunProductionAbility sunAbility) {
                return sunAbility.getCurrentGrowthStage();
            }
        }
        return Math.max(1, growthStage);
    }

    public void setGrowthStage(int growthStage) {
        this.growthStage = Math.max(1, growthStage);
    }

    public PlantGameplayPhase getGameplayPhase() {
        return gameplayPhase;
    }

    public void setGameplayPhase(PlantGameplayPhase gameplayPhase) {
        this.gameplayPhase = gameplayPhase == null ? PlantGameplayPhase.DEFAULT : gameplayPhase;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing < 0 ? -1 : 1;
    }

    public boolean facesLeft() {
        return facing < 0;
    }

    public void syncFrame(float interpolationAlpha) {
        if (abilities == null) {
            return;
        }
        for (PlantAbility ability : abilities) {
            if (ability != null) {
                ability.syncFrame(this, interpolationAlpha);
            }
        }
    }

    public void addFreezeLevel() {
        freezeController.addFreeze(this);
    }

    public void reduceFreezeLevel() {
        freezeController.reduceFreeze(1);
    }

    public int getFreezeLevel() {
        return freezeController.getFreezeLevel();
    }
}
