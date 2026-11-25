package graphics.plant;

import app.model.App;
import game.entity.plant.Plant;
import graphics.GraphicsEngine;

public final class PlantDamageFlash {

    private PlantDamageFlash() {
    }

    public static void apply(Plant plant) {
        if (plant == null || App.getRealTimeEngine() == null) {
            return;
        }
        GraphicsEngine graphicsEngine = App.getRealTimeEngine().getGraphicsEngine();
        if (graphicsEngine == null) {
            return;
        }
        PlantRenderInstance instance = graphicsEngine.getPlantInstance(plant);
        if (instance != null) {
            instance.getDamageFlashController().notifyDamaged();
        }
    }
}
