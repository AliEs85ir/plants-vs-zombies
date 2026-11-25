package game.entity.zombie.data.properties;

public interface Property {
    String getAlias();
    void setAlias(String alias);
    double getSpeed();
    int getInitialHealth();
    double getRunningSpeedScale();
    int getZombieWeight();
    int getZombieWaveCost();
    int getEatDPS();
    float getFireDamageMultiplier();
    int getSmashDamage();
    float getSmashDuration();
    int getMaxClaimedSunCurrency();
    int getAmmo();
    int getNumberOfTombsToSpawn();
    float getTimeBetweenCasts();
    int getTimeBetweenRaisings();
    String printInfo();
    boolean canSpawnPlantFood();
    int getCooldown();

    // TODO : Property class should have common methods!, gonna be implemented by MohammadReza.
}
