package game.entity.zombie.strategy.movement;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroundTrackedMovementTest {

    @Test
    void matchesRequestedZombieFamilies() {
        assertTrue(GroundTrackedMovement.appliesTo("Basic Egypt"));
        assertTrue(GroundTrackedMovement.appliesTo("Basic Halloween"));
        assertTrue(GroundTrackedMovement.appliesTo("Basic Pirate"));
        assertTrue(GroundTrackedMovement.appliesTo("Halloween Flag"));
        assertTrue(GroundTrackedMovement.appliesTo("Egypt Flag"));
        assertTrue(GroundTrackedMovement.appliesTo("West Flag11"));
        assertTrue(GroundTrackedMovement.appliesTo("Gargantuar1"));
        assertTrue(GroundTrackedMovement.appliesTo("ZombieGargantuar"));
        assertTrue(GroundTrackedMovement.appliesTo("Imp5"));
        assertTrue(GroundTrackedMovement.appliesTo("ZombieImp"));
        assertTrue(GroundTrackedMovement.appliesTo("ZombieNewspaper"));
    }

    @Test
    void leavesOtherZombiesOnNormalMovement() {
        assertFalse(GroundTrackedMovement.appliesTo("ZombieRa"));
        assertFalse(GroundTrackedMovement.appliesTo("ZombieTombRaiser"));
        assertFalse(GroundTrackedMovement.appliesTo("Birthday Basic"));
        assertFalse(GroundTrackedMovement.appliesTo((String) null));
    }
}
