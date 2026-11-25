package game.world.chapter;

import app.enums.SeasonType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChapterIdTest {
    @Test
    void parseMinigameChapterResolvesBeghouled() {
        ChapterId id = ChapterId.parseMinigameChapter("BEGHOULED");
        assertEquals(SeasonType.MINIGAME, id.seasonType());
        assertEquals(5, id.chapterNumber());
    }
}
