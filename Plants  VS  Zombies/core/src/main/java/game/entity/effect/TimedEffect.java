package game.entity.effect;

import app.enums.effect.EffectType;
import game.engine.time.GameClock;

public abstract class TimedEffect extends Effect
{
    protected long startTick;
    protected float durationSeconds;
    protected long durationTicks;


    public TimedEffect(float durationInSecond , EffectType type) {
        this.startTick = GameClock.currentTick;
        this.durationSeconds = durationInSecond;
        this.durationTicks = (long) durationInSecond * GameClock.TICKS_PER_SECOND;
        this.type = type;
    }

    @Override
    public boolean isExpired(long currentTick)
    {
        return currentTick >= startTick + durationTicks;
    }

    public void refresh(long currentTick, long durationTicks) {
        this.startTick = currentTick;
        this.durationTicks = durationTicks;
    }

    public void refresh(TimedEffect other) {
        if (other == null) {
            return;
        }
        refresh(other.startTick, other.durationTicks);
    }

    public long getStartTick() {
        return startTick;
    }

    public long getDurationTicks() {
        return durationTicks;
    }
}
