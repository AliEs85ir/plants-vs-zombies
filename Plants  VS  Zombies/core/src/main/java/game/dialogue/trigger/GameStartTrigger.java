package game.dialogue.trigger;

public final class GameStartTrigger implements DialogueTrigger {
    @Override
    public boolean shouldFire(DialogueTriggerContext context) {
        return context != null && context.getEvent() == DialogueTriggerEvent.GAMEPLAY_READY;
    }
}
