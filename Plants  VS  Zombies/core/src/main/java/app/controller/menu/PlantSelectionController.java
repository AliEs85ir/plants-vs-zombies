package app.controller.menu;

import app.controller.game.izombie.IZombieMatchContext;
import app.controller.game.izombie.IZombieNetworkController;
import app.controller.game.izombie.IZombieUiStages;
import app.enums.Menu;
import app.enums.plant.PlantName;
import app.model.App;
import app.model.game.PendingGameSession;
import app.service.plant.PlantBoostService;
import app.service.plantselection.PlantSelectionService;
import app.view.game.PlayScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import common.Result;
import game.engine.GameSimulationEngine;
import game.save.LevelSaveData;
import game.save.SaveService;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterWorldBuilder;
import game.world.minigame.izombie.IZombieMode;
import game.world.minigame.izombie.IZombieRules;
import game.world.minigame.vasebreaker.VaseBreakerMode;
import plants_vs_zombies.PlantsVSZombies;

public class PlantSelectionController {
    private static final PlantSelectionService plantSelectionService = new PlantSelectionService();
    public static Result showAllPlants() {
        return new Result(true, plantSelectionService.showAllPlants());
    }

    public static Result showAvailablePlants() {
        return new Result(true,  plantSelectionService.showAvailablePlants());
    }

    public static Result addPlant(String plantName) {
        if (plantName == null || plantName.trim().isEmpty()) {
            return new Result(false, "Plant type cannot be empty.");
        }

        PlantName name = PlantName.parsePlantName(plantName);
        if (!plantSelectionService.isPlantInRegistry(name)) return new Result(false, "Unknown plant.");

        //if (!App.getCurrentUser().isUnlocked(name)) return new Result(false, "Plant locked.");

//        if (!plantSelectionService.isPlantAvailableInGame(name))
//            return new Result(false , "It is not possible to select this plant at this chapter.");

//        if (plantSelectionService.isPlantAlreadySelected(name))
//            return new Result(false ,"You just added this plant to your list.");
//        int size = App.getPendingGameSession().getSelectedPlants().size();

        Chapter currentChapter = App.getPendingGameSession().getSelectedChapter();
//        if(currentChapter != null && currentChapter.getMaxPlantsAvailable() <= size) {
//            return new Result(false, "Max inventory capacity reached!");
//        }

        App.getPendingGameSession().addPlant(name);
        return new Result(true, plantName + " added.");
    }

    public static Result removePlant(String plantName) {
        if (plantName == null || plantName.trim().isEmpty()) {
            return new Result(false, "Plant type cannot be empty.");
        }

        PlantName name = PlantName.parsePlantName(plantName);
        if (!plantSelectionService.isPlantInRegistry(name)) return new Result(false, "Unknown plant.");

        if (!plantSelectionService.isPlantAlreadySelected(name))
            return new Result(false ,"Such a plant is not added to your current list.");

        App.getPendingGameSession().getSelectedPlants().remove(name);
        return new Result(true, "Plant " + plantName.trim() + " removed from selection.");
    }

    public static Result boostPlant(String plantName) {
        if (plantName == null || plantName.trim().isEmpty()) {
            return new Result(false, "Plant type cannot be empty.");
        }

        PlantName name = PlantName.parsePlantName(plantName);
        if (!plantSelectionService.isPlantInRegistry(name)) return new Result(false, "Unknown plant.");

        return PlantBoostService.tryBoost(name);
    }

    public static Result startGame() {
        PendingGameSession session = App.getPendingGameSession();
        Chapter chapter = session.getSelectedChapter();
        if (chapter == null) {
            return new Result(false, "No chapter selected.");
        }
        if (IZombieRules.needsMatchmaking(chapter)) {
            IZombieNetworkController controller = IZombieNetworkController.get();
            Stage stage = IZombieUiStages.current();
            if (controller != null && stage != null) {
                controller.openMatchPopup(stage, IZombieRules.queueChapterName(chapter), () -> {});
                return Result.success(IZombieRules.PENDING_MATCHMAKING);
            }
        }

        applyPendingRestoreSelection(session);

        GameWorld world = ChapterWorldBuilder.buildWorld(chapter);
        IZombieMatchContext match = IZombieMatchContext.get();
        if (match != null && IZombieRules.matches(chapter)) {
            IZombieRules.applyLayoutSeed(chapter, match.layoutSeed());
        }
        world.loadSelectedGamePlants(session.getSelectedPlants());
        GameSimulationEngine engine = new GameSimulationEngine(world);
        App.setCurrentGameEngine(engine);

        if(chapter.getMinigameLogic() instanceof VaseBreakerMode) {
            MenuNavigator.goTo(Menu.VaseBreakerMenu);
        }
        if(chapter.getMinigameLogic() instanceof IZombieMode) {
            MenuNavigator.goTo(Menu.IZombieMenu);
        }
        Gdx.app.postRunnable(() -> {
            PlayScreen screen = new PlayScreen(chapter);
            PlantsVSZombies.getPvz().setScreen(screen);
            System.out.println("I set the screen to play screen");
        });
        return new Result(true, "Game started!");
    }

    public static Result manageExit() {
        MenuNavigator.goTo(Menu.GameMenu);
        return new Result(true, "Exited from game successfully");
    }

    public static Result restartCurrentLevel() {
        PendingGameSession session = App.getPendingGameSession();
        if (session.getSelectedChapter() == null) {
            return new Result(false, "No chapter selected.");
        }
        SaveService.clearPendingRestore();
        return startGame();
    }

    private static void applyPendingRestoreSelection(PendingGameSession session) {
        LevelSaveData restore = SaveService.peekPendingRestore();
        if (restore == null || restore.getSelectedPlants() == null) {
            return;
        }
        session.getSelectedPlants().clear();
        for (String raw : restore.getSelectedPlants()) {
            PlantName name;
            try {
                name = PlantName.valueOf(raw);
            } catch (IllegalArgumentException ignored) {
                name = PlantName.parsePlantName(raw);
            }
            if (name != null) {
                session.addPlant(name);
            }
        }
    }
}
