package graphics.zombie;

public enum ZombieVisualState {
    WALK,
    ATTACK,
    DEATH,
    FROZEN,
    IDLE,
    ABILITY,
    INTRO
    //TODO -> Add zombie specific states.

    ;
    public boolean isInterruptible() {
        return this == IDLE;
    }

    public boolean isActionState() {
        return this == ATTACK || this == DEATH || this == ABILITY || this == INTRO;
    }
}
