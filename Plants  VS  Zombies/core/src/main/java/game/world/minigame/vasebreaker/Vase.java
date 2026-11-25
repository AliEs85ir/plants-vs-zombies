package game.world.minigame.vasebreaker;


import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import common.model.Position;
import game.entity.obstacle.Obstacle;
import game.service.minigame.VaseBreakerService;

public class Vase extends Obstacle {
    private boolean broken = false;
    private PlantName containedPlant;
    private String containedZombieAlias;
    private Position position;
    private boolean spawned;
    private VaseType vaseType;

    public Vase(PlantName plant, Position position) {
        super(position, ObstacleType.VASE);
        this.containedPlant = plant;
        this.vaseType = VaseType.PLANT_VASE;
        this.position = position;
        this.spawned = false;
    }
    public Vase(String zombieAlias, Position position) {
        super(position, ObstacleType.VASE);
        this.containedZombieAlias = zombieAlias;
        this.vaseType = VaseType.GHOUL_VASE;
        this.position = position;
        this.spawned = false;
    }

    public void breakVase() {
        this.broken = true;
    }
    public boolean isBroken() { return broken; }
    public void update(long currentTick, VaseBreakerService service) {
        if(!spawned) {
            if(broken) {
                service.breakVase(this);
                spawned = true;
            }
        }
    }
    // Getters for contained items...


    public PlantName getContainedPlantName() {
        return containedPlant;
    }

    public String getContainedZombieAlias() {
        return containedZombieAlias;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public VaseType getVaseType() {
        return vaseType;
    }

    public void setVaseType(VaseType vaseType) {
        this.vaseType = vaseType;
    }

}
