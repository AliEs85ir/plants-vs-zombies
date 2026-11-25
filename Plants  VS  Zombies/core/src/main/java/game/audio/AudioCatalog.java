package game.audio;

import app.enums.SeasonType;
import game.world.chapter.Chapter;
import game.world.chapter.ChapterType;
import game.world.minigame.izombie.IZombieRules;
import game.world.minigame.vasebreaker.VaseBreakerMode;

public final class AudioCatalog {
    private AudioCatalog() {
    }

    public static AudioId musicFor(Chapter chapter) {
        if (IZombieRules.matches(chapter)) {
            return AudioId.PVP_BATTLE;
        }
        SeasonPack pack = pack(chapter);
        if (pack == null) {
            return AudioId.LEVEL_MUSIC;
        }
        if (chapter != null && chapter.getChapterType() == ChapterType.BOSS) {
            return pack.ultimateBattle != null ? pack.ultimateBattle : pack.level;
        }
        return pack.level;
    }

    public static AudioId chooseSeedsFor(Chapter chapter) {
        if (IZombieRules.matches(chapter)) {
            return AudioId.PVP_MENU;
        }
        SeasonPack pack = pack(chapter);
        if (pack == null || pack.chooseSeeds == null) {
            return AudioId.MENU_MUSIC;
        }
        return pack.chooseSeeds;
    }

    public static AudioId waveFor(Chapter chapter, int waveNumber) {
        SeasonPack pack = pack(chapter);
        if (pack == null) {
            return null;
        }
        int total = chapter != null ? chapter.getTotalWaves() : 0;
        if (total > 0 && waveNumber >= total) {
            return pack.finalWave;
        }
        if (waveNumber <= 1) {
            return pack.firstWave;
        }
        return pack.midWave(waveNumber);
    }

    public static AudioId victoryFor(Chapter chapter) {
        if (chapter != null && chapter.getMinigameLogic() instanceof VaseBreakerMode) {
            return AudioId.PINATA_REWARD;
        }
        SeasonPack pack = pack(chapter);
        if (pack == null || pack.victory == null) {
            return AudioId.WIN_MUSIC;
        }
        return pack.victory;
    }

    public static AudioId gameOverFor(Chapter chapter) {
        SeasonPack pack = pack(chapter);
        if (pack == null || pack.gameOver == null) {
            return AudioId.LOSE_MUSIC;
        }
        return pack.gameOver;
    }

    public static AudioId zombossIntroFor(Chapter chapter) {
        SeasonPack pack = pack(chapter);
        if (pack == null || pack.zombossIntro == null) {
            return AudioId.ZOMBOSS_INTRO;
        }
        return pack.zombossIntro;
    }

    public static AudioId rewardFor(Chapter chapter) {
        SeasonPack pack = pack(chapter);
        if (pack == null || pack.reward == null) {
            return AudioId.REWARD_JINGLE;
        }
        return pack.reward;
    }

    public static void playChooseSeeds(Chapter chapter) {
        AudioManager.play(chooseSeedsFor(chapter), MusicTransition.fade(0.35f));
    }

    public static void playLevelTheme(Chapter chapter) {
        AudioManager.play(musicFor(chapter), MusicTransition.fade(0.45f));
    }

    public static void playWave(Chapter chapter, int waveNumber) {
        AudioId stinger = waveFor(chapter, waveNumber);
        if (stinger != null) {
            AudioManager.play(stinger);
        }
        Sfx.waveStarted(chapter, waveNumber);
    }

    private static SeasonPack pack(Chapter chapter) {
        if (chapter == null || chapter.getSeason() == null || chapter.getSeason().getSeasonType() == null) {
            return MINIGAME;
        }
        return switch (chapter.getSeason().getSeasonType()) {
            case ANCIENT_EGYPT -> EGYPT;
            case FROSTBITE_CAVES -> CAVES;
            case BIG_WAVE_BEACH -> BEACH;
            case DARK_AGES -> DARK_AGES;
            case MINIGAME -> MINIGAME;
        };
    }

    private static final SeasonPack EGYPT = new SeasonPack(
        AudioId.EGYPT_CHOOSE_SEEDS,
        AudioId.EGYPT_LEVEL,
        AudioId.EGYPT_FIRST_WAVE,
        AudioId.EGYPT_MIDWAVE,
        null,
        AudioId.EGYPT_FINAL_WAVE,
        AudioId.EGYPT_GAME_OVER,
        AudioId.EGYPT_VICTORY,
        AudioId.EGYPT_ULTIMATE_BATTLE,
        AudioId.EGYPT_ZOMBOSS_INTRO,
        AudioId.EGYPT_REWARD
    );

    private static final SeasonPack CAVES = new SeasonPack(
        AudioId.CAVES_CHOOSE_SEEDS,
        AudioId.CAVES_LEVEL,
        AudioId.CAVES_FIRST_WAVE,
        AudioId.CAVES_MIDWAVE_A,
        AudioId.CAVES_MIDWAVE_B,
        AudioId.CAVES_FINAL_WAVE,
        AudioId.CAVES_GAME_OVER,
        AudioId.CAVES_VICTORY,
        AudioId.CAVES_ULTIMATE_BATTLE,
        AudioId.CAVES_ZOMBOSS_INTRO,
        AudioId.CAVES_REWARD
    );

    private static final SeasonPack BEACH = new SeasonPack(
        AudioId.BEACH_CHOOSE_SEEDS,
        AudioId.LEVEL_MUSIC,
        AudioId.BEACH_FIRST_WAVE,
        AudioId.BEACH_MIDWAVE_A,
        AudioId.BEACH_MIDWAVE_B,
        AudioId.BEACH_FINAL_WAVE,
        AudioId.BEACH_GAME_OVER,
        AudioId.BEACH_VICTORY,
        AudioId.BEACH_ULTIMATE_BATTLE,
        AudioId.ZOMBOSS_INTRO,
        AudioId.BEACH_REWARD
    );

    private static final SeasonPack DARK_AGES = new SeasonPack(
        AudioId.DARK_AGES_CHOOSE_SEEDS,
        AudioId.DARK_AGES_LEVEL,
        AudioId.DARK_AGES_FIRST_WAVE,
        AudioId.DARK_AGES_MIDWAVE_A,
        AudioId.DARK_AGES_MIDWAVE_B,
        AudioId.DARK_AGES_FINAL_WAVE,
        AudioId.DARK_AGES_GAME_OVER,
        AudioId.DARK_AGES_VICTORY,
        AudioId.DARK_AGES_ULTIMATE_BATTLE,
        AudioId.DARK_AGES_ZOMBOSS_INTRO,
        AudioId.DARK_AGES_REWARD
    );

    private static final SeasonPack MINIGAME = new SeasonPack(
        null,
        AudioId.LEVEL_MUSIC,
        AudioId.MINIGAME_WAVES,
        AudioId.MINIGAME_WAVES,
        null,
        AudioId.MINIGAME_WAVES,
        AudioId.MINIGAME_GAME_OVER,
        AudioId.MINIGAME_VICTORY,
        null,
        AudioId.ZOMBOSS_INTRO,
        AudioId.REWARD_JINGLE
    );

    private record SeasonPack(
        AudioId chooseSeeds,
        AudioId level,
        AudioId firstWave,
        AudioId midwaveA,
        AudioId midwaveB,
        AudioId finalWave,
        AudioId gameOver,
        AudioId victory,
        AudioId ultimateBattle,
        AudioId zombossIntro,
        AudioId reward
    ) {
        AudioId midWave(int waveNumber) {
            if (midwaveB == null) {
                return midwaveA;
            }
            return waveNumber % 2 == 0 ? midwaveB : midwaveA;
        }
    }
}
