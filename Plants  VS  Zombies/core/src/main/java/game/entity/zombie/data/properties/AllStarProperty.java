package game.entity.zombie.data.properties;

public class AllStarProperty extends ZombieProperty{
    private double RunningSpeedScale;
    private int SmashDamage;


    @Override
    public double getRunningSpeedScale() {
        return RunningSpeedScale;
    }


    @Override
    public int getSmashDamage() {
        return SmashDamage;
    }
}
