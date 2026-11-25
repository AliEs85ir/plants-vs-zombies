package game.world.playground;

import app.enums.plant.PlantName;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import game.entity.obstacle.Obstacle;
import game.entity.plant.Plant;
import game.entity.plant.PlantDefinition;
import game.entity.plant.PlantStackLayer;
import game.entity.zombie.Zombie;
import game.service.plant.PlantPlacementPolicy;
import game.world.playground.celltypes.CellType;
import game.world.playground.celltypes.WaterCellType;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final int row;
    private final int col;
    private final Rectangle bounds;
    private final Vector2 worldPosition;

    private CellType cellType;
    private Obstacle obstacle;
    private final List<Plant> plants = new ArrayList<>();

    public Cell(int row, int col, float x, float y, float width, float height) {
        this.row = row;
        this.col = col;
        this.bounds = new Rectangle(x, y, width, height);
        this.worldPosition = new Vector2(x + width / 2f, y + height / 2f);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Vector2 getWorldPosition() {
        return worldPosition;
    }

    public float getWorldX() {
        return worldPosition.x;
    }

    public float getWorldY() {
        return worldPosition.y;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public float getWidth() {
        return bounds.width;
    }

    public float getHeight() {
        return bounds.height;
    }

    public boolean contains(float worldX, float worldY) {
        return bounds.contains(worldX, worldY);
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public boolean isWater() {
        return cellType instanceof WaterCellType;
    }

    public boolean isLand() {
        return !isWater();
    }

    public boolean isPlantable() {
        return obstacle == null
            && plants.isEmpty()
            && (cellType == null || cellType.canPlant());
    }

    public boolean isPlantable(Plant plant) {
        return plant != null && isPlantable(plant.getDefinition());
    }

    public boolean isPlantable(PlantDefinition plantDefinition) {
        return PlantPlacementPolicy.canPlace(this, plantDefinition);
    }

    public boolean hasPlant(PlantName name) {
        for (Plant plant : plants) {
            if (plant.getDefinition().getName() == name) {
                return true;
            }
        }
        return false;
    }

    public int getPlantCount(PlantName name) {
        int count = 0;
        for (Plant plant : plants) {
            if (plant.getDefinition().getName() == name) {
                count++;
            }
        }
        return count;
    }

    public int getCorePlantCount() {
        return PlantPlacementPolicy.coreCount(this);
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public void setObstacle(Obstacle obstacle) {
        this.obstacle = obstacle;
    }

    public boolean hasObstacle() {
        return obstacle != null;
    }

    public void clearObstacle() {
        obstacle = null;
    }

    public Plant getPlant() {
        return getCurrentPlant();
    }

    public void setPlant(Plant plant) {
        if (plant == null) {
            return;
        }
        addPlant(plant);
    }

    public boolean hasPlant() {
        return !plants.isEmpty();
    }

    public void removePlant() {
        if (!plants.isEmpty()) {
            plants.remove(plants.size() - 1);
        }
    }

    public void removePlant(Plant plant) {
        if (plant != null) {
            plants.remove(plant);
        }
    }

    public List<Zombie> getZombies() {
        return null;
    }

    public boolean isBeach() {
        return isWater();
    }

    public void setBeach(boolean beach) {
        if (beach) {
            cellType = new WaterCellType();
        }
    }

    public Plant getCurrentPlant() {
        return getTopPlant();
    }

    public Plant getTopPlant() {
        if (plants.isEmpty()) {
            return null;
        }
        return plants.get(plants.size() - 1);
    }

    public Plant getDamageTarget() {
        for (int i = plants.size() - 1; i >= 0; i--) {
            Plant plant = plants.get(i);
            if (plant != null && !plant.isDead()) {
                return plant;
            }
        }
        return null;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public void setCurrentPlant(Plant currentPlant) {
        addPlant(currentPlant);
    }

    public void addPlant(Plant plant) {
        if (plant == null) {
            return;
        }
        int index = PlantPlacementPolicy.insertionIndex(this, plant.getDefinition());
        index = Math.max(0, Math.min(index, plants.size()));
        plants.add(index, plant);
        plant.setCell(this);
    }

    public boolean hasLayer(PlantStackLayer layer) {
        return PlantPlacementPolicy.hasLayer(this, layer);
    }
}
