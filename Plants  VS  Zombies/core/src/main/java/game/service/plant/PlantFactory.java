package game.service.plant;

import app.enums.plant.PlantName;
import app.enums.plant.PlantState;
import app.model.App;
import com.badlogic.gdx.physics.box2d.Body;
import common.model.Position;
import game.component.physic.BodyFactory;
import game.entity.plant.*;
import game.entity.registry.PlantRegistry;
import game.world.playground.Cell;
import game.world.playground.MapConfig;

public final class PlantFactory
{
    private PlantFactory() {}

    public static Plant create(PlantName name)
    {
        Plant plant = new Plant();
        PlantDefinition plantDefinition = PlantRegistry.get(name);
        plant.setDefinition(plantDefinition);
        plant.setHealth(new PlantHealth(PlantUpgradeService.getEffectiveStats(name).maxHealth()));
        plant.setState(PlantState.ALIVE);
        return plant;
    }

    public static Plant create(PlantName name , Position position , long plantedTick , Cell cell )
    {
        Plant plant = new Plant();
        PlantDefinition plantDefinition = PlantRegistry.get(name);
        plant.setDefinition(plantDefinition);
        plant.setHealth(new PlantHealth(PlantUpgradeService.getEffectiveStats(name).maxHealth()));
        plant.setState(PlantState.ALIVE);
        plant.setPosition(position);
        plant.setPlantedTick(plantedTick);
        plant.setCell(cell);
        plant.setAbilities(AbilityFactory.getAbilities(name));

        MapConfig config = App.getCurrentGameEngine().getGameWorld().getGrid().getConfig();
        float size = 2.3f;
        //TODO: size?
        float bodyWidth = config.getCellWidth();
        float bodyHeight = config.getCellHeight();
        Body plantBody = BodyFactory.getInstance().createPlantBody(
            (float) position.getX(),
            (float) position.getY(),
            bodyWidth/size,
            bodyHeight/size
        );
        plantBody.setUserData(plant);
        plant.setBody(plantBody);

        return plant;
    }
}
