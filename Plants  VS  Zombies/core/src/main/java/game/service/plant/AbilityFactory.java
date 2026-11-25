package game.service.plant;

import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.plant.ability.*;

import java.util.ArrayList;

public class AbilityFactory {
    public static ArrayList<PlantAbility> getAbilities(PlantName name) {
        ArrayList<PlantAbility> abilities = new ArrayList<>();
        int actionInterval = (int) PlantUpgradeService.getEffectiveStats(name).actionIntervalTicks();
        var modifiers = PlantUpgradeService.getModifiers(name);
        PlantAbility plantFoodAbility = PlantFoodAbilityFactory.create(name);
        if (plantFoodAbility != null) {
            abilities.add(plantFoodAbility);
        }
        switch (name) {
            case SUNFLOWER:
                abilities.add(new SunProductionAbility(actionInterval));
                break;
            case TWIN_SUNFLOWER:
                abilities.add(new SunProductionAbility(actionInterval));
                break;
            case SUN_SHROOM:
                abilities.add(new SunProductionAbility(
                    actionInterval,
                    Math.max(1, 240 - modifiers.growTimeReductionTicks()),
                    2.0f
                ));
                break;
            case PRIMAL_SUNFLOWER:
                abilities.add(new SunProductionAbility(actionInterval));
                break;
            case GOLD_BLOOM:
                abilities.add(new SunProductionAbility(actionInterval));
                break;
            case PEASHOOTER:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case REPEATER:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case THREEPEATER:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case SNOW_PEA:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case ROTOBAGA:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case PEA_POD:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case SPLIT_PEA:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case CITRON:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case CAULIPOWER:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case ELECTRIC_BLUEBERRY:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case BOWLING_BULB:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case CACTUS:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case FIRE_PEASHOOTER:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case STARFRUIT:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case GOO_PEASHOOTER:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case MEGA_GATLING_PEA:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case SEA_SHROOM:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case PUFF_SHROOM:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case FUME_SHROOM:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case CABBAGE_PULT:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case KERNEL_PULT:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case MELON_PULT:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case WINTER_MELON:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case PEPPER_PULT:
                abilities.add(new AttackAbility(actionInterval));
                break;
            case POTATO_MINE:
                abilities.add(new ArmedMineAbility(
                    Math.max(0, 15 * GameClock.TICKS_PER_SECOND - modifiers.armTimeReductionTicks()),
                    0,
                    "potato_mine_explosion"
                ));
                break;
            case PRIMAL_POTATO_MINE:
                abilities.add(new ArmedMineAbility(
                    Math.max(0, 5 * GameClock.TICKS_PER_SECOND - modifiers.armTimeReductionTicks()),
                    1,
                    "primal_potato_mine_explosion"
                ));
                break;
            case CHERRY_BOMB:
                abilities.add(new InstantExplosionAbility(1, "cherry_bomb_explosion", 8, 0.10f));
                break;
            case SQUASH:
                abilities.add(new SquashAbility());
                break;
            case GRAPESHOT:
                abilities.add(new GrapeshotAbility());
                break;
            case JALAPENO:
                abilities.add(new JalapenoAbility());
                break;
            case DOOM_SHROOM:
                abilities.add(new DoomShroomAbility());
                break;
            case TANGLE_KELP:
                abilities.add(new TangleKelpAbility());
                break;
            case ICEBERG_LETTUCE:
                abilities.add(new IcebergLettuceAbility());
                break;
            case BONK_CHOY:
                abilities.add(new AdjacentMeleeAbility(actionInterval, 1, "splat_pea"));
                break;
            case PHAT_BEET:
                abilities.add(new AreaMeleeAbility(actionInterval, 5, 1, "phatbeets attack pulse", "phatbeets idle pulse"));
                break;
            case CHOMPER:
                abilities.add(new ChomperAbility());
                break;
            case WASABI_WHIP:
                abilities.add(new AdjacentMeleeAbility(
                    actionInterval,
                    AdjacentMeleeAbility.WINDUP_DAMAGE_DELAY_TICKS,
                    "splat_pea",
                    true
                ));
                break;
            case KIWIBEAST:
                abilities.add(GrowthAbility.ofDurationSeconds(
                    GrowthAbility.DEFAULT_MAX_STAGE,
                    GrowthAbility.STAGE_DURATION_SECONDS
                ));
                abilities.add(new AreaMeleeAbility(
                    actionInterval,
                    AreaMeleeAbility.KIWIBEAST_DAMAGE_DELAY_TICKS,
                    AreaMeleeAbility.CELL_RADIUS_3X3,
                    null,
                    new AreaMeleeAbility.Cue(
                        AreaMeleeAbility.KIWIBEAST_PULSE_DELAY_TICKS,
                        "kiwibeast attack pulse"
                    ),
                    new AreaMeleeAbility.Cue(
                        AreaMeleeAbility.KIWIBEAST_TILE_HIT_DELAY_TICKS,
                        "kiwibeast tile hit"
                    )
                ));
                break;
            case WALL_NUT:
                break;
            case TALL_NUT:
                break;
            case ENDURIAN:
                abilities.add(new EatingMeleeAbility(
                    EatingMeleeAbility.ATTACK_INTERVAL_TICKS,
                    EatingMeleeAbility.DAMAGE_DELAY_TICKS
                ));
                break;
            case GARLIC:
                abilities.add(new GarlicAbility());
                break;
            case SWEET_POTATO:
                abilities.add(new SweetPotatoAbility());
                break;
            case EXPLODE_O_NUT:
                abilities.add(new ExplodeAbility());
                break;
            case PUMPKIN:
                break;
            case SUN_BEAN:
                abilities.add(new SunBeanAbility());
                break;
            case TORCHWOOD:
                abilities.add(new ModifierProjectileAbility());
                break;
            case MAGNET_SHROOM:
                break;
            case HYPNO_SHROOM:
                break;
            case CAT_TAIL:
                break;
            case IMITATER:
                abilities.add(new ImitaterAbility());
                break;
            case ICE_SHROOM:
                abilities.add(new IceShroomAbility());
                break;
            case LILY_PAD:
                break;
            case HOT_POTATO:
                abilities.add(new HotPotatoAbility());
                break;
            case GRAVE_BUSTER:
                abilities.add(new GraveBusterAbility());
                break;
            case ENLIGHTEN_MINT:
            case APPEASE_MINT:
            case ARMA_MINT:
            case BOMBARD_MINT:
            case ENFORCE_MINT:
            case REINFORCE_MINT:
            case ENCHANT_MINT:
            case PIERCE_MINT:
            case CAT_TAIL_MINT:
                abilities.add(new MintEmpowermentAbility());
                break;
        }
        return abilities;
    }
}
