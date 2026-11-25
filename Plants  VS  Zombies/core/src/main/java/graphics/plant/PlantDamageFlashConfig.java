package graphics.plant;

import game.engine.time.GameClock;

public final class PlantDamageFlashConfig {

    /** How long each white flash stays visible. 1 tick = 100ms at 10 TPS. */
    public static final int DURATION_TICKS = 1;

    /** Time from the start of one flash to the start of the next. 3 ticks = 300ms. */
    public static final int INTERVAL_TICKS = 3;

    /**
     * Visual DAMAGED state ends if no damage is received for this long.
     * Must be greater than {@link #INTERVAL_TICKS}. Eat attacks apply damage every tick,
     * so 4 ticks still covers a missed beat without stretching the cadence.
     */
    public static final int STATE_TIMEOUT_TICKS = 4;

    /** Peak opacity of the white damage flash overlay (0–1). */
    public static final float ALPHA = 0.60f;

    public static final float DURATION = DURATION_TICKS * GameClock.SECONDS_PER_TICK;
    public static final float INTERVAL = INTERVAL_TICKS * GameClock.SECONDS_PER_TICK;
    public static final float STATE_TIMEOUT = STATE_TIMEOUT_TICKS * GameClock.SECONDS_PER_TICK;

    private PlantDamageFlashConfig() {
    }
}
