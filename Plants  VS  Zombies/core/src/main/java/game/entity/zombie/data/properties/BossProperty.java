package game.entity.zombie.data.properties;

public class BossProperty extends ZombieProperty{
    private float phase1AttackChance = 0.5f;
    private float phase2AttackChance = 0.5f;
    private float phase3AttackChance = 0.7f;
    private int summonDuration = 100;
    private int fireAttackDuration = 150;
    private int fireBombDuration = 200;
    private int vulnerableDuration = 300;
    private String summonZombieType = "ZombieNormal";

    public float getPhase1AttackChance() { return phase1AttackChance; }
    public float getPhase2AttackChance() { return phase2AttackChance; }
    public float getPhase3AttackChance() { return phase3AttackChance; }
    public int getSummonDuration() { return summonDuration; }
    public int getFireAttackDuration() { return fireAttackDuration; }
    public int getFireBombDuration() { return fireBombDuration; }
    public int getVulnerableDuration() { return vulnerableDuration; }
    public String getSummonZombieType() { return summonZombieType; }
}
