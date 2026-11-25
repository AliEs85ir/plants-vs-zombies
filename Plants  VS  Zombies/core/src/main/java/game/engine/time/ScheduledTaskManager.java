package game.engine.time;

import java.util.PriorityQueue;

public class ScheduledTaskManager {
    private final PriorityQueue<ScheduledTask> queue = new PriorityQueue<>(
        (a, b) -> Long.compare(a.executeTick, b.executeTick)
    );

    public void scheduleRelative(long currentTick, long delayTicks, Runnable action) {
        queue.add(new ScheduledTask(currentTick + delayTicks, action));
    }

    public void executeTasks(long currentTick) {
        while (!queue.isEmpty() && queue.peek().executeTick <= currentTick) {
            queue.poll().action.run();
        }
    }
}
