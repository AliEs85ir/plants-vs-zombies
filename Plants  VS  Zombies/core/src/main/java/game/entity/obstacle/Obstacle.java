package game.entity.obstacle;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import common.model.Position;
import game.component.contacts.ContactBits;
import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.entity.Entity;
import game.entity.obstacle.ability.*;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.service.obstacle.ObstacleService;
import game.world.GameWorld;
import graphics.general.event.ObstacleVisualEventType;
import graphics.general.event.VisualEvent;
import graphics.obstacle.ObstacleRenderInstance;

import java.util.ArrayList;
import java.util.List;

public class Obstacle extends Entity {
    private Position position;
    private List<ObstacleAbility> abilities;
    private boolean dead;
    private ObstacleType type;
    private boolean isBackGround;
    private boolean hasAnimationOnDeath;
    private Plant targetPlant;
    private List<Zombie> targetZombies;

    public Obstacle(Position position, ObstacleType type) {
        this.position = position;
        this.dead = false;
        this.type = type;
        this.abilities =  new ArrayList<>();
        this.targetPlant = null;
        this.targetZombies = new ArrayList<>();
    }

    public Plant getTargetPlant() {
        return targetPlant;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isDead() {
        return dead;
    }

    public void update(long currentTick, ObstacleService service) {
        if(dead) {
            return;
        }
        updateLaneMask();
        for(ObstacleAbility ability : abilities) {
            ability.update(this, service, currentTick);
            if(dead) {
                return;
            }
        }
        syncPosition();
    }

    private void syncPosition() {
        if (body == null) {
            return;
        }
        Vector2 b2Pos = body.getPosition();
        setPosition(new Position(b2Pos.x * PhysicService.PIXEL_PER_METER,
            b2Pos.y * PhysicService.PIXEL_PER_METER));
    }


    public List<ObstacleAbility> getAbilities() {
        return abilities;
    }

    public void addAbility(ObstacleAbility ability) {
        abilities.add(ability);
    }

    public boolean isDamageable() {
        return abilities.stream().anyMatch(ability ->
                ability instanceof TakeDamageAbility ||
                ability instanceof IceBlockAbility ||
                ability instanceof OctopusAbility ||
                ability instanceof BossIceAbility ||
                ability instanceof GlacierAbility
        );
    }

    public ObstacleType getType() {
        return type;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void pushEvent(ObstacleVisualEventType type) {
        ObstacleRenderInstance inst = App.getRealTimeEngine().getGraphicsEngine().getObstacleInstance(this);
        if (inst == null) {
            return;
        }
        if (inst.getCurrentEvent() == null || inst.getCurrentEvent().getType() != type) {
            inst.pushEvent(new VisualEvent<>(type));
        }
    }

    public void stopMovement() {
        body.setLinearVelocity(0,0);
    }

    public void takeDamage(int amount) {
        for (ObstacleAbility ability : abilities) {
            if(ability instanceof TakeDamageAbility damageAbility) {
                damageAbility.takeDamage(amount, this);
            }
            else if(ability instanceof IceBlockAbility damageAbility) {
                damageAbility.takeDamage(amount, this);
            }
            else if(ability instanceof OctopusAbility damageAbility) {
                damageAbility.takeDamage(amount, this);
            }else if(ability instanceof BossIceAbility damageAbility) {
                damageAbility.takeDamage(amount, this);
            }else if(ability instanceof GlacierAbility damageAbility) {
                damageAbility.takeDamage(amount, this);
            }

        }
    }

    public void onWaveStart(GameWorld world, int waveNumber) {
        for(ObstacleAbility ability : abilities) {
            if(ability instanceof NecromancyAbility necromancyAbility) {
                necromancyAbility.onWaveStart(world, waveNumber, this);
            }
            if(ability instanceof PuddleAbility puddleAbility) {
                puddleAbility.onWaveStart(world, waveNumber, this);
            }
        }
    }

    public void die() {
        if(dead) {
            return;
        }

        for (ObstacleAbility ability : abilities) {
            ability.onDeath(this);
        }

        if (hasAnimationOnDeath) {
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                GameClock.currentTick,
                10,
                this::destroyBox2dBody
            );
        }
        else {
            destroyBox2dBody();
        }

    }

    private void destroyBox2dBody() {
        dead = true;
        App.getRealTimeEngine().getGraphicsEngine().unregisterObstacle(this);
        PhysicService physicService = App.getRealTimeEngine().getPhysicService();
        System.err.println("destroyed");
        destroyBody(physicService);
    }

    public void setAbilities(List<ObstacleAbility> abilities) {
        this.abilities = (abilities == null)
            ? new ArrayList<>()
            : new ArrayList<>(abilities);
    }

    public void setBackGround(boolean backGround) {
        isBackGround = backGround;
    }

    public boolean isBackGround() {
        return isBackGround;
    }

    public void setHasAnimationOnDeath(boolean hasAnimationOnDeath) {
        this.hasAnimationOnDeath = hasAnimationOnDeath;
    }

    public void setTargetPlant(Plant targetPlant) {
        this.targetPlant = targetPlant;
    }

    public List<Zombie> getTargetZombies() {
        return targetZombies;
    }

    public void addTargetZombie(Zombie zombie) {
        if (!targetZombies.contains(zombie)) {
            targetZombies.add(zombie);
        }
    }

    public void clearTargetZombies() {
        targetZombies.clear();
    }
}
