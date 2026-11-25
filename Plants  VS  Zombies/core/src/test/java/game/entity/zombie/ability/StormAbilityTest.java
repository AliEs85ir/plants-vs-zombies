package game.entity.zombie.ability;

import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.plant.PlantTag;
import common.model.Position;
import game.entity.plant.Plant;
import game.entity.plant.PlantDefinition;
import game.entity.plant.PlantHealth;
import game.entity.plant.PlantTags;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StormAbilityTest {
    @Test
    void infernoStormIsFasterThanSandStorm() {
        assertEquals(3.75, InfernoStormAbility.SPEED_MULTIPLIER, 0.001);
    }

    @Test
    void infernoStormDestroysPlantsThatAreNotFireTagged() {
        Plant peashooter = plantWithTag(PlantName.PEASHOOTER, null);
        Plant firePeashooter = plantWithTag(PlantName.FIRE_PEASHOOTER, PlantTag.FIRE);

        assertTrue(InfernoStormAbility.shouldDestroyPlant(peashooter));
        assertFalse(InfernoStormAbility.shouldDestroyPlant(firePeashooter));
        assertFalse(InfernoStormAbility.shouldDestroyPlant(null));
    }

    @Test
    void snowStormAppliesThreeFreezeLevelsOnFirstContact() {
        Plant plant = livingPlant();
        Zombie zombie = zombieWithTarget(plant);
        SnowStormAbility ability = new SnowStormAbility();

        ability.applyReachedPlant(zombie);

        assertTrue(plant.isFrozen());
        assertFalse(plant.isCanAct());
        assertEquals(1, plant.getEffects().size());

        ability.applyReachedPlant(zombie);
        assertEquals(1, plant.getEffects().size());
    }

    private static Plant livingPlant() {
        Plant plant = new Plant();
        plant.setHealth(new PlantHealth(100));
        return plant;
    }

    private static Plant plantWithTag(PlantName name, PlantTag tag) {
        PlantTags tags = new PlantTags();
        if (tag != null) {
            tags.addTag(tag);
        }
        PlantDefinition definition = new PlantDefinition(
            name,
            PlantCategory.SHOOTER,
            0,
            100,
            0,
            tags,
            List.of(),
            0,
            1f
        );
        Plant plant = livingPlant();
        plant.setDefinition(definition);
        return plant;
    }

    private static Zombie zombieWithTarget(Plant plant) {
        return new Zombie(
            10,
            null,
            null,
            null,
            new Position(0, 0),
            new ArrayList<>(),
            new GameWorld(),
            plant,
            null
        );
    }
}
