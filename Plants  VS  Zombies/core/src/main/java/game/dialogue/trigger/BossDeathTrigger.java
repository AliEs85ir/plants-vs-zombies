package game.dialogue.trigger;

import game.world.chapter.Chapter;
import game.world.chapter.ChapterType;

public final class BossDeathTrigger implements DialogueTrigger {
    @Override
    public boolean shouldFire(DialogueTriggerContext context) {
        if (context == null || context.getEvent() != DialogueTriggerEvent.BOSS_DEFEATED) {
            return false;
        }
        Chapter chapter = context.getChapter();
        return chapter != null && chapter.getChapterType() == ChapterType.BOSS;
    }
}
