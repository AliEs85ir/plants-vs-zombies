package graphics.general.animation.strategy;

import game.entity.plant.HealthSegments;
import game.entity.plant.Plant;
import game.entity.plant.PlantHealth;
import graphics.plant.PlantAnimationTag;
import graphics.plant.PlantAnimationVariant;
import graphics.plant.PlantConditions;
import graphics.plant.PlantRenderInstance;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlantIdleSelectionStrategyTest {

    @Test
    void wallNutSelectsDamageClipsFromLiveHealthQuarters() {
        Plant plant = plantWithHealth(4000);
        PlantRenderInstance instance = new PlantRenderInstance(plant);
        PlantIdleSelectionStrategy strategy = new PlantIdleSelectionStrategy();
        List<PlantAnimationVariant> variants = wallNutIdleVariants();

        assertEquals("idle", strategy.select(variants, instance).getClipName());

        plant.getHealth().setCurrentHealth(3000);
        assertEquals("damage", strategy.select(variants, instance).getClipName());

        plant.getHealth().setCurrentHealth(2000);
        assertEquals("damage2", strategy.select(variants, instance).getClipName());

        plant.getHealth().setCurrentHealth(1000);
        assertEquals("damage3", strategy.select(variants, instance).getClipName());

        plant.heal(plant.getHealth().getMaxHealth());
        assertEquals("idle", strategy.select(variants, instance).getClipName());
    }

    @Test
    void sweetPotatoSelectsDamageClipsFromLiveHealthQuarters() {
        Plant plant = plantWithHealth(3000);
        PlantRenderInstance instance = new PlantRenderInstance(plant);
        PlantIdleSelectionStrategy strategy = new PlantIdleSelectionStrategy();
        List<PlantAnimationVariant> variants = List.of(
            defaultClip("idle", 0, HealthSegments.QUARTERS),
            defaultClip("idle_damage", 1, HealthSegments.QUARTERS),
            defaultClip("idle_damage2", 2, HealthSegments.QUARTERS),
            defaultClip("idle_damage3", 3, HealthSegments.QUARTERS),
            new PlantAnimationVariant("idle", true, 0, PlantAnimationTag.DEFAULT)
        );

        assertEquals("idle", strategy.select(variants, instance).getClipName());

        plant.getHealth().setCurrentHealth(2250);
        assertEquals("idle_damage", strategy.select(variants, instance).getClipName());

        plant.getHealth().setCurrentHealth(1500);
        assertEquals("idle_damage2", strategy.select(variants, instance).getClipName());

        plant.getHealth().setCurrentHealth(750);
        assertEquals("idle_damage3", strategy.select(variants, instance).getClipName());

        plant.heal(plant.getHealth().getMaxHealth());
        assertEquals("idle", strategy.select(variants, instance).getClipName());
    }

    @Test
    void loopingBlinkDoesNotBlockHealthStageChange() {
        Plant plant = plantWithHealth(4000);
        PlantRenderInstance instance = new PlantRenderInstance(plant);
        PlantIdleSelectionStrategy strategy = new PlantIdleSelectionStrategy();
        List<PlantAnimationVariant> variants = wallNutIdleVariants();

        PlantAnimationVariant blink = variants.get(1);
        instance.getAnimationController().changeVariant(blink, null, null);
        assertEquals("idle2", strategy.select(variants, instance).getClipName());

        plant.getHealth().setCurrentHealth(2500);
        assertEquals("damage", strategy.select(variants, instance).getClipName());
    }

    @Test
    void tallNutSelectsThirdBasedDamageClips() {
        Plant plant = plantWithHealth(8000);
        PlantRenderInstance instance = new PlantRenderInstance(plant);
        PlantIdleSelectionStrategy strategy = new PlantIdleSelectionStrategy();
        List<PlantAnimationVariant> variants = List.of(
            defaultClip("water", 0, HealthSegments.THIRDS),
            defaultClip("damage", 1, HealthSegments.THIRDS),
            defaultClip("damage2", 2, HealthSegments.THIRDS)
        );

        assertEquals("water", strategy.select(variants, instance).getClipName());

        plant.getHealth().setCurrentHealth(8000 * 2.0d / 3.0d);
        assertEquals("damage", strategy.select(variants, instance).getClipName());

        plant.getHealth().setCurrentHealth(8000 / 3.0d);
        assertEquals("damage2", strategy.select(variants, instance).getClipName());
    }

    @Test
    void endurianIdleUsesSameQuarterThresholdsAsWallNut() {
        Plant plant = plantWithHealth(3000);
        PlantRenderInstance instance = new PlantRenderInstance(plant);
        PlantIdleSelectionStrategy strategy = new PlantIdleSelectionStrategy();
        List<PlantAnimationVariant> variants = wallNutIdleVariants();

        plant.getHealth().setCurrentHealth(1500);
        assertEquals("damage2", strategy.select(variants, instance).getClipName());
    }

    @Test
    void explodeONutSelectsQuarterDamageClips() {
        Plant plant = plantWithHealth(4000);
        PlantRenderInstance instance = new PlantRenderInstance(plant);
        PlantIdleSelectionStrategy strategy = new PlantIdleSelectionStrategy();
        List<PlantAnimationVariant> variants = List.of(
            defaultClip("idle", 0, HealthSegments.QUARTERS),
            defaultClip("damage", 1, HealthSegments.QUARTERS),
            defaultClip("damage2", 2, HealthSegments.QUARTERS),
            defaultClip("damage3", 3, HealthSegments.QUARTERS),
            new PlantAnimationVariant("idle", true, 0, PlantAnimationTag.DEFAULT)
        );

        assertEquals("idle", strategy.select(variants, instance).getClipName());
        plant.getHealth().setCurrentHealth(3000);
        assertEquals("damage", strategy.select(variants, instance).getClipName());
        plant.getHealth().setCurrentHealth(2000);
        assertEquals("damage2", strategy.select(variants, instance).getClipName());
        plant.getHealth().setCurrentHealth(1000);
        assertEquals("damage3", strategy.select(variants, instance).getClipName());
        plant.heal(plant.getHealth().getMaxHealth());
        assertEquals("idle", strategy.select(variants, instance).getClipName());
    }

    @Test
    void pumpkinSelectsThirdBasedIdleClips() {
        Plant plant = plantWithHealth(4000);
        PlantRenderInstance instance = new PlantRenderInstance(plant);
        PlantIdleSelectionStrategy strategy = new PlantIdleSelectionStrategy();
        List<PlantAnimationVariant> variants = List.of(
            defaultClip("idle", 0, HealthSegments.THIRDS),
            defaultClip("idle2", 1, HealthSegments.THIRDS),
            defaultClip("idle3", 2, HealthSegments.THIRDS),
            new PlantAnimationVariant("idle", true, 0, PlantAnimationTag.DEFAULT)
        );

        assertEquals("idle", strategy.select(variants, instance).getClipName());
        plant.getHealth().setCurrentHealth(4000 * 2.0d / 3.0d);
        assertEquals("idle2", strategy.select(variants, instance).getClipName());
        plant.getHealth().setCurrentHealth(4000 / 3.0d);
        assertEquals("idle3", strategy.select(variants, instance).getClipName());
        plant.heal(plant.getHealth().getMaxHealth());
        assertEquals("idle", strategy.select(variants, instance).getClipName());
    }

    private static Plant plantWithHealth(int maxHealth) {
        Plant plant = new Plant();
        plant.setHealth(new PlantHealth(maxHealth));
        return plant;
    }

    private static List<PlantAnimationVariant> wallNutIdleVariants() {
        return List.of(
            defaultClip("idle", 0, HealthSegments.QUARTERS),
            blinkClip("idle2", 0, HealthSegments.QUARTERS),
            defaultClip("damage", 1, HealthSegments.QUARTERS),
            defaultClip("damage2", 2, HealthSegments.QUARTERS),
            defaultClip("damage3", 3, HealthSegments.QUARTERS),
            new PlantAnimationVariant("idle", true, 0, PlantAnimationTag.DEFAULT)
        );
    }

    private static PlantAnimationVariant defaultClip(String clip, int segment, int count) {
        return new PlantAnimationVariant(
            clip,
            true,
            0,
            PlantAnimationTag.DEFAULT,
            PlantConditions.healthSegment(segment, count)
        );
    }

    private static PlantAnimationVariant blinkClip(String clip, int segment, int count) {
        return new PlantAnimationVariant(
            clip,
            true,
            0,
            PlantAnimationTag.BLINK,
            PlantConditions.healthSegment(segment, count)
        );
    }
}
