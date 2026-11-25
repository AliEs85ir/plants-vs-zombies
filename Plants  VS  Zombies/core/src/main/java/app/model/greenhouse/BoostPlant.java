package app.model.greenhouse;

import app.enums.plant.PlantName;
import app.service.greenhouse.GreenhouseGameplaySettings;

import java.time.Duration;
import java.time.LocalDateTime;

public class BoostPlant extends GreenhousePlant
{
    private final PlantName plant;

    public BoostPlant(PlantName plant , LocalDateTime plantedAt) {
        this(plant, plantedAt, GreenhouseGameplaySettings.current().plantFoodGrowthDuration());
    }

    public BoostPlant(PlantName plant, LocalDateTime plantedAt, Duration growth) {
        super(app.enums.greenhouse.GreenhousePlantType.BOOST, plantedAt, growth);
        this.plant = plant;
    }

    public BoostPlant(PlantName plant, LocalDateTime plantedAt, LocalDateTime readyAt) {
        super(app.enums.greenhouse.GreenhousePlantType.BOOST, plantedAt, readyAt);
        this.plant = plant;
    }

    public PlantName getPlant() {
        return plant;
    }

    @Override
    public String getPlantName() {
        return plant.toString();
    }
}
