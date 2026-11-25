package game.entity.zombie.part;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ZombieBodyPartLayersTest {

    @Test
    void basicZombieDropsOuterHandAndLowerArm() {
        List<String> layers = List.of(
            "zombie_hand_inner_01",
            "zombie_arm_inner_lower",
            "zombie_jaw",
            "zombie_skull",
            "zombie_hand_outer_01",
            "zombie_arm_outer_lower",
            "zombie_arm_outer_upper_bone",
            "zombie_armor_bucket_norm",
            "particle_head",
            "particle_arm"
        );

        List<String> hand = ZombieBodyPartLayers.matching(ZombieBodyPart.HAND, layers);
        assertTrue(hand.contains("zombie_hand_outer_01"));
        assertTrue(hand.contains("zombie_arm_outer_lower"));
        assertTrue(hand.contains("particle_arm"));
        assertFalse(hand.contains("zombie_hand_inner_01"));
        assertFalse(hand.contains("zombie_arm_outer_upper_bone"));

        List<String> head = ZombieBodyPartLayers.matching(ZombieBodyPart.HEAD, layers);
        assertEquals(List.of("zombie_jaw", "zombie_skull", "particle_head"), head);
    }

    @Test
    void egyptAndFlagLayersFollowTheSameOuterArmRule() {
        List<String> layers = List.of(
            "zombie_egypt_hand_inner_01",
            "zombie_egypt_hand_outer_01",
            "zombie_egypt_arm_outer_lower",
            "zombie_egypt_jaw",
            "zombie_egypt_skull",
            "flag_01",
            "flag_stick"
        );

        List<String> hand = ZombieBodyPartLayers.matching(ZombieBodyPart.HAND, layers);
        assertEquals(List.of("zombie_egypt_hand_outer_01", "zombie_egypt_arm_outer_lower"), hand);

        List<String> head = ZombieBodyPartLayers.matching(ZombieBodyPart.HEAD, layers);
        assertEquals(List.of("zombie_egypt_jaw", "zombie_egypt_skull"), head);
        assertFalse(head.contains("flag_01"));
    }

    @Test
    void impDropsOuterHandAndHeadPieces() {
        List<String> layers = List.of(
            "zombie_imp_hand_inner",
            "zombie_imp_hand_outer",
            "zombie_imp_arm_outer_lower",
            "zombie_imp_jaw",
            "zombie_imp_skull",
            "_zombie_imp_head_top",
            "zombie_imp_tail"
        );

        List<String> hand = ZombieBodyPartLayers.matching(ZombieBodyPart.HAND, layers);
        assertEquals(List.of("zombie_imp_hand_outer", "zombie_imp_arm_outer_lower"), hand);

        List<String> head = ZombieBodyPartLayers.matching(ZombieBodyPart.HEAD, layers);
        assertTrue(head.contains("zombie_imp_jaw"));
        assertTrue(head.contains("zombie_imp_skull"));
        assertTrue(head.contains("_zombie_imp_head_top"));
        assertFalse(head.contains("zombie_imp_tail"));
    }

    @Test
    void gargantuarIgnoresNestedImpAndBasicLayers() {
        List<String> layers = List.of(
            "Zombie_gargantuar_outerarm_hand",
            "Zombie_gargantuar_outerarm_lower",
            "Zombie_gargantuar_outerarm_upper",
            "Zombie_gargantuar_outerarm_finger",
            "Zombie_gargantuar_head",
            "Zombie_gargantuar_jaw",
            "Zombie_gargantuar_headBehind",
            "Gargantuar_Head_Particle",
            "zombie_imp_hand_outer",
            "zombie_imp_skull",
            "zombie_egypt_hand_outer_01",
            "zombie_egypt_jaw"
        );

        List<String> hand = ZombieBodyPartLayers.matching(ZombieBodyPart.HAND, layers);
        assertTrue(hand.contains("Zombie_gargantuar_outerarm_hand"));
        assertTrue(hand.contains("Zombie_gargantuar_outerarm_lower"));
        assertTrue(hand.contains("Zombie_gargantuar_outerarm_upper"));
        assertFalse(hand.contains("zombie_imp_hand_outer"));
        assertFalse(hand.contains("zombie_egypt_hand_outer_01"));

        List<String> head = ZombieBodyPartLayers.matching(ZombieBodyPart.HEAD, layers);
        assertTrue(head.contains("Zombie_gargantuar_head"));
        assertTrue(head.contains("Zombie_gargantuar_jaw"));
        assertTrue(head.contains("Gargantuar_Head_Particle"));
        assertFalse(head.contains("zombie_imp_skull"));
        assertFalse(head.contains("zombie_egypt_jaw"));
    }

    @Test
    void effectVisibilityTurnsOffOtherPartsAndArmorOnly() {
        List<String> layers = List.of(
            "root",
            "zombie_hand_outer_01",
            "zombie_arm_outer_lower",
            "zombie_skull",
            "zombie_jaw",
            "zombie_armor_bucket_norm",
            "particle_arm",
            "particle_head"
        );

        Map<String, Boolean> handDrop = ZombieBodyPartLayers.effectVisibility(ZombieBodyPart.HAND, layers);
        assertEquals(Boolean.TRUE, handDrop.get("zombie_hand_outer_01"));
        assertEquals(Boolean.TRUE, handDrop.get("particle_arm"));
        assertEquals(Boolean.FALSE, handDrop.get("zombie_skull"));
        assertEquals(Boolean.FALSE, handDrop.get("particle_head"));
        assertEquals(Boolean.FALSE, handDrop.get("zombie_armor_bucket_norm"));
        assertFalse(handDrop.containsKey("root"));

        Map<String, Boolean> headDrop = ZombieBodyPartLayers.effectVisibility(ZombieBodyPart.HEAD, layers);
        assertEquals(Boolean.TRUE, headDrop.get("zombie_skull"));
        assertEquals(Boolean.TRUE, headDrop.get("particle_head"));
        assertEquals(Boolean.FALSE, headDrop.get("zombie_hand_outer_01"));
        assertEquals(Boolean.FALSE, headDrop.get("particle_arm"));
    }
}
