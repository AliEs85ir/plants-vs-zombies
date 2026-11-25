package game.entity.projectile;

/**
 * Data-driven combat/visual flags for a projectile. Generic hit/movement code
 * reads these instead of branching on plant names.
 */
public final class ProjectileCapabilities {
    private final boolean unstoppable;
    private final boolean pierceAll;
    private final boolean instantKill;
    private final boolean enemyOnly;
    private final boolean plantFoodVisual;
    private final String hitEffectId;

    private ProjectileCapabilities(Builder builder) {
        this.unstoppable = builder.unstoppable;
        this.pierceAll = builder.pierceAll;
        this.instantKill = builder.instantKill;
        this.enemyOnly = builder.enemyOnly;
        this.plantFoodVisual = builder.plantFoodVisual;
        this.hitEffectId = builder.hitEffectId;
    }

    public boolean isUnstoppable() {
        return unstoppable;
    }

    public boolean isPierceAll() {
        return pierceAll;
    }

    public boolean isInstantKill() {
        return instantKill;
    }

    public boolean isEnemyOnly() {
        return enemyOnly;
    }

    public boolean isPlantFoodVisual() {
        return plantFoodVisual;
    }

    public String getHitEffectId() {
        return hitEffectId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean unstoppable;
        private boolean pierceAll;
        private boolean instantKill;
        private boolean enemyOnly;
        private boolean plantFoodVisual;
        private String hitEffectId;

        public Builder unstoppable(boolean unstoppable) {
            this.unstoppable = unstoppable;
            return this;
        }

        public Builder pierceAll(boolean pierceAll) {
            this.pierceAll = pierceAll;
            return this;
        }

        public Builder instantKill(boolean instantKill) {
            this.instantKill = instantKill;
            return this;
        }

        public Builder enemyOnly(boolean enemyOnly) {
            this.enemyOnly = enemyOnly;
            return this;
        }

        public Builder plantFoodVisual(boolean plantFoodVisual) {
            this.plantFoodVisual = plantFoodVisual;
            return this;
        }

        public Builder hitEffectId(String hitEffectId) {
            this.hitEffectId = hitEffectId;
            return this;
        }

        public ProjectileCapabilities build() {
            return new ProjectileCapabilities(this);
        }
    }
}