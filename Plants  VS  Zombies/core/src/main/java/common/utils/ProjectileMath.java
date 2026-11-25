package common.utils;

import com.badlogic.gdx.math.Vector2;
import common.model.Position;

public class ProjectileMath {
    public static final float WORLD_GRAVITY = 10f;   // m/s²

    public static Vector2 calculateRequiredVelocityM(Vector2 startM, Vector2 endM, float heightM) {
        float dx = endM.x - startM.x;
        float dy = endM.y - startM.y;
        float timeToApex = (float) Math.sqrt(2 * heightM / WORLD_GRAVITY);
        float totalTime = 2 * timeToApex;
        float vx = dx / totalTime;
        float vy = (dy + WORLD_GRAVITY * totalTime * totalTime / 2) / totalTime;
        return new Vector2(vx, vy);
    }

    public static Vector2 calculateRequiredVelocityPx(Vector2 startPx, Vector2 endPx,
                                                      float arcHeightPx, float gravityPxPerTick2) {
        float dx = endPx.x - startPx.x;
        float dy = endPx.y - startPx.y;
        float absG = Math.abs(gravityPxPerTick2);
        float totalTime = (float) (2 * Math.sqrt(2 * arcHeightPx / absG));
        float vx = dx / totalTime;
        float vy = (dy - 0.5f * gravityPxPerTick2 * totalTime * totalTime) / totalTime;
        return new Vector2(vx, vy);
    }
}
