package game.entity.registry;

import app.enums.plant.PlantName;
import game.engine.time.GameClock;
import game.entity.projectile.ProjectileCapabilities;
import game.entity.plant.ability.PlantTargeting;
import game.entity.plant.plantfood.PlantFoodAbilityType;
import game.entity.plant.plantfood.PlantFoodDefinition;
import game.entity.plant.combat.StarfruitCombat;
import game.entity.plant.plantfood.PlantFoodShot;

public final class PlantFoodDefinitionLoader {
    private static final int RAPID_FIRE_DURATION_TICKS = 4 * GameClock.TICKS_PER_SECOND;
    private static final int WALL_NUT_PLANT_FOOD_HEAL = 4000;
    private static final int TALL_NUT_PLANT_FOOD_HEAL = 8000;
    private static final int ENDURIAN_PLANT_FOOD_HEAL = 3000;
    private static final int GARLIC_PLANT_FOOD_EFFECT_TICK = 12;
    private static final int GARLIC_PLANT_FOOD_DIVERT_TICK = 16;
    private static final float GARLIC_PROJECTILE_OFFSET_X = 100f;
    private static final float GARLIC_PROJECTILE_OFFSET_Y = -4f;
    private static final String GARLIC_PLANT_FOOD_PROJECTILE_EFFECT_ID = "garlic plantfood projectile";

    private PlantFoodDefinitionLoader() {
    }

    public static void load() {
        registerSunProducers();
        registerShooters();
        registerMelee();
        registerDefense();
    }

    private static void registerSunProducers() {
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.SUNFLOWER, PlantFoodAbilityType.SUN_BURST)
                .sunAmount(150)
                .effectDelayTicks(2)
                .animationDurationTicks(2 * GameClock.TICKS_PER_SECOND)
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.TWIN_SUNFLOWER, PlantFoodAbilityType.SUN_BURST)
                .sunAmount(250)
                .effectDelayTicks(2)
                .animationDurationTicks(2 * GameClock.TICKS_PER_SECOND)
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.SUN_SHROOM, PlantFoodAbilityType.GROWTH_SUN_BURST)
                .sunAmount(225)
                .forceMaxGrowth(true)
                .effectDelayTicks(28)
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.PRIMAL_SUNFLOWER, PlantFoodAbilityType.SUN_BURST)
                .sunAmount(225)
                .effectDelayTicks(4)
                .animationDurationTicks(2 * GameClock.TICKS_PER_SECOND)
                .build()
        );
    }

    private static void registerShooters() {
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.PEASHOOTER, PlantFoodAbilityType.RAPID_FIRE)
                .durationTicks(RAPID_FIRE_DURATION_TICKS)
                .fireIntervalTicks(4)
                .fireStartDelayTicks(1)
                .addStream(PlantFoodShot.builder().offset(50, 25).delayTicks(1).build())
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.REPEATER, PlantFoodAbilityType.RAPID_FIRE)
                .durationTicks(RAPID_FIRE_DURATION_TICKS)
                .fireIntervalTicks(4)
                .fireStartDelayTicks(1)
                .addStream(PlantFoodShot.builder().offset(50, 20).delayTicks(1).build())
                .addStream(PlantFoodShot.builder().offset(140, 26).delayTicks(18).animationStage(1).build())
                .finishingShot(
                    PlantFoodShot.builder()
                        .offset(140, 26)
                        .delayTicks(RAPID_FIRE_DURATION_TICKS)
                        .damageMultiplier(20f)
                        .effectId("repeater plantfood giantpea")
                        .animationStage(1)
                        .build()
                )
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.THREEPEATER, PlantFoodAbilityType.RAPID_FIRE)
                .durationTicks(RAPID_FIRE_DURATION_TICKS)
                .fireIntervalTicks(2)
                .fireStartDelayTicks(1)
                .addStream(
                    PlantFoodShot.builder()
                        .offset(80, 25)
                        .delayTicks(1)
                        .speedMultiplier(2f)
                        .allLanes(true)
                        .build()
                )
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.SNOW_PEA, PlantFoodAbilityType.RAPID_FIRE)
                .durationTicks(RAPID_FIRE_DURATION_TICKS)
                .fireIntervalTicks(4)
                .fireStartDelayTicks(1)
                .laneFreeze(true)
                .freezeEffectId("snowpea plantfood freeze")
                .freezeOffEffectId("snowpea plantfood freeze off")
                .freezeHoldSeconds(RAPID_FIRE_DURATION_TICKS / (float) GameClock.TICKS_PER_SECOND)
                .particleEffectId("snowpea plantfood slow")
                .addStream(
                    PlantFoodShot.builder()
                        .offset(45, 25)
                        .delayTicks(1)
                        .speedMultiplier(1.5f)
                        .ice(true)
                        .build()
                )
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.ROTOBAGA, PlantFoodAbilityType.RAPID_FIRE)
                .fireIntervalTicks(1)
                .fireStartDelayTicks(5)
                .maxShotsPerStream(14)
                .addStream(diagonal(40, 5, 1.5f, 50, 70))
                .addStream(diagonal(-40, 5, 1.5f, 45, -25))
                .addStream(diagonal(140, 5, 1.5f, -55, 70))
                .addStream(diagonal(220, 5, 1.5f, -45, -20))
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.SPLIT_PEA, PlantFoodAbilityType.RAPID_FIRE)
                .durationTicks(RAPID_FIRE_DURATION_TICKS)
                .fireIntervalTicks(5)
                .fireStartDelayTicks(1)
                .addStream(PlantFoodShot.builder().offset(60, 20).delayTicks(1).build())
                .addStream(PlantFoodShot.builder().offset(-70, 27).angle(180).delayTicks(2).build())
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.CITRON, PlantFoodAbilityType.RAPID_FIRE)
                .fireIntervalTicks(1)
                .fireStartDelayTicks(8)
                .maxShotsPerStream(1)
                .addStream(
                    PlantFoodShot.builder()
                        .offset(60f, 0f)
                        .delayTicks(8)
                        .fromPamVisualCenter(true)
                        .applyFacing(true)
                        .immediateSpawn(true)
                        .capabilities(
                            ProjectileCapabilities.builder()
                                .unstoppable(true)
                                .pierceAll(true)
                                .instantKill(true)
                                .enemyOnly(true)
                                .plantFoodVisual(true)
                                .hitEffectId("citron_plantfood_orb_hit")
                                .build()
                        )
                        .build()
                )
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.CACTUS, PlantFoodAbilityType.EMPOWER)
                .durationTicks(15 * GameClock.TICKS_PER_SECOND)
                .animationDurationTicks(15 * GameClock.TICKS_PER_SECOND)
                .addStream(
                    PlantFoodShot.builder()
                        .offset(65f, 55f)
                        .delayTicks(5)
                        .fromPamVisualCenter(true)
                        .applyFacing(true)
                        .capabilities(
                            ProjectileCapabilities.builder()
                                .pierceAll(true)
                                .enemyOnly(true)
                                .plantFoodVisual(true)
                                .hitEffectId("cactus_plantfood_hit")
                                .build()
                        )
                        .build()
                )
                .build()
        );

        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.FIRE_PEASHOOTER, PlantFoodAbilityType.FIRE_LINE)
                .durationTicks(60)
                .animationDurationTicks(60)
                .fireLine(true)
                .damageIntervalTicks(2)
                .particleEffectId("firepeashooter_plantfood_fire")
                .build()
        );
        PlantFoodDefinition.Builder starfruit = PlantFoodDefinition.builder(PlantName.STARFRUIT, PlantFoodAbilityType.RAPID_FIRE)
            .durationTicks(30)
            .animationDurationTicks(30)
            .fireIntervalTicks(1)
            .fireStartDelayTicks(0);
        for (StarfruitCombat.Shot star : StarfruitCombat.SHOTS) {
            starfruit.addStream(
                PlantFoodShot.builder()
                    .offset(star.offsetX(), star.offsetY())
                    .angle(star.angleDegrees())
                    .delayTicks(0)
                    .immediateSpawn(true)
                    .build()
            );
        }
        PlantFoodDefinitionRegistry.register(starfruit.build());
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.GOO_PEASHOOTER, PlantFoodAbilityType.RAPID_FIRE)
                .fireIntervalTicks(1)
                .fireStartDelayTicks(11)
                .maxShotsPerStream(1)
                .addStream(
                    PlantFoodShot.builder()
                        .offset(180f, 20f)
                        .delayTicks(11)
                        .fromPamVisualCenter(true)
                        .applyFacing(true)
                        .immediateSpawn(true)
                        .capabilities(
                            ProjectileCapabilities.builder()
                                .unstoppable(true)
                                .pierceAll(true)
                                .instantKill(true)
                                .enemyOnly(true)
                                .plantFoodVisual(true)
                                .build()
                        )
                        .build()
                )
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.MEGA_GATLING_PEA, PlantFoodAbilityType.RAPID_FIRE)
                .durationTicks(40)
                .animationDurationTicks(40)
                .fireIntervalTicks(3)
                .fireStartDelayTicks(1)
                .addStream(
                    PlantFoodShot.builder()
                        .offset(100f, 25f)
                        .delayTicks(1)
                        .fromPamVisualCenter(true)
                        .applyFacing(true)
                        .immediateSpawn(true)
                        .build()
                )
                .build()
        );
    }

    private static void registerMelee() {
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.BONK_CHOY, PlantFoodAbilityType.AREA_PULSE)
                .durationTicks(RAPID_FIRE_DURATION_TICKS)
                .animationDurationTicks(RAPID_FIRE_DURATION_TICKS)
                .fireIntervalTicks(1)
                .fireStartDelayTicks(0)
                .areaRadius(1)
                .areaPercent(0.25f)
                .build()
        );
        int wasabiWhipDurationTicks = (int) (PlantName.WASABI_WHIP.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND);
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.WASABI_WHIP, PlantFoodAbilityType.AREA_PULSE)
                .durationTicks(wasabiWhipDurationTicks)
                .animationDurationTicks(wasabiWhipDurationTicks)
                .fireIntervalTicks(1)
                .fireStartDelayTicks(0)
                .areaRadius(1)
                .areaPercent(0.25f)
                .build()
        );
        int kiwibeastDurationTicks = lastScheduledTick(10, 17, 27) + 1;
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.KIWIBEAST, PlantFoodAbilityType.AREA_PULSE)
                .durationTicks(kiwibeastDurationTicks)
                .animationDurationTicks(kiwibeastDurationTicks)
                .forceMaxGrowth(true)
                .areaRadius(PlantTargeting.ALL_LAWN_RADIUS)
                .pulseTicks(10, 17, 27)
                .effectTicks(5, 12, 22)
                .particleEffectId("kiwibeast pf pulse")
                .secondaryParticleEffectId("kiwibeast tile hit")
                .build()
        );
        int phatBeetDurationTicks = (int) (PlantName.PHAT_BEET.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND);
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.PHAT_BEET, PlantFoodAbilityType.AREA_PULSE)
                .durationTicks(phatBeetDurationTicks)
                .animationDurationTicks(phatBeetDurationTicks)
                .fireIntervalTicks(phatBeetDurationTicks)
                .fireStartDelayTicks(6)
                .areaRadius(PlantTargeting.ALL_LAWN_RADIUS)
                .particleEffectId("phatbeets pf pulse")
                .build()
        );
    }

    private static void registerDefense() {
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.WALL_NUT, PlantFoodAbilityType.HEAL)
                .healAmount(WALL_NUT_PLANT_FOOD_HEAL)
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.TALL_NUT, PlantFoodAbilityType.HEAL)
                .healAmount(TALL_NUT_PLANT_FOOD_HEAL)
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.ENDURIAN, PlantFoodAbilityType.HEAL)
                .healAmount(ENDURIAN_PLANT_FOOD_HEAL)
                .build()
        );
        int garlicDurationTicks = Math.max(
            GARLIC_PLANT_FOOD_DIVERT_TICK + 1,
            (int) (PlantName.GARLIC.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND)
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.GARLIC, PlantFoodAbilityType.LANE_DIVERT)
                .restoreFullHealth(true)
                .durationTicks(garlicDurationTicks)
                .animationDurationTicks(garlicDurationTicks)
                .effectTicks(GARLIC_PLANT_FOOD_EFFECT_TICK)
                .pulseTicks(GARLIC_PLANT_FOOD_DIVERT_TICK)
                .particleEffectId(GARLIC_PLANT_FOOD_PROJECTILE_EFFECT_ID)
                .effectOffset(GARLIC_PROJECTILE_OFFSET_X, GARLIC_PROJECTILE_OFFSET_Y)
                .build()
        );
        int sweetPotatoDurationTicks = (int) (PlantName.SWEET_POTATO.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND);
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.SWEET_POTATO, PlantFoodAbilityType.LANE_DIVERT)
                .restoreFullHealth(true)
                .attractToPlantLane(true)
                .includeAdjacentLanes(true)
                .pulseTicks(0)
                .durationTicks(sweetPotatoDurationTicks)
                .animationDurationTicks(sweetPotatoDurationTicks)
                .build()
        );
        int explodeONutDurationTicks = (int) (PlantName.EXPLODE_O_NUT.getPlantFoodDuration() * GameClock.TICKS_PER_SECOND);
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.EXPLODE_O_NUT, PlantFoodAbilityType.HEAL)
                .restoreFullHealth(true)
                .durationTicks(explodeONutDurationTicks)
                .animationDurationTicks(explodeONutDurationTicks)
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.PUMPKIN, PlantFoodAbilityType.HEAL)
                .restoreFullHealth(true)
                .durationTicks(1)
                .animationDurationTicks(1)
                .build()
        );
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.SUN_BEAN, PlantFoodAbilityType.HEAL)
                .restoreFullHealth(true)
                .build()
        );
        int torchwoodDurationTicks = 6 * GameClock.TICKS_PER_SECOND;
        PlantFoodDefinitionRegistry.register(
            PlantFoodDefinition.builder(PlantName.TORCHWOOD, PlantFoodAbilityType.EMPOWER)
                .durationTicks(torchwoodDurationTicks)
                .animationDurationTicks(torchwoodDurationTicks)
                .build()
        );
    }

    private static int lastScheduledTick(int... ticks) {
        int last = 0;
        if (ticks == null) {
            return last;
        }
        for (int tick : ticks) {
            if (tick > last) {
                last = tick;
            }
        }
        return last;
    }

    private static PlantFoodShot diagonal(double angle, int delayTicks, float speedMultiplier, float offsetX, float offsetY) {
        return PlantFoodShot.builder()
            .angle(angle)
            .delayTicks(delayTicks)
            .speedMultiplier(speedMultiplier)
            .offset(offsetX, offsetY)
            .build();
    }
}
