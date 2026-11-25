package game.service.plant;

import app.enums.plant.PlantName;
import game.entity.plant.HealthSegments;
import game.entity.plant.Plant;
import game.entity.plant.PlantStackLayer;
import game.entity.plant.ability.ExplodeAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.ability.WallNutAbility;
import game.entity.plant.ability.plantfood.HealPlantFoodAbility;
import game.entity.plant.plantfood.PlantFoodAbilityType;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.world.playground.Cell;
import game.world.playground.celltypes.WaterCellType;
import graphics.plant.PlantConditions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PumpkinAbilityWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void pumpkinIsAShieldLayerWithHealPlantFood() {
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.PUMPKIN);
        assertTrue(abilities.stream().anyMatch(HealPlantFoodAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(WallNutAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(ExplodeAbility.class::isInstance));
        assertEquals(PlantStackLayer.SHIELD, PlantFactory.create(PlantName.PUMPKIN).getDefinition().getStackLayer());
        assertInstanceOf(HealPlantFoodAbility.class, PlantFoodAbilityFactory.create(PlantName.PUMPKIN));
    }

    @Test
    void plantFoodRestoresOnlyPumpkinHealth() {
        assertTrue(PlantFoodDefinitionRegistry.isImplemented(PlantName.PUMPKIN));
        PlantFoodDefinition definition = PlantFoodDefinitionRegistry.get(PlantName.PUMPKIN);
        assertNotNull(definition);
        assertEquals(PlantFoodAbilityType.HEAL, definition.getAbilityType());
        assertTrue(definition.isRestoreFullHealth());
        assertEquals(0, definition.getHealAmount());
        assertEquals(1, definition.getDurationTicks());

        Plant pumpkin = PlantFactory.create(PlantName.PUMPKIN);
        Plant pea = PlantFactory.create(PlantName.PEASHOOTER);
        int pumpkinMax = pumpkin.getHealth().getMaxHealth();
        double peaHealth = pea.getHealth().getCurrentHealth();
        pumpkin.getHealth().setCurrentHealth(pumpkinMax / 4.0d);
        pumpkin.heal(pumpkinMax);
        assertEquals(pumpkinMax, pumpkin.getHealth().getCurrentHealth(), 0.0001);
        assertEquals(pumpkinMax, pumpkin.getHealth().getMaxHealth());
        assertEquals(peaHealth, pea.getHealth().getCurrentHealth(), 0.0001);
        assertTrue(PlantConditions.healthSegment(0, HealthSegments.THIRDS).matches(pumpkin));
    }

    @Test
    void healthThirdsDriveIdleClipsFromEffectiveMaxHealth() {
        Plant plant = PlantFactory.create(PlantName.PUMPKIN);
        int max = plant.getHealth().getMaxHealth();
        assertTrue(PlantConditions.healthSegment(0, HealthSegments.THIRDS).matches(plant));

        plant.getHealth().setCurrentHealth(max * 2.0d / 3.0d);
        assertTrue(PlantConditions.healthSegment(1, HealthSegments.THIRDS).matches(plant));

        plant.getHealth().setCurrentHealth(max / 3.0d);
        assertTrue(PlantConditions.healthSegment(2, HealthSegments.THIRDS).matches(plant));
    }

    @Test
    void pumpkinStacksOnLandCoreAndBecomesDamageTarget() {
        Cell cell = landCell();
        Plant pea = PlantFactory.create(PlantName.PEASHOOTER);
        Plant pumpkin = PlantFactory.create(PlantName.PUMPKIN);

        assertTrue(PlantPlacementPolicy.canPlace(cell, pea.getDefinition()));
        cell.addPlant(pea);
        assertTrue(PlantPlacementPolicy.canPlace(cell, pumpkin.getDefinition()));
        cell.addPlant(pumpkin);

        assertSame(pumpkin, cell.getDamageTarget());
        assertSame(pumpkin, cell.getCurrentPlant());
        assertEquals(0, cell.getPlants().indexOf(pea));
        assertEquals(1, cell.getPlants().indexOf(pumpkin));
        assertFalse(PlantPlacementPolicy.canPlace(cell, PlantFactory.create(PlantName.PUMPKIN).getDefinition()));
    }

    @Test
    void pumpkinCannotBePlacedAloneOrOnWater() {
        Cell emptyLand = landCell();
        assertFalse(PlantPlacementPolicy.canPlace(emptyLand, PlantFactory.create(PlantName.PUMPKIN).getDefinition()));

        Cell water = landCell();
        water.setCellType(new WaterCellType());
        Plant lily = PlantFactory.create(PlantName.LILY_PAD);
        Plant pea = PlantFactory.create(PlantName.PEASHOOTER);
        water.addPlant(lily);
        water.addPlant(pea);
        assertFalse(PlantPlacementPolicy.canPlace(water, PlantFactory.create(PlantName.PUMPKIN).getDefinition()));
    }

    @Test
    void pumpkinStacksOnAnyLandCoreNotOnlyPeashooter() {
        Cell cell = landCell();
        Plant wallNut = PlantFactory.create(PlantName.WALL_NUT);
        Plant pumpkin = PlantFactory.create(PlantName.PUMPKIN);
        cell.addPlant(wallNut);
        assertTrue(PlantPlacementPolicy.canPlace(cell, pumpkin.getDefinition()));
        cell.addPlant(pumpkin);
        assertSame(pumpkin, cell.getDamageTarget());
        assertEquals(0, cell.getPlants().indexOf(wallNut));
        assertEquals(1, cell.getPlants().indexOf(pumpkin));
    }

    @Test
    void corePlantedAfterShieldStillRendersUnderPumpkin() {
        Cell cell = landCell();
        Plant pumpkin = PlantFactory.create(PlantName.PUMPKIN);
        Plant pea = PlantFactory.create(PlantName.PEASHOOTER);
        cell.addPlant(pumpkin);
        cell.addPlant(pea);

        assertEquals(0, cell.getPlants().indexOf(pea));
        assertEquals(1, cell.getPlants().indexOf(pumpkin));
        assertSame(pumpkin, cell.getDamageTarget());
    }

    @Test
    void pumpkinDeathReturnsDamageTargetToBasePlant() {
        Cell cell = landCell();
        Plant pea = PlantFactory.create(PlantName.PEASHOOTER);
        Plant pumpkin = PlantFactory.create(PlantName.PUMPKIN);
        cell.addPlant(pea);
        cell.addPlant(pumpkin);
        assertSame(pumpkin, cell.getDamageTarget());

        pumpkin.getHealth().die();
        assertSame(pea, cell.getDamageTarget());
        cell.removePlant(pumpkin);
        assertSame(pea, cell.getDamageTarget());
        assertEquals(pea.getHealth().getMaxHealth(), pea.getHealth().getCurrentHealth(), 0.0001);
    }

    @Test
    void zombieDamageHitsPumpkinInsteadOfBasePlant() {
        Cell cell = landCell();
        Plant pea = PlantFactory.create(PlantName.PEASHOOTER);
        Plant pumpkin = PlantFactory.create(PlantName.PUMPKIN);
        cell.addPlant(pea);
        cell.addPlant(pumpkin);

        double peaBefore = pea.getHealth().getCurrentHealth();
        double pumpkinBefore = pumpkin.getHealth().getCurrentHealth();
        pea.takeDamage(250, null, 0);

        assertEquals(peaBefore, pea.getHealth().getCurrentHealth(), 0.0001);
        assertEquals(pumpkinBefore - 250, pumpkin.getHealth().getCurrentHealth(), 0.0001);
    }

    private static Cell landCell() {
        return new Cell(2, 3, 0f, 0f, 80f, 80f);
    }
}
