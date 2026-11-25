package graphics.general.event;

public enum ObstacleVisualEventType {
    DEATH,
    IDLE,
    UNDAMAGED,
    DAMAGE1,
    DAMAGE2,
    DAMAGE3,
    DAMAGE4,

    // octopus events :
    FLY,
    LAND,

    // Gold tile events :
    PLANT_ON_GOLD_TILE,

    // vase events :
    VASE_BREAK,
    VASE_DROP,

    // projectiles :
    IMPACT,

    // slide :
    SLIDING_DOWN_START,
    SLIDING_DOWN_LOOP,
    SLIDING_DOWN_END,
    SLIDING_UP_START,
    SLIDING_UP_LOOP,
    SLIDING_UP_END,

    // necromancy :
    SPAWN_ZOMBIE,

    // puddle :
    PUDDLE_EXIT,

    // temporary scorch :
    SCORCH_START,
    SCORCH_LOOP,

    // glacier :
    GLACIER_SPAWN,

    // squash events :
    SQUASH_JUMP_UP,
    SQUASH_JUMP_DOWN,

    ;

    public boolean isInterruptable() {
        return this == VASE_BREAK ;
    }
}
