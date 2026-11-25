package app.view.levelselect;

import app.enums.SeasonType;
import app.model.user.GameProgress;
import app.service.ConfigFiles;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelSelectProgressAndConfigTest {
    @Test
    void sanitizeProvidesFourChaptersAndResolvesBackgrounds() {
        LevelSelectMenuConfig config = new LevelSelectMenuConfig().sanitize();
        assertEquals(4, config.visibleChapters().size());
        assertEquals("egypt", config.visibleChapters().get(0).id);
        assertEquals("menu/bg/egypt.png", config.chapterById("egypt").resolvedBackgroundPath());
        assertEquals("menu/bg/ice.png", config.chapterById("ice").resolvedBackgroundPath());
        assertEquals(SeasonType.ANCIENT_EGYPT, config.chapterForSeason(SeasonType.ANCIENT_EGYPT).seasonType());
        assertEquals(SeasonType.FROSTBITE_CAVES, config.chapterForSeason(SeasonType.FROSTBITE_CAVES).seasonType());
        assertEquals("768/INITIAL/WORLDMAP/ZOMBOSS_NODE_EGYPT/ZOMBOSS_NODE_EGYPT.PAM", config.chapterById("egypt").main.pamPath);
        assertEquals("768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_RA/ZOMBIE_EGYPT_RA.PAM", config.chapterById("egypt").subordinate.pamPath);
        assertTrue(config.chapterById("egypt").subordinate.layer > config.chapterById("egypt").main.layer);
        assertTrue(config.chapterById("egypt").subordinate.present());
        assertTrue(!config.chapterById("egypt").levelReference.present());
        assertTrue(config.chapterById("egypt").level.pamPath.contains("DANGER_NODE"));
        assertEquals("unlocked_animation", config.chapterById("dark").level.unlockAnimationClip);
        assertTrue(config.layout.levelLineDistance > 0f);
        assertTrue(config.layout.levelScale > 0f);
        assertTrue(ConfigFiles.resolve("ui/level_select_menu.json").isFile());
    }

    @Test
    void progressMapsCompletedCurrentAndLockedLevels() {
        GameProgress progress = new GameProgress();
        progress.markLevelCompleted(SeasonType.ANCIENT_EGYPT, 2);
        assertEquals(
            LevelSelectProgress.NodeState.UNLOCKED,
            LevelSelectProgress.state(progress, SeasonType.ANCIENT_EGYPT, 1, 4)
        );
        assertEquals(
            LevelSelectProgress.NodeState.UNLOCKED,
            LevelSelectProgress.state(progress, SeasonType.ANCIENT_EGYPT, 2, 4)
        );
        assertEquals(
            LevelSelectProgress.NodeState.NEWLY_UNLOCKED,
            LevelSelectProgress.state(progress, SeasonType.ANCIENT_EGYPT, 3, 4)
        );
        assertEquals(
            LevelSelectProgress.NodeState.LOCKED,
            LevelSelectProgress.state(progress, SeasonType.ANCIENT_EGYPT, 4, 4)
        );
        assertEquals(
            LevelSelectProgress.NodeState.LOCKED,
            LevelSelectProgress.state(progress, SeasonType.ANCIENT_EGYPT, 0, 4)
        );
        assertEquals(
            LevelSelectProgress.NodeState.LOCKED,
            LevelSelectProgress.state(null, SeasonType.ANCIENT_EGYPT, 2, 4)
        );
        assertEquals(
            LevelSelectProgress.NodeState.NEWLY_UNLOCKED,
            LevelSelectProgress.state(null, SeasonType.ANCIENT_EGYPT, 1, 4)
        );
    }

    @Test
    void clipResolverKeepsExactAndCaseInsensitiveMatchesWithoutSubstitution() {
        List<String> clips = List.of("Active", "defeated", "locked_idle");
        assertEquals("Active", LevelSelectClipResolver.resolve("active", clips, "ice.pam"));
        assertEquals("locked_idle", LevelSelectClipResolver.resolve("locked_idle", clips, "ice.pam"));
        assertNull(LevelSelectClipResolver.resolve("idle", clips, "ice.pam"));
        assertNull(LevelSelectClipResolver.resolve("missing", clips, "ice.pam"));
        assertNull(LevelSelectClipResolver.resolve("active", List.of(), "ice.pam"));
    }
}
