package game.entity.zombie.data.properties.iceAge;

import app.enums.zombie.ImpType;
import game.entity.zombie.data.properties.ZombieProperty;
import game.entity.zombie.data.zombieActions.ZombieAction;

import java.util.List;

public class TroglobiteProperty extends ZombieProperty {
    private List<ZombieAction> actions;
    private boolean chillInsteadOfFreeze;
    private ImpType impType;
}
