package game.service.sun;

import app.enums.plant.PlantName;
import app.enums.sun.SunState;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.sun.PlantSun;
import game.entity.sun.SunLaunchSpec;
import game.entity.sun.SunSpawnPoint;
import game.entity.sun.SunToss;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SunLaunchTrajectoryTest {
    private static final Position CENTER = new Position(100, 200);
    private static final Position LANDING = new Position(110, 190);

    @Test
    void catalogOffsetsMatchConfiguredPlants() {
        assertPoint(SunLaunchCatalog.get(PlantName.SUNFLOWER).getSources().get(0), 0, 40);
        assertPoint(SunLaunchCatalog.get(PlantName.SUNFLOWER).getPlantFoodSources().get(0), 0, 40);

        SunLaunchSpec twin = SunLaunchCatalog.get(PlantName.TWIN_SUNFLOWER);
        assertEquals(2, twin.getSources().size());
        assertPoint(twin.getSources().get(0), 40, 40);
        assertPoint(twin.getSources().get(1), -25, 30);
        assertPoint(twin.getPlantFoodSources().get(0), 40, 40);
        assertPoint(twin.getPlantFoodSources().get(1), -25, 30);

        assertPoint(SunLaunchCatalog.get(PlantName.SUN_SHROOM).getSources().get(0), 0, 30);
        assertPoint(SunLaunchCatalog.get(PlantName.SUN_SHROOM).getPlantFoodSources().get(0), 0, 30);

        SunLaunchSpec primal = SunLaunchCatalog.get(PlantName.PRIMAL_SUNFLOWER);
        assertPoint(primal.getSources().get(0), 0, 60);
        assertPoint(primal.getPlantFoodSources().get(0), 65, 0);

        SunLaunchSpec gold = SunLaunchCatalog.get(PlantName.GOLD_BLOOM);
        assertPoint(gold.getSources().get(0), 0, 240);
        assertTrue(gold.getPlantFoodSources().isEmpty());
    }

    @Test
    void sunflowerSourceConvertsFromPamCenter() {
        Position world = sourceWorld(PlantName.SUNFLOWER, false, 0);
        assertEquals(100d, world.getX(), 0.0001d);
        assertEquals(240d, world.getY(), 0.0001d);
    }

    @Test
    void twinSourcesAreIndependent() {
        Position a = sourceWorld(PlantName.TWIN_SUNFLOWER, false, 0);
        Position b = sourceWorld(PlantName.TWIN_SUNFLOWER, false, 1);
        assertEquals(140d, a.getX(), 0.0001d);
        assertEquals(240d, a.getY(), 0.0001d);
        assertEquals(75d, b.getX(), 0.0001d);
        assertEquals(230d, b.getY(), 0.0001d);

        SunToss tossA = plan(PlantName.TWIN_SUNFLOWER, false, 0);
        SunToss tossB = plan(PlantName.TWIN_SUNFLOWER, false, 1);
        assertNotEquals(tossA.spawn().getX(), tossB.spawn().getX());
        tossA.advance();
        assertEquals(0, tossB.getElapsedTicks());
        assertTrue(a.getX() != b.getX() || a.getY() != b.getY());
    }

    @Test
    void sunShroomSourceConvertsFromPamCenter() {
        Position world = sourceWorld(PlantName.SUN_SHROOM, false, 0);
        assertEquals(100d, world.getX(), 0.0001d);
        assertEquals(230d, world.getY(), 0.0001d);
    }

    @Test
    void primalNormalAndPlantFoodUseDifferentOffsets() {
        Position normal = sourceWorld(PlantName.PRIMAL_SUNFLOWER, false, 0);
        Position plantFood = sourceWorld(PlantName.PRIMAL_SUNFLOWER, true, 0);
        assertEquals(100d, normal.getX(), 0.0001d);
        assertEquals(260d, normal.getY(), 0.0001d);
        assertEquals(165d, plantFood.getX(), 0.0001d);
        assertEquals(200d, plantFood.getY(), 0.0001d);
    }

    @Test
    void goldBloomOffsetIsNotDoubleScaled() {
        Position world = sourceWorld(PlantName.GOLD_BLOOM, false, 0);
        double expectedY = 200d + 240d * SunSourceLocator.PAM_RENDER_SCALE;
        assertEquals(100d, world.getX(), 0.0001d);
        assertEquals(expectedY, world.getY(), 0.0001d);
        assertEquals(440d, world.getY(), 0.0001d);
        assertTrue(SunSourceLocator.PAM_RENDER_SCALE == 1d);
    }

    @Test
    void directRequestHasNoArc() {
        SunSpawnRequest direct = SunSpawnRequest.builder(25, LANDING)
            .producerLabel("sky")
            .build();
        assertFalse(direct.isLaunch());
        assertNull(direct.getSourceWorld());
    }

    @Test
    void launchRequestCarriesSourceAndLanding() {
        Position source = sourceWorld(PlantName.SUNFLOWER, false, 0);
        SunSpawnRequest launch = SunSpawnRequest.builder(50, LANDING)
            .sourceWorld(source)
            .durationTicks(SunLaunchCatalog.DEFAULT_DURATION_TICKS)
            .arcHeight(SunLaunchCatalog.DEFAULT_ARC_HEIGHT)
            .build();
        assertTrue(launch.isLaunch());
        assertEquals(source.getX(), launch.getSourceWorld().getX(), 0.0001d);
        assertEquals(LANDING.getX(), launch.getLanding().getX(), 0.0001d);
    }

    @Test
    void lastTickSnapsToExactLandingAndStops() {
        SunToss toss = plan(PlantName.SUNFLOWER, false, 0);
        PlantSun sun = new PlantSun(toss.spawn(), 50);
        sun.startToss(toss);
        assertFalse(sun.isCollectable());
        assertEquals(SunState.DROPPING, sun.getState());

        int duration = toss.getDurationTicks();
        assertEquals(SunLaunchCatalog.DEFAULT_DURATION_TICKS, duration);
        assertTrue(duration >= 6 && duration <= 8);
        assertEquals(10, GameClock.TICKS_PER_SECOND);

        Position previous = null;
        for (int i = 0; i < duration; i++) {
            sun.advanceToss();
            previous = sun.getPosition();
        }
        assertFalse(sun.isTossing());
        assertTrue(sun.isCollectable());
        assertEquals(LANDING.getX(), sun.getPosition().getX(), 0.0001d);
        assertEquals(LANDING.getY(), sun.getPosition().getY(), 0.0001d);
        assertEquals(SunState.ON_PLANT, sun.getState());
        assertNull(sun.getToss());

        sun.advanceToss();
        assertEquals(previous.getX(), sun.getPosition().getX(), 0.0001d);
        assertEquals(previous.getY(), sun.getPosition().getY(), 0.0001d);
        assertEquals(LANDING.getX(), sun.getPosition().getX(), 0.0001d);
        assertEquals(LANDING.getY(), sun.getPosition().getY(), 0.0001d);
    }

    @Test
    void collectionBlockedDuringFlightThenAllowed() {
        SunToss toss = plan(PlantName.SUNFLOWER, false, 0);
        PlantSun sun = new PlantSun(toss.spawn(), 50);
        sun.startToss(toss);
        sun.advanceToss();
        assertTrue(sun.isTossing());
        assertFalse(sun.isCollectable());
        while (sun.isTossing()) {
            assertFalse(sun.isCollectable());
            sun.advanceToss();
        }
        assertTrue(sun.isCollectable());
    }

    @Test
    void parabolaMatchesExistingSunToss() {
        SunToss toss = plan(PlantName.SUNFLOWER, false, 0);
        assertEquals(0d, toss.arc(0d), 0.0001d);
        assertEquals(0d, toss.arc(1d), 0.0001d);
        assertEquals(SunLaunchCatalog.DEFAULT_ARC_HEIGHT, toss.arc(0.5d), 0.0001d);
        Position mid = toss.pointAt(0.5d);
        double expectedY = (CENTER.getY() + 40d + LANDING.getY()) * 0.5d + SunLaunchCatalog.DEFAULT_ARC_HEIGHT;
        assertEquals((CENTER.getX() + LANDING.getX()) * 0.5d, mid.getX(), 0.0001d);
        assertEquals(expectedY, mid.getY(), 0.0001d);
    }

    @Test
    void worksBothDirections() {
        SunLaunchSpec spec = SunLaunchCatalog.get(PlantName.SUNFLOWER);
        Position leftLanding = new Position(50, 200);
        SunToss left = SunLaunchPlanner.plan(CENTER, spec, leftLanding, 0, false);
        Position rightLanding = new Position(150, 200);
        SunToss right = SunLaunchPlanner.plan(CENTER, spec, rightLanding, 0, false);
        assertTrue(left.pointAt(1d).getX() < CENTER.getX());
        assertTrue(right.pointAt(1d).getX() > CENTER.getX());
        assertEquals(leftLanding.getX(), left.pointAt(1d).getX(), 0.0001d);
        assertEquals(rightLanding.getX(), right.pointAt(1d).getX(), 0.0001d);
    }

    @Test
    void peashooterHasNoLaunchSpecSoDirect() {
        assertNull(SunLaunchCatalog.get(PlantName.PEASHOOTER));
    }

    private static Position sourceWorld(PlantName name, boolean plantFood, int index) {
        SunToss toss = plan(name, plantFood, index);
        assertNotNull(toss);
        return toss.spawn();
    }

    private static SunToss plan(PlantName name, boolean plantFood, int index) {
        return SunLaunchPlanner.plan(CENTER, SunLaunchCatalog.get(name), LANDING, index, plantFood);
    }

    private static void assertPoint(SunSpawnPoint point, double x, double y) {
        assertEquals(x, point.getOffsetX(), 0.0001d);
        assertEquals(y, point.getOffsetY(), 0.0001d);
    }
}
