package graphics.obstacle;

public enum ObstacleVisualState {
    IDLE,
    DEATH,
    ABILITY
    ;

    public boolean isActionState() {
        return this == ABILITY;
    }
}
