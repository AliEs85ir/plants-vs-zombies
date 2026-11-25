package game.world.chapter.meta;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import game.world.chapter.ChapterType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ChapterMetadataRegistry {

    private static final String JSON_PATH = "chapters/chapter_info.json";
    private static final int DEFAULT_SELECTION_COUNT = 8;

    private static final Map<String, ChapterMetadata> OVERRIDES = new HashMap<>();
    private static boolean loaded = false;

    private ChapterMetadataRegistry() {}

    public static synchronized void load() {
        if (loaded) return;
        loaded = true;

        try {
            if (!Gdx.files.internal(JSON_PATH).exists()) {
                Gdx.app.log("ChapterMetadataRegistry",
                    "No overrides at " + JSON_PATH + ", using generated metadata.");
                return;
            }

            Json json = new Json();
            JsonValue root = json.fromJson(null, Gdx.files.internal(JSON_PATH));
            if (root == null) return;

            for (JsonValue node = root.child; node != null; node = node.next) {
                OVERRIDES.put(node.name.toUpperCase(), parse(node));
            }
        } catch (Exception e) {
            Gdx.app.error("ChapterMetadataRegistry", "Failed to load " + JSON_PATH, e);
        }
    }

    private static ChapterMetadata parse(JsonValue node) {
        List<PlantName> recommended = new ArrayList<>();
        JsonValue recommendedNode = node.get("recommended");
        if (recommendedNode != null) {
            for (JsonValue entry = recommendedNode.child; entry != null; entry = entry.next) {
                try {
                    recommended.add(PlantName.valueOf(entry.asString()));
                } catch (IllegalArgumentException e) {
                    Gdx.app.error("ChapterMetadataRegistry",
                        "Unknown PlantName in recommended list: " + entry.asString());
                }
            }
        }
        return new ChapterMetadata(
            node.getString("description", null),
            node.getString("difficulty", null),
            node.getInt("selectionCount", -1),
            recommended
        );
    }

    public static String key(SeasonType seasonType, int chapterNumber) {
        String season = (seasonType != null) ? seasonType.name() : "UNKNOWN";
        return season + "-" + chapterNumber;
    }

    public static ChapterMetadata resolve(SeasonType seasonType, int chapterNumber,
                                          ChapterType chapterType, int totalWaves,
                                          List<PlantName> availablePlants,
                                          String inlineDescription, int inlineSelectionCount,
                                          List<PlantName> inlineRecommended) {
        if (!loaded) load();

        ChapterMetadata override = OVERRIDES.get(key(seasonType, chapterNumber));

        String description = firstNonEmpty(
            inlineDescription,
            (override != null) ? override.getDescription() : null,
            generateDescription(seasonType, chapterNumber, chapterType, totalWaves, availablePlants));

        int selectionCount = firstPositive(
            inlineSelectionCount,
            (override != null) ? override.getSelectionCount() : -1,
            DEFAULT_SELECTION_COUNT);
        if (availablePlants != null && !availablePlants.isEmpty()) {
            selectionCount = Math.min(selectionCount, availablePlants.size());
        }

        List<PlantName> configured = (inlineRecommended != null && !inlineRecommended.isEmpty())
            ? inlineRecommended
            : ((override != null) ? override.getRecommendedPlants() : List.of());

        List<PlantName> recommended =
            RecommendedPlantResolver.resolve(configured, availablePlants, selectionCount);

        String difficulty = firstNonEmpty(
            (override != null) ? override.getDifficulty() : null,
            generateDifficulty(chapterType, totalWaves));

        return new ChapterMetadata(description, difficulty, selectionCount, recommended);
    }

    private static String generateDescription(SeasonType seasonType, int chapterNumber,
                                              ChapterType chapterType, int totalWaves,
                                              List<PlantName> availablePlants) {
        String season = readableSeason(seasonType);
        int plantCount = (availablePlants != null) ? availablePlants.size() : 0;

        StringBuilder builder = new StringBuilder();
        builder.append(seasonFlavour(seasonType));
        builder.append(" This is ").append(season)
               .append(" chapter ").append(chapterNumber).append(".");

        if (chapterType == ChapterType.BOSS) {
            builder.append(" A boss zombie leads the assault, so bring sustained damage"
                         + " and something that can stall it.");
        } else if (chapterType == ChapterType.MINIGAME) {
            builder.append(" Special rules apply in this challenge, so read the lawn"
                         + " carefully before committing your lineup.");
        } else if (chapterType == ChapterType.SPECIAL) {
            builder.append(" Conditions on this lawn are unusual, so favour flexible plants.");
        }

        builder.append(" Survive ").append(totalWaves)
               .append(totalWaves == 1 ? " wave" : " waves");
        if (plantCount > 0) {
            builder.append(" using a lineup drawn from ").append(plantCount)
                   .append(" available plants");
        }
        builder.append(". Balance sun production, offense and defense before you begin.");

        return builder.toString();
    }

    private static String seasonFlavour(SeasonType seasonType) {
        if (seasonType == null) return "An unknown threat approaches.";
        switch (seasonType) {
            case ANCIENT_EGYPT:
                return "Sand shifts across the ancient tombs and the dead do not rest.";
            case DARK_AGES:
                return "Night never lifts over these ruins and torchlight is your only warning.";
            case BIG_WAVE_BEACH:
                return "The tide rolls in and brings the horde with it.";
            case FROSTBITE_CAVES:
                return "Bitter wind howls through the ice caverns and freezes the unprepared.";
            case MINIGAME:
                return "An unusual challenge awaits on this lawn.";
            default:
                return "A new threat approaches your garden.";
        }
    }

    private static String generateDifficulty(ChapterType chapterType, int totalWaves) {
        if (chapterType == ChapterType.BOSS) return "Boss";
        if (chapterType == ChapterType.MINIGAME) return "Special";
        if (totalWaves >= 20) return "Hard";
        if (totalWaves >= 10) return "Normal";
        return "Easy";
    }

    private static String readableSeason(SeasonType seasonType) {
        if (seasonType == null) return "this";
        String[] words = seasonType.name().toLowerCase().split("_");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (builder.length() > 0) builder.append(' ');
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }

    private static String firstNonEmpty(String... candidates) {
        for (String candidate : candidates) {
            if (candidate != null && !candidate.trim().isEmpty()) {
                return candidate;
            }
        }
        return "";
    }

    private static int firstPositive(int... candidates) {
        for (int candidate : candidates) {
            if (candidate > 0) return candidate;
        }
        return DEFAULT_SELECTION_COUNT;
    }
}
