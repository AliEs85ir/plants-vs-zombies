package game.dialogue.trigger;

public interface DialogueTrigger {
    boolean shouldFire(DialogueTriggerContext context);
}
