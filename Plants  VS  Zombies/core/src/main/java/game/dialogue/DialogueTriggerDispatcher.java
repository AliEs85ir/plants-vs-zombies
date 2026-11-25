package game.dialogue;

import game.dialogue.trigger.DialogueTriggerContext;
import game.world.chapter.Chapter;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.NpcDialogueRule;

public final class DialogueTriggerDispatcher {
    private DialogueTriggerDispatcher() {}

    public static void dispatch(DialogueTriggerContext context, DialogueManager manager) {
        if (context == null || manager == null || manager.isActive()) {
            return;
        }
        Chapter chapter = context.getChapter();
        if (chapter == null || chapter.getRules() == null) {
            return;
        }
        for (ChapterRule rule : chapter.getRules()) {
            if (rule instanceof NpcDialogueRule npcRule
                && npcRule.isEnabled()
                && npcRule.consumeIfShouldFire(context)) {
                manager.start(npcRule.getDialogues(), npcRule.getResumeDelaySeconds(), npcRule.spawnsNpcs());
                return;
            }
        }
    }
}
