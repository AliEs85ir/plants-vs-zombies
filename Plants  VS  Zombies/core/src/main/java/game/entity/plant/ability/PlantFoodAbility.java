package game.entity.plant.ability;

import app.enums.plant.PlantName;
import game.entity.plant.Plant;
import game.service.plant.PlantService;

public class PlantFoodAbility extends AbstractPlantAbility{
    AttackPlantFood attackPlantFood;
    ExplodePlantFood explodePlantFood;
    MeleePlantFood meleePlantFood;
    WallNutPlantFood wallNutPlantFood;

    public PlantFoodAbility(long intervalTicks){
        attackPlantFood = new AttackPlantFood(intervalTicks);
        explodePlantFood = new ExplodePlantFood();
        meleePlantFood = new MeleePlantFood();
        wallNutPlantFood = new WallNutPlantFood();
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null) return;
        if (!plant.getPlantFoodState().isActive()) return;
        PlantName name = plant.getDefinition().getName();
        switch (name)
        {
            case SUNFLOWER -> sunflowerHandler(plant , plantService , currentTick);
            case TWIN_SUNFLOWER -> twinSunflowerHandler(plant , plantService , currentTick);
            case SUN_SHROOM -> sunShroomHandler(plant , plantService , currentTick);
            case PRIMAL_SUNFLOWER -> primalSunflowerHandler(plant , plantService , currentTick);

            // Attacker plants
            case PEASHOOTER, REPEATER, THREEPEATER, SNOW_PEA, FIRE_PEASHOOTER,
                 GOO_PEASHOOTER, MEGA_GATLING_PEA, ROTOBAGA, PEA_POD, SPLIT_PEA,
                 CITRON, BOWLING_BULB, STARFRUIT, SEA_SHROOM, PUFF_SHROOM,
                 CABBAGE_PULT, KERNEL_PULT, MELON_PULT, WINTER_MELON, PEPPER_PULT,
                 CACTUS, FUME_SHROOM ->
                    attackPlantFood.doUpdate(plant , plantService , currentTick);

            // Explode plants
            case SQUASH, EXPLODE_O_NUT ->
                    explodePlantFood.doUpdate(plant , plantService , currentTick);

            // Melee plants
            case PHAT_BEET, CHOMPER, WASABI_WHIP, KIWIBEAST ->
                    meleePlantFood.doUpdate(plant , plantService , currentTick);

            // Defensive / support plants
            case WALL_NUT, TALL_NUT, ENDURIAN, GARLIC, SWEET_POTATO,
                 PUMPKIN, SUN_BEAN ->
                    wallNutPlantFood.doUpdate(plant , plantService , currentTick);
        }
    }

    private void sunflowerHandler(Plant plant, PlantService plantService, long currentTick)
    {
        plantService.getSunSpawner().spawnPlantSun(plant , 150);
        plant.getPlantFoodState().deactivate();
    }

    private void twinSunflowerHandler(Plant plant, PlantService plantService, long currentTick)
    {
        plantService.getSunSpawner().spawnPlantSun(plant , 250);
        plant.getPlantFoodState().deactivate();
    }

    private void sunShroomHandler(Plant plant, PlantService plantService, long currentTick) {
    }

    private void primalSunflowerHandler(Plant plant, PlantService plantService, long currentTick)
    {
        plantService.getSunSpawner().spawnPlantSun(plant , 225);
        plant.getPlantFoodState().deactivate();
    }
}
