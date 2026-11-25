package game.entity.effect;

import app.model.App;
import game.entity.plant.Plant;

public class NoActEffect extends PermanentEffect{

    @Override
    public void onApply(Plant plant) {
        plant.setCanAct(false);
        App.getCurrentGameEngine().getGameWorld().addEvent(
                "plant " + plant.getName() + " at position " + plant.getPosition().toString() + " can not act anymore!"
        );
    }

    @Override
    public void onRemove(Plant plant) {
        plant.setCanAct(true);
        App.getCurrentGameEngine().getGameWorld().addEvent(
                "plant " + plant.getName() + " at position " + plant.getPosition().toString() + " can act from now!"
        );
    }
}
