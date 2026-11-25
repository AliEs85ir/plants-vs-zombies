package game.entity.plant.plantfood;

import game.entity.projectile.ProjectileCapabilities;

public final class PlantFoodShot {
    private final float offsetX;
    private final float offsetY;
    private final double angleDegrees;
    private final int delayTicks;
    private final float speedMultiplier;
    private final float damageMultiplier;
    private final boolean ice;
    private final boolean allLanes;
    private final String effectId;
    private final int animationStage;
    private final boolean fromPamVisualCenter;
    private final boolean applyFacing;
    private final boolean immediateSpawn;
    private final ProjectileCapabilities capabilities;

    private PlantFoodShot(Builder builder) {
        this.offsetX = builder.offsetX;
        this.offsetY = builder.offsetY;
        this.angleDegrees = builder.angleDegrees;
        this.delayTicks = builder.delayTicks;
        this.speedMultiplier = builder.speedMultiplier;
        this.damageMultiplier = builder.damageMultiplier;
        this.ice = builder.ice;
        this.allLanes = builder.allLanes;
        this.effectId = builder.effectId;
        this.animationStage = builder.animationStage;
        this.fromPamVisualCenter = builder.fromPamVisualCenter;
        this.applyFacing = builder.applyFacing;
        this.immediateSpawn = builder.immediateSpawn;
        this.capabilities = builder.capabilities;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public double getAngleDegrees() {
        return angleDegrees;
    }

    public int getDelayTicks() {
        return delayTicks;
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public boolean isIce() {
        return ice;
    }

    public boolean isAllLanes() {
        return allLanes;
    }

    public String getEffectId() {
        return effectId;
    }

    public int getAnimationStage() {
        return animationStage;
    }

    public boolean isFromPamVisualCenter() {
        return fromPamVisualCenter;
    }

    public boolean isApplyFacing() {
        return applyFacing;
    }

    public boolean isImmediateSpawn() {
        return immediateSpawn || capabilities != null;
    }

    public ProjectileCapabilities getCapabilities() {
        return capabilities;
    }

    public boolean hasCombatFlags() {
        return capabilities != null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private float offsetX;
        private float offsetY;
        private double angleDegrees;
        private int delayTicks;
        private float speedMultiplier = 1f;
        private float damageMultiplier = 1f;
        private boolean ice;
        private boolean allLanes;
        private String effectId;
        private int animationStage = -1;
        private boolean fromPamVisualCenter;
        private boolean applyFacing;
        private boolean immediateSpawn;
        private ProjectileCapabilities capabilities;

        public Builder offset(float x, float y) {
            this.offsetX = x;
            this.offsetY = y;
            return this;
        }

        public Builder angle(double degrees) {
            this.angleDegrees = degrees;
            return this;
        }

        public Builder delayTicks(int delayTicks) {
            this.delayTicks = delayTicks;
            return this;
        }

        public Builder speedMultiplier(float speedMultiplier) {
            this.speedMultiplier = speedMultiplier;
            return this;
        }

        public Builder damageMultiplier(float damageMultiplier) {
            this.damageMultiplier = damageMultiplier;
            return this;
        }

        public Builder ice(boolean ice) {
            this.ice = ice;
            return this;
        }

        public Builder allLanes(boolean allLanes) {
            this.allLanes = allLanes;
            return this;
        }

        public Builder effectId(String effectId) {
            this.effectId = effectId;
            return this;
        }

        public Builder animationStage(int animationStage) {
            this.animationStage = animationStage;
            return this;
        }

        public Builder fromPamVisualCenter(boolean fromPamVisualCenter) {
            this.fromPamVisualCenter = fromPamVisualCenter;
            return this;
        }

        public Builder applyFacing(boolean applyFacing) {
            this.applyFacing = applyFacing;
            return this;
        }

        public Builder immediateSpawn(boolean immediateSpawn) {
            this.immediateSpawn = immediateSpawn;
            return this;
        }

        public Builder capabilities(ProjectileCapabilities capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public PlantFoodShot build() {
            return new PlantFoodShot(this);
        }
    }
}