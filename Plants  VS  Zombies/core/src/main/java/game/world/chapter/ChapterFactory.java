package game.world.chapter;

import app.controller.game.BeghouledController;
import app.controller.game.GameController;
import app.controller.game.IZombieController;
import app.controller.game.IZombieCouchPlayController;
import app.controller.game.PlayScreenController;
import app.controller.game.WalnutBowlingController;
import app.enums.SeasonType;
import app.enums.lawnmower.LawnMowerType;
import app.enums.obstacle.ObstacleType;
import app.enums.plant.PlantName;
import app.view.game.states.CameraIntroState;
import app.view.game.states.PlayScreenState;
import app.view.hud.*;
import game.entity.zombie.data.properties.Property;
import game.world.chapter.definition.ChapterPlantCatalog;
import game.world.chapter.definition.ChapterRulesCatalog;
import game.world.chapter.definition.ChapterStageCatalog;
import game.world.chapter.definition.SeasonProfiles;
import game.world.chapter.definition.StageDifficultyConfig;
import game.world.chapter.definition.ZombiePools;
import game.world.chapter.meta.ChapterMetadata;
import game.world.chapter.meta.ChapterMetadataRegistry;
import game.world.chapter.rule.BossRule;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.scoringgame.ArmorPopPattern;
import game.world.chapter.rule.scoringgame.CloseCallPattern;
import game.world.chapter.rule.scoringgame.ExplodonatorPattern;
import game.world.chapter.rule.scoringgame.GroupKillOncePattern;
import game.world.chapter.rule.scoringgame.GroupKillOneProjectilePattern;
import game.world.chapter.rule.scoringgame.KillComboPattern;
import game.world.chapter.rule.scoringgame.PerfectWavePattern;
import game.world.chapter.rule.scoringgame.QuickZombieKillPattern;
import game.world.chapter.rule.scoringgame.ScoringPattern;
import game.world.chapter.rule.scoringgame.SpudowPattern;
import game.world.chapter.rule.speciallevelRules.*;
import game.world.chapter.definition.ChapterUnlockCatalog;
import game.world.chapter.wave.DefaultWaveFormula;
import game.world.chapter.wave.ScaledWaveFormula;
import game.world.chapter.wave.WaveConfig;
import game.world.chapter.wave.WaveFormula;
import game.world.levelConditions.conditions.*;
import game.world.minigame.Minigame;
import game.world.minigame.beghouled.BeghouledLevelConfig;
import game.world.minigame.beghouled.BeghouledMode;
import game.world.minigame.beghouled.BeghouledUpgradeCatalog;
import game.world.minigame.izombie.IZombieMode;
import game.world.minigame.izombie_couchplay.IZombieCouchPlayMode;
import game.world.minigame.plantzombies.PlantZombiesLevelConfig;
import game.world.minigame.plantzombies.PlantZombiesMode;
import game.world.minigame.vasebreaker.VaseBreakerLevelConfig;
import game.world.minigame.vasebreaker.VaseBreakerMode;
import game.world.minigame.wallnut_bowling.WallNutBowlingLevelConfig;
import game.world.minigame.wallnut_bowling.WallNutBowlingMode;
import game.world.playground.GridConfig;
import game.world.seasons.AncientEgyptSeason;
import game.world.seasons.DarkAgesSeason;
import game.world.seasons.FrostBiteCavesSeason;
import game.world.seasons.Season;

import java.util.ArrayList;
import java.util.List;

public class ChapterFactory {
    private static final List<ObstacleType> MINIGAME_OBSTACLES = List.of(
        ObstacleType.RANDOM_VASE,
        ObstacleType.PLANT_VASE,
        ObstacleType.GARGANTUAR_VASE,
        ObstacleType.WALLNUT,
        ObstacleType.TALLNUT,
        ObstacleType.EXPLODE_O_NUT,
        ObstacleType.PERMANENT_SCORCHED,
        ObstacleType.TEMPORARY_SCORCH,
        ObstacleType.BRAIN
    );

    public Chapter createWorldStage(ChapterId id) {
        ChapterStageCatalog.StageSpec spec = ChapterStageCatalog.spec(id);
        if (spec == null) {
            throw new IllegalArgumentException("Unknown world stage: " + id);
        }
        SeasonProfiles.Profile profile = SeasonProfiles.of(spec.seasonType());
        if (profile == null) {
            throw new IllegalStateException("Missing season profile for " + spec.seasonType());
        }

        List<PlantName> plants = ChapterPlantCatalog.plantsFor(id);
        List<PlantName> recommended = ChapterPlantCatalog.recommendedFor(id);
        List<Property> zombies = ZombiePools.forChapter(id);
        ChapterRulesCatalog.BuiltRules builtRules = ChapterRulesCatalog.build(id, plants);

        return assembleChapter(
            spec.id().chapterNumber(),
            spec.type(),
            spec.seasonFactory().get(),
            spec.baseWaves(),
            GridConfig.defaultConfig(),
            plants,
            zombies,
            new DefaultWaveFormula(),
            builtRules.rules(),
            null,
            spec.needsPlantSelection(),
            profile.mapPath(spec.mapSlot()),
            spec.description(),
            8,
            recommended,
            resolveHud(builtRules),
            new CameraIntroState(),
            new PlayScreenController(),
            profile.lawnMowerType(),
            profile.obstacleTypes(),
            WaveConfig.intense()
        );
    }

    public Chapter createAncientEgyptChapter1() {
        return createWorldStage(new ChapterId(SeasonType.ANCIENT_EGYPT, 1));
    }

    public Chapter createAncientEgyptChapter2() {
        return createWorldStage(new ChapterId(SeasonType.ANCIENT_EGYPT, 2));
    }

    public Chapter createAncientEgyptChapter3() {
        return createWorldStage(new ChapterId(SeasonType.ANCIENT_EGYPT, 3));
    }

    public Chapter createAncientEgyptChapter4() {
        return createWorldStage(new ChapterId(SeasonType.ANCIENT_EGYPT, 4));
    }

    public Chapter createDarkAgesChapter1() {
        return createWorldStage(new ChapterId(SeasonType.DARK_AGES, 1));
    }

    public Chapter createDarkAgesChapter2() {
        return createWorldStage(new ChapterId(SeasonType.DARK_AGES, 2));
    }

    public Chapter createDarkAgesChapter3() {
        return createWorldStage(new ChapterId(SeasonType.DARK_AGES, 3));
    }

    public Chapter createDarkAgesChapter4() {
        return createWorldStage(new ChapterId(SeasonType.DARK_AGES, 4));
    }

    public Chapter createBigWaveBeachChapter1() {
        return createWorldStage(new ChapterId(SeasonType.BIG_WAVE_BEACH, 1));
    }

    public Chapter createBigWaveBeachChapter2() {
        return createWorldStage(new ChapterId(SeasonType.BIG_WAVE_BEACH, 2));
    }

    public Chapter createBigWaveBeachChapter3() {
        return createWorldStage(new ChapterId(SeasonType.BIG_WAVE_BEACH, 3));
    }

    public Chapter createBigWaveBeachChapter4() {
        return createWorldStage(new ChapterId(SeasonType.BIG_WAVE_BEACH, 4));
    }

    public Chapter createFrostBiteCavesChapter1() {
        return createWorldStage(new ChapterId(SeasonType.FROSTBITE_CAVES, 1));
    }

    public Chapter createFrostBiteCavesChapter2() {
        return createWorldStage(new ChapterId(SeasonType.FROSTBITE_CAVES, 2));
    }

    public Chapter createFrostBiteCavesChapter3() {
        return createWorldStage(new ChapterId(SeasonType.FROSTBITE_CAVES, 3));
    }

    public Chapter createFrostBiteCavesChapter4() {
        return createWorldStage(new ChapterId(SeasonType.FROSTBITE_CAVES, 4));
    }

    public Chapter createVaseBreakerChapter() {
        return createVaseBreakerChapter(1);
    }

    public Chapter createVaseBreakerChapter(int level) {
        Season season = new AncientEgyptSeason();
        List<PlantName> plants = List.of(
            PlantName.REPEATER, PlantName.THREEPEATER, PlantName.SNOW_PEA, PlantName.ROTOBAGA,
            PlantName.PEASHOOTER, PlantName.CACTUS, PlantName.STARFRUIT, PlantName.CABBAGE_PULT,
            PlantName.MELON_PULT
        );
        VaseBreakerLevelConfig levelConfig = VaseBreakerLevelConfig.forLevel(level);
        Minigame logic = new VaseBreakerMode(plants, List.of("Imp1"), levelConfig);

        return assembleChapter(
            1,
            ChapterType.MINIGAME,
            season,
            3,
            GridConfig.defaultConfig(),
            plants,
            ZombiePools.resolve(
                "Basic Egypt",
                "ZombieNewspaper"
            ),
            new DefaultWaveFormula(),
            List.of(new VaseBreakerRule()),
            logic,
            true,
            SeasonProfiles.of(SeasonType.ANCIENT_EGYPT).normalMapPath(),
            "Break vases, plant what you find, and survive. Level "
                + levelConfig.level() + " of 3.",
            plants.size(),
            plants,
            null,
            new CameraIntroState(),
            new PlayScreenController(),
            LawnMowerType.EGYPT,
            MINIGAME_OBSTACLES,
            null
        );
    }

    public Chapter createScoringGameChapter() {
        Season season = new DarkAgesSeason();
        List<PlantName> plants = List.of(
            PlantName.SUNFLOWER, PlantName.TWIN_SUNFLOWER, PlantName.SUN_SHROOM,
            PlantName.PRIMAL_SUNFLOWER, PlantName.GOLD_BLOOM, PlantName.PEASHOOTER,
            PlantName.REPEATER, PlantName.THREEPEATER, PlantName.SNOW_PEA,
            PlantName.ROTOBAGA, PlantName.PEA_POD, PlantName.SPLIT_PEA,
            PlantName.CITRON, PlantName.CAULIPOWER, PlantName.ELECTRIC_BLUEBERRY,
            PlantName.BOWLING_BULB, PlantName.CACTUS, PlantName.FIRE_PEASHOOTER,
            PlantName.STARFRUIT, PlantName.GOO_PEASHOOTER, PlantName.MEGA_GATLING_PEA,
            PlantName.SEA_SHROOM, PlantName.PUFF_SHROOM, PlantName.FUME_SHROOM,
            PlantName.CABBAGE_PULT, PlantName.KERNEL_PULT, PlantName.MELON_PULT,
            PlantName.WINTER_MELON, PlantName.PEPPER_PULT, PlantName.POTATO_MINE,
            PlantName.PRIMAL_POTATO_MINE, PlantName.CHERRY_BOMB, PlantName.SQUASH,
            PlantName.GRAPESHOT, PlantName.JALAPENO, PlantName.DOOM_SHROOM,
            PlantName.TANGLE_KELP, PlantName.ICEBERG_LETTUCE, PlantName.BONK_CHOY,
            PlantName.PHAT_BEET, PlantName.CHOMPER, PlantName.WASABI_WHIP,
            PlantName.KIWIBEAST, PlantName.WALL_NUT, PlantName.TALL_NUT,
            PlantName.ENDURIAN, PlantName.GARLIC, PlantName.SWEET_POTATO,
            PlantName.EXPLODE_O_NUT, PlantName.PUMPKIN, PlantName.SUN_BEAN,
            PlantName.TORCHWOOD, PlantName.MAGNET_SHROOM, PlantName.HYPNO_SHROOM,
            PlantName.CAT_TAIL, PlantName.IMITATER, PlantName.ICE_SHROOM,
            PlantName.LILY_PAD, PlantName.HOT_POTATO, PlantName.GRAVE_BUSTER,
            PlantName.ENLIGHTEN_MINT, PlantName.APPEASE_MINT, PlantName.ARMA_MINT,
            PlantName.BOMBARD_MINT, PlantName.ENFORCE_MINT, PlantName.REINFORCE_MINT,
            PlantName.ENCHANT_MINT, PlantName.PIERCE_MINT, PlantName.CAT_TAIL_MINT
        );
        List<PlantName> recommended = List.of(
            PlantName.SUNFLOWER, PlantName.PEASHOOTER, PlantName.REPEATER,
            PlantName.CHERRY_BOMB, PlantName.POTATO_MINE, PlantName.MELON_PULT
        );

        return assembleChapter(
            2,
            ChapterType.MINIGAME,
            season,
            100,
            GridConfig.defaultConfig(),
            plants,
            ZombiePools.catalog(),
            new DefaultWaveFormula(),
            List.of(
                new QuickZombieKillPattern(5, 5),
                new GroupKillOneProjectilePattern(3),
                new GroupKillOncePattern(3),
                new KillComboPattern(),
                new ExplodonatorPattern(),
                new SpudowPattern(),
                new CloseCallPattern(),
                new ArmorPopPattern(),
                new PerfectWavePattern()
            ),
            null,
            true,
            SeasonProfiles.of(SeasonType.DARK_AGES).normalMapPath(),
            "Score combos by chaining kills!",
            8,
            recommended,
            new ScoringHud(),
            new CameraIntroState(),
            ScoringPattern.controller,
            LawnMowerType.DARK,
            SeasonProfiles.of(SeasonType.DARK_AGES).obstacleTypes(),
            null
        );
    }

    public Chapter createIZombieChapter() {
        Season season = new FrostBiteCavesSeason();
        return assembleChapter(
            1,
            ChapterType.MINIGAME,
            season,
            3,
            GridConfig.defaultConfig(),
            List.of(
                PlantName.SUNFLOWER, PlantName.TWIN_SUNFLOWER, PlantName.SUN_SHROOM,
                PlantName.PRIMAL_SUNFLOWER, PlantName.GOLD_BLOOM, PlantName.PEASHOOTER,
                PlantName.REPEATER, PlantName.THREEPEATER, PlantName.SNOW_PEA,
                PlantName.ROTOBAGA, PlantName.PEA_POD, PlantName.SPLIT_PEA,
                PlantName.CITRON, PlantName.CAULIPOWER, PlantName.ELECTRIC_BLUEBERRY,
                PlantName.BOWLING_BULB, PlantName.CACTUS, PlantName.FIRE_PEASHOOTER,
                PlantName.STARFRUIT, PlantName.GOO_PEASHOOTER, PlantName.MEGA_GATLING_PEA,
                PlantName.SEA_SHROOM, PlantName.PUFF_SHROOM, PlantName.FUME_SHROOM,
                PlantName.CABBAGE_PULT, PlantName.KERNEL_PULT, PlantName.MELON_PULT,
                PlantName.WINTER_MELON, PlantName.PEPPER_PULT, PlantName.POTATO_MINE,
                PlantName.PRIMAL_POTATO_MINE, PlantName.CHERRY_BOMB, PlantName.SQUASH,
                PlantName.GRAPESHOT, PlantName.JALAPENO, PlantName.DOOM_SHROOM,
                PlantName.TANGLE_KELP, PlantName.ICEBERG_LETTUCE, PlantName.BONK_CHOY,
                PlantName.PHAT_BEET, PlantName.CHOMPER, PlantName.WASABI_WHIP,
                PlantName.KIWIBEAST, PlantName.WALL_NUT, PlantName.TALL_NUT,
                PlantName.ENDURIAN, PlantName.GARLIC, PlantName.SWEET_POTATO,
                PlantName.EXPLODE_O_NUT, PlantName.PUMPKIN, PlantName.SUN_BEAN,
                PlantName.TORCHWOOD, PlantName.MAGNET_SHROOM, PlantName.HYPNO_SHROOM,
                PlantName.CAT_TAIL, PlantName.IMITATER, PlantName.ICE_SHROOM,
                PlantName.LILY_PAD, PlantName.HOT_POTATO, PlantName.GRAVE_BUSTER,
                PlantName.ENLIGHTEN_MINT, PlantName.APPEASE_MINT, PlantName.ARMA_MINT,
                PlantName.BOMBARD_MINT, PlantName.ENFORCE_MINT, PlantName.REINFORCE_MINT,
                PlantName.ENCHANT_MINT, PlantName.PIERCE_MINT, PlantName.CAT_TAIL_MINT
            ),
            ZombiePools.catalog(),
            new DefaultWaveFormula(),
            List.of(),
            new IZombieMode(),
            false,
            SeasonProfiles.of(SeasonType.FROSTBITE_CAVES).normalMapPath(),
            "",
            -1,
            List.of(),
            new IZombieHud(),
            new CameraIntroState(),
            new IZombieController(),
            null,
            SeasonProfiles.of(SeasonType.FROSTBITE_CAVES).obstacleTypes(),
            null
        );
    }

    public Chapter createIZombieCouchPlayChapter() {
        Season season = new FrostBiteCavesSeason();
        List<PlantName> plants = List.of(
            PlantName.SUNFLOWER, PlantName.TWIN_SUNFLOWER, PlantName.SUN_SHROOM,
            PlantName.PRIMAL_SUNFLOWER, PlantName.GOLD_BLOOM, PlantName.PEASHOOTER,
            PlantName.REPEATER, PlantName.THREEPEATER, PlantName.SNOW_PEA,
            PlantName.ROTOBAGA, PlantName.PEA_POD, PlantName.SPLIT_PEA,
            PlantName.CITRON, PlantName.CAULIPOWER, PlantName.ELECTRIC_BLUEBERRY,
            PlantName.BOWLING_BULB, PlantName.CACTUS, PlantName.FIRE_PEASHOOTER,
            PlantName.STARFRUIT, PlantName.GOO_PEASHOOTER, PlantName.MEGA_GATLING_PEA,
            PlantName.SEA_SHROOM, PlantName.PUFF_SHROOM, PlantName.FUME_SHROOM,
            PlantName.CABBAGE_PULT, PlantName.KERNEL_PULT, PlantName.MELON_PULT,
            PlantName.WINTER_MELON, PlantName.PEPPER_PULT, PlantName.POTATO_MINE,
            PlantName.PRIMAL_POTATO_MINE, PlantName.CHERRY_BOMB, PlantName.SQUASH,
            PlantName.GRAPESHOT, PlantName.JALAPENO, PlantName.DOOM_SHROOM,
            PlantName.TANGLE_KELP, PlantName.ICEBERG_LETTUCE, PlantName.BONK_CHOY,
            PlantName.PHAT_BEET, PlantName.CHOMPER, PlantName.WASABI_WHIP,
            PlantName.KIWIBEAST, PlantName.WALL_NUT, PlantName.TALL_NUT,
            PlantName.ENDURIAN, PlantName.GARLIC, PlantName.SWEET_POTATO,
            PlantName.EXPLODE_O_NUT, PlantName.PUMPKIN, PlantName.SUN_BEAN,
            PlantName.TORCHWOOD, PlantName.MAGNET_SHROOM, PlantName.HYPNO_SHROOM,
            PlantName.CAT_TAIL, PlantName.IMITATER, PlantName.ICE_SHROOM,
            PlantName.LILY_PAD, PlantName.HOT_POTATO, PlantName.GRAVE_BUSTER,
            PlantName.ENLIGHTEN_MINT, PlantName.APPEASE_MINT, PlantName.ARMA_MINT,
            PlantName.BOMBARD_MINT, PlantName.ENFORCE_MINT, PlantName.REINFORCE_MINT,
            PlantName.ENCHANT_MINT, PlantName.PIERCE_MINT, PlantName.CAT_TAIL_MINT
        );

        return assembleChapter(
            6,
            ChapterType.MINIGAME,
            season,
            3,
            GridConfig.defaultConfig(),
            plants,
            ZombiePools.catalog(),
            new DefaultWaveFormula(),
            List.of(),
            new IZombieCouchPlayMode(),
            true,
            "Maps/egypt.tmx",
            "Couch I, Zombie: plants on the mouse, zombies on the keyboard. Both players ready up, then fight.",
            8,
            List.of(
                PlantName.SUNFLOWER, PlantName.PEASHOOTER, PlantName.WALL_NUT,
                PlantName.POTATO_MINE, PlantName.CHERRY_BOMB, PlantName.SNOW_PEA,
                PlantName.SQUASH, PlantName.REPEATER
            ),
            new IZombieCouchPlayHud(),
            new CameraIntroState(),
            new IZombieCouchPlayController(),
            null,
            SeasonProfiles.of(SeasonType.FROSTBITE_CAVES).obstacleTypes(),
            null
        );
    }

    public Chapter createWallNutBowlingChapter() {
        return createWallNutBowlingChapter(1);
    }

    public Chapter createWallNutBowlingChapter(int level) {
        Season season = new AncientEgyptSeason();
        WallNutBowlingLevelConfig levelConfig = WallNutBowlingLevelConfig.forLevel(level);
        List<PlantName> bowlingPlants = List.of(
            PlantName.WALL_NUT, PlantName.EXPLODE_O_NUT, PlantName.TALL_NUT
        );

        return assembleChapter(
            1,
            ChapterType.MINIGAME,
            season,
            levelConfig.totalWaves(),
            GridConfig.defaultConfig(),
            bowlingPlants,
            ZombiePools.resolve(
                "Basic Egypt",
                "ZombieNewspaper"
            ),
            levelConfig.waveFormula(),
            List.of(
                new ConveyorBeltRule(bowlingPlants),
                new NoSunRule(),
                new BowlingRule(2)
            ),
            new WallNutBowlingMode(levelConfig),
            false,
            SeasonProfiles.of(SeasonType.ANCIENT_EGYPT).normalMapPath(),
            "Bowl walnuts into the zombies. Survive "
                + levelConfig.totalWaves() + " waves. Level "
                + levelConfig.level() + " of 3.",
            bowlingPlants.size(),
            bowlingPlants,
            null,
            new CameraIntroState(),
            new WalnutBowlingController(),
            LawnMowerType.EGYPT,
            MINIGAME_OBSTACLES,
            levelConfig.waveConfig()
        );
    }

    public Chapter createPlantZombiesChapter() {
        return createPlantZombiesChapter(1);
    }

    public Chapter createPlantZombiesChapter(int level) {
        Season season = new AncientEgyptSeason();
        PlantZombiesLevelConfig levelConfig = PlantZombiesLevelConfig.forLevel(level);
        List<PlantName> plants = List.of(
            PlantName.SUNFLOWER, PlantName.TWIN_SUNFLOWER, PlantName.SUN_SHROOM,
            PlantName.PRIMAL_SUNFLOWER, PlantName.GOLD_BLOOM, PlantName.PEASHOOTER,
            PlantName.REPEATER, PlantName.THREEPEATER, PlantName.SNOW_PEA,
            PlantName.ROTOBAGA, PlantName.PEA_POD, PlantName.SPLIT_PEA,
            PlantName.CITRON, PlantName.CAULIPOWER, PlantName.ELECTRIC_BLUEBERRY,
            PlantName.BOWLING_BULB, PlantName.CACTUS, PlantName.FIRE_PEASHOOTER,
            PlantName.STARFRUIT, PlantName.GOO_PEASHOOTER, PlantName.MEGA_GATLING_PEA,
            PlantName.SEA_SHROOM, PlantName.PUFF_SHROOM, PlantName.FUME_SHROOM,
            PlantName.CABBAGE_PULT, PlantName.KERNEL_PULT, PlantName.MELON_PULT,
            PlantName.WINTER_MELON, PlantName.PEPPER_PULT, PlantName.POTATO_MINE,
            PlantName.PRIMAL_POTATO_MINE, PlantName.CHERRY_BOMB, PlantName.SQUASH,
            PlantName.GRAPESHOT, PlantName.JALAPENO, PlantName.DOOM_SHROOM,
            PlantName.TANGLE_KELP, PlantName.ICEBERG_LETTUCE, PlantName.BONK_CHOY,
            PlantName.PHAT_BEET, PlantName.CHOMPER, PlantName.WASABI_WHIP,
            PlantName.KIWIBEAST, PlantName.WALL_NUT, PlantName.TALL_NUT,
            PlantName.ENDURIAN, PlantName.GARLIC, PlantName.SWEET_POTATO,
            PlantName.EXPLODE_O_NUT, PlantName.PUMPKIN, PlantName.SUN_BEAN,
            PlantName.TORCHWOOD, PlantName.MAGNET_SHROOM, PlantName.HYPNO_SHROOM,
            PlantName.CAT_TAIL, PlantName.IMITATER, PlantName.ICE_SHROOM,
            PlantName.LILY_PAD, PlantName.HOT_POTATO, PlantName.GRAVE_BUSTER,
            PlantName.ENLIGHTEN_MINT, PlantName.APPEASE_MINT, PlantName.ARMA_MINT,
            PlantName.BOMBARD_MINT, PlantName.ENFORCE_MINT, PlantName.REINFORCE_MINT,
            PlantName.ENCHANT_MINT, PlantName.PIERCE_MINT, PlantName.CAT_TAIL_MINT
        );
        List<PlantName> recommended = List.of(
            PlantName.SUNFLOWER, PlantName.PEASHOOTER, PlantName.WALL_NUT,
            PlantName.CHERRY_BOMB, PlantName.POTATO_MINE
        );

        return assembleChapter(
            7,
            ChapterType.MINIGAME,
            season,
            levelConfig.totalWaves(),
            GridConfig.defaultConfig(),
            plants,
            ZombiePools.resolve("PeashooterZombie", "WalnutZombie", "SquashZombie"),
            levelConfig.waveFormula(),
            List.of(),
            new PlantZombiesMode(levelConfig),
            true,
            SeasonProfiles.of(SeasonType.ANCIENT_EGYPT).normalMapPath(),
            "Defend the lawn against peashooter, walnut, and squash zombies. Level "
                + levelConfig.level() + " of 3.",
            5,
            recommended,
            null,
            new CameraIntroState(),
            new PlayScreenController(),
            LawnMowerType.EGYPT,
            SeasonProfiles.of(SeasonType.ANCIENT_EGYPT).obstacleTypes(),
            levelConfig.waveConfig()
        );
    }

    public Chapter createBeghouledChapter() {
        return createBeghouledChapter(1);
    }

    public Chapter createBeghouledChapter(int level) {
        Season season = new AncientEgyptSeason();
        BeghouledLevelConfig levelConfig = BeghouledLevelConfig.forLevel(level);
        List<PlantName> availablePlants = new ArrayList<>(BeghouledUpgradeCatalog.availablePlantsForLevel(level));
        List<PlantName> recommended = BeghouledUpgradeCatalog.cardPlantsForLevel(level);

        return assembleChapter(
            5,
            ChapterType.MINIGAME,
            season,
            1,
            GridConfig.defaultConfig(),
            availablePlants,
            ZombiePools.resolve("ZombieArmor1", "ZombieArmor2", "ZombieNewspaper"),
            levelConfig.waveFormula(),
            List.of(new BeghouledRule()),
            new BeghouledMode(levelConfig),
            false,
            SeasonProfiles.of(SeasonType.ANCIENT_EGYPT).normalMapPath(),
            "Match plants, spend sun on upgrades, and survive "
                + levelConfig.comboTarget() + " combinations. Level "
                + levelConfig.level() + " of 3.",
            recommended.size(),
            recommended,
            new BeghouledHud(),
            new CameraIntroState(),
            new BeghouledController(),
            LawnMowerType.EGYPT,
            SeasonProfiles.of(SeasonType.ANCIENT_EGYPT).obstacleTypes(),
            levelConfig.waveConfig()
        );
    }

    private Hud resolveHud(ChapterRulesCatalog.BuiltRules builtRules) {
        if (builtRules.bossRule() != null && builtRules.conveyorBeltRule() != null) {
            return new BossHud(builtRules.conveyorBeltRule(), builtRules.bossRule());
        }
        if (builtRules.conveyorBeltRule() != null) {
            return new ConveyorBeltHud(builtRules.conveyorBeltRule());
        }
        return null;
    }

    private Chapter assembleChapter(
        int chapterNumber,
        ChapterType chapterType,
        Season season,
        int totalWaves,
        GridConfig gridConfig,
        List<PlantName> availablePlants,
        List<Property> availableZombies,
        WaveFormula waveFormula,
        List<ChapterRule> rules,
        Minigame logic,
        boolean needsPlantSelection,
        String mapPath,
        String description,
        int selectionCount,
        List<PlantName> recommendedPlants,
        Hud hud,
        PlayScreenState firstState,
        GameController controller,
        LawnMowerType lawnMowerType,
        List<ObstacleType> obstacleTypes,
        WaveConfig waveConfig
    ) {
        ChapterMetadata metadata = ChapterMetadataRegistry.resolve(
            season != null ? season.getSeasonType() : null,
            chapterNumber,
            chapterType,
            totalWaves,
            availablePlants,
            description,
            selectionCount,
            recommendedPlants
        );

        Hud resolvedHud = hud != null ? hud : HudFactory.fromRules(rules);
        PlayScreenState resolvedFirstState = firstState != null ? firstState : new CameraIntroState();
        GameController resolvedController = controller != null ? controller : new PlayScreenController();

        StageDifficultyConfig difficultyConfig = StageDifficultyConfig.forOrder(chapterNumber);
        int resolvedWaves = chapterType == ChapterType.BOSS || chapterType == ChapterType.MINIGAME
            ? totalWaves
            : difficultyConfig.applyWaveCount(totalWaves);
        WaveFormula resolvedFormula = chapterType == ChapterType.MINIGAME
            ? (waveFormula != null ? waveFormula : new DefaultWaveFormula())
            : new ScaledWaveFormula(
                waveFormula != null ? waveFormula : new DefaultWaveFormula(),
                difficultyConfig.costMultiplier()
            );
        WaveConfig resolvedWaveConfig;
        if (waveConfig != null) {
            resolvedWaveConfig = waveConfig;
        } else if (chapterType == ChapterType.MINIGAME) {
            resolvedWaveConfig = WaveConfig.getDefaultWaveConfig();
        } else {
            resolvedWaveConfig = difficultyConfig.applyTo(WaveConfig.getDefaultWaveConfig());
        }

        SeasonType seasonType = season != null ? season.getSeasonType() : null;
        SeasonProfiles.Profile profile = SeasonProfiles.of(seasonType);
        List<ObstacleType> resolvedObstacles = obstacleTypes != null && !obstacleTypes.isEmpty()
            ? obstacleTypes
            : (profile != null ? profile.obstacleTypes() : List.of(ObstacleType.BRAIN));

        Chapter chapter = new Chapter(
            chapterNumber,
            chapterType,
            season,
            resolvedObstacles,
            resolvedWaves,
            gridConfig,
            availablePlants,
            availableZombies,
            lawnMowerType,
            resolvedFormula,
            resolvedWaveConfig,
            rules != null ? rules : List.of(),
            logic,
            needsPlantSelection,
            mapPath,
            metadata.getDescription(),
            metadata.getSelectionCount(),
            metadata.getRecommendedPlants(),
            resolvedHud,
            resolvedFirstState,
            resolvedController,
            resolveLevelCondition(rules, logic)
        );

        if (seasonType != null && seasonType != SeasonType.MINIGAME) {
            chapter.setUnlockPlants(ChapterUnlockCatalog.unlockPlantsFor(seasonType, chapterNumber));
        }
        chapter.setDifficulty(metadata.getDifficulty());
        return chapter;
    }

    private LevelCondition resolveLevelCondition(List<ChapterRule> rules, Minigame logic) {
        if (logic instanceof VaseBreakerMode) {
            return new VaseBreakerCondition();
        }
        if (logic instanceof WallNutBowlingMode) {
            return new WalnutBowlingCondition();
        }
        if (logic instanceof PlantZombiesMode) {
            return new PlantZombiesCondition();
        }
        if (rules == null) {
            return new NormalCondition();
        }
        boolean hasBoss = false;
        boolean hasTimedWar = false;
        boolean hasVaseBreaker = false;
        boolean hasBeghouled = false;
        for (ChapterRule rule : rules) {
            if (rule instanceof DeadLineRules) {
                return new DeadLineCondition();
            }
            if (rule instanceof SaveOurSeedRules) {
                return new SaveOurSeedCondition();
            }
            if (rule instanceof BeghouledRule) {
                hasBeghouled = true;
            }
            if (rule instanceof BossRule) {
                hasBoss = true;
            }
            if (rule instanceof TimedWarRules) {
                hasTimedWar = true;
            }
            if (rule instanceof VaseBreakerRule) {
                hasVaseBreaker = true;
            }
            if (rule instanceof BowlingRule) {
                return new WalnutBowlingCondition();
            }
            if (rule instanceof LovePlantsRules) {
                return new LovePlantsCondition();
            }
        }
        if (hasBeghouled) {
            return new BeghouledCondition();
        }
        if (hasBoss) {
            return new BossCondition();
        }
        if (hasTimedWar) {
            return new TimedWarCondition();
        }
        if (hasVaseBreaker) {
            return new VaseBreakerCondition();
        }
        return new NormalCondition();
    }
}
