package game.entity.plant.ability;

import app.enums.plant.PlantName;
import app.enums.sun.SunState;
import app.model.App;
import game.audio.Sfx;
import game.entity.plant.Plant;
import game.entity.sun.PlantSun;
import game.service.plant.PlantService;
import game.service.plant.PlantUpgradeService;
import game.world.minigame.beghouled.BeghouledWorld;
import graphics.GraphicsEngine;
import graphics.general.event.PlantVisualEventType;
import graphics.general.event.VisualEvent;

public class SunProductionAbility extends AbstractPlantAbility {
    private final long productionDurationTicks;
    private int currentGrowthStage;
    private final long stageDurationTicks;
    private float stageDurationMultiplier;

    private PlantSun sunProduced;

    private ProductionState productionState;
    private long stateStartTick;

    private long goldBloomDelayTicks = 55;
    private boolean goldBloomProduced = false;

    public SunProductionAbility(long productionDurationTicks, long stageDurationTicks , float stageDurationMultiplier)
    {
        this.productionDurationTicks = productionDurationTicks;
        this.currentGrowthStage = 1;
        this.stageDurationTicks = stageDurationTicks;
        this.stageDurationMultiplier = stageDurationMultiplier;
        this.productionState = ProductionState.PRODUCING;
    }

    public SunProductionAbility(long productionDurationTicks) {
        this.productionDurationTicks = productionDurationTicks;
        this.currentGrowthStage = 1;
        this.stageDurationTicks = 0;
        this.productionState = ProductionState.PRODUCING;
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null ) return;
        if (plantService != null && plantService.getGameWorld() instanceof BeghouledWorld) {
            return;
        }
        PlantName name = plant.getDefinition().getName();
        if (name == PlantName.GOLD_BLOOM) goldBloomHandler(plant,plantService,currentTick); //!Important
        if (!plant.isAlive()) return;
        switch (name){
            case SUNFLOWER -> sunFlowerHandler(plant , currentTick,plantService);
            case TWIN_SUNFLOWER -> twinSunFlowerHandler(plant , currentTick,plantService);
            case SUN_SHROOM -> sunShroomHandler(plant , currentTick , plantService);
            case PRIMAL_SUNFLOWER -> primalSunFlowerHandler(plant , currentTick,plantService);
        }
    }

    private int calculateSunAmount(Plant plant) {
        PlantName name = plant.getDefinition().getName();
        int amount = switch (name) {
            case SUNFLOWER -> 50;
            case TWIN_SUNFLOWER -> 50;
            case PRIMAL_SUNFLOWER -> 75;
            case SUN_SHROOM -> currentGrowthStage * 25;
            case GOLD_BLOOM -> 375;
            default -> 0;
        };
        var modifiers = PlantUpgradeService.getModifiers(name);
        amount += modifiers.extraImmediateSun();
        if (modifiers.hasDoubleSunChance() && Math.random() < 0.5d) {
            amount *= 2;
        }
        return amount;
    }

    private void produceSun(Plant plant, PlantService plantService, int amount , long currentTick) {
        produceSun(plant, plantService, amount, currentTick, 0);
    }

    private void produceSun(Plant plant, PlantService plantService, int amount , long currentTick, int sourceIndex) {
        GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
        graphicsEngine.getPlantInstance(plant).pushEvent(new VisualEvent<>(PlantVisualEventType.ATTACK_TRIGGERED));

        App.getCurrentGameEngine().getTaskManager().scheduleRelative(currentTick , 10 ,
            ()-> produceSun(plant , amount , plantService, sourceIndex));

        productionState = ProductionState.WAITING_COLLECTION;
    }

    private void produceSun(Plant plant , int amount , PlantService plantService, int sourceIndex)
    {
        sunProduced = (PlantSun) plantService.getSunSpawner().spawnPlantSun(plant , amount, sourceIndex);
        Sfx.onEvent(plant);
    }


    private void sunFlowerHandler(Plant plant , long currentTick , PlantService plantService) {
        if (stateStartTick == 0) {
            stateStartTick = plant.getPlantedTick();
        }

        switch (productionState) {
            case WAITING_COLLECTION -> {
                if (sunProduced != null &&
                    sunProduced.getState() == SunState.COLLECTED) {

                    sunProduced = null;
                    productionState = ProductionState.PRODUCING;
                    stateStartTick = currentTick;
                }
            }

            case PRODUCING -> {
                long elapsed = currentTick - stateStartTick;
                if (elapsed >= productionDurationTicks) produceSun(plant , plantService , calculateSunAmount(plant) , currentTick);
            }
        }
    }

    private void twinSunFlowerHandler(Plant plant , long currentTick , PlantService plantService)
    {
        if (stateStartTick == 0) {
            stateStartTick = plant.getPlantedTick();
        }

        switch (productionState) {
            case WAITING_COLLECTION -> {
                if (sunProduced != null &&
                    sunProduced.getState() == SunState.COLLECTED) {

                    sunProduced = null;
                    productionState = ProductionState.PRODUCING;
                    stateStartTick = currentTick;
                }
            }

            case PRODUCING -> {
                long elapsed = currentTick - stateStartTick;
                if (elapsed >= productionDurationTicks)
                {
                    produceSun(plant , plantService , calculateSunAmount(plant) , currentTick, 0);
                    produceSun(plant , plantService , calculateSunAmount(plant) , currentTick, 1);
                }
            }
        }
    }

    private void sunShroomHandler(Plant plant, long currentTick, PlantService plantService) {
        long elapsedSincePlanted = currentTick - plant.getPlantedTick();
        if (currentGrowthStage < 3) {
            currentGrowthStage = getNewStage(plant , elapsedSincePlanted , plantService);
        } else currentGrowthStage = 3;

        sunFlowerHandler(plant, currentTick, plantService);
    }

    private int getNewStage(Plant plant , long elapsedSincePlanted , PlantService plantService)
    {
        int newStage = 1;
        if (elapsedSincePlanted > stageDurationTicks) {
            newStage = 2;
        }
        if (elapsedSincePlanted > stageDurationTicks + (long)(stageDurationTicks * stageDurationMultiplier)) {
            newStage = 3;
        }
        return newStage;
    }

    private void primalSunFlowerHandler(Plant plant, long currentTick, PlantService plantService)
    {
        sunFlowerHandler(plant, currentTick, plantService);
    }

    private void goldBloomHandler(Plant plant, PlantService plantService, long currentTick) {
        if (goldBloomProduced) return;

        if (stateStartTick == 0) {
            stateStartTick = plant.getPlantedTick();
        }

        long elapsed = currentTick - stateStartTick;

        if (elapsed >= goldBloomDelayTicks) {
            produceSun(plant, plantService, calculateSunAmount(plant), currentTick);
            goldBloomProduced = true;
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(currentTick , 10 ,()->plant.markDead(App.getCurrentGameEngine().getGameWorld()) );
        }
    }

    public long getProductionDurationTicks() {
        return productionDurationTicks;
    }

    public int getCurrentGrowthStage() {
        return currentGrowthStage;
    }

    public void forceGrowthStage(int stage) {
        this.currentGrowthStage = Math.max(1, Math.min(3, stage));
    }

    public long getStageDurationTicks() {
        return stageDurationTicks;
    }

    public float getStageDurationMultiplier() {
        return stageDurationMultiplier;
    }

    public PlantSun getSunProduced() {
        return sunProduced;
    }

    public ProductionState getProductionState() {
        return productionState;
    }

    public long getStateStartTick() {
        return stateStartTick;
    }

    public long getGoldBloomDelayTicks() {
        return goldBloomDelayTicks;
    }

    public void setGoldBloomDelayTicks(long goldBloomDelayTicks) {
        this.goldBloomDelayTicks = goldBloomDelayTicks;
    }

    @Override
    public void resetCycle(long currentTick) {
        super.resetCycle(currentTick);
        productionState = ProductionState.PRODUCING;
        stateStartTick = currentTick;
        sunProduced = null;
    }
}
