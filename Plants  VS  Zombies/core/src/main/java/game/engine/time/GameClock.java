package game.engine.time;

// >>> Maintains current game time in ticks.
public class GameClock
{
    // -> Properties
    public static long currentTick = 0;
    private float accumulator = 0f;
    public static final int TICKS_PER_SECOND = 10;
    public static final float SECONDS_PER_TICK = 1f / TICKS_PER_SECOND;
    // Allows up to ~3x game speed without clamping at low frame rates.
    private static final int MAX_TICKS_PER_FRAME = 15;

    public int update(float deltaSeconds) {
        accumulator += deltaSeconds;
        int ticksToProcess = (int) (accumulator / SECONDS_PER_TICK);

        ticksToProcess = Math.min(ticksToProcess, MAX_TICKS_PER_FRAME);

        if (ticksToProcess > 0) {
            accumulator -= ticksToProcess * SECONDS_PER_TICK;
        }

        return ticksToProcess;
    }
    public long getCurrentTick()
    {
        return currentTick;
    }

    public long advance()
    {
        return ++currentTick;
    }

    public double getCurrentSeconds()
    {
        return (double)currentTick / TICKS_PER_SECOND;
    }

    public float getInterpolationAlpha() {
        return accumulator / SECONDS_PER_TICK;
    }

    public float getAccumulator() {
        return accumulator;
    }

    public void reset() {
        currentTick = 0;
        accumulator = 0f;
    }

    public void restore(long tick, float restoredAccumulator) {
        currentTick = Math.max(0L, tick);
        accumulator = Math.max(0f, restoredAccumulator);
    }
}
