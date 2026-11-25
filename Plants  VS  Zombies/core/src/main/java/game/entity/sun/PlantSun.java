package game.entity.sun;

import app.enums.sun.SunSourceType;
import app.enums.sun.SunState;
import app.enums.sun.SunType;
import common.model.Position;
import game.component.physic.PhysicService;

public class PlantSun extends Sun {
    private SunToss toss;

    public PlantSun(Position position, int value) {
        this.position = position;
        this.value = value;
        this.type = SunType.NORMAL;
        this.state = SunState.ON_PLANT;
        this.sourceType = SunSourceType.PLANT;
    }

    public void startToss(SunToss toss) {
        this.toss = toss;
        if (toss == null) {
            return;
        }
        this.state = SunState.DROPPING;
        this.position = toss.spawn();
        syncBody();
    }

    public boolean isTossing() {
        return toss != null && !toss.isFinished();
    }

    public SunToss getToss() {
        return toss;
    }

    public void advanceToss() {
        if (toss == null) {
            return;
        }
        toss.advance();
        this.position = toss.pointAt(toss.progress());
        syncBody();
        if (toss.isFinished()) {
            this.position = toss.landing();
            this.state = SunState.ON_PLANT;
            this.toss = null;
            syncBody();
        }
    }

    public void syncBody() {
        if (body == null || position == null) {
            return;
        }
        body.setTransform(
            (float) position.getX() / PhysicService.PIXEL_PER_METER,
            (float) position.getY() / PhysicService.PIXEL_PER_METER,
            body.getAngle()
        );
    }

    @Override
    public boolean isCollectable() {
        return !isTossing() && state != SunState.COLLECTED;
    }
}
