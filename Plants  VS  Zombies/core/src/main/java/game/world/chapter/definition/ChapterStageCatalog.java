package game.world.chapter.definition;

import app.enums.SeasonType;
import game.world.chapter.ChapterId;
import game.world.chapter.ChapterType;
import game.world.seasons.AncientEgyptSeason;
import game.world.seasons.DarkAgesSeason;
import game.world.seasons.FrostBiteCavesSeason;
import game.world.seasons.Season;
import game.world.tide.TideConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ChapterStageCatalog {
    public record StageSpec(
        ChapterId id,
        ChapterType type,
        int baseWaves,
        String description,
        SeasonProfiles.MapSlot mapSlot,
        Supplier<Season> seasonFactory,
        boolean needsPlantSelection
    ) {
        public SeasonType seasonType() {
            return id.seasonType();
        }

        public int stageNumber() {
            return id.chapterNumber();
        }
    }

    private static final Map<ChapterId, StageSpec> STAGES = new HashMap<>();

    static {
        registerEgypt();
        registerDarkAges();
        registerBeach();
        registerFrostbite();
    }

    private ChapterStageCatalog() {
    }

    public static StageSpec spec(ChapterId id) {
        if (id == null) {
            return null;
        }
        return STAGES.get(id);
    }

    public static StageSpec spec(SeasonType season, int stage) {
        return spec(new ChapterId(season, stage));
    }

    private static void stage(
        SeasonType season,
        int number,
        ChapterType type,
        int waves,
        String description,
        SeasonProfiles.MapSlot mapSlot,
        Supplier<Season> seasonFactory,
        boolean needsPlantSelection
    ) {
        ChapterId id = new ChapterId(season, number);
        STAGES.put(id, new StageSpec(id, type, waves, description, mapSlot, seasonFactory, needsPlantSelection));
    }

    private static void registerEgypt() {
        stage(SeasonType.ANCIENT_EGYPT, 1, ChapterType.NORMAL, 5,
            "Goals: 1. Plant 3 Sunflowers within the first minute. 2. Defeat 10 zombies total. 3. Use at least one Wall-Nut to protect your front line.",
            SeasonProfiles.MapSlot.NORMAL, AncientEgyptSeasonFactory::create, true);
        stage(SeasonType.ANCIENT_EGYPT, 2, ChapterType.SPECIAL, 4,
            "Deadline stage — if a zombie crosses the gold line, the desert is lost.",
            SeasonProfiles.MapSlot.NORMAL, AncientEgyptSeasonFactory::create, true);
        stage(SeasonType.ANCIENT_EGYPT, 3, ChapterType.SPECIAL, 5,
            "Sidebar special stage — plants arrive on the conveyor. Clear the graves and hold the line.",
            SeasonProfiles.MapSlot.NORMAL, AncientEgyptSeasonFactory::create, true);
        stage(SeasonType.ANCIENT_EGYPT, 4, ChapterType.BOSS, 1,
            "The Egypt boss rises. Survive the final assault on the sacred lawn.",
            SeasonProfiles.MapSlot.BOSS, AncientEgyptSeasonFactory::create, true);
    }

    private static void registerDarkAges() {
        stage(SeasonType.DARK_AGES, 1, ChapterType.NORMAL, 3,
            "Goals: 1. Maintain a sun bank of at least 500. 2. Use a multi-lane or splash-damage plant. 3. Finish the level without losing any lawnmowers.",
            SeasonProfiles.MapSlot.NORMAL, DarkAgesSeasonFactory::create, true);
        stage(SeasonType.DARK_AGES, 2, ChapterType.SPECIAL, 4,
            "No sun from the sky. Time is the most important factor , kill zombies in the required time.",
            SeasonProfiles.MapSlot.NORMAL, DarkAgesSeasonFactory::create, true);
        stage(SeasonType.DARK_AGES, 3, ChapterType.SPECIAL, 5,
            "No sun from the sky. Plant what you got.",
            SeasonProfiles.MapSlot.NORMAL, DarkAgesSeasonFactory::create, true);
        stage(SeasonType.DARK_AGES, 4, ChapterType.BOSS, 1,
            "The Dark Ages boss commands the night. End the reign of the undead.",
            SeasonProfiles.MapSlot.BOSS, DarkAgesSeasonFactory::create, true);
    }

    private static void registerBeach() {
        stage(SeasonType.BIG_WAVE_BEACH, 1, ChapterType.NORMAL, 4,
            " Goals: 1. Deploy at least 2 Lily Pads to save space. 2. Defeat 5 zombies while they are in the water. 3. Ensure every lane has at least one plant protecting it.",
            SeasonProfiles.MapSlot.NORMAL,
            () -> SeasonProfiles.beachSeason(TideConfig.bigWaveBeachChapter1()),
            true);
        stage(SeasonType.BIG_WAVE_BEACH, 2, ChapterType.NORMAL, 6,
            "Goals: 1. Place at least 3 Lily Pads on the water tiles. 2. Use a plant to attack a zombie in the water. 3. Keep your sun production steady even during the high tide waves.",
            SeasonProfiles.MapSlot.NORMAL,
            () -> SeasonProfiles.beachSeason(TideConfig.bigWaveBeachDefault()),
            true);
        stage(SeasonType.BIG_WAVE_BEACH, 3, ChapterType.NORMAL, 6,
            "The tide climbs farther up the sand. Hold the line as water swallows more columns.",
            SeasonProfiles.MapSlot.NORMAL,
            () -> SeasonProfiles.beachSeason(TideConfig.bigWaveBeachDefault()),
            true);
        stage(SeasonType.BIG_WAVE_BEACH, 4, ChapterType.BOSS, 1,
            "The beach boss crashes ashore. Survive the final high tide.",
            SeasonProfiles.MapSlot.BOSS,
            () -> SeasonProfiles.beachSeason(TideConfig.bigWaveBeachChapter1()),
            true);
    }

    private static void registerFrostbite() {
        stage(SeasonType.FROSTBITE_CAVES, 1, ChapterType.NORMAL, 3,
            "Goals: 1. Plant 3 heating plants to keep your garden warm. 2. Defeat 5 zombies using a plant that deals area damage. 3. Successfully defend all 5 lanes.",
            SeasonProfiles.MapSlot.NORMAL, FrostbiteSeasonFactory::create, true);
        stage(SeasonType.FROSTBITE_CAVES, 2, ChapterType.NORMAL, 3,
            "Goals: 1. Protect your Sunflowers with at least two Wall-Nuts. 2. Use a plant to slow down a group of zombies. 3. Reach the end of the level without using any Plant Food.",
            SeasonProfiles.MapSlot.NORMAL, FrostbiteSeasonFactory::create, true);
        stage(SeasonType.FROSTBITE_CAVES, 3, ChapterType.SPECIAL, 3,
            "Protect the endangered plants. If any protected plant falls, you lose.",
            SeasonProfiles.MapSlot.NORMAL, FrostbiteSeasonFactory::create, true);
        stage(SeasonType.FROSTBITE_CAVES, 4, ChapterType.BOSS, 1,
            "The Mammoth boss of Frostbite Caves. End the ice age assault.",
            SeasonProfiles.MapSlot.BOSS, FrostbiteSeasonFactory::create, true);
    }

    private static final class AncientEgyptSeasonFactory {
        private AncientEgyptSeasonFactory() {
        }

        private static Season create() {
            SeasonProfiles.Profile profile = SeasonProfiles.of(SeasonType.ANCIENT_EGYPT);
            return profile != null ? profile.createSeason() : new AncientEgyptSeason();
        }
    }

    private static final class DarkAgesSeasonFactory {
        private DarkAgesSeasonFactory() {
        }

        private static Season create() {
            SeasonProfiles.Profile profile = SeasonProfiles.of(SeasonType.DARK_AGES);
            return profile != null ? profile.createSeason() : new DarkAgesSeason();
        }
    }

    private static final class FrostbiteSeasonFactory {
        private FrostbiteSeasonFactory() {
        }

        private static Season create() {
            SeasonProfiles.Profile profile = SeasonProfiles.of(SeasonType.FROSTBITE_CAVES);
            return profile != null ? profile.createSeason() : new FrostBiteCavesSeason();
        }
    }
}

