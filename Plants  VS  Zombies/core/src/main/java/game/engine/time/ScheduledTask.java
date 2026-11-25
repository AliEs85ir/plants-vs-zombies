package game.engine.time;

public class ScheduledTask {
    final long executeTick;
    final Runnable action;

    ScheduledTask(long executeTick, Runnable action) {
        this.executeTick = executeTick;
        this.action = action;
    }
}
