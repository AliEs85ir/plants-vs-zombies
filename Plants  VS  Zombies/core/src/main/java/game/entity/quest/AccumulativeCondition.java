package game.entity.quest;

import app.enums.quest.EventType;

import java.util.EnumSet;
import java.util.function.Predicate;

public class AccumulativeCondition implements QuestCondition {
    private final EventType eventType;
    private final EnumSet<EventType> resetEventTypes;
    private final Predicate<GameEvent> filter;
    private int requiredProgress;
    private int currentProgress;

    public AccumulativeCondition(EventType eventType, int requiredProgress) {
        this(eventType, requiredProgress, event -> true);
    }

    public AccumulativeCondition(EventType eventType, int requiredProgress, Predicate<GameEvent> filter) {
        this(eventType, requiredProgress, filter, new EventType[0]);
    }

    public AccumulativeCondition(
        EventType eventType,
        EventType resetEventType,
        int requiredProgress,
        Predicate<GameEvent> filter
    ) {
        this(eventType, requiredProgress, filter, resetEventType);
    }

    public AccumulativeCondition(
        EventType eventType,
        int requiredProgress,
        Predicate<GameEvent> filter,
        EventType... resetEvents
    ) {
        this.eventType = eventType;
        this.resetEventTypes = EnumSet.noneOf(EventType.class);
        if (resetEvents != null) {
            for (EventType resetEvent : resetEvents) {
                if (resetEvent != null) {
                    this.resetEventTypes.add(resetEvent);
                }
            }
        }
        this.requiredProgress = requiredProgress;
        this.currentProgress = 0;
        this.filter = filter == null ? event -> true : filter;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event == null) {
            return;
        }
        if (resetEventTypes.contains(event.type())) {
            if (!isReached()) {
                reset();
            }
            return;
        }
        if (event.type() != eventType) {
            return;
        }
        if (!filter.test(event)) {
            return;
        }
        currentProgress += event.amount();
    }

    @Override
    public boolean isReached() {
        return currentProgress >= requiredProgress;
    }

    @Override
    public void reset() {
        currentProgress = 0;
    }

    @Override
    public int getCurrentProgress() {
        return currentProgress;
    }

    @Override
    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    @Override
    public void setRequiredProgress(int progress) {
        this.requiredProgress = progress;
    }

    @Override
    public boolean verifyFinalState(LevelFacts facts) {
        return false;
    }

    @Override
    public int getRequiredProgress() {
        return requiredProgress;
    }
}
