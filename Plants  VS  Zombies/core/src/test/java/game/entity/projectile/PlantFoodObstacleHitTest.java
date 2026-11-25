package game.entity.projectile;

import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import common.model.Position;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ability.TakeDamageAbility;
import game.entity.plant.Plant;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.service.plant.PlantFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PlantFoodObstacleHitTest {
    @BeforeAll
    static void load() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
    }

    @Test
    void unstoppablePlantFoodStillHitsObstacle() {
        Plant plant = PlantFactory.create(PlantName.CITRON);
        plant.setPosition(new Position(80, 40));
        ShooterProjectile projectile = new ShooterProjectile(plant, 5, 1.5, 0);
        projectile.setSpawnWorld(new Position(140, 40));
        projectile.applyCapabilities(
            PlantFoodDefinitionRegistry.get(PlantName.CITRON).getStreams().get(0).getCapabilities()
        );
        Obstacle obstacle = new Obstacle(new Position(200, 40), ObstacleType.EGYPT_GRAVE);
        obstacle.addAbility(new TakeDamageAbility(400));
        projectile.handleHit(obstacle, 0);
        assertTrue(projectile.getHitObstacles().contains(obstacle));
        assertTrue(projectile.isActive());
    }
}
