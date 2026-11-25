package game.entity.zombie.data.properties.DarkAges;

import common.model.MaxMin;
import game.entity.zombie.data.properties.ZombieProperty;

import java.util.List;

public class JugglerProperty extends ZombieProperty {

    // >>> projectiles that juggler doges (Shokhosh Juggler doge dad !) :

    private List<String> angleAgnosticProjectiles;
    private List<String> juggleAbleProjectiles;
    private List<String> bounceAbleProjectiles;
    private List<String> unthrowableProjectiles;

    private double projectileBounceDistance;
    private double projectileBounceHeight;
    private double projectileBounceTime;

    private int catchArcDegrees;
    private int juggleLaunchDelay;
    private LaunchAcceleration launchAcceleration;
    private MaxMin launchHeight;
    private int maxProjectilesToJuggle;
    private double moveSpeedMultiplierWhileJuggling;


    // >>> innerHelperClass :

    public static class LaunchAcceleration{
        public double x;
        public double y;
        public double z;

        public LaunchAcceleration(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
