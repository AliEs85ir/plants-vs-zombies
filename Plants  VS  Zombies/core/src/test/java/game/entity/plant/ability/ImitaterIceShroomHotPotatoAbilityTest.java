package game.entity.plant.ability;

import app.enums.plant.PlantName;
import game.entity.plant.Plant;
import game.entity.registry.PlantDefinitionLoader;
import game.service.plant.ImitaterReplacement;
import game.service.plant.PlantFactory;
import game.world.playground.Cell;
import game.world.playground.celltypes.GrassCellType;
import game.world.playground.celltypes.IceCellType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImitaterIceShroomHotPotatoAbilityTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
    }

    @Test
    void iceShroomArmsThenAttacksOnce() {
        Plant plant = PlantFactory.create(PlantName.ICE_SHROOM);
        plant.setPlantedTick(0);
        IceShroomAbility ability = new IceShroomAbility();
        plant.setAbilities(new ArrayList<>(List.of(ability)));

        ability.update(plant, null, 9);
        assertFalse(ability.hasStartedAttack());
        assertFalse(ability.hasExecutedAttack());

        ability.update(plant, null, 10);
        assertTrue(ability.hasStartedAttack());
        assertTrue(ability.hasExecutedAttack());

        ability.update(plant, null, 15);
        assertTrue(ability.hasExecutedAttack());
        assertFalse(ability.isFinished());

        ability.update(plant, null, 20);
        assertTrue(ability.isFinished());
    }

    @Test
    void hotPotatoMeltsOnlyItsCellAfterArmDelay() {
        Cell target = new Cell(1, 2, 0, 0, 80, 80);
        target.setCellType(new IceCellType());
        Cell neighbor = new Cell(1, 3, 80, 0, 80, 80);
        neighbor.setCellType(new IceCellType());

        Plant plant = PlantFactory.create(PlantName.HOT_POTATO);
        plant.setPlantedTick(0);
        plant.setCell(target);
        HotPotatoAbility ability = new HotPotatoAbility();
        plant.setAbilities(new ArrayList<>(List.of(ability)));

        ability.update(plant, null, 9);
        assertFalse(ability.hasMelted());
        assertTrue(target.getCellType() instanceof IceCellType);

        ability.update(plant, null, 10);
        assertTrue(ability.hasStartedAttack());
        assertTrue(ability.hasMelted());
        assertTrue(target.getCellType() instanceof GrassCellType);
        assertTrue(neighbor.getCellType() instanceof IceCellType);

        assertFalse(HotPotatoAbility.meltIceTerrain(target));
        ability.update(plant, null, 20);
        assertTrue(ability.isFinished());
    }

    @Test
    void imitaterReplacementIgnoresSelfAndSecondClickUntilTicksElapse() {
        Cell cell = new Cell(0, 0, 0, 0, 80, 80);
        Plant imitater = PlantFactory.create(PlantName.IMITATER);
        ImitaterAbility ability = new ImitaterAbility();
        imitater.setAbilities(new ArrayList<>(List.of(ability)));
        cell.setPlant(imitater);

        assertFalse(ImitaterReplacement.canBegin(PlantName.IMITATER, cell));
        assertTrue(ImitaterReplacement.canBegin(PlantName.PEASHOOTER, cell));

        assertTrue(ability.beginReplacement(PlantName.PEASHOOTER, 0));
        assertTrue(ability.isInProgress());
        assertFalse(ability.beginReplacement(PlantName.WALL_NUT, 1));
        assertEquals(PlantName.PEASHOOTER, ability.getReplacement());
        assertFalse(ImitaterReplacement.canBegin(PlantName.WALL_NUT, cell));

        ability.update(imitater, null, 13);
        assertTrue(ability.isInProgress());
        ability.update(imitater, null, 14);
        assertTrue(ability.isFinished());
    }

    @Test
    void imitaterDeathDuringTransformCancelsReplacement() {
        Plant imitater = PlantFactory.create(PlantName.IMITATER);
        ImitaterAbility ability = new ImitaterAbility();
        imitater.setAbilities(new ArrayList<>(List.of(ability)));
        assertTrue(ability.beginReplacement(PlantName.SUNFLOWER, 5));
        assertFalse(ability.handleDeath(imitater, null, 6));
        assertTrue(ability.isFinished());
        assertNull(ability.getReplacement());
    }
}
