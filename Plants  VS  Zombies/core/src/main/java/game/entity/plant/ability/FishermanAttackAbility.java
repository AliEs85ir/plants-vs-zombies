package game.entity.plant.ability;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.plant.Plant;
import game.service.plant.PlantService;
import game.world.playground.Cell;
import game.world.playground.Grid;

public class FishermanAttackAbility extends AbstractPlantAbility{
    private static final float PULL_SPEED = 6f;

    private final Cell targetCell;
    private final Vector2 targetWorld;
    private final Vector2 targetPhysics;

    private boolean moving = false;
    private boolean done = false;

    public FishermanAttackAbility(Grid grid, Plant plant) {
        Cell myCell = grid.getCellAt(plant.getPosition());

        if (myCell == null) {
            this.targetCell = null;
            this.targetWorld = null;
            this.targetPhysics = null;
            return;
        }

        this.targetCell = grid.getCell(myCell.getRow(), myCell.getCol() + 1);

        if (targetCell == null) {
            this.targetWorld = null;
            this.targetPhysics = null;
        } else {
            this.targetWorld = new Vector2(targetCell.getWorldPosition());   // copy
            this.targetPhysics = new Vector2(
                targetWorld.x / PhysicService.PIXEL_PER_METER,
                targetWorld.y / PhysicService.PIXEL_PER_METER
            );
        }
    }

    @Override
    protected void doUpdate(Plant plant, PlantService plantService, long currentTick) {
        if (done) {
            return;
        }

        Body body = plant.getBody();

        boolean blocked = targetCell == null
            || targetCell.hasObstacle()
            || targetCell.getDamageTarget() != null;

        if (blocked || body == null) {
            plant.markDead(plantService.getGameWorld());
            done = true;
            return;
        }

        if (!moving) {
            moving = true;
            body.setType(BodyDef.BodyType.DynamicBody);
            body.setAwake(true);
            body.setLinearVelocity(0f, 0f);
        }

        Vector2 pos = body.getPosition();                        // meters
        float step = PULL_SPEED / PhysicService.PIXEL_PER_METER; // meters per tick

        Vector2 delta = targetPhysics.cpy().sub(pos);            // meters, target - current
        float dist = delta.len();

        if (dist <= step) {
            arrive(plant);                                       // close enough → snap
        } else {
            pos.add(delta.nor().scl(step));                      // advance one step
            body.setTransform(pos, body.getAngle());
        }
    }

    private void arrive(Plant plant) {
        Body body = plant.getBody();

        body.setTransform(targetPhysics, 0f);
        body.setLinearVelocity(0f, 0f);
        body.setAngularVelocity(0f);
        body.setType(BodyDef.BodyType.StaticBody);

        plant.setPosition(new Position(targetWorld.x, targetWorld.y));

        Cell oldCell = plant.getCell();
        if (oldCell != null) {
            oldCell.removePlant(plant);
        }
        plant.setCell(targetCell);
        targetCell.addPlant(plant);

        done = true;
    }

}
