package game.entity.lawnmower;

import app.enums.lawnmower.LawnMowerType;
import app.model.App;
import app.view.game.PlayScreen;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.component.physic.PhysicService;
import game.entity.Entity;
import game.entity.zombie.Zombie;
import game.service.lawnmower.LawnMowerService;
import game.service.quest.QuestSignals;
import game.world.playground.MapConfig;

import java.util.ArrayList;
import java.util.List;

public class LawnMower extends Entity {
    public static final float LAWNMOWER_WIDTH = 30f;
    public static final float LAWNMOWER_HEIGHT = 15f;
    public static final float LAWNMOWER_SPEED = 2f;

    private LawnMowerType type;
    private Position position;
    private boolean used;
    private int row;

    private List<Zombie> targetZombies = new ArrayList<>();

    private LawnmowerState state;

    public LawnMower(Vector2 pos) {
        this.position = new Position(pos.x, pos.y);
        this.used = false;
        this.state = LawnmowerState.IDLE;
    }

    public void update(LawnMowerService lmService, long currentTick) {
        if (state == LawnmowerState.DEAD) {
            return;
        }
        if (body == null) {
            retire();
            return;
        }
        updateLaneMask();
        LawnmowerState previous = state;
        evaluateState();
        if (previous != LawnmowerState.ATTACK && state == LawnmowerState.ATTACK) {
            PlayScreen.shake(0.45f, 14f);
            game.audio.Sfx.play(game.audio.AudioId.SFX_LAWNMOWER);
        }
        syncPosition();
        switch (state) {
            case ATTACK -> {
                used = true;
                move();
                int killed = 0;
                for (Zombie z : targetZombies) {
                    if (z != null && !z.isDead()) {
                        z.die();
                        killed++;
                    }
                }
                targetZombies.clear();
                if (killed > 0) {
                    QuestSignals.lawnMowerKilled(killed);
                }
            }
            case IDLE -> {
            }
        }

        if (state == LawnmowerState.ATTACK && hasRunPastLawn(lmService)) {
            die();
        }
    }

    private void evaluateState() {
        // CHANGED: Transition to ATTACK if the list has at least one valid zombie
        for (Zombie z : targetZombies) {
            if (z != null && !z.isDead()) {
                this.state = LawnmowerState.ATTACK;
                break;
            }
        }
    }

    private void syncPosition() {
        if (body == null) {
            return;
        }
        Vector2 b2Pos = body.getPosition();
        setPosition(new Position(b2Pos.x * PhysicService.PIXEL_PER_METER,
            b2Pos.y * PhysicService.PIXEL_PER_METER));
    }

    private boolean hasRunPastLawn(LawnMowerService lmService) {
        MapConfig config = lmService.getGameWorld().getGrid().getConfig();
        float playgroundRight = config.getPlaygroundX() + config.getPlaygroundWidth();
        float mapRight = config.getMapWidth() > 0
            ? config.getMapX() + config.getMapWidth()
            : playgroundRight;
        return position.getX() > Math.max(playgroundRight, mapRight);
    }

    private void move() {
        if(body != null) {
            body.setLinearVelocity(LAWNMOWER_SPEED, 0);
        }
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public boolean isUsed() {
        return used;
    }

    public void trigger() {
        used = true;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public LawnMowerType getType() {
        return type;
    }

    public void setType(LawnMowerType type) {
        this.type = type;
    }

    public LawnmowerState getState() {
        return state;
    }

    public void setState(LawnmowerState state) {
        this.state = state;
    }

    // CHANGED: Swapped getter/setter for list methods
    public List<Zombie> getTargetZombies() {
        return targetZombies;
    }

    public void addTargetZombie(Zombie zombie) {
        if (zombie != null && !targetZombies.contains(zombie)) {
            this.targetZombies.add(zombie);
        }
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void die() {
        System.out.println("LAWN-MOWER OUTSIDE THE MAP!");
        retire();
    }

    public void retire() {
        if (state == LawnmowerState.DEAD) {
            return;
        }
        used = true;
        state = LawnmowerState.DEAD;
        if (App.getRealTimeEngine() != null && App.getRealTimeEngine().getGraphicsEngine() != null) {
            App.getRealTimeEngine().getGraphicsEngine().unregisterLawnmower(this);
        }
        if (body != null) {
            if (body.getWorld() != null) {
                body.getWorld().destroyBody(body);
            }
            body = null;
        }
    }
}
