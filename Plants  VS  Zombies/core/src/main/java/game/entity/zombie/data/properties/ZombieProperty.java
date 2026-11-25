package game.entity.zombie.data.properties;

import app.enums.zombie.ZombieSize;
import common.model.Offset;
import common.model.Rect;
import common.model.ShadowOffset;
import common.model.Vector;
import game.entity.zombie.data.StatsData;
import game.entity.zombie.data.properties.armor.ArmorProperty;

import java.util.List;

public abstract class ZombieProperty implements Property{
    private String alias;
    private int EatDPS;
    private int Cost;
    private Vector ArtCenter;
    private Rect AttackRect;
    private String GroundTrackName;
    private boolean CanSpawnPlantFood;
    private Rect HitRect;
    private int Hitpoints;
    private ScalingProperty ScaledProps;
    private ShadowOffset ShadowOffset;
    private ZombieSize zombieSize;
    private double Speed;
    private int WavePointCost;
    private int Weight;
    private StatsData ZombieStats;
    private Offset hypnoShroomOffset;
    private List<ArmorProperty> armor;
    private float FireDamageMultiplier;

    @Override
    public float getFireDamageMultiplier() {
        return FireDamageMultiplier;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public double getSpeed() {
        return Speed;
    }

    @Override
    public int getInitialHealth() {
        return Hitpoints;
    }

    @Override
    public double getRunningSpeedScale() {
        return 1f;
    }

    @Override
    public int getZombieWeight() {
        return Weight;
    }

    @Override
    public int getZombieWaveCost() {
        return WavePointCost;
    }

    @Override
    public int getEatDPS() {
        if(EatDPS >= 0) {
            return EatDPS;
        }
        else {
            return 150;
        }
    }

    @Override
    public int getSmashDamage() {
        return 0;
    }

    @Override
    public float getSmashDuration() {
        return 0;
    }

    @Override
    public int getMaxClaimedSunCurrency() {
        return 0;
    }

    @Override
    public int getAmmo() {
        return 0;
    }

    @Override
    public int getNumberOfTombsToSpawn() {
        return 0;
    }

    @Override
    public float getTimeBetweenCasts() {
        return 0;
    }

    @Override
    public int getTimeBetweenRaisings() {
        return 0;
    }

    @Override
    public String printInfo() {
        StringBuilder zombieInfo = new StringBuilder();

        String border = "🧟════════════════════════════════════════🧟";
        String separator = "┠────────────────────────────────────────┨";

        zombieInfo.append(border).append("\n");
        zombieInfo.append(String.format("┃ 🧠 ZOMBIE ALIAS: %-21s ┃\n", (alias != null ? alias : "Unknown")));
        zombieInfo.append(separator).append("\n");

        zombieInfo.append(String.format("┃ 🩸 Hitpoints (Initial): %-16d ┃\n", Hitpoints));
        zombieInfo.append(String.format("┃ 💨 Speed: %-28.2f ┃\n", Speed));
        zombieInfo.append(String.format("┃ 🦷 Eat DPS: %-26d ┃\n", EatDPS));
        zombieInfo.append(String.format("┃ ⚖️ Chance of appearance: %-27d ┃\n", Weight));
        zombieInfo.append(String.format("┃ 📏 Size: %-29s ┃\n", (zombieSize != null ? zombieSize.name() : "N/A")));
        zombieInfo.append(String.format("┃ 🍀 Spawns Plant Food: %-15s ┃\n", (CanSpawnPlantFood ? "Yes ✅" : "No ❌")));
        zombieInfo.append(String.format("┃ $$ Cost : %-16d ┃\n", getZombieWaveCost()));

        if (armor != null && !armor.isEmpty()) {
            zombieInfo.append(separator).append("\n");
            zombieInfo.append("┃ 🛡️ Armor Equipped:                       ┃\n");
            for (ArmorProperty piece : armor) {
                // Adjust piece.toString() based on what your ArmorProperty class exposes
                zombieInfo.append(String.format("┃   🔸 %-34s┃\n", piece.getClass().getSimpleName()));
            }
        }

        zombieInfo.append("╚════════════════════════════════════════╝\n");

        return zombieInfo.toString();
    }

    @Override
    public int getCooldown() {
        return 50;
    }

    @Override
    public boolean canSpawnPlantFood() {
        return CanSpawnPlantFood;
    }
}
