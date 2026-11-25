package game.world.chapter.definition;

import app.enums.SeasonType;
import app.enums.lawnmower.LawnMowerType;
import app.enums.obstacle.ObstacleType;
import game.world.seasons.AncientEgyptSeason;
import game.world.seasons.BigWaveBeachSeason;
import game.world.seasons.DarkAgesSeason;
import game.world.seasons.FrostBiteCavesSeason;
import game.world.seasons.Season;
import game.world.tide.TideConfig;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class SeasonProfiles {
    public enum MapSlot {
        NORMAL,
        BOSS
    }

    public record Profile(
        SeasonType seasonType,
        LawnMowerType lawnMowerType,
        List<ObstacleType> obstacleTypes,
        String normalMapPath,
        String bossMapPath,
        Supplier<Season> defaultSeason
    ) {
        public String mapPath(MapSlot slot) {
            return slot == MapSlot.BOSS ? bossMapPath : normalMapPath;
        }

        public Season createSeason() {
            return defaultSeason.get();
        }
    }

    private static final String EGYPT_MAP = "Maps/egypt.tmx";
    private static final String EGYPT_BOSS_MAP = "Maps/egypt-boss.tmx";
    private static final String DARK_MAP = "Maps/dark.tmx";
    private static final String DARK_BOSS_MAP = "Maps/darkboss_map.tmx";
    private static final String ICE_MAP = "Maps/IceAge.tmx";
    private static final String ICE_BOSS_MAP = "Maps/IceAgeBoss.tmx";
    private static final String BEACH_MAP = "Maps/BigWaveBeach.tmx";
    private static final String BEACH_BOSS_MAP = "Maps/BigWaveBeachBoss.tmx";

    private static final Map<SeasonType, Profile> PROFILES = new EnumMap<>(SeasonType.class);

    static {
        PROFILES.put(SeasonType.ANCIENT_EGYPT, new Profile(
            SeasonType.ANCIENT_EGYPT,
            LawnMowerType.EGYPT,
            List.of(
                ObstacleType.EGYPT_GRAVE,
                ObstacleType.PERMANENT_SCORCHED,
                ObstacleType.TEMPORARY_SCORCH,
                ObstacleType.BRAIN
            ),
            EGYPT_MAP,
            EGYPT_BOSS_MAP,
            AncientEgyptSeason::new
        ));
        PROFILES.put(SeasonType.DARK_AGES, new Profile(
            SeasonType.DARK_AGES,
            LawnMowerType.DARK,
            List.of(
                ObstacleType.TUTORIAL_GRAVE,
                ObstacleType.SUN_GRAVE,
                ObstacleType.PLANTFOOD_GRAVE,
                ObstacleType.NECROMANCY_TILE,
                ObstacleType.PERMANENT_SCORCHED,
                ObstacleType.TEMPORARY_SCORCH,
                ObstacleType.BRAIN
            ),
            DARK_MAP,
            DARK_BOSS_MAP,
            DarkAgesSeason::new
        ));
        PROFILES.put(SeasonType.BIG_WAVE_BEACH, new Profile(
            SeasonType.BIG_WAVE_BEACH,
            LawnMowerType.BEACH,
            List.of(
                ObstacleType.PUDDLE,
                ObstacleType.GARGANTUAR_PUDDLE,
                ObstacleType.PERMANENT_SCORCHED,
                ObstacleType.TEMPORARY_SCORCH,
                ObstacleType.BRAIN
            ),
            BEACH_MAP,
            BEACH_BOSS_MAP,
            () -> new BigWaveBeachSeason(TideConfig.bigWaveBeachDefault())
        ));
        PROFILES.put(SeasonType.FROSTBITE_CAVES, new Profile(
            SeasonType.FROSTBITE_CAVES,
            LawnMowerType.ICEAGE,
            List.of(
                ObstacleType.SLIDING_TILE_UP,
                ObstacleType.SLIDING_TILE_DOWN,
                ObstacleType.ICE_BLOCK,
                ObstacleType.MAMMOTH_GLACIER,
                ObstacleType.BOSS_ICE,
                ObstacleType.SLINGSHOT,
                ObstacleType.SNOWBALL,
                ObstacleType.FIREBALL,
                ObstacleType.MISSILE,
                ObstacleType.PERMANENT_SCORCHED,
                ObstacleType.TEMPORARY_SCORCH,
                ObstacleType.BRAIN
            ),
            ICE_MAP,
            ICE_BOSS_MAP,
            FrostBiteCavesSeason::new
        ));
    }

    private SeasonProfiles() {
    }

    public static Profile of(SeasonType seasonType) {
        if (seasonType == null) {
            return null;
        }
        return PROFILES.get(seasonType);
    }

    public static Season beachSeason(TideConfig tideConfig) {
        return new BigWaveBeachSeason(
            tideConfig != null ? tideConfig : TideConfig.bigWaveBeachDefault()
        );
    }
}
