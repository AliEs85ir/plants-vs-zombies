package game.service.special_levels;

import game.engine.time.GameClock;

public class ConveyorBeltConfig {

    public static final float DEFAULT_MIN_INTERVAL_SECONDS = 4f;
    public static final float DEFAULT_MAX_INTERVAL_SECONDS = 7f;
    public static final int DEFAULT_MAX_CAPACITY = 0;

    private float minIntervalSeconds = DEFAULT_MIN_INTERVAL_SECONDS;
    private float maxIntervalSeconds = DEFAULT_MAX_INTERVAL_SECONDS;
    private int maxCapacity = DEFAULT_MAX_CAPACITY;

    public static ConveyorBeltConfig defaults() {
        return new ConveyorBeltConfig();
    }

    public float getMinIntervalSeconds() {
        return minIntervalSeconds;
    }

    public ConveyorBeltConfig setMinIntervalSeconds(float minIntervalSeconds) {
        this.minIntervalSeconds = Math.max(0.1f, minIntervalSeconds);
        if (this.maxIntervalSeconds < this.minIntervalSeconds) {
            this.maxIntervalSeconds = this.minIntervalSeconds;
        }
        return this;
    }

    public float getMaxIntervalSeconds() {
        return maxIntervalSeconds;
    }

    public ConveyorBeltConfig setMaxIntervalSeconds(float maxIntervalSeconds) {
        this.maxIntervalSeconds = Math.max(this.minIntervalSeconds, maxIntervalSeconds);
        return this;
    }

    public ConveyorBeltConfig setIntervalSeconds(float minSeconds, float maxSeconds) {
        this.minIntervalSeconds = Math.max(0.1f, minSeconds);
        this.maxIntervalSeconds = Math.max(this.minIntervalSeconds, maxSeconds);
        return this;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public ConveyorBeltConfig setMaxCapacity(int maxCapacity) {
        this.maxCapacity = Math.max(0, maxCapacity);
        return this;
    }

    public boolean hasCapacityLimit() {
        return maxCapacity > 0;
    }

    public int getMinIntervalTicks() {
        return secondsToTicks(minIntervalSeconds);
    }

    public int getMaxIntervalTicks() {
        return secondsToTicks(maxIntervalSeconds);
    }

    public static int secondsToTicks(float seconds) {
        return Math.max(1, Math.round(seconds * GameClock.TICKS_PER_SECOND));
    }
}
