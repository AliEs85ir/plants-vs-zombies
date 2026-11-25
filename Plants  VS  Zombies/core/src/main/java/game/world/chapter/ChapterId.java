package game.world.chapter;

import app.enums.SeasonType;

public record ChapterId(SeasonType seasonType, int chapterNumber) {

    public static ChapterId parseNormalChapter(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Chapter name cannot be null.");
        }

        String[] parts = input.trim().split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Chapter name must be in format season.chapter like 1.1");
        }

        int season = Integer.parseInt(parts[0]);
        SeasonType type;

        switch (season) {
            case 1 -> type = SeasonType.ANCIENT_EGYPT;
            case 2 -> type = SeasonType.DARK_AGES;
            case 3 -> type = SeasonType.FROSTBITE_CAVES;
            case 4 -> type = SeasonType.BIG_WAVE_BEACH;
            default -> throw new IllegalArgumentException("incompatible season type");
        }

        int chapter = Integer.parseInt(parts[1]);
        return new ChapterId(type, chapter);
    }

    public String toName() {
        int season = switch (seasonType) {
            case ANCIENT_EGYPT -> 1;
            case DARK_AGES -> 2;
            case FROSTBITE_CAVES -> 3;
            case BIG_WAVE_BEACH -> 4;
            case MINIGAME -> throw new IllegalStateException("Minigame chapters use named ids");
        };
        return season + "." + chapterNumber;
    }

    public static ChapterId parseMinigameChapter(String name) {
        switch (name) {
            case "VASE_BREAKER" -> {
                return new ChapterId(SeasonType.MINIGAME, 1);
            }
            case "SCORING_GAME" -> {
                return new ChapterId(SeasonType.MINIGAME, 2);
            }
            case "I_ZOMBIE" -> {
                return new ChapterId(SeasonType.MINIGAME, 3);
            }
            case "WALLNUT_BOWLING" -> {
                return new ChapterId(SeasonType.MINIGAME, 4);
            }
            case "BEGHOULED" -> {
                return new ChapterId(SeasonType.MINIGAME, 5);
            }
            case "I_ZOMBIE_COUCH" -> {
                return new ChapterId(SeasonType.MINIGAME, 6);
            }
            case "PLANT_ZOMBIES" -> {
                return new ChapterId(SeasonType.MINIGAME, 7);
            }
            default -> throw new IllegalArgumentException("couldn't find minigame with name : " + name);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChapterId other)) return false;
        return seasonType == other.seasonType && chapterNumber == other.chapterNumber;
    }

}
