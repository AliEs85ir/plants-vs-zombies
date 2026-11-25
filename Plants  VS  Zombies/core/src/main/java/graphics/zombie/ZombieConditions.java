package graphics.zombie;

import app.model.App;
import game.entity.zombie.Zombie;
import game.entity.zombie.ZombieArmor;
import game.entity.zombie.data.properties.NewspaperProperty;
import graphics.general.animation.condition.AnimationCondition;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;

import java.util.List;

public class ZombieConditions {
    public static AnimationCondition<Zombie> allStar(ZombieVisualEventType type) {
        return zombie -> {
            VisualEvent<ZombieVisualEventType> event = App.getRealTimeEngine()
                .getGraphicsEngine()
                .getZombieInstance(zombie) == null ? null :
                App.getRealTimeEngine()
                    .getGraphicsEngine()
                    .getZombieInstance(zombie)
                .getCurrentEvent();
            return event != null && event.getType() == type;
        };
    }

    public static AnimationCondition<Zombie> newspaper(boolean needsNewspaper) {
        return zombie -> {
            List<ZombieArmor> armors = zombie.getArmors();
            boolean hasNewspaper = false;
            for(ZombieArmor armor : armors) {
                if(armor.getProperty().getAlias().equals("NewspaperDefault")) {
                    hasNewspaper = true;
                }
            }
            return (hasNewspaper == needsNewspaper);
        };
    }
}
