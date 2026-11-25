package game.engine.system.special_levels;

import game.engine.time.TickSystem;
import game.service.special_levels.ConveyorBeltService;

public class ConveyorBeltSystem implements TickSystem {

    private final ConveyorBeltService service;

    public ConveyorBeltSystem(ConveyorBeltService service) {
        this.service = service;
    }

    @Override
    public void update(long currentTick) {
        if (service == null) return;
        service.update(currentTick);
    }
}
