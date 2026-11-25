package game.service.plant;

import app.controller.game.EventResult;
import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import app.enums.plant.PlantTag;
import app.view.hud.config.HudLayoutConfig;
import com.badlogic.gdx.graphics.Color;
import common.model.Position;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.plant.PlantDefinition;
import game.entity.plant.ability.GraveBusterAbility;
import game.entity.plant.ability.PlantAbility;
import game.entity.plant.upgrade.PlantUpgradeConfigLoader;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantRegistry;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.celltypes.GrassCellType;
import game.world.playground.celltypes.WaterCellType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraveBusterWiringTest {
    @BeforeAll
    static void loadDefinitions() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        PlantUpgradeConfigLoader.get();
    }

    @Test
    void wiresGraveTagAbilityAndNoPlantFood() {
        PlantDefinition definition = PlantRegistry.get(PlantName.GRAVE_BUSTER);
        assertTrue(definition.hasTag(PlantTag.GRAVE));
        assertTrue(PlantPlacementPolicy.isGraveBuster(definition));
        ArrayList<PlantAbility> abilities = AbilityFactory.getAbilities(PlantName.GRAVE_BUSTER);
        assertTrue(abilities.stream().anyMatch(GraveBusterAbility.class::isInstance));
        assertFalse(abilities.stream().anyMatch(ability -> ability == null));
        assertFalse(PlantName.GRAVE_BUSTER.hasPlantFoodEffect());
    }

    @Test
    void placementValidOnlyOnGameplayGrave() {
        PlantDefinition graveBuster = PlantRegistry.get(PlantName.GRAVE_BUSTER);
        PlantDefinition peashooter = PlantRegistry.get(PlantName.PEASHOOTER);

        Cell empty = grass();
        assertFalse(PlantPlacementPolicy.canPlace(empty, graveBuster));
        assertFalse(PlantPlacementPolicy.isValidGraveBusterPlacement(empty));
        assertTrue(PlantPlacementPolicy.canPlace(empty, peashooter));

        Cell water = grass();
        water.setCellType(new WaterCellType());
        assertFalse(PlantPlacementPolicy.canPlace(water, graveBuster));

        Cell occupied = grass();
        occupied.addPlant(PlantFactory.create(PlantName.PEASHOOTER));
        assertFalse(PlantPlacementPolicy.canPlace(occupied, graveBuster));

        Cell iceBlock = grass();
        iceBlock.setObstacle(obstacle(ObstacleType.ICE_BLOCK, 100));
        assertFalse(PlantPlacementPolicy.canPlace(iceBlock, graveBuster));
        assertFalse(PlantPlacementPolicy.canPlace(iceBlock, peashooter));

        Cell grave = grass();
        grave.setObstacle(obstacle(ObstacleType.EGYPT_GRAVE, 400));
        assertTrue(PlantPlacementPolicy.isValidGraveBusterPlacement(grave));
        assertTrue(PlantPlacementPolicy.canPlace(grave, graveBuster));
        assertFalse(PlantPlacementPolicy.canPlace(grave, peashooter));

        Cell tutorial = grass();
        tutorial.setObstacle(obstacle(ObstacleType.TUTORIAL_GRAVE, 400));
        assertTrue(PlantPlacementPolicy.canPlace(tutorial, graveBuster));

        grave.addPlant(PlantFactory.create(PlantName.GRAVE_BUSTER));
        assertFalse(PlantPlacementPolicy.canPlace(grave, graveBuster));
    }

    @Test
    void hoverAndClickShareValidatorAndGraveErrorMessage() {
        PlantDefinition graveBuster = PlantRegistry.get(PlantName.GRAVE_BUSTER);
        PlantDefinition peashooter = PlantRegistry.get(PlantName.PEASHOOTER);
        GameWorld world = new GameWorld();
        world.loadSelectedGamePlants(List.of(PlantName.GRAVE_BUSTER, PlantName.PEASHOOTER));
        PlantPlacementValidator validator = new PlantPlacementValidator(world);

        Cell grave = grass();
        grave.setObstacle(obstacle(ObstacleType.EGYPT_GRAVE, 400));
        Cell empty = grass();

        assertEquals(validator.canPlant(PlantName.GRAVE_BUSTER, grave),
            PlantPlacementPolicy.canPlace(grave, graveBuster));
        assertEquals(validator.canPlant(PlantName.GRAVE_BUSTER, empty),
            PlantPlacementPolicy.canPlace(empty, graveBuster));
        assertTrue(validator.canPlant(PlantName.GRAVE_BUSTER, grave));
        assertFalse(validator.canPlant(PlantName.GRAVE_BUSTER, empty));
        assertEquals(EventResult.GRAVE_PLANT_REQUIRES_GRAVE, validator.validate(PlantName.GRAVE_BUSTER, empty));
        assertEquals("Grave Buster can only be planted on graves.",
            EventResult.GRAVE_PLANT_REQUIRES_GRAVE.getMessage());

        assertTrue(validator.canPlant(PlantName.PEASHOOTER, empty));
        assertFalse(validator.canPlant(PlantName.PEASHOOTER, grave));
        assertEquals(EventResult.PLANT_PLACEMENT_CELL_OCCUPIED, validator.validate(PlantName.PEASHOOTER, grave));

        assertEquals(Color.WHITE, HudLayoutConfig.GRID_HOVER.validColor);
        Color invalid = HudLayoutConfig.GRID_HOVER.invalidColor;
        assertEquals(1f, invalid.r, 0.0001f);
        assertEquals(0.18f, invalid.g, 0.0001f);
        assertEquals(0.18f, invalid.b, 0.0001f);
        assertEquals(peashooter.hasTag(PlantTag.GRAVE), false);
    }

    private static Cell grass() {
        Cell cell = new Cell(0, 0, 0, 0, 80, 80);
        cell.setCellType(new GrassCellType());
        return cell;
    }

    private static Obstacle obstacle(ObstacleType type, double health) {
        Obstacle obstacle = new Obstacle(new Position(0, 0), type);
        obstacle.addAbility(new TakeDamageAbility(health));
        return obstacle;
    }
}
