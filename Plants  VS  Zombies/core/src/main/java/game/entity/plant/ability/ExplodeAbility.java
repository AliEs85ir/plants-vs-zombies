package game.entity.plant.ability;

import app.enums.plant.PlantName;
import app.model.App;
import game.entity.DeathStyle;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;

import java.util.List;

public class ExplodeAbility extends AbstractPlantAbility {
    public static final int EXPLOSION_CELL_RADIUS = AreaMeleeAbility.CELL_RADIUS_3X3;
    public static final float EXPLOSION_AREA_PERCENT = 0.07f;
    public static final String EXPLOSION_EFFECT_ID = "cherry_bomb_explosion";

    private AttackService attackService;
    private boolean detonated;

    public ExplodeAbility() {
        super();
    }

    public boolean hasDetonated() {
        return detonated;
    }

    public void setEnableExplodeONut(boolean enableExplodeONut) {
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || plant.getDefinition() == null) {
            return;
        }
        if (plant.getDefinition().getName() == PlantName.EXPLODE_O_NUT) {
            return;
        }
        if (!plant.isAlive()) {
            return;
        }
        GameWorld gameWorld = worldOf(plantService);
        if (gameWorld == null) {
            return;
        }
        if (attackService == null) {
            attackService = PlantAbilitySupport.attacks(gameWorld);
        }
    }

    @Override
    public boolean handleDeath(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || plant.getDefinition() == null) {
            return false;
        }
        if (plant.getDefinition().getName() != PlantName.EXPLODE_O_NUT) {
            return false;
        }
        detonate(plant, plantService, currentTick);
        return false;
    }

    private void detonate(Plant plant, PlantService plantService, long currentTick) {
        if (detonated) {
            return;
        }
        detonated = true;
        GameWorld world = worldOf(plantService);
        if (world == null) {
            return;
        }
        if (attackService == null) {
            attackService = PlantAbilitySupport.attacks(world);
        }
        Cell cell = PlantTargeting.cellOf(world, plant);
        PlantAbilitySupport.spawnEffect(EXPLOSION_EFFECT_ID, plant.getPosition());
        PlantAbilitySupport.notifyExploded(plant);
        if (cell == null) {
            return;
        }
        int damage = plant.getAttackDamage();
        List<Zombie> zombies = PlantTargeting.zombiesInOccupiedChebyshev(
            world,
            cell,
            EXPLOSION_CELL_RADIUS,
            EXPLOSION_AREA_PERCENT
        );
        for (Zombie zombie : zombies) {
            attackService.damageZombie(zombie, currentTick, damage, DeathStyle.ASH, plant);
        }
    }

    private static GameWorld worldOf(PlantService plantService) {
        if (plantService != null && plantService.getGameWorld() != null) {
            return plantService.getGameWorld();
        }
        if (App.getCurrentGameEngine() != null) {
            return App.getCurrentGameEngine().getGameWorld();
        }
        return null;
    }
}
