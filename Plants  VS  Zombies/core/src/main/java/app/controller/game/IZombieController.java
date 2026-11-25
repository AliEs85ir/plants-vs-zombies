package app.controller.game;


import app.controller.game.interaction.InteractionType;
import app.controller.game.interaction.hover.PlantingHoverHandler;
import app.controller.game.interaction.hover.ZombiePlacementHoverHandler;
import app.controller.game.izombie.IZombieMatchContext;
import app.enums.plant.PlantName;
import app.view.game.PlayScreen;
import game.service.Zombie.ZombiePlacementService;
import game.service.plant.IZombiePlantPlacementService;
import game.service.plant.PlantPlacementService;
import game.world.minigame.izombie.IZombieRules;
import game.world.minigame.izombie.IZombieSide;
import game.world.minigame.izombie.IZombieWorld;

public class IZombieController extends GameController {
    private PlantPlacementService plantPlacement;
    private PlantingHoverHandler plantingHoverHandler;
    private ZombiePlacementService zombiePlacement;
    private ZombiePlacementHoverHandler zombieHoverHandler;

    public IZombieController(PlayScreen screen) {
        super(screen);
        bindPlacement();
        hud.setForwardListener(this);
    }

    public IZombieController() {
        super();
    }

    @Override
    public void initialize(PlayScreen screen) {
        super.initialize(screen);
        bindPlacement();
        hud.setForwardListener(this);
    }

    private void bindPlacement() {
        this.plantPlacement = new IZombiePlantPlacementService(screen);
        this.plantingHoverHandler = new PlantingHoverHandler(plantPlacement);
        this.zombiePlacement = new ZombiePlacementService((IZombieWorld) world, engine);
        this.zombieHoverHandler = new ZombiePlacementHoverHandler(zombiePlacement);
    }

    private IZombieSide localSide() {
        return IZombieRules.localSide();
    }

    @Override
    protected boolean handleGameplayClick(float worldX, float worldY) {
        return tryCollectSun(worldX, worldY);
    }

    public void cancelPlacement() {
        if (interaction.is(InteractionType.SHOVEL) || interaction.is(InteractionType.PLANT_FOOD)) {
            return;
        }
        interaction.clear();
        hoveredCell = null;
        hud.deselectAllCards();
    }

    @Override
    public void onPlantCardSelected(PlantName plantName) {
        if (!localSide().isPlants()) {
            return;
        }
        if (plantName == null || interaction.blocksSeedSelection()) {
            return;
        }
        plantingHoverHandler.setPlantName(plantName);
        interaction.beginPlanting(plantName, plantingHoverHandler);
    }

    @Override
    public void onPlantCardDeselected(PlantName plantName) {
        if (!localSide().isPlants()) {
            return;
        }
        if (interaction.selectedPlant() == plantName) {
            cancelPlacement();
        }
    }

    @Override
    public void onZombieCardSelected(String alias) {
        if (!localSide().isZombies()) {
            return;
        }
        if (alias == null || interaction.blocksSeedSelection()) {
            return;
        }
        zombieHoverHandler.setAlias(alias);
        interaction.beginZombiePlacement(alias, zombieHoverHandler);
    }

    @Override
    public void onZombieCardDeselected(String alias) {
        if (!localSide().isZombies()) {
            return;
        }
        if (alias != null && alias.equals(interaction.selectedZombie())) {
            cancelPlacement();
        }
    }

    public void applyNetworkPlantPlacement(PlantName plantName, int row, int col) {
        if (plantPlacement == null || world == null || world.getGrid() == null || plantName == null) {
            return;
        }
        runRemote(() -> plantPlacement.placePlant(plantName, world.getGrid().getCell(row, col)));
    }

    public void applyNetworkZombiePlacement(String alias, int row, int col) {
        if (zombiePlacement == null || world == null || world.getGrid() == null) {
            return;
        }
        runRemote(() -> zombiePlacement.placeZombie(alias, world.getGrid().getCell(row, col)));
    }

    private void runRemote(Runnable action) {
        IZombieMatchContext context = IZombieMatchContext.get();
        if (context != null) {
            context.applyRemote(action);
            return;
        }
        action.run();
    }
}
