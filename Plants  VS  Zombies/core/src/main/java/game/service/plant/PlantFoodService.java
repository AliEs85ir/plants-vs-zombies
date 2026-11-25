package game.service.plant;

import app.controller.game.EventResult;
import app.service.AppService;
import app.enums.plant.PlantName;
import game.audio.AudioId;
import game.audio.Sfx;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.plant.ability.PlantAbilitySupport;
import game.entity.plantfood.PlantFoodInventory;
import game.entity.registry.PlantFoodDefinitionRegistry;
import game.world.GameWorld;
import game.world.playground.Cell;

public class PlantFoodService {
    private final GameWorld gameWorld;
    private final PlantFoodInventory inventory;

    public PlantFoodService(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.inventory = gameWorld.getPlantFoodInventory();
    }

    public PlantFoodInventory getInventory() {
        return inventory;
    }

    public void collectPlantFood() {
        if (inventory != null) {
            inventory.addPlantFood();
            AppService.markGameDataDirty();
        }
    }

    public EventResult validateTarget(Cell cell) {
        if (cell == null) {
            return EventResult.PLANT_FOOD_OUT_OF_BOUNDS;
        }
        Plant plant = cell.getCurrentPlant();
        if (plant == null || plant.isDead()) {
            return EventResult.PLANT_FOOD_NO_PLANT;
        }
        PlantName name = plant.getDefinition().getName();
        if (!name.hasPlantFoodEffect()) {
            return EventResult.PLANT_FOOD_NOT_SUPPORTED;
        }
        if (!PlantFoodDefinitionRegistry.isImplemented(name)) {
            return EventResult.PLANT_FOOD_NOT_IMPLEMENTED;
        }
        if (plant.getPlantFoodState().isActive()) {
            return EventResult.PLANT_FOOD_NOT_SUPPORTED;
        }
        return EventResult.PLANT_FOOD_SUCCESS;
    }

    public boolean canUsePlantFood(Plant plant) {
        if (plant == null || plant.isDead() || inventory == null || !inventory.hasPlantFood()) {
            return false;
        }
        return canActivatePlantFoodEffect(plant);
    }

    public boolean activatePlantFood(Plant plant) {
        if (!canUsePlantFood(plant)) {
            return false;
        }
        inventory.consumePlantFood();
        AppService.markGameDataDirty();
        applyPlantFoodEffect(plant, true);
        return true;
    }

    public boolean activateFromBoost(Plant plant) {
        return activateExistingEffect(plant, GameClock.currentTick);
    }


    public static boolean activateExistingEffect(Plant plant, long currentTick) {
        return activateExistingEffect(plant, currentTick, false);
    }

    public static boolean canActivatePlantFoodEffect(Plant plant) {
        if (plant == null || plant.isDead() || plant.getDefinition() == null) {
            return false;
        }
        PlantName name = plant.getDefinition().getName();
        return name != null
            && name.hasPlantFoodEffect()
            && PlantFoodDefinitionRegistry.isImplemented(name)
            && plant.getPlantFoodState() != null
            && !plant.getPlantFoodState().isActive();
    }

    private static boolean activateExistingEffect(Plant plant, long currentTick, boolean spawnPickupEffect) {
        if (!canActivatePlantFoodEffect(plant)) {
            return false;
        }
        plant.getPlantFoodState().activate(currentTick, 0);
        Sfx.play(AudioId.SFX_PLANTERN);
        Sfx.play(AudioId.SFX_LIGHTFILL);
        if (spawnPickupEffect && plant.getPosition() != null) {
            PlantAbilitySupport.spawnEffect("plantfood pickup", plant.getPosition());
        }
        return true;
    }

    private void applyPlantFoodEffect(Plant plant, boolean spawnPickupEffect) {
        activateExistingEffect(plant, GameClock.currentTick, spawnPickupEffect);
    }

    public void consumePlantFood() {
        if (inventory != null) {
            inventory.consumePlantFood();
        }
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }
}
