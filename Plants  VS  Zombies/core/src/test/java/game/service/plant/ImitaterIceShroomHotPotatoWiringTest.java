package game.service.plant;

import app.enums.plant.PlantName;
import app.enums.plant.PlantTag;
import game.engine.time.GameClock;
import game.entity.plant.PlantDefinition;
import game.entity.plant.ability.HotPotatoAbility;
import game.entity.plant.ability.IceShroomAbility;
import game.entity.plant.ability.ImitaterAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantRegistry;
import game.world.playground.Cell;
import game.world.playground.celltypes.GrassCellType;
import game.world.playground.celltypes.IceCellType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImitaterIceShroomHotPotatoWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void wiresDedicatedAbilitiesAndNoPlantFood() {
        ArrayList<PlantAbility> imitater = AbilityFactory.getAbilities(PlantName.IMITATER);
        assertTrue(imitater.stream().anyMatch(ImitaterAbility.class::isInstance));
        assertFalse(PlantName.IMITATER.hasPlantFoodEffect());

        ArrayList<PlantAbility> ice = AbilityFactory.getAbilities(PlantName.ICE_SHROOM);
        assertTrue(ice.stream().anyMatch(IceShroomAbility.class::isInstance));
        assertFalse(ice.stream().anyMatch(ability -> ability != null && ability.getClass().getSimpleName().equals("ExplodeAbility")));
        assertFalse(PlantName.ICE_SHROOM.hasPlantFoodEffect());

        ArrayList<PlantAbility> potato = AbilityFactory.getAbilities(PlantName.HOT_POTATO);
        assertTrue(potato.stream().anyMatch(HotPotatoAbility.class::isInstance));
        assertFalse(PlantName.HOT_POTATO.hasPlantFoodEffect());
    }

    @Test
    void tickDurationsMatchOneHundredMillisecondClock() {
        assertEquals(10, GameClock.TICKS_PER_SECOND);
        assertEquals(10, IceShroomAbility.ARM_DELAY_TICKS);
        assertEquals(10, IceShroomAbility.ATTACK_DURATION_TICKS);
        assertEquals(50, IceShroomAbility.FREEZE_DURATION_TICKS);
        assertEquals(10, HotPotatoAbility.ARM_DELAY_TICKS);
        assertEquals(10, HotPotatoAbility.ATTACK_DURATION_TICKS);
        assertEquals(14, ImitaterAbility.REPLACEMENT_DELAY_TICKS);
    }

    @Test
    void hotPotatoPlacesOnlyOnIceTerrain() {
        PlantDefinition hotPotato = PlantRegistry.get(PlantName.HOT_POTATO);
        assertTrue(hotPotato.hasTag(PlantTag.ICE_TERRAIN));
        Cell ice = cell();
        ice.setCellType(new IceCellType());
        assertTrue(PlantPlacementPolicy.canPlace(ice, hotPotato));

        Cell grass = cell();
        grass.setCellType(new GrassCellType());
        assertFalse(PlantPlacementPolicy.canPlace(grass, hotPotato));

        PlantDefinition peashooter = PlantRegistry.get(PlantName.PEASHOOTER);
        assertFalse(PlantPlacementPolicy.canPlace(ice, peashooter));
        assertTrue(PlantPlacementPolicy.canPlace(grass, peashooter));
    }

    private static Cell cell() {
        return new Cell(0, 0, 0, 0, 80, 80);
    }
}
