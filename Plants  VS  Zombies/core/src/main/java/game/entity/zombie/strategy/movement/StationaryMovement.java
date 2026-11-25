package game.entity.zombie.strategy.movement;

import game.entity.zombie.Zombie;
import game.entity.zombie.data.properties.DarkAges.KingProperty;
import game.entity.zombie.data.properties.beach.FishermanProperty;
import game.service.Zombie.ZombieService;
import graphics.general.event.ZombieVisualEventType;

public class StationaryMovement implements MovementStrategy{
    @Override
    public boolean move(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (zombie.getBody() != null) {
            zombie.getBody().setLinearVelocity(0f, zombie.getBody().getLinearVelocity().y);
            if(zombie.getPropertyData() instanceof FishermanProperty) {
                zombie.topEvent();
                if (zombie.topEvent() != ZombieVisualEventType.HOOKING) {
                    zombie.pushEvent(ZombieVisualEventType.IDLE);
                }
            }
            if(zombie.getPropertyData() instanceof KingProperty) {
                zombie.pushEvent(ZombieVisualEventType.IDLE);
            }else if (zombie.getName().equals("SunProducer")) {
                zombie.pushEvent(ZombieVisualEventType.IDLE);
            }

            return true;
        }
        return false;
    }
}
