package game.entity.zombie.data.properties;

import common.model.Offset;
import common.model.Vector;
import game.entity.zombie.data.FilterList;

public class PianoProperty extends ZombieProperty{
    private double fastMoveSpeed;
    private boolean normalDeathWhenMowed;
    private FilterList plantsWhichBreakPianoOnCollision;
    private Vector streetCriticalSize;
    private Offset streetOffset;
    private Offset  streetSize;

}
