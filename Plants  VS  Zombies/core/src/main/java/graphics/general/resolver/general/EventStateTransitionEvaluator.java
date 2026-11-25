package graphics.general.resolver.general;

import graphics.general.event.ObstacleVisualEventType;
import graphics.obstacle.ObstacleRenderInstance;
import graphics.obstacle.ObstacleVisualState;
import graphics.zombie.ZombieRenderInstance;
import graphics.general.event.PlantVisualEventType;
import graphics.general.event.VisualEvent;
import graphics.general.event.ZombieVisualEventType;
import graphics.plant.PlantRenderInstance;
import graphics.plant.PlantVisualState;
import graphics.zombie.ZombieVisualState;

public class EventStateTransitionEvaluator {

    public PlantVisualState evaluateNextEventState(PlantRenderInstance instance) {
        VisualEvent<PlantVisualEventType> event = instance.pollEvent();
        if (event == null) {
            return null;
        }

        return switch (event.getType()) {
            case DIED -> PlantVisualState.DEATH;
            case PLANT_FOOD_ACTIVATED -> PlantVisualState.PLANT_FOOD;
            case ATTACK_TRIGGERED -> PlantVisualState.ATTACK;
            case ATTACK_2_TRIGGERED -> PlantVisualState.ATTACK_2;
            case RECOVER_TRIGGERED -> PlantVisualState.RECOVER;
            case TURN_TRIGGERED -> PlantVisualState.TURN;
            case DAMAGE_TAKEN -> PlantVisualState.HURT;
            case SLEPT -> PlantVisualState.SLEEP;
            case FROZEN -> PlantVisualState.FROZEN;
            default -> null;
        };
    }

    public ZombieVisualState evaluateNextEventState(ZombieRenderInstance instance) {
        VisualEvent<ZombieVisualEventType> event = instance.pollEvent();
        if(event == null) {
            return null;
        }
        instance.setCurrentEvent(event);
        return switch (event.getType()) {
            case RUN, TACKLE, FIRE, CANON_FIRE, FLY, NEWSPAPER_DEFEAT, POWER_UP
            , SKULL_UP, SKULL, SKULL_DOWN, LASER, LAND, BLAST_OFF, THROW_SNOWBALL,
                 SPINUP, SPIN, SPINDOWN, DRAGON_FIRE_START, DRAGON_FIRE,
                 DRAGON_FIRE_END, DRAGON_BOMB_START, DRAGON_BOMB,
                 DRAGON_BOMB_END, DRAGON_SUMMONING, DRAGON_VULNERABLE_START,
                 DRAGON_VULNERABLE, DRAGON_VULNERABLE_END, STUN_START,
                 STUN_LOOP, STUN_END, OCTOPUS_THROW, THROW_HOOK, HOOKING, REEL, TOSS_OUT,
                 STOMP, PORTAL_START, PORTAL_LOOP, PORTAL_END, MISSILE_START,
                 MISSILE, SPAWN_TOMB, MAMMOTH_WIND1, MAMMOTH_WIND2, MAMMOTH_WIND3, MAMMOTH_WIND4,
                 MAMMOTH_SLINGSHOT, MAMMOTH_COLUMN1, MAMMOTH_COLUMN2, MAMMOTH_COLUMN3,
                 MAMMOTH_COLUMN4, MAMMOTH_COLUMN5, MAMMOTH_COLUMN6, MAMMOTH_REVEAL,
                 MAMMOTH_COVER_UP, SUBMERGE, EMERGE, SHARK_SPAWN, SUCTION_START,
                 SUCTION_LOOP, SUCTION_END, PUSH_CABINET, TURN_INTO_SHEEP, GIVE_ARMOR-> ZombieVisualState.ABILITY;
            case WALK,WALK_FORWARD,
                 WALK_BACKWARD,
                 WALK_DOWN,
                 WALK_UP -> ZombieVisualState.WALK;
            case EAT, SMASH -> ZombieVisualState.ATTACK;
            case DIED , DRAGON_DIE,
                 DRAGON_TALK,
                 DRAGON_EXIT,
                 EGYPT_DIE1,
                 EGYPT_DIE2,
                 EGYPT_TALK,
                 EGYPT_EXIT, MAMMOTH_DIE,
                 MAMMOTH_TALK,
                 MAMMOTH_EXIT,
                 SHARK_DIE,
                 SHARK_TALK,
                 SHARK_EXIT-> ZombieVisualState.DEATH;
            case INTRO -> ZombieVisualState.INTRO;
            case IDLE -> ZombieVisualState.IDLE;
            default -> null;
        };
    }

    public ObstacleVisualState evaluateNextEventState(ObstacleRenderInstance instance) {
        VisualEvent<ObstacleVisualEventType> event = instance.pollEvent();
        if(event == null) {
            return null;
        }
        instance.setCurrentEvent(event);

        return switch (event.getType()) {
            case UNDAMAGED, IDLE -> ObstacleVisualState.IDLE;
            case DAMAGE1, DAMAGE2, DAMAGE3, DAMAGE4 , FLY, LAND,
                 PLANT_ON_GOLD_TILE, VASE_DROP, VASE_BREAK, IMPACT,
                 SLIDING_DOWN_END, SLIDING_DOWN_START, SLIDING_UP_END, SLIDING_DOWN_LOOP ,
                 SLIDING_UP_LOOP, SLIDING_UP_START, SPAWN_ZOMBIE, PUDDLE_EXIT,
                 SCORCH_START, SCORCH_LOOP, GLACIER_SPAWN,
                 SQUASH_JUMP_UP, SQUASH_JUMP_DOWN -> ObstacleVisualState.ABILITY;
            case DEATH -> ObstacleVisualState.DEATH;
            default -> null;
        };
    }

}
