package game.entity.plant.ability;

import app.enums.plant.PlantName;
import app.model.App;
import game.entity.plant.Plant;
import game.service.plant.AttackService;
import game.service.plant.PlantService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;

public class ExplodePlantFood extends AbstractPlantAbility {

    private long squashDelayTicks = 10;
    private boolean triggered = false;
    private long triggerTick = -1;
    private int targetRow = -1;
    private int targetCol = -1;
    private boolean isEnableExplodeONut;
    private AttackService attackService;

    public ExplodePlantFood() {
        super();
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (plant == null || !plant.isAlive()) return;
        if (!plant.getPlantFoodState().isActive()) return;

        GameWorld gameWorld = App.getCurrentGameEngine().getGameWorld();
        if (this.attackService == null) {
            this.attackService = new AttackService(gameWorld);
        }

        PlantName name = plant.getDefinition().getName();

        switch (name) {
            case SQUASH -> squashHandler(plant, plantService, gameWorld, currentTick);
            case EXPLODE_O_NUT -> explodeONutHandler(plant, plantService, gameWorld, currentTick);
        }
    }

    private void explodeONutHandler(Plant plant, PlantService plantService, GameWorld gameWorld, long currentTick) {
        Grid grid = gameWorld.getGrid();
        Cell cell = grid.getCellAt(plant.getPosition());
        if (cell != null && (plant.getHealth().isDead() || isEnableExplodeONut)) {
            PlantAbilitySupport.notifyExploded(plant);
            attackService.damageArea(cell.getRow(), cell.getCol(), 1, plant.getAttackDamage(), currentTick);
            isEnableExplodeONut = false;
            plantService.removePlant(plant);
        }
    }

    private void squashHandler(Plant plant, PlantService plantService, GameWorld gameWorld, long currentTick) {
        Grid grid = gameWorld.getGrid();
        Cell cell = grid.getCellAt(plant.getPosition());
        if (cell == null) return;

        int row = cell.getRow();
        int col = cell.getCol();

        if (!triggered) {
            Cell ownCell = grid.getCell(row, col);
            Cell frontCell = grid.getCell(row, col + 1);

            if (ownCell != null && attackService.hasActiveZombieInCell(ownCell)) {
                startSquashTrigger(currentTick, row, col);
            } else if (frontCell != null && attackService.hasActiveZombieInCell(frontCell)) {
                startSquashTrigger(currentTick, row, col + 1);
            }
        } else {
            if (currentTick - triggerTick >= squashDelayTicks) {
                gameWorld.addEvent("Squash smashed all zombies in cell (" + targetRow + ", " + targetCol + ")");
                PlantAbilitySupport.notifyExploded(plant);
                attackService.damageZombiesInCell(targetRow, targetCol, plant.getAttackDamage(), currentTick);
                plantService.removePlant(plant);
            }
        }
    }

    private void startSquashTrigger(long currentTick, int row, int col) {
        this.triggered = true;
        this.triggerTick = currentTick;
        this.targetRow = row;
        this.targetCol = col;
    }

    public void setSquashDelayTicks(long ticks) { this.squashDelayTicks = ticks; }

    public void setEnableExplodeONut(boolean enableExplodeONut) {
        isEnableExplodeONut = enableExplodeONut;
    }
}
