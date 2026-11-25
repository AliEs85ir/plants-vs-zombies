package graphics.plant;

public enum PlantVisualState {
    IDLE,
    ATTACK,
    ATTACK_2,
    HURT,
    PLANT_FOOD,
    DEATH,
    SLEEP,
    RECOVER,
    TURN,
    FROZEN;

    public boolean isInterruptible() {
        return this == IDLE || this == SLEEP;
    }

    public boolean isActionState() {
        return this == ATTACK || this == ATTACK_2 || this == PLANT_FOOD || this == RECOVER || this == TURN;
    }
}
