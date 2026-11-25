package game.entity.zombie.ability;

import app.model.App;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.playground.Cell;
import graphics.general.instance.VisibilityController;
import graphics.zombie.ZombieRenderInstance;

public class InsideWaterAbility extends AbstractZombieAbility {
    private boolean submerged;
    private String[] appliedLayers = new String[0];

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if (zombie == null || zombie.getWorld() == null || zombie.getWorld().getGrid() == null
            || zombie.getPosition() == null) {
            return;
        }
        if (App.getRealTimeEngine() == null || App.getRealTimeEngine().getGraphicsEngine() == null) {
            return;
        }
        ZombieRenderInstance instance = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(zombie);
        if (instance == null) {
            return;
        }
        Cell cell = zombie.getWorld().getGrid().getCellAt(zombie.getPosition());
        boolean inWater = cell != null && cell.isWater();
        if (inWater == submerged && appliedLayers.length > 0 == inWater) {
            return;
        }
        String[] layers = layersToHide(zombie.getName());
        if (inWater) {
            hideLayers(instance, layers);
            appliedLayers = layers;
            if (!submerged) {
                game.audio.Sfx.play(game.audio.AudioId.SFX_ZOMBIE_ENTERING_WATER);
            }
        } else {
            unhideLayers(instance, appliedLayers);
            appliedLayers = new String[0];
        }
        submerged = inWater;
    }

    private static void hideLayers(ZombieRenderInstance instance, String[] layers) {
        VisibilityController controller = instance.getVisibilityController();
        for (String layer : layers) {
            controller.setInvisible(layer);
        }
    }

    private static void unhideLayers(ZombieRenderInstance instance, String[] layers) {
        VisibilityController controller = instance.getVisibilityController();
        for (String layer : layers) {
            controller.clearOverride(layer);
        }
    }

    private static String[] layersToHide(String name) {
        if (name == null) {
            return new String[0];
        }
        if ("ZombieBeachSnorkel".equals(name)) {
            return concat(LOWER_BODY_LAYERS, SNORKEL_EXTRA_LAYERS);
        }
        if (name.startsWith("Basic") || name.contains("Flag")
            || "Imp5".equals(name) || "ZombieBeachOctopus".equals(name)) {
            return LOWER_BODY_LAYERS;
        }
        return new String[0];
    }

    private static String[] concat(String[] first, String[] second) {
        String[] combined = new String[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    @Override
    public void onDeath(Zombie zombie) {
    }

    // Legs, feet, waist, tail, and ground shadow. Torso/head/arms stay visible
    // except for snorkel, which also hides SNORKEL_EXTRA_LAYERS.
    private static final String[] LOWER_BODY_LAYERS = {
        "ground_swatch",
        "ground_swatch_plane",
        "foot",
        "foot_inside",
        "leg_hip",
        "leg_upper",
        "leg_upper_inside",
        "zombie_foot_inner_heel",
        "zombie_foot_inner_toe",
        "zombie_foot_inner_toe_01",
        "zombie_foot_inner_toe_02",
        "zombie_foot_outer_heel",
        "zombie_foot_outer_toe",
        "zombie_leg_inner_lower",
        "zombie_leg_inner_upper",
        "zombie_leg_outer_lower",
        "zombie_leg_upper_outer",
        "zombie_waist",
        "zombie_egypt_foot_inner_heel",
        "zombie_egypt_foot_inner_toe_01",
        "zombie_egypt_foot_inner_toe_03",
        "zombie_egypt_foot_outer_heel",
        "zombie_egypt_foot_outer_toe",
        "zombie_egypt_leg_inner_lower",
        "zombie_egypt_leg_inner_upper",
        "zombie_egypt_leg_outer_lower",
        "zombie_egypt_leg_upper_outer",
        "zombie_egypt_waist",
        "zombie_egyptflag_foot_inner_heel",
        "zombie_egyptflag_foot_inner_toe",
        "zombie_egyptflag_foot_outer_heel",
        "zombie_egyptflag_foot_outer_toe",
        "zombie_egyptflag_leg_inner_lower",
        "zombie_egyptflag_leg_inner_upper",
        "zombie_egyptflag_leg_outer_lower",
        "zombie_egyptflag_leg_upper_outer",
        "zombie_egyptflag_waist",
        "zombie_pirate_basic_foot_inner_heel",
        "zombie_pirate_basic_foot_inner_toe",
        "zombie_pirate_basic_foot_outer_heel",
        "zombie_pirate_basic_foot_outer_toe",
        "zombie_pirate_basic_leg_inner_lower",
        "zombie_pirate_basic_leg_inner_upper",
        "zombie_pirate_basic_leg_outer_lower",
        "zombie_pirate_basic_leg_upper_outer",
        "zombie_pirate_basic_waist",
        "zombie_west_foot_inner_heel",
        "zombie_west_foot_inner_toe",
        "zombie_west_foot_outer_heel",
        "zombie_west_foot_outer_toe",
        "zombie_west_leg_inner_lower",
        "zombie_west_leg_inner_upper",
        "zombie_west_leg_outer_lower",
        "zombie_west_leg_upper_outer",
        "zombie_west_waist",
        "zombie_poncho_foot_inner_heel",
        "zombie_poncho_foot_inner_toe",
        "zombie_poncho_foot_outer_heel",
        "zombie_poncho_foot_outer_toe",
        "zombie_poncho_leg_inner_lower",
        "zombie_poncho_leg_outer_lower",
        "zombie_poncho_leg_upper_inner",
        "zombie_poncho_leg_upper_outer",
        "zombie_poncho_waist",
        "zombie_raincoat_foot_inner_heel",
        "zombie_raincoat_foot_inner_toe",
        "zombie_raincoat_foot_outer_heel",
        "zombie_raincoat_foot_outer_toe",
        "zombie_raincoat_leg_inner_lower",
        "zombie_raincoat_leg_inner_upper",
        "zombie_raincoat_leg_outer_lower",
        "zombie_raincoat_leg_outer_upper",
        "zombie_roman_foot_inner_heel",
        "zombie_roman_foot_inner_toe_01",
        "zombie_roman_foot_outer_heel",
        "zombie_roman_foot_outer_toe",
        "zombie_roman_leg_inner_lower",
        "zombie_roman_leg_inner_upper",
        "zombie_roman_leg_outer_lower",
        "zombie_roman_leg_upper_outer",
        "zombie_roman_waist",
        "zombie_swash_foot_inner_heel",
        "zombie_swash_foot_inner_toe_01",
        "zombie_swash_foot_inner_toe_02",
        "zombie_swash_foot_outer_heel",
        "zombie_swash_foot_outer_toe",
        "zombie_swash_leg_inner_lower",
        "zombie_swash_leg_inner_upper",
        "zombie_swash_leg_outer_lower",
        "zombie_swash_leg_upper_outer",
        "zombie_swash_waist",
        "zombie_imp_leg_inner_lower",
        "zombie_imp_leg_inner_upper",
        "zombie_imp_leg_outer_lower",
        "zombie_imp_leg_outer_upper",
        "zombie_imp_toe_inner",
        "zombie_imp_toe_outer",
        "zombie_imp_tail",
        "zombie_imp_waist",
        "zombie_snorkeler_foot_inner_heel",
        "zombie_snorkeler_foot_inner_toe",
        "zombie_snorkeler_foot_inner_toe2",
        "zombie_snorkeler_foot_inner_toe3",
        "zombie_snorkeler_foot_inner_toe4",
        "zombie_snorkeler_foot_inner_toe5",
        "zombie_snorkeler_foot_outer_heel",
        "zombie_snorkeler_foot_outer_toe",
        "zombie_snorkeler_foot_outer_toe2",
        "zombie_snorkeler_foot_outer_toe3",
        "zombie_snorkeler_foot_outer_toe4",
        "zombie_snorkeler_foot_outer_toe5",
        "zombie_snorkeler_leg_inner_lower",
        "zombie_snorkeler_leg_inner_upper",
        "zombie_snorkeler_leg_outer_lower",
        "zombie_snorkeler_leg_upper_outer",
        "_zombie_snorkeler_leg_upper_outer",
        "zombie_snorkeler_waist",
        "_zombie_snorkeler_waist"
    };

    private static final String[] SNORKEL_EXTRA_LAYERS = {
        "_zombie_snorkeler_arm_outer_upper",
        "_zombie_snorkeler_arm_outer_upper02",
        "_zombie_snorkeler_arm_outer_upper2",
        "_zombie_snorkeler_arm_outer_upper3",
        "_zombie_snorkeler_arms_outer_upper",
        "_zombie_snorkeler_arms_outer_upper1",
        "zombie_snorkeler_arm_inner_lower_01",
        "zombie_snorkeler_arm_inner_lower_02",
        "zombie_snorkeler_arm_inner_upper",
        "zombie_snorkeler_arm_outer_lower",
        "zombie_snorkeler_arm_outer_lower1",
        "zombie_snorkeler_arm_outer_upper_03",
        "zombie_snorkeler_basic_hand_outer_01",
        "zombie_snorkeler_hand_01",
        "zombie_snorkeler_hand_inner_lantern",
        "zombie_snorkeler_hand_outer_01",
        "zombie_snorkeler_hand_outer_02",
        "zombie_snorkeler_hand_outer_1",
        "zombie_snorkeler_torso",
        "_zombie_snorkeler_torso"
    };
}
