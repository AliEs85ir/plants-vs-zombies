package game.entity.plant.ability;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.plant.PlantState;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.service.plant.AbilityFactory;
import game.service.plant.PlantFactory;
import game.service.plant.PlantFoodService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MintEmpowermentAbilityTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
    }

    @Test
    void tickRateIsTenAndLifetimeIsFortyTicks() {
        assertEquals(10, GameClock.TICKS_PER_SECOND);
        assertEquals(40, MintEmpowermentAbility.MINT_DURATION_TICKS);
        Plant mint = plant(PlantName.ENLIGHTEN_MINT);
        assertEquals(40, new MintEmpowermentAbility().lifetimeTicks(mint));
    }

    @Test
    void allNineMintsUseTheSameAbilityClass() {
        for (PlantName name : List.of(
            PlantName.ENLIGHTEN_MINT,
            PlantName.APPEASE_MINT,
            PlantName.ARMA_MINT,
            PlantName.BOMBARD_MINT,
            PlantName.ENFORCE_MINT,
            PlantName.REINFORCE_MINT,
            PlantName.ENCHANT_MINT,
            PlantName.PIERCE_MINT,
            PlantName.CAT_TAIL_MINT
        )) {
            boolean found = false;
            for (PlantAbility ability : AbilityFactory.getAbilities(name)) {
                if (ability instanceof MintEmpowermentAbility) {
                    found = true;
                }
            }
            assertTrue(found, name + " must register MintEmpowermentAbility");
        }
    }

    @Test
    void targetCategoryComesFromMintDefinition() {
        assertEquals(PlantCategory.SUN_PRODUCER, MintEmpowermentAbility.empoweredCategory(plant(PlantName.ENLIGHTEN_MINT)));
        assertEquals(PlantCategory.SHOOTER, MintEmpowermentAbility.empoweredCategory(plant(PlantName.APPEASE_MINT)));
        assertEquals(PlantCategory.LOBBER, MintEmpowermentAbility.empoweredCategory(plant(PlantName.ARMA_MINT)));
        assertEquals(PlantCategory.EXPLOSIVE, MintEmpowermentAbility.empoweredCategory(plant(PlantName.BOMBARD_MINT)));
        assertEquals(PlantCategory.MELEE, MintEmpowermentAbility.empoweredCategory(plant(PlantName.ENFORCE_MINT)));
        assertEquals(PlantCategory.WALL_NUT, MintEmpowermentAbility.empoweredCategory(plant(PlantName.REINFORCE_MINT)));
        assertEquals(PlantCategory.MODIFIER, MintEmpowermentAbility.empoweredCategory(plant(PlantName.ENCHANT_MINT)));
        assertEquals(PlantCategory.STRIKE_THROUGH, MintEmpowermentAbility.empoweredCategory(plant(PlantName.PIERCE_MINT)));
        assertEquals(PlantCategory.HOMING, MintEmpowermentAbility.empoweredCategory(plant(PlantName.CAT_TAIL_MINT)));
    }

    @Test
    void enlightenEmpowersSunProducersAndNotShooters() {
        GameWorld world = new GameWorld();
        Plant sunflower = plant(PlantName.SUNFLOWER);
        Plant twin = plant(PlantName.TWIN_SUNFLOWER);
        Plant primal = plant(PlantName.PRIMAL_SUNFLOWER);
        Plant peashooter = plant(PlantName.PEASHOOTER);
        world.addPlant(sunflower);
        world.addPlant(twin);
        world.addPlant(primal);
        world.addPlant(peashooter);

        Plant mint = mintOn(world, PlantName.ENLIGHTEN_MINT);
        MintEmpowermentAbility ability = abilityOf(mint);
        ability.update(mint, new PlantService(world), 0);

        assertTrue(ability.hasEmpowered());
        assertEquals(3, ability.getSnapshotSize());
        assertTrue(sunflower.getPlantFoodState().isActive());
        assertTrue(twin.getPlantFoodState().isActive());
        assertTrue(primal.getPlantFoodState().isActive());
        assertFalse(peashooter.getPlantFoodState().isActive());
        assertEquals(MintEmpowermentAbility.Phase.INTRO, ability.getPhase());
    }

    @Test
    void appeaseEmpowersShootersOnly() {
        GameWorld world = new GameWorld();
        Plant peashooter = plant(PlantName.PEASHOOTER);
        Plant repeater = plant(PlantName.REPEATER);
        Plant sunflower = plant(PlantName.SUNFLOWER);
        world.addPlant(peashooter);
        world.addPlant(repeater);
        world.addPlant(sunflower);

        Plant mint = mintOn(world, PlantName.APPEASE_MINT);
        abilityOf(mint).update(mint, new PlantService(world), 5);

        assertTrue(peashooter.getPlantFoodState().isActive());
        assertTrue(repeater.getPlantFoodState().isActive());
        assertFalse(sunflower.getPlantFoodState().isActive());
    }

    @Test
    void emptyBoardDoesNotCrash() {
        GameWorld world = new GameWorld();
        Plant mint = mintOn(world, PlantName.ENLIGHTEN_MINT);
        MintEmpowermentAbility ability = abilityOf(mint);
        ability.update(mint, new PlantService(world), 0);
        assertTrue(ability.hasEmpowered());
        assertEquals(0, ability.getSnapshotSize());
        assertEquals(MintEmpowermentAbility.Phase.INTRO, ability.getPhase());
    }

    @Test
    void empowersExactlyOnceEvenOnDoubleUpdate() {
        GameWorld world = new GameWorld();
        Plant sunflower = plant(PlantName.SUNFLOWER);
        world.addPlant(sunflower);
        Plant mint = mintOn(world, PlantName.ENLIGHTEN_MINT);
        MintEmpowermentAbility ability = abilityOf(mint);
        PlantService service = new PlantService(world);

        ability.update(mint, service, 0);
        assertTrue(sunflower.getPlantFoodState().isActive());
        sunflower.getPlantFoodState().deactivate();
        ability.update(mint, service, 0);
        ability.update(mint, service, 1);
        assertFalse(sunflower.getPlantFoodState().isActive());
        assertTrue(ability.hasEmpowered());
    }

    @Test
    void plantsPlacedAfterMintAreNotEmpowered() {
        GameWorld world = new GameWorld();
        Plant first = plant(PlantName.SUNFLOWER);
        world.addPlant(first);
        Plant mint = mintOn(world, PlantName.ENLIGHTEN_MINT);
        PlantService service = new PlantService(world);
        abilityOf(mint).update(mint, service, 0);
        assertTrue(first.getPlantFoodState().isActive());

        Plant late = plant(PlantName.SUNFLOWER);
        world.addPlant(late);
        abilityOf(mint).update(mint, service, 1);
        assertFalse(late.getPlantFoodState().isActive());
    }

    @Test
    void deadPlantsAreNotTargeted() {
        GameWorld world = new GameWorld();
        Plant live = plant(PlantName.SUNFLOWER);
        Plant dead = plant(PlantName.SUNFLOWER);
        dead.getHealth().die();
        dead.setState(PlantState.DEAD);
        world.addPlant(live);
        world.addPlant(dead);

        Plant mint = mintOn(world, PlantName.ENLIGHTEN_MINT);
        MintEmpowermentAbility ability = abilityOf(mint);
        ability.update(mint, new PlantService(world), 0);
        assertEquals(1, ability.getSnapshotSize());
        assertTrue(live.getPlantFoodState().isActive());
        assertFalse(dead.getPlantFoodState().isActive());
    }

    @Test
    void plantFoodPathMatchesManualActivation() {
        GameWorld world = new GameWorld();
        Plant fromMint = plant(PlantName.SUNFLOWER);
        Plant fromManual = plant(PlantName.SUNFLOWER);
        world.addPlant(fromMint);
        Plant mint = mintOn(world, PlantName.ENLIGHTEN_MINT);

        abilityOf(mint).update(mint, new PlantService(world), 7);
        assertTrue(PlantFoodService.activateExistingEffect(fromManual, 7));

        assertTrue(fromMint.getPlantFoodState().isActive());
        assertTrue(fromManual.getPlantFoodState().isActive());
        assertEquals(fromManual.getPlantFoodState().getStartTick(), fromMint.getPlantFoodState().getStartTick());
        assertEquals(fromManual.getPlantFoodState().getDurationTicks(), fromMint.getPlantFoodState().getDurationTicks());
        assertFalse(PlantFoodService.activateExistingEffect(fromMint, 8));
    }

    @Test
    void lifecycleIntroLoopOutroRemovedAroundFortyTicks() {
        GameWorld world = new GameWorld();
        Plant mint = mintOn(world, PlantName.ENLIGHTEN_MINT);
        MintEmpowermentAbility ability = abilityOf(mint);
        PlantService service = new PlantService(world);

        ability.update(mint, service, 0);
        assertEquals(MintEmpowermentAbility.Phase.INTRO, ability.getPhase());
        assertFalse(ability.isFinished());

        ability.update(mint, service, 1);
        assertEquals(MintEmpowermentAbility.Phase.LOOP, ability.getPhase());

        ability.update(mint, service, MintEmpowermentAbility.MINT_DURATION_TICKS - 1);
        assertEquals(MintEmpowermentAbility.Phase.LOOP, ability.getPhase());
        assertFalse(ability.isFinished());

        ability.update(mint, service, MintEmpowermentAbility.MINT_DURATION_TICKS);
        assertEquals(MintEmpowermentAbility.Phase.OUTRO, ability.getPhase());
        assertFalse(ability.isFinished());

        ability.update(mint, service, MintEmpowermentAbility.MINT_DURATION_TICKS + 1);
        assertEquals(MintEmpowermentAbility.Phase.REMOVED, ability.getPhase());
        assertTrue(ability.isFinished());
    }

    @Test
    void multipleMintsAreIndependent() {
        GameWorld world = new GameWorld();
        Plant sunflower = plant(PlantName.SUNFLOWER);
        Plant peashooter = plant(PlantName.PEASHOOTER);
        world.addPlant(sunflower);
        world.addPlant(peashooter);

        Plant enlighten = mintOn(world, PlantName.ENLIGHTEN_MINT);
        Plant appease = mintOn(world, PlantName.APPEASE_MINT);
        PlantService service = new PlantService(world);
        abilityOf(enlighten).update(enlighten, service, 0);
        abilityOf(appease).update(appease, service, 0);

        assertTrue(sunflower.getPlantFoodState().isActive());
        assertTrue(peashooter.getPlantFoodState().isActive());
        assertEquals(1, abilityOf(enlighten).getSnapshotSize());
        assertEquals(1, abilityOf(appease).getSnapshotSize());
    }

    @Test
    void deathDuringIntroDoesNotEmpowerOrCorrupt() {
        GameWorld world = new GameWorld();
        Plant sunflower = plant(PlantName.SUNFLOWER);
        world.addPlant(sunflower);
        Plant mint = mintOn(world, PlantName.ENLIGHTEN_MINT);
        MintEmpowermentAbility ability = abilityOf(mint);
        mint.getHealth().die();
        mint.setState(PlantState.DEAD);

        ability.update(mint, new PlantService(world), 0);
        assertFalse(ability.hasEmpowered());
        assertFalse(sunflower.getPlantFoodState().isActive());
        assertTrue(ability.isFinished());
        assertEquals(MintEmpowermentAbility.Phase.REMOVED, ability.getPhase());
    }

    private static Plant plant(PlantName name) {
        return PlantFactory.create(name);
    }

    private static Plant mintOn(GameWorld world, PlantName name) {
        Plant mint = plant(name);
        MintEmpowermentAbility ability = new MintEmpowermentAbility();
        mint.setAbilities(new ArrayList<>(List.of(ability)));
        mint.setPlantedTick(0);
        world.addPlant(mint);
        return mint;
    }

    private static MintEmpowermentAbility abilityOf(Plant plant) {
        return assertInstanceOf(MintEmpowermentAbility.class, plant.getAbilities().get(0));
    }
}
