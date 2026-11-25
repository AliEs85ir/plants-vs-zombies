package game.audio;

import app.enums.drop.DropType;
import app.enums.plant.PlantCategory;
import app.enums.plant.PlantName;
import app.enums.sun.SunType;
import game.dialogue.Speaker;
import game.entity.Entity;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.world.chapter.Chapter;
import game.world.playground.Cell;

import java.util.concurrent.ThreadLocalRandom;

public final class Sfx {
    private static final AudioId[] THROWS = {AudioId.SFX_THROW, AudioId.SFX_THROW2};
    private static final AudioId[] IGNITES = {AudioId.SFX_IGNITE, AudioId.SFX_IGNITE2};
    private static final AudioId[] SHIELDS = {AudioId.SFX_SHIELDHIT, AudioId.SFX_SHIELDHIT2};
    private static final AudioId[] GROANS = {
        AudioId.VOX_GROAN1, AudioId.VOX_GROAN2, AudioId.VOX_GROAN3,
        AudioId.VOX_GROAN4, AudioId.VOX_GROAN5, AudioId.VOX_GROAN6
    };
    private static final AudioId[] LOW_GROANS = {AudioId.VOX_LOWGROAN, AudioId.VOX_LOWGROAN2};
    private static final AudioId[] DEATH_VOICES = {
        AudioId.VOX_SUKHBIR, AudioId.VOX_SUKHBIR5, AudioId.VOX_SUKHBIR6, AudioId.VOX_SCREAM
    };
    private static final AudioId[] DAVE_SHORT = {
        AudioId.VOX_DAVE_SHORT1, AudioId.VOX_DAVE_SHORT2, AudioId.VOX_DAVE_SHORT3, AudioId.VOX_DAVE_CRAZY
    };
    private static final AudioId[] DAVE_LONG = {
        AudioId.VOX_DAVE_LONG1, AudioId.VOX_DAVE_LONG2, AudioId.VOX_DAVE_LONG3
    };
    private static final AudioId[] DAVE_EXTRA_LONG = {
        AudioId.VOX_DAVE_EXTRALONG1, AudioId.VOX_DAVE_EXTRALONG2, AudioId.VOX_DAVE_EXTRALONG3
    };
    private static final AudioId[] IMPS = {AudioId.VOX_IMP, AudioId.VOX_IMP2};
    private static final AudioId[] YUCKS = {AudioId.VOX_YUCK, AudioId.VOX_YUCK2};
    private static final AudioId[] SQUASH_HMMS = {AudioId.VOX_SQUASH_HMM, AudioId.VOX_SQUASH_HMM2};
    private static final AudioId[] JACK_SURPRISES = {AudioId.VOX_JACK_SURPRISE, AudioId.VOX_JACK_SURPRISE2};
    private static final AudioId[] NEWSPAPER_RARRGHS = {AudioId.VOX_NEWSPAPER_RARRGH, AudioId.VOX_NEWSPAPER_RARRGH2};
    private static final AudioId[] BOWLING_IMPACTS = {AudioId.SFX_BOWLING_IMPACT, AudioId.SFX_BOWLING_IMPACT2};
    private static final AudioId[] EGYPT_STINGERS = {AudioId.SFX_EGYPT_STINGER_A, AudioId.SFX_EGYPT_STINGER_B};

    private Sfx() {
    }

    public static void play(AudioId id) {
        AudioManager.play(id);
    }

    public static void playRandom(AudioId... ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        AudioManager.play(ids[ThreadLocalRandom.current().nextInt(ids.length)]);
    }

    public static void plantPlaced(Plant plant, Cell cell) {
        PlantName name = plant != null && plant.getDefinition() != null
            ? plant.getDefinition().getName()
            : null;
        if (name == PlantName.GRAVE_BUSTER) {
            play(AudioId.SFX_GRAVEBUSTERCHOMP);
            play(AudioId.SFX_GRAVEBUTTON);
            return;
        }
        if (name == PlantName.MAGNET_SHROOM) {
            play(AudioId.SFX_MAGNETSHROOM);
            play(AudioId.SFX_HYDRAULIC_SHORT);
        }
        if (name == PlantName.LILY_PAD || (cell != null && cell.isWater())) {
            play(AudioId.SFX_PLANT_WATER);
            return;
        }
        play(AudioId.SFX_PLANTGROW);
        play(AudioId.SFX_GRASSSTEP);
        if (name == PlantName.GOLD_BLOOM || name == PlantName.SUN_SHROOM || name == PlantName.ENLIGHTEN_MINT) {
            play(AudioId.SFX_PLANTERN);
        }
    }

    public static void plantAttack(PlantName name) {
        if (name == null) {
            playRandom(THROWS);
            return;
        }
        switch (name) {
            case FUME_SHROOM -> play(AudioId.SFX_FUME);
            case SNOW_PEA, WINTER_MELON -> play(AudioId.SFX_SNOW_PEA_SPARKLES);
            case ELECTRIC_BLUEBERRY, CAULIPOWER, CITRON -> play(AudioId.SFX_THUNDER);
            default -> playRandom(THROWS);
        }
    }

    public static void plantExploded(Plant plant) {
        PlantName name = plant != null && plant.getDefinition() != null
            ? plant.getDefinition().getName()
            : null;
        if (name == null) {
            play(AudioId.SFX_EXPLOSION);
            return;
        }
        switch (name) {
            case CHERRY_BOMB -> play(AudioId.SFX_CHERRYBOMB);
            case DOOM_SHROOM -> play(AudioId.SFX_DOOMSHROOM);
            case JALAPENO -> {
                play(AudioId.SFX_JALAPENO);
                play(AudioId.SFX_IGNITE);
            }
            case POTATO_MINE, PRIMAL_POTATO_MINE -> play(AudioId.SFX_POTATO_MINE);
            case EXPLODE_O_NUT -> {
                play(AudioId.SFX_JACK_IN_THE_BOX);
                play(AudioId.SFX_EXPLOSION);
            }
            case ICE_SHROOM -> {
                play(AudioId.SFX_FROZEN);
                play(AudioId.SFX_THUNDER);
            }
            case ICEBERG_LETTUCE -> play(AudioId.SFX_FROZEN);
            case TANGLE_KELP -> {
                play(AudioId.SFX_ZOMBIE_SPLASH);
                play(AudioId.VOX_ZOMBAQUARIUM_DIE);
            }
            case SQUASH -> play(AudioId.SFX_GARGANTUAR_THUMP);
            default -> play(AudioId.SFX_EXPLOSION);
        }
    }

    public static void plantDamaged(Plant plant) {
        if (plant == null || plant.getDefinition() == null) {
            return;
        }
        PlantName name = plant.getDefinition().getName();
        if (name == PlantName.GARLIC) {
            playRandom(YUCKS);
            return;
        }
        PlantCategory category = plant.getDefinition().getCategory();
        if (category == PlantCategory.WALL_NUT || name == PlantName.PUMPKIN || name == PlantName.ENDURIAN) {
            playRandom(SHIELDS);
        }
    }

    public static void plantDied() {
        play(AudioId.VOX_SCREAM);
    }

    public static void zombieSpawned(Zombie zombie) {
        if (chance(0.45f)) {
            playRandom(GROANS);
        } else if (chance(0.2f)) {
            playRandom(LOW_GROANS);
        }
        if (zombie != null && isImp(zombie.getName())) {
            playRandom(IMPS);
        }
    }

    public static void zombieDied(Zombie zombie) {
        String name = zombie != null ? zombie.getName() : null;
        if (isGargantuar(name)) {
            play(AudioId.VOX_GARGANTUAR_DEATH);
            return;
        }
        if (isImp(name)) {
            playRandom(IMPS);
            return;
        }
        if (zombie != null && zombie.getWorld() != null && zombie.getWorld().getGrid() != null
            && zombie.getPosition() != null) {
            Cell cell = zombie.getWorld().getGrid().getCellAt(zombie.getPosition());
            if (cell != null && cell.isWater()) {
                play(AudioId.VOX_ZOMBAQUARIUM_DIE);
                play(AudioId.SFX_ZOMBIE_SPLASH);
                return;
            }
        }
        if (chance(0.4f)) {
            playRandom(DEATH_VOICES);
        }
    }

    public static void zombieHit(Zombie zombie) {
        if (zombie != null && zombie.getArmors() != null && !zombie.getArmors().isEmpty()) {
            playRandom(SHIELDS);
            return;
        }
        AudioManager.play(AudioId.ZOMBIE_DAMAGE);
    }

    public static void dialogueLine(Speaker speaker, String text) {
        if (speaker == Speaker.CRAZY_DAVE) {
            int length = text != null ? text.length() : 0;
            if (length >= 90) {
                playRandom(DAVE_EXTRA_LONG);
            } else if (length >= 45) {
                playRandom(DAVE_LONG);
            } else {
                playRandom(DAVE_SHORT);
            }
            return;
        }
        if (speaker == Speaker.ZOMBOSS) {
            play(AudioId.VOX_EVILLAUGH);
        }
    }

    public static void waveStarted(Chapter chapter, int waveNumber) {
        int total = chapter != null ? chapter.getTotalWaves() : 0;
        if (total > 0 && waveNumber >= total) {
            play(AudioId.SFX_FINAL_WAVE);
            play(AudioId.SFX_HUGE_WAVE);
            play(AudioId.SFX_FINAL_FANFARE);
            return;
        }
        if (total > 2 && waveNumber == Math.max(2, total / 2)) {
            play(AudioId.SFX_HUGE_WAVE);
        }
    }

    public static void dropSpawned(DropType type) {
        if (type == null) {
            return;
        }
        switch (type) {
            case GOLD_COIN, SILVER_COIN, DIAMOND -> play(AudioId.SFX_MONEYFALLS);
            case PLANT_FOOD -> play(AudioId.SFX_LIGHTFILL);
            default -> play(AudioId.SFX_POINTS);
        }
    }

    public static void dropCollected(DropType type) {
        if (type == null) {
            return;
        }
        switch (type) {
            case GOLD_COIN, SILVER_COIN, DIAMOND -> play(AudioId.SFX_COIN);
            case PLANT_FOOD -> play(AudioId.SFX_PLANTERN);
            default -> play(AudioId.SFX_POINTS);
        }
    }

    public static void sunCollected(SunType type) {
        if (type == SunType.RADIOACTIVE) {
            play(AudioId.SFX_LIGHTFILL);
            return;
        }
        play(AudioId.SFX_POINTS);
    }

    public static void egyptSandstorm() {
        play(AudioId.SFX_DANCER);
        playRandom(EGYPT_STINGERS);
    }

    public static void snowStorm() {
        play(AudioId.SFX_FROZEN);
    }

    public static void darkAgesWave(int waveNumber) {
        if (waveNumber <= 1) {
            play(AudioId.SFX_NIGHT);
        }
        play(AudioId.SFX_GRAVESTONE_RUMBLE);
    }

    public static void squashJump() {
        playRandom(SQUASH_HMMS);
    }

    public static void vaseBroken(boolean gargantuar) {
        play(AudioId.SFX_VASE_BREAKING);
        if (gargantuar) {
            playRandom(JACK_SURPRISES);
            play(AudioId.SFX_JACK_IN_THE_BOX);
        }
    }

    public static void bowlingPlaced() {
        play(AudioId.SFX_BOWLING);
    }

    public static void bowlingHit() {
        playRandom(BOWLING_IMPACTS);
    }

    public static void newspaperRipped() {
        play(AudioId.SFX_NEWSPAPER_RIP);
        playRandom(NEWSPAPER_RARRGHS);
    }

    public static void flyingStarted(Zombie zombie) {
        if (zombie != null && isImp(zombie.getName())) {
            play(AudioId.SFX_POGO_ZOMBIE);
            playRandom(IMPS);
            return;
        }
        play(AudioId.SFX_GONDOLA_INTRO);
    }

    private static boolean isGargantuar(String name) {
        return name != null && name.toLowerCase().contains("gargantuar");
    }

    private static boolean isImp(String name) {
        return name != null && name.toLowerCase().contains("imp");
    }

    private static boolean chance(float probability) {
        return ThreadLocalRandom.current().nextFloat() < probability;
    }


    public static void onEvent(Plant plant) {
        if (plant == null || plant.getDefinition() == null || plant.getDefinition().getName() == null) {
            return;
        }

        switch (plant.getDefinition().getName()) {
            case SUNFLOWER, TWIN_SUNFLOWER, PRIMAL_SUNFLOWER -> playRandom(
                AudioId.SFX_SUNFLOWER_1,
                AudioId.SFX_SUNFLOWER_2,
                AudioId.SFX_SUNFLOWER_3,
                AudioId.SFX_SUNFLOWER_4,
                AudioId.SFX_SUNFLOWER_5
            );

            case PEASHOOTER -> playRandom(
                AudioId.SFX_PEASHOOTER_1,
            //    AudioId.SFX_PEASHOOTER_2,
                AudioId.SFX_PEASHOOTER_3,
                AudioId.SFX_PEASHOOTER_4,
                AudioId.SFX_PEASHOOTER_5,
                AudioId.SFX_PEASHOOTER_6
            );

            case REPEATER -> playRandom(
                AudioId.SFX_REPEATER_1
            //    AudioId.SFX_REPEATER_2,
            //    AudioId.SFX_REPEATER_3
            );

            case THREEPEATER -> playRandom(
                AudioId.SFX_THREEPEATER_1
           //     AudioId.SFX_THREEPEATER_2
            );

            case SNOW_PEA -> playRandom(
          //      AudioId.SFX_SNOWPEA_1,
                AudioId.SFX_SNOWPEA_2,
                AudioId.SFX_SNOWPEA_3,
                AudioId.SFX_SNOWPEA_4
            );

            case SPLIT_PEA -> playRandom(
                AudioId.SFX_SPLITPEA_1,
                AudioId.SFX_SPLITPEA_2
           //     AudioId.SFX_SPLITPEA_3
            );

            case FIRE_PEASHOOTER -> playRandom(
                AudioId.SFX_FIREPEASHOOTER_1,
                AudioId.SFX_FIREPEASHOOTER_2,
                AudioId.SFX_FIREPEASHOOTER_3,
                AudioId.SFX_FIREPEASHOOTER_5,
                AudioId.SFX_FIREPEASHOOTER_6,
                AudioId.SFX_FIREPEASHOOTER_7,
                AudioId.SFX_FIREPEASHOOTER_8,
                AudioId.SFX_FIREPEASHOOTER_10,
                AudioId.SFX_FIREPEASHOOTER_11,
                AudioId.SFX_FIREPEASHOOTER_13
            );

            case WALL_NUT -> playRandom(
                AudioId.SFX_WALLNUT_DEFAULT,
                AudioId.SFX_WALLNUT_1
            );

            case TALL_NUT -> play(AudioId.SFX_TALLNUT_1);

            case ICEBERG_LETTUCE -> playRandom(
                AudioId.SFX_ICEBERGLETTUCE_1,
                AudioId.SFX_ICEBERGLETTUCE_2
            );

            case BONK_CHOY -> playRandom(
                AudioId.SFX_BONKCHOY_1,
                AudioId.SFX_BONKCHOY_2,
                AudioId.SFX_BONKCHOY_3
            );

            case BOWLING_BULB -> playRandom(
                AudioId.SFX_BOWLINGBULB_1,
                AudioId.SFX_BOWLINGBULB_10,
                AudioId.SFX_BOWLINGBULB_11,
                AudioId.SFX_BOWLINGBULB_12,
                AudioId.SFX_BOWLINGBULB_14,
                AudioId.SFX_BOWLINGBULB_15,
                AudioId.SFX_BOWLINGBULB_16,
                AudioId.SFX_BOWLINGBULB_18,
                AudioId.SFX_BOWLINGBULB_21,
                AudioId.SFX_BOWLINGBULB_23,
                AudioId.SFX_BOWLINGBULB_24
            );

            case CABBAGE_PULT -> playRandom(
                AudioId.SFX_CABBAGEPULT_1,
                AudioId.SFX_CABBAGEPULT_2,
                AudioId.SFX_CABBAGEPULT_3,
                AudioId.SFX_CABBAGEPULT_4,
                AudioId.SFX_CABBAGEPULT_5
            );

            case CACTUS -> playRandom(
                AudioId.SFX_CACTUS_1,
                AudioId.SFX_CACTUS_10,
                AudioId.SFX_CACTUS_11,
                AudioId.SFX_CACTUS_12,
                AudioId.SFX_CACTUS_13,
                AudioId.SFX_CACTUS_14,
                AudioId.SFX_CACTUS_15,
                AudioId.SFX_CACTUS_16,
                AudioId.SFX_CACTUS_17
            );

            case CAULIPOWER -> playRandom(
                AudioId.SFX_CAULIPOWER_1,
                AudioId.SFX_CAULIPOWER_2,
                AudioId.SFX_CAULIPOWER_3,
                AudioId.SFX_CAULIPOWER_4,
                AudioId.SFX_CAULIPOWER_5,
                AudioId.SFX_CAULIPOWER_6,
                AudioId.SFX_CAULIPOWER_10
            );

            case CHERRY_BOMB -> play(AudioId.SFX_CHERRYBOMB_1);

            case CHOMPER -> playRandom(
                AudioId.SFX_CHOMPER_1,
                AudioId.SFX_CHOMPER_10,
                AudioId.SFX_CHOMPER_11,
                AudioId.SFX_CHOMPER_12
            );

            case CITRON -> playRandom(
                AudioId.SFX_CITRON_1,
                AudioId.SFX_CITRON_3,
                AudioId.SFX_CITRON_4,
                AudioId.SFX_CITRON_5,
                AudioId.SFX_CITRON_6,
                AudioId.SFX_CITRON_7,
                AudioId.SFX_CITRON_8,
                AudioId.SFX_CITRON_9,
                AudioId.SFX_CITRON_10
            );

            case DOOM_SHROOM -> playRandom(
                AudioId.SFX_DOOMSHROOM_1,
                AudioId.SFX_DOOMSHROOM_2,
                AudioId.SFX_DOOMSHROOM_3
            );

            case ELECTRIC_BLUEBERRY -> playRandom(
                AudioId.SFX_ELECTRICBLUEBERRY_1,
                AudioId.SFX_ELECTRICBLUEBERRY_10,
                AudioId.SFX_ELECTRICBLUEBERRY_11,
                AudioId.SFX_ELECTRICBLUEBERRY_12,
                AudioId.SFX_ELECTRICBLUEBERRY_13,
                AudioId.SFX_ELECTRICBLUEBERRY_14
            );

            case ENDURIAN -> play(AudioId.SFX_ENDURIAN_3);

            case EXPLODE_O_NUT -> playRandom(
                AudioId.SFX_EXPLODEONUT_1,
                AudioId.SFX_EXPLODEONUT_2,
                AudioId.SFX_EXPLODEONUT_3,
                AudioId.SFX_EXPLODEONUT_4,
                AudioId.SFX_EXPLODEONUT_5,
                AudioId.SFX_EXPLODEONUT_6,
                AudioId.SFX_EXPLODEONUT_8
            );

            case FUME_SHROOM -> playRandom(
             //   AudioId.SFX_FUMESHROOM_1,
                AudioId.SFX_FUMESHROOM_2
            );

            case GARLIC -> play(AudioId.SFX_GARLIC_1);

            case PEPPER_PULT -> playRandom(
                AudioId.SFX_PEPPERPULT_2,
                AudioId.SFX_PEPPERPULT_3
            );

            case PHAT_BEET -> playRandom(
                AudioId.SFX_PHATBEET_1,
                AudioId.SFX_PHATBEET_2,
                AudioId.SFX_PHATBEET_4,
                AudioId.SFX_PHATBEET_6
            );

            case POTATO_MINE, PRIMAL_POTATO_MINE -> playRandom(
                AudioId.SFX_POTATOMINE_1,
                AudioId.SFX_POTATOMINE_2,
                AudioId.SFX_POTATOMINE_3
            );

            case PUFF_SHROOM -> playRandom(
                AudioId.SFX_PUFFSHROOM_1,
                //      AudioId.SFX_PUFFSHROOM_2,
                AudioId.SFX_PUFFSHROOM_3,
                AudioId.SFX_PUFFSHROOM_4
           //     AudioId.SFX_PUFFSHROOM_5
            );

            case PUMPKIN -> playRandom(
                AudioId.SFX_PUMPKIN_1,
                AudioId.SFX_PUMPKIN_2,
                AudioId.SFX_PUMPKIN_3
            );

            case SEA_SHROOM -> playRandom(
                AudioId.SFX_SEASHROOM_1,
                AudioId.SFX_SEASHROOM_2,
                AudioId.SFX_SEASHROOM_3,
                AudioId.SFX_SEASHROOM_4,
                AudioId.SFX_SEASHROOM_5,
                AudioId.SFX_SEASHROOM_6
            );

            case SQUASH -> playRandom(
                AudioId.SFX_SQUASH_1,
                AudioId.SFX_SQUASH_10,
                AudioId.SFX_SQUASH_11,
                AudioId.SFX_SQUASH_12,
                AudioId.SFX_SQUASH_13,
                AudioId.SFX_SQUASH_15
            );

            case STARFRUIT -> playRandom(
                AudioId.SFX_STARFRUIT_1,
                AudioId.SFX_STARFRUIT_2,
                AudioId.SFX_STARFRUIT_3,
                AudioId.SFX_STARFRUIT_4
            );

            case SUN_SHROOM -> playRandom(
                AudioId.SFX_SUNSHROOM_1,
                AudioId.SFX_SUNSHROOM_2,
                AudioId.SFX_SUNSHROOM_3,
                AudioId.SFX_SUNSHROOM_4
            );

            case TANGLE_KELP -> playRandom(
                AudioId.SFX_TANGLEKELP_1,
                AudioId.SFX_TANGLEKELP_10,
                AudioId.SFX_TANGLEKELP_11,
                AudioId.SFX_TANGLEKELP_12,
                AudioId.SFX_TANGLEKELP_13,
                AudioId.SFX_TANGLEKELP_14
            );

            case TORCHWOOD -> playRandom(
                AudioId.SFX_TORCHWOOD_1,
                AudioId.SFX_TORCHWOOD_2,
                AudioId.SFX_TORCHWOOD_3,
                AudioId.SFX_TORCHWOOD_4
            );

            case WASABI_WHIP -> playRandom(
                AudioId.SFX_WASABIWHIP_1,
                AudioId.SFX_WASABIWHIP_2,
                AudioId.SFX_WASABIWHIP_14,
                AudioId.SFX_WASABIWHIP_19
            );

            case WINTER_MELON -> playRandom(
                AudioId.SFX_WINTERMELON_1,
                AudioId.SFX_WINTERMELON_2,
                AudioId.SFX_WINTERMELON_3,
                AudioId.SFX_WINTERMELON_4,
                AudioId.SFX_WINTERMELON_5,
                AudioId.SFX_WINTERMELON_6
            );

            default -> {}
        }
    }

}
