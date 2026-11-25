package game.entity;

public record DamageContext(Entity attacker, double amount, boolean ignoresArmor, DeathStyle deathStyle) {
    public DamageContext(Entity attacker, double amount, boolean ignoresArmor) {
        this(attacker, amount, ignoresArmor, DeathStyle.NORMAL);
    }

    public DamageContext {
        if (deathStyle == null) {
            deathStyle = DeathStyle.NORMAL;
        }
    }
}
