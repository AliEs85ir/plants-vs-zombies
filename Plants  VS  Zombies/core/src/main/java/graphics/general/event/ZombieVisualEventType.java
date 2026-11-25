package graphics.general.event;

public enum ZombieVisualEventType {
    IDLE,
    DAMAGE_TAKEN,
    DIED,
    ARM_LOST,
    BOMBED,
    ATTACK,
    INTRO,
    CONE_BREAK,
    BUCKET_BREAK,
    BRICK_BREAK,

    //normal zombie :
    EAT,
    WALK,


    // all star events :
    RUN,
    TACKLE,

    // gargantuar events :
    SMASH,
    FIRE,
    CANON_FIRE,

    // imp events :
    LAND,
    FLY,

    // newspaper events :
    NEWSPAPER_DEFEAT,

    // Ra events :
    POWER_UP,

    //tomb raiser events :
    SPAWN_TOMB,

    // Skull events :
    SKULL_UP,
    SKULL,
    SKULL_DOWN,
    LASER,

    // prospector events :
    BLAST_OFF,


    // hunter events :
    THROW_SNOWBALL,

    // jester events :
    SPINUP,
    SPIN,
    SPINDOWN,


    // dragon events :
    DRAGON_FIRE_START,
    DRAGON_FIRE,
    DRAGON_FIRE_END,

    DRAGON_BOMB_START,
    DRAGON_BOMB,
    DRAGON_BOMB_END,

    DRAGON_SUMMONING,

    DRAGON_VULNERABLE_START,
    DRAGON_VULNERABLE,
    DRAGON_VULNERABLE_END,

    STUN_START,
    STUN_LOOP,
    STUN_END,

    DRAGON_DIE,
    DRAGON_TALK,
    DRAGON_EXIT,


    // octopus throw events :
    OCTOPUS_THROW,

    // fisherman events :
    THROW_HOOK,
    HOOKING,
    REEL,
    TOSS_OUT,


    // sphinxInator events :
    STOMP,
    PORTAL_START,
    PORTAL_LOOP,
    PORTAL_END,
    MISSILE_START,
    MISSILE,

    WALK_FORWARD,
    WALK_BACKWARD,
    WALK_DOWN,
    WALK_UP,

    EGYPT_DIE1,
    EGYPT_DIE2,
    EGYPT_TALK,
    EGYPT_EXIT,


    // Sand storm effect
    SANDSTORM_INTRO,
    SANDSTORM_LOOP,
    SANDSTORM_OUTRO,

    // Mammoth events :
    MAMMOTH_WIND1,
    MAMMOTH_WIND2,
    MAMMOTH_WIND3,
    MAMMOTH_WIND4,
    MAMMOTH_SLINGSHOT,
    MAMMOTH_COLUMN1,
    MAMMOTH_COLUMN2,
    MAMMOTH_COLUMN3,
    MAMMOTH_COLUMN4,
    MAMMOTH_COLUMN5,
    MAMMOTH_COLUMN6,
    MAMMOTH_REVEAL,
    MAMMOTH_COVER_UP,
    MAMMOTH_DIE,
    MAMMOTH_TALK,
    MAMMOTH_EXIT,

    // shark events :
    SUBMERGE,
    EMERGE,
    SHARK_SPAWN,
    SUCTION_START,
    SUCTION_LOOP,
    SUCTION_END,
    SHARK_DIE,
    SHARK_TALK,
    SHARK_EXIT,

    // arcade :
    PUSH_CABINET,
    // wizard:
    TURN_INTO_SHEEP,

    // king :
    GIVE_ARMOR
    ;

    public boolean isInterruptable() {
        return this == TACKLE ;
    }

}
