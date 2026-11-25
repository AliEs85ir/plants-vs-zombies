package game.world.chapter.definition;

import app.enums.SeasonType;
import app.enums.plant.PlantName;
import game.dialogue.DialogueConfig;
import game.dialogue.DialogueScripts;
import game.world.chapter.ChapterId;
import game.world.chapter.rule.BossRule;
import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.NormalWorldRule;
import game.world.chapter.rule.NpcDialogueRule;
import game.world.chapter.rule.speciallevelRules.*;

import java.util.ArrayList;
import java.util.List;

public final class ChapterRulesCatalog {
    public record BuiltRules(List<ChapterRule> rules, ConveyorBeltRule conveyorBeltRule, BossRule bossRule) {
        public static BuiltRules of(List<ChapterRule> rules) {
            return new BuiltRules(rules, null, null);
        }
    }

    private ChapterRulesCatalog() {
    }

    public static BuiltRules build(ChapterId id, List<PlantName> availablePlants) {
        if (id == null) {
            return BuiltRules.of(List.of(new NormalWorldRule(4)));
        }
        return switch (id.seasonType()) {
            case ANCIENT_EGYPT -> egyptRules(id, availablePlants);
            case DARK_AGES -> darkAgesRules(id, availablePlants);
            case BIG_WAVE_BEACH -> beachRules(id, availablePlants);
            case FROSTBITE_CAVES -> frostbiteRules(id, availablePlants);
            default -> BuiltRules.of(List.of(new NormalWorldRule(4)));
        };
    }

    private static BuiltRules egyptRules(ChapterId id, List<PlantName> plants) {
        return switch (id.chapterNumber()) {
            case 1 -> BuiltRules.of(List.of(
                new NormalWorldRule(4),
                NpcDialogueRule.gameStart(
                    DialogueScripts.firstChapter(),
                    DialogueConfig.ENABLE_FIRST_CHAPTER_DIALOGUE
                )
            ));
            case 2 -> BuiltRules.of(List.of(
                new DeadLineRules(4)
            ));
            case 3 -> conveyorStage(plants, new NormalWorldRule(5));
            case 4 -> bossStage(plants);
            default -> BuiltRules.of(List.of(new NormalWorldRule(4)));
        };
    }

    private static BuiltRules darkAgesRules(ChapterId id, List<PlantName> plants) {
        return switch (id.chapterNumber()) {
            case 1 -> BuiltRules.of(List.of(new NormalWorldRule(6), new NoSunRule()));
            case 2 -> BuiltRules.of(List.of(new NormalWorldRule(5), new NoSunRule(), new TimedWarRules(10, 5)));
            case 3 -> BuiltRules.of(List.of(new NoSunRule(), new NormalWorldRule(5), new PlantWhatYouGotRules()));
            case 4 -> bossStage(plants);
            default -> BuiltRules.of(List.of(new NormalWorldRule(4), new NoSunRule()));
        };
    }

    private static BuiltRules beachRules(ChapterId id, List<PlantName> plants) {
        return switch (id.chapterNumber()) {
            case 1 -> BuiltRules.of(List.of(new NormalWorldRule(3)));
            case 2 -> BuiltRules.of(List.of(new NormalWorldRule(5)));
            case 3 -> BuiltRules.of(List.of(new NormalWorldRule(2), new LovePlantsRules(2)));
            case 4 -> bossStage(plants);
            default -> BuiltRules.of(List.of(new NormalWorldRule(3)));
        };
    }

    private static BuiltRules frostbiteRules(ChapterId id, List<PlantName> plants) {
        return switch (id.chapterNumber()) {
            case 1 -> BuiltRules.of(List.of(new NormalWorldRule(6)));
            case 2 -> BuiltRules.of(List.of(new NormalWorldRule(7)));
            case 3 -> BuiltRules.of(List.of(new SaveOurSeedRules(3)));
            case 4 -> bossStage(plants);
            default -> BuiltRules.of(List.of(new NormalWorldRule(4)));
        };
    }

    private static BuiltRules conveyorStage(List<PlantName> plants, ChapterRule... extraRules) {
        ConveyorBeltRule conveyor = new ConveyorBeltRule(plants);
        List<ChapterRule> rules = new ArrayList<>();
        for (ChapterRule rule : extraRules) {
            rules.add(rule);
        }
        rules.add(conveyor);
        return new BuiltRules(rules, conveyor, null);
    }

    private static BuiltRules bossStage(List<PlantName> plants) {
        BossRule bossRule = new BossRule();
        ConveyorBeltRule conveyor = new ConveyorBeltRule(plants);
        List<ChapterRule> rules = new ArrayList<>();
        rules.add(bossRule);
        rules.add(conveyor);
        rules.add(NpcDialogueRule.bossIntro(
            DialogueScripts.bossIntro(),
            DialogueConfig.ENABLE_BOSS_INTRO_DIALOGUE
        ));
        rules.add(NpcDialogueRule.bossOutro(
            DialogueScripts.bossOutro(),
            DialogueConfig.ENABLE_BOSS_OUTRO_DIALOGUE
        ));
        return new BuiltRules(rules, conveyor, bossRule);
    }
}
