package game.dialogue.trigger;

import game.world.GameWorld;
import game.world.chapter.Chapter;

public final class DialogueTriggerContext {
    private final GameWorld world;
    private final Chapter chapter;
    private final DialogueTriggerEvent event;

    public DialogueTriggerContext(GameWorld world, Chapter chapter, DialogueTriggerEvent event) {
        this.world = world;
        this.chapter = chapter;
        this.event = event;
    }

    public GameWorld getWorld() {
        return world;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public DialogueTriggerEvent getEvent() {
        return event;
    }
}
