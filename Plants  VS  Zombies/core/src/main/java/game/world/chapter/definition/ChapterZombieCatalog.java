package game.world.chapter.definition;

import app.enums.SeasonType;
import game.world.chapter.ChapterId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ChapterZombieCatalog {
    private static final Map<ChapterId, List<String>> ZOMBIES = new HashMap<>();

    /**
     * hand dropping and head dropping and armor dropping zombies :
     * "Basic Egypt", "Basic Halloween", "Basic West", "Basic West2",
     *  "Basic Pirate", "Basic IceAge", "ZombieNewspaper"
     */

    static {
        put(SeasonType.ANCIENT_EGYPT, 1,
            "Basic Egypt",
            "Basic Egypt Flag", "ZombieRa", "ZombieModernAllStar",
            "ZombieArcade", "Basic Halloween", "Halloween Flag",
            "Basic Pirate", "Basic West", "Basic West2", "Birthday Basic",
            "Birthday Basic2", "Birthday Basic3",
            "ZombieCrystalSkull",
            "Egypt Camel",
            "Egypt Flag", "Egypt Flag2", "ZombieNewspaper", "ZombieProspector",
            "ZombieTombRaiser", "Tutorial Flag", "Valentines Basic", "West Flag", "West Flag2",
            "West Flag3", "West Flag4", "West Flag5", "West Flag6", "West Flag7", "West Flag8", "West Flag9",
            "West Flag10", "West Flag11", "ZombieWizard", "Gargantuar11", "Gargantuar8", "Gargantuar7", "Imp8"
        );

        put(SeasonType.ANCIENT_EGYPT, 2, "Basic Egypt", "ZombieRa", "ZombieTombRaiser",
            "Egypt Flag", "Egypt Flag2", "ZombieNewspaper", "ZombieProspector",
            "ZombieTombRaiser", "West Flag5", "West Flag6", "West Flag7", "West Flag8", "West Flag9",
            "West Flag10", "West Flag11", "ZombieWizard", "Gargantuar11", "Gargantuar8", "Gargantuar7", "Imp8",
            "Imp1", "Imp2", "Imp3", "Imp4", "Imp5", "Imp6", "Imp7", "Imp10", "Imp11", "Imp12", "Imp13", "Imp9",
            "Gargantuar1", "Gargantuar2", "Gargantuar4", "Gargantuar13");
        put(SeasonType.ANCIENT_EGYPT, 3, "ZombieArcade",
            "ZombieIceAgeTroglobite", "ZombieBeachFisherman", "jester1",
            "jester2", "jester3", "jester4", "ZombieWizard", "ZombieDarkKing");
        put(SeasonType.ANCIENT_EGYPT, 4, "ZombieRa", "West Flag", "West Flag2",
            "West Flag3", "West Flag4", "West Flag5", "West Flag6", "West Flag7", "West Flag8", "West Flag9",
            "West Flag10", "West Flag11", "Tutorial Flag", "Valentines Basic", "ZombieNewspaper", "ZombieModernAllStar");

        put(SeasonType.DARK_AGES, 1, "Basic Halloween", "ZombieWizard", "jester1", "ZombieDarkKing",
            "jester2", "jester3", "jester4", "Gargantuar13", "Gargantuar4", "Gargantuar2", "Imp9");
        put(SeasonType.DARK_AGES, 2, "Basic Halloween", "ZombieWizard", "jester1",
            "jester2", "jester3", "jester4", "Gargantuar11", "Gargantuar13", "Gargantuar2", "Imp9");
        put(SeasonType.DARK_AGES, 3, "ZombieModernAllStar", "ZombieArcade",
            "Basic Halloween", "ZombieCrystalSkull", "ZombieNewspaper", "ZombieProspector");
        put(SeasonType.DARK_AGES, 4, "Halloween Flag",
            "ZombieNewspaper", "ZombieCrystalSkull", "ZombieProspector", "ZombieModernAllStar", "Gargantuar13");

        put(SeasonType.FROSTBITE_CAVES, 1, "Ice Yeti", "ZombieIceAgeHunter",
            "Basic IceAge", "ZombieIceAgeTroglobite", "jester4", "Gargantuar4", "Gargantuar12");
        put(SeasonType.FROSTBITE_CAVES, 2, "ZombieModernAllStar", "ZombieIceAgeHunter",
            "Basic IceAge", "ZombieIceAgeTroglobite", "jester4", "Gargantuar4", "Gargantuar12", "ZombieArcade",
            "ZombieWizard", "ZombieCrystalSkull");
        put(SeasonType.FROSTBITE_CAVES, 3, "ZombieModernAllStar", "ZombieIceAgeHunter",
            "Basic IceAge", "ZombieIceAgeTroglobite", "Gargantuar12", "ZombieArcade",
            "ZombieWizard", "ZombieCrystalSkull", "Ice Yeti");
        put(SeasonType.FROSTBITE_CAVES, 4, "ZombieIceAgeHunter",
            "Basic IceAge", "ZombieNewspaper");

        put(SeasonType.BIG_WAVE_BEACH, 1,
            "ZombieBeachFisherman", "ZombieBeachSnorkel",
            "Imp5", "Gargantuar5", "ZombieBeachOctopus");
        put(SeasonType.BIG_WAVE_BEACH, 2,
            "ZombieBeachFisherman", "ZombieBeachSnorkel",
            "Imp5", "Gargantuar5", "ZombieBeachOctopus", "ZombieCrystalSkull", "Basic West2", "Birthday Basic");
        put(SeasonType.BIG_WAVE_BEACH, 3,
            "ZombieBeachFisherman", "ZombieBeachSnorkel",
            "Imp5", "Gargantuar5", "ZombieBeachOctopus", "ZombieCrystalSkull", "Basic West2", "Birthday Basic");
        put(SeasonType.BIG_WAVE_BEACH, 4,
            "ZombieBeachFisherman", "ZombieBeachSnorkel", "Imp5", "Basic Pirate", "Basic West");
    }

    private ChapterZombieCatalog() {
    }

    private static void put(SeasonType season, int stage, String... aliases) {
        ZOMBIES.put(new ChapterId(season, stage), List.of(aliases));
    }

    public static List<String> zombiesFor(ChapterId id) {
        if (id == null) {
            return List.of();
        }
        return ZOMBIES.getOrDefault(id, List.of());
    }

    public static List<String> zombiesFor(SeasonType season, int stage) {
        if (season == null || season == SeasonType.MINIGAME) {
            return List.of();
        }
        return zombiesFor(new ChapterId(season, stage));
    }
}
