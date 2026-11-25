package game.service.sun;

import common.model.Position;
import game.entity.sun.SunLaunchSpec;
import game.entity.sun.SunSpawnPoint;
import game.entity.sun.SunToss;

import java.util.List;

/**
 * Builds a {@link SunToss} from launch spec + landing. Movement never branches
 * on plant type; source offsets come from the spec lists.
 * <p>
 * Trajectory (same as existing SunToss): P(t) = (1-t)S + t L + H * 4 t (1-t)
 * on Y, with t = elapsedTicks / durationTicks. Last tick t=1 snaps to landing.
 */
public final class SunLaunchPlanner {
    private SunLaunchPlanner() {
    }

    public static SunToss plan(
        Position visualCenter,
        SunLaunchSpec spec,
        Position landing,
        int sourceIndex,
        boolean plantFood
    ) {
        if (visualCenter == null || spec == null || landing == null) {
            return null;
        }
        List<SunSpawnPoint> points = spec.sourcesFor(plantFood);
        if (points.isEmpty()) {
            return null;
        }
        SunSpawnPoint point = points.get(Math.floorMod(sourceIndex, points.size()));
        Position source = SunSourceLocator.worldFromPamCenter(visualCenter, point);
        return new SunToss(
            source.getX(),
            source.getY(),
            landing.getX(),
            landing.getY(),
            spec.getArcHeight(),
            spec.getDurationTicks()
        );
    }
}
