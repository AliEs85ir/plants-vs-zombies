package app.controller.menu;

import app.enums.plant.PlantName;
import app.model.App;
import app.model.Collection;
import app.model.user.User;
import app.service.plant.PlantUnlockPurchaseService;
import app.service.plantselection.PlantSelectionService;
import common.Result;
import game.entity.plant.Plant;
import game.entity.plant.PlantDefinition;
import game.entity.registry.PlantRegistry;
import game.entity.registry.ZombieRegistry;
import game.entity.zombie.Zombie;
import game.entity.zombie.data.properties.Property;
import game.service.plant.PlantUpgradeService;

public class CollectionMenuController {

    private Collection getCollection() {
        return App.getCurrentProgress().getCollection();
    }

    public Result showPlants() {
        StringBuilder output = new StringBuilder();
        for (PlantName plant : App.getCurrentUser().getUnlockedPlants()) {
            PlantDefinition definition = PlantRegistry.get(plant);
            output.append(definition.printInfo());
            output.append("\n");
        }
        return new Result(true, output.toString());
    }

    public Result showAllPlants() {
        StringBuilder output = new StringBuilder();
        PlantSelectionService service = new PlantSelectionService();
        return new Result(true, service.showAllPlants());
    }

    public Result showZombies() {
        StringBuilder output = new StringBuilder();
        for (Property zombie : getCollection().getSeenZombieProperties()) {
            output.append(zombie.getAlias());
            output.append("\n");
        }
        return new Result(true, output.toString());
    }

    public Result showAllZombies() {
        StringBuilder output = new StringBuilder();
        for (Property zombie : ZombieRegistry.getAllZombies()) {
            output.append(zombie.printInfo());
            output.append("\n");
        }
        return new Result(true, output.toString());
    }

    public Result showPlant(String plantName) {
        game.entity.plant.PlantDefinition definition = PlantRegistry.get(PlantName.parsePlantName(plantName));
        if (definition == null)
            return new Result(false, "Plant not found.");
        return new Result(true, definition.printInfo());
    }

    public Result showZombie(String zombieName) {
        game.entity.zombie.data.properties.Property property = ZombieRegistry.getZombie(zombieName);
        if (property == null)
            return new Result(false, "Zombie not found.");
        return new Result(true, property.printInfo());
    }


    public Result upgradePlant(String plantName) {
        PlantName name = PlantName.parsePlantName(plantName);
        if (name == null)
            return new Result(false, "Plant not found.");
        User user = App.getCurrentUser();
        return PlantUpgradeService.upgrade(user, name);
    }


    public Result purchasePlant(String plantName) {
        PlantName name = PlantName.parsePlantName(plantName);
        if (name == null)
            return new Result(false, "Plant not found");
        return PlantUnlockPurchaseService.purchase(name);
    }


    private String showPlantStats(Plant plant) {
        return plant.getDefinition().printInfo();
    }

    private String showZombieStats(Zombie zombie) {
        return zombie.printInfo();
    }

}
