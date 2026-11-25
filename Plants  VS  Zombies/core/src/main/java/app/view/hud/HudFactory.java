package app.view.hud;

import game.world.chapter.rule.ChapterRule;
import game.world.chapter.rule.scoringgame.ScoringPattern;
import game.world.chapter.rule.speciallevelRules.*;

import java.util.List;

public final class HudFactory {

    private HudFactory() {}

    public static Hud fromRules(List<ChapterRule> rules) {
        if (rules != null) {
            for (ChapterRule rule : rules) {
                if (rule instanceof ScoringPattern) {
                    return new ScoringHud();
                }
                if (rule instanceof ConveyorBeltRule conveyorRule) {
                    return new ConveyorBeltHud(conveyorRule);
                }
                if (rule instanceof VaseBreakerRule vaseBreakerRule) {
                    return new VaseBreakerHud(vaseBreakerRule);
                }
                if (rule instanceof LovePlantsRules lovePlantsRules) {
                    return new LovePlantsHud(lovePlantsRules);
                }
                if (rule instanceof PlantWhatYouGotRules plantWhatYouGotRules) {
                    return new PlantWhatYouGotHud();
                }
                if (rule instanceof TimedWarRules timedWarRules) {
                    return new TimedWarHud(timedWarRules);
                }
            }
        }
        return new NormalHud();
    }
}
