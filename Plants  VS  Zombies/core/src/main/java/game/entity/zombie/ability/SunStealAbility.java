package game.entity.zombie.ability;

import app.enums.sun.SunType;
import app.model.App;
import common.model.Position;
import common.model.Vector;
import game.entity.sun.Sun;
import game.entity.zombie.Zombie;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import graphics.general.event.ZombieVisualEventType;

public class SunStealAbility extends AbstractZombieAbility{
    public static final long SUN_STEAL_COOLDOWN_TICKS = 100;
    public static final double STEAL_RANGE = 1000f;

    private long lastStealTick = -1;
    private int maxClaimedSunCurrency;
    private int totalSunStolen = 0;

    public SunStealAbility(int maxClaimedSunCurrency) {
        this.maxClaimedSunCurrency = maxClaimedSunCurrency;
    }

    public SunStealAbility() {
        this.maxClaimedSunCurrency = Integer.MAX_VALUE;
    }

    @Override
    public void update(Zombie zombie, ZombieService zombieService, long currentTick) {
        if(!canSteal(currentTick) || hasReachedMaxClaimedSun()) {
            return;
        }
        GameWorld world = zombie.getWorld();
        Sun nearestSun = findNearestStealableSun(zombie, world);
        if(nearestSun != null) {
            zombie.pushEvent(ZombieVisualEventType.POWER_UP);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                currentTick,
                20,
                () -> stealSun(zombie, nearestSun, world, currentTick)
            );
        }
    }


    @Override
    public void onDeath(Zombie zombie) {
        GameWorld world = zombie.getWorld();
        world.addEvent("Ra zombie died and it gave you back " + totalSunStolen + " suns!");
        world.addSunAmount(totalSunStolen);
    }

    private boolean canSteal(long currentTick) {
        if(lastStealTick < 0) {
            return true;
        }
        return (currentTick - lastStealTick) >= SUN_STEAL_COOLDOWN_TICKS;
    }

    private boolean hasReachedMaxClaimedSun() {
        return maxClaimedSunCurrency != Integer.MAX_VALUE && totalSunStolen >= maxClaimedSunCurrency;
    }

    private Sun findNearestStealableSun(Zombie zombie, GameWorld world) {
        Sun nearestSun = null;
        double nearestDistance = Double.MAX_VALUE;
        Position zombiePosition = zombie.getPosition();

        for(Sun sun : world.getSuns()) {

            if(!sun.isCollectable()){continue;}

            double distance = Vector.findDist(zombiePosition, sun.getPosition());

            if(distance <= STEAL_RANGE && distance < nearestDistance && sun.getType() != SunType.RADIOACTIVE) {
                nearestSun = sun;
                nearestDistance = distance;
            }
        }
        return nearestSun;
    }

    private void stealSun(Zombie zombie, Sun targetSun, GameWorld world, long currentTick) {

        targetSun.destroyBody(App.getRealTimeEngine().getPhysicService());
        App.getRealTimeEngine().getGraphicsEngine().unregisterSun(targetSun);

        int stolenValue = targetSun.getValue();
        world.getSuns().remove(targetSun);
        totalSunStolen += stolenValue;
        lastStealTick = currentTick;

        world.addEvent("Ra Zombie stole " + stolenValue + " sun!");
    }

    public void setMaxClaimedSunCurrency(int maxClaimedSunCurrency) {
        this.maxClaimedSunCurrency = maxClaimedSunCurrency;
    }

    public int getTotalSunStolen() {
        return totalSunStolen;
    }
}
