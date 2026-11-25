package game.entity.zombie.data.zombieActions;

import common.model.Rect;
import game.entity.zombie.data.FilterList;

public class PushGridItemAction{
    private FilterList pushableGridItems;
    private FilterList pushBlockingGridItems;
    private FilterList notSquashedPlants;
    private double relocationDuration;
    private Rect pushRectangle;
}
