package app.view;

import app.controller.menu.MenuController;
import app.model.App;
import game.entity.registry.PlantDefinitionLoader;
import game.entity.registry.PlantFoodDefinitionLoader;
import game.service.Zombie.ZombieInfoRegistry;
import game.service.Zombie.ZombieJsonParserService;
import game.service.plant.PlantInfoRegistry;
import game.world.chapter.meta.ChapterMetadataRegistry;

public class AppLoop {
    private static Thread consoleThread;

    public static void loadGameData() {
        PlantDefinitionLoader.load();
        PlantFoodDefinitionLoader.load();
        ZombieJsonParserService.loadZombiesFromJson("zombies.json");
        ZombieJsonParserService.loadArmorsFromJson("armor.json");
        PlantInfoRegistry.load();
        ZombieInfoRegistry.load();
        ChapterMetadataRegistry.load();
    }

    public static synchronized void startConsoleThread() {
        if (consoleThread != null) {
            return;
        }
        consoleThread = new Thread(() -> {
            while (true) {
                AppMenu menu = MenuController.getMenu(App.getCurrentMenu());
                if (menu != null) {
                    menu.checkCommand();
                }
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

}
