package game.world.chapter;

import app.enums.SeasonType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ChapterRegistry {
    private static final ChapterFactory FACTORY = new ChapterFactory();
    private static final Map<ChapterId, Supplier<Chapter>> CHAPTERS = new HashMap<>();


    static {
        CHAPTERS.put(new ChapterId(SeasonType.ANCIENT_EGYPT, 1), FACTORY::createAncientEgyptChapter1);
        CHAPTERS.put(new ChapterId(SeasonType.ANCIENT_EGYPT, 2), FACTORY::createAncientEgyptChapter2);
        CHAPTERS.put(new ChapterId(SeasonType.ANCIENT_EGYPT, 3), FACTORY::createAncientEgyptChapter3);
        CHAPTERS.put(new ChapterId(SeasonType.ANCIENT_EGYPT, 4), FACTORY::createAncientEgyptChapter4);

        CHAPTERS.put(new ChapterId(SeasonType.DARK_AGES, 1), FACTORY::createDarkAgesChapter1);
        CHAPTERS.put(new ChapterId(SeasonType.DARK_AGES, 2), FACTORY::createDarkAgesChapter2);
        CHAPTERS.put(new ChapterId(SeasonType.DARK_AGES, 3), FACTORY::createDarkAgesChapter3);
        CHAPTERS.put(new ChapterId(SeasonType.DARK_AGES, 4), FACTORY::createDarkAgesChapter4);

        CHAPTERS.put(new ChapterId(SeasonType.BIG_WAVE_BEACH, 1), FACTORY::createBigWaveBeachChapter1);
        CHAPTERS.put(new ChapterId(SeasonType.BIG_WAVE_BEACH, 2), FACTORY::createBigWaveBeachChapter2);
        CHAPTERS.put(new ChapterId(SeasonType.BIG_WAVE_BEACH, 3), FACTORY::createBigWaveBeachChapter3);
        CHAPTERS.put(new ChapterId(SeasonType.BIG_WAVE_BEACH, 4), FACTORY::createBigWaveBeachChapter4);

        CHAPTERS.put(new ChapterId(SeasonType.FROSTBITE_CAVES, 1), FACTORY::createFrostBiteCavesChapter1);
        CHAPTERS.put(new ChapterId(SeasonType.FROSTBITE_CAVES, 2), FACTORY::createFrostBiteCavesChapter2);
        CHAPTERS.put(new ChapterId(SeasonType.FROSTBITE_CAVES, 3), FACTORY::createFrostBiteCavesChapter3);
        CHAPTERS.put(new ChapterId(SeasonType.FROSTBITE_CAVES, 4), FACTORY::createFrostBiteCavesChapter4);

        CHAPTERS.put(new ChapterId(SeasonType.MINIGAME, 1), FACTORY::createVaseBreakerChapter);
        CHAPTERS.put(new ChapterId(SeasonType.MINIGAME, 2), FACTORY::createScoringGameChapter);
        CHAPTERS.put(new ChapterId(SeasonType.MINIGAME, 3), FACTORY::createIZombieChapter);
        CHAPTERS.put(new ChapterId(SeasonType.MINIGAME, 4), FACTORY::createWallNutBowlingChapter);
        CHAPTERS.put(new ChapterId(SeasonType.MINIGAME, 5), FACTORY::createBeghouledChapter);
        CHAPTERS.put(new ChapterId(SeasonType.MINIGAME, 6), FACTORY::createIZombieCouchPlayChapter);
        CHAPTERS.put(new ChapterId(SeasonType.MINIGAME, 7), FACTORY::createPlantZombiesChapter);
    }
    private ChapterRegistry() {}

    public static Chapter createWorldStage(ChapterId id) {
        return FACTORY.createWorldStage(id);
    }

    public static Chapter createVaseBreakerChapter(int level) {
        return FACTORY.createVaseBreakerChapter(level);
    }

    public static Chapter createWallNutBowlingChapter(int level) {
        return FACTORY.createWallNutBowlingChapter(level);
    }

    public static Chapter createPlantZombiesChapter(int level) {
        return FACTORY.createPlantZombiesChapter(level);
    }

    public static Chapter createBeghouledChapter() {
        return FACTORY.createBeghouledChapter();
    }

    public static Chapter createBeghouledChapter(int level) {
        return FACTORY.createBeghouledChapter(level);
    }

    public static Chapter getChapterByName(String name) {
        ChapterId id = ChapterId.parseNormalChapter(name);
        Supplier<Chapter> supplier = CHAPTERS.get(id);

        if (supplier == null) {
            throw new IllegalArgumentException("Unknown chapter: " + name);
        }

        return supplier.get();
    }

    public static Chapter getMinigameChapterByName(String minigame) {
        ChapterId id = ChapterId.parseMinigameChapter(minigame);
        Supplier<Chapter> supplier = CHAPTERS.get(id);

        if (supplier == null) {
            throw new IllegalArgumentException("Unknown Minigame: " + minigame);
        }

        return supplier.get();
    }

    public static int countForSeason(SeasonType season) {
        if (season == null) {
            return 0;
        }
        int count = 0;
        for (ChapterId id : CHAPTERS.keySet()) {
            if (id.seasonType() == season) {
                count++;
            }
        }
        return count;
    }

    public static ChapterId nextStageId(ChapterId id) {
        if (id == null || id.seasonType() == SeasonType.MINIGAME) {
            return null;
        }
        ChapterId next = new ChapterId(id.seasonType(), id.chapterNumber() + 1);
        return CHAPTERS.containsKey(next) ? next : null;
    }

    public static ChapterId previousStageId(ChapterId id) {
        if (id == null || id.seasonType() == SeasonType.MINIGAME || id.chapterNumber() <= 1) {
            return null;
        }
        ChapterId previous = new ChapterId(id.seasonType(), id.chapterNumber() - 1);
        return CHAPTERS.containsKey(previous) ? previous : null;
    }
}
