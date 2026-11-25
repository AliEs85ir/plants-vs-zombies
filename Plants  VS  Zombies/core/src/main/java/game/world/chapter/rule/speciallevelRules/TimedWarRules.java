package game.world.chapter.rule.speciallevelRules;

import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.engine.time.GameClock;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import graphics.GraphicsEngine;

import java.util.HashSet;
import java.util.Set;

public class TimedWarRules implements ChapterRule {
    public enum RuleState {
        WAITING_FOR_KILL,
        TIMER_ACTIVE,
        COMPLETED
    }

    private final int totalSecondsLimit;
    private final int targetKillCount;

    private RuleState currentState = RuleState.WAITING_FOR_KILL;
    private long startTick = 0;
    private int killsAtWindowStart = 0;
    private int zombiesKilledInWindow = 0;

    public TimedWarRules(int secondsLimit, int targetKillCount) {
        this.totalSecondsLimit = secondsLimit;
        this.targetKillCount = targetKillCount;
    }

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
        killsAtWindowStart = world.getTotalDeadZombies();
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
        int currentTotalDead = world.getTotalDeadZombies();

        switch (currentState) {
            case WAITING_FOR_KILL:
                if (currentTotalDead > killsAtWindowStart) {
                    startTick = currentTick;
                    currentState = RuleState.TIMER_ACTIVE;
                } else {
                    killsAtWindowStart = currentTotalDead;
                }
                break;

            case TIMER_ACTIVE:
                zombiesKilledInWindow = currentTotalDead - killsAtWindowStart;

                if (zombiesKilledInWindow >= targetKillCount) {
                    currentState = RuleState.COMPLETED;
                    System.err.println("TIMED WAR WON!");
                } else {
                    long ticksElapsed = currentTick - startTick;
                    if (ticksElapsed >= (long) totalSecondsLimit * GameClock.TICKS_PER_SECOND) {
                        // Timer failed, reset back to waiting state
                        currentState = RuleState.WAITING_FOR_KILL;
                        killsAtWindowStart = currentTotalDead;
                        zombiesKilledInWindow = 0;
                        System.err.println("TIMER RESET!");
                    }
                }
                break;

            case COMPLETED:
                break;
        }
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {}

    public int getZombiesKilled() {
        return zombiesKilledInWindow;
    }

    public int getTargetKillCount() {
        return targetKillCount;
    }

    public RuleState getCurrentState() {
        return currentState;
    }
}
