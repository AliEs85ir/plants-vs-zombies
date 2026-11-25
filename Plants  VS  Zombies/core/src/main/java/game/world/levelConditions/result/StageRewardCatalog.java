package game.world.levelConditions.result;

import app.enums.SeasonType;
import game.world.chapter.ChapterId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StageRewardCatalog {
    public enum RewardKind {
        COINS,
        DIAMONDS,
        GREENHOUSE_TABLE,
        GREENHOUSE_POT,
        SEED_PACKETS_RANDOM_UNLOCKED,
        UNLOCK_RANDOM_LOCKED_PLANTS
    }

    public record RewardSpec(
        RewardKind kind,
        int amount,
        boolean uniqueAmongBatch
    ) {
        public static RewardSpec coins(int amount) {
            return new RewardSpec(RewardKind.COINS, amount, false);
        }

        public static RewardSpec diamonds(int amount) {
            return new RewardSpec(RewardKind.DIAMONDS, amount, false);
        }

        public static RewardSpec table() {
            return new RewardSpec(RewardKind.GREENHOUSE_TABLE, 1, false);
        }

        public static RewardSpec pot() {
            return new RewardSpec(RewardKind.GREENHOUSE_POT, 1, false);
        }

        public static RewardSpec seeds(int amount, boolean unique) {
            return new RewardSpec(RewardKind.SEED_PACKETS_RANDOM_UNLOCKED, amount, unique);
        }

        public static RewardSpec unlockLocked(int amount) {
            return new RewardSpec(RewardKind.UNLOCK_RANDOM_LOCKED_PLANTS, amount, true);
        }
    }

    private static final Map<ChapterId, List<RewardSpec>> REWARDS = new HashMap<>();

    static {
        put(SeasonType.ANCIENT_EGYPT, 1,
            RewardSpec.coins(200), RewardSpec.diamonds(2));
        put(SeasonType.ANCIENT_EGYPT, 2,
            RewardSpec.coins(300), RewardSpec.diamonds(2), RewardSpec.table());
        put(SeasonType.ANCIENT_EGYPT, 3,
            RewardSpec.coins(500), RewardSpec.diamonds(3), RewardSpec.seeds(30, false));
        put(SeasonType.ANCIENT_EGYPT, 4,
            RewardSpec.coins(600), RewardSpec.diamonds(2), RewardSpec.seeds(150, false));

        put(SeasonType.FROSTBITE_CAVES, 1,
            RewardSpec.coins(300), RewardSpec.diamonds(4), RewardSpec.table());
        put(SeasonType.FROSTBITE_CAVES, 2,
            RewardSpec.coins(400), RewardSpec.diamonds(2), RewardSpec.pot(), RewardSpec.seeds(40, false));
        put(SeasonType.FROSTBITE_CAVES, 3,
            RewardSpec.coins(500), RewardSpec.diamonds(4), RewardSpec.seeds(30, false));
        put(SeasonType.FROSTBITE_CAVES, 4,
            RewardSpec.coins(600), RewardSpec.diamonds(4),
            RewardSpec.seeds(100, true), RewardSpec.seeds(100, true));

        put(SeasonType.DARK_AGES, 1,
            RewardSpec.coins(100), RewardSpec.diamonds(6), RewardSpec.table(),
            RewardSpec.seeds(20, true), RewardSpec.seeds(20, true));
        put(SeasonType.DARK_AGES, 2,
            RewardSpec.coins(400), RewardSpec.diamonds(2), RewardSpec.pot(), RewardSpec.table(),
            RewardSpec.seeds(70, false));
        put(SeasonType.DARK_AGES, 3,
            RewardSpec.coins(1500), RewardSpec.diamonds(1), RewardSpec.unlockLocked(15));
        put(SeasonType.DARK_AGES, 4,
            RewardSpec.coins(50), RewardSpec.diamonds(15),
            RewardSpec.seeds(100, true), RewardSpec.seeds(100, true));

        put(SeasonType.BIG_WAVE_BEACH, 1,
            RewardSpec.coins(300), RewardSpec.diamonds(4), RewardSpec.table(),
            RewardSpec.seeds(20, true), RewardSpec.seeds(20, true));
        put(SeasonType.BIG_WAVE_BEACH, 2,
            RewardSpec.coins(400), RewardSpec.diamonds(2), RewardSpec.pot(), RewardSpec.table(),
            RewardSpec.seeds(10, false));
        put(SeasonType.BIG_WAVE_BEACH, 3,
            RewardSpec.coins(200), RewardSpec.diamonds(4),
            RewardSpec.seeds(15, true), RewardSpec.seeds(15, true), RewardSpec.seeds(15, true));
        put(SeasonType.BIG_WAVE_BEACH, 4,
            RewardSpec.coins(500), RewardSpec.diamonds(30),
            RewardSpec.seeds(100, true), RewardSpec.seeds(100, true));
    }

    private StageRewardCatalog() {
    }

    private static void put(SeasonType season, int stage, RewardSpec... specs) {
        REWARDS.put(new ChapterId(season, stage), List.of(specs));
    }

    public static List<RewardSpec> rewardsFor(SeasonType season, int stage) {
        if (season == null || season == SeasonType.MINIGAME) {
            return List.of();
        }
        return REWARDS.getOrDefault(new ChapterId(season, stage), List.of());
    }

    public static List<RewardSpec> rewardsFor(ChapterId id) {
        if (id == null) {
            return List.of();
        }
        return rewardsFor(id.seasonType(), id.chapterNumber());
    }

    public static Map<ChapterId, List<RewardSpec>> all() {
        return Collections.unmodifiableMap(REWARDS);
    }
}
