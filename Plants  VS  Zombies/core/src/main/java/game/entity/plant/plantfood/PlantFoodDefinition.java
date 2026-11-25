package game.entity.plant.plantfood;

import app.enums.plant.PlantName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlantFoodDefinition {
    private final PlantName plantName;
    private final PlantFoodAbilityType abilityType;
    private final int sunAmount;
    private final int healAmount;
    private final boolean restoreFullHealth;
    private final boolean forceMaxGrowth;
    private final int effectDelayTicks;
    private final int animationDurationTicks;
    private final int durationTicks;
    private final int fireIntervalTicks;
    private final int fireStartDelayTicks;
    private final int maxShotsPerStream;
    private final boolean laneFreeze;
    private final String freezeEffectId;
    private final String freezeOffEffectId;
    private final float freezeHoldSeconds;
    private final float freezeScale;
    private final float particleScale;
    private final String particleEffectId;
    private final String applyEffectId;
    private final int areaRadius;
    private final float areaPercent;
    private final List<PlantFoodShot> streams;
    private final PlantFoodShot finishingShot;
    private final List<Integer> pulseTicks;
    private final List<Integer> effectTicks;
    private final String secondaryParticleEffectId;
    private final float effectOffsetX;
    private final float effectOffsetY;
    private final boolean attractToPlantLane;
    private final boolean includeAdjacentLanes;
    private final boolean fireLine;
    private final int damageIntervalTicks;

    private PlantFoodDefinition(Builder builder) {
        this.plantName = builder.plantName;
        this.abilityType = builder.abilityType;
        this.sunAmount = builder.sunAmount;
        this.healAmount = builder.healAmount;
        this.restoreFullHealth = builder.restoreFullHealth;
        this.forceMaxGrowth = builder.forceMaxGrowth;
        this.effectDelayTicks = builder.effectDelayTicks;
        this.animationDurationTicks = builder.animationDurationTicks;
        this.durationTicks = builder.durationTicks;
        this.fireIntervalTicks = builder.fireIntervalTicks;
        this.fireStartDelayTicks = builder.fireStartDelayTicks;
        this.maxShotsPerStream = builder.maxShotsPerStream;
        this.laneFreeze = builder.laneFreeze;
        this.freezeEffectId = builder.freezeEffectId;
        this.freezeOffEffectId = builder.freezeOffEffectId;
        this.freezeHoldSeconds = builder.freezeHoldSeconds;
        this.freezeScale = builder.freezeScale;
        this.particleScale = builder.particleScale;
        this.particleEffectId = builder.particleEffectId;
        this.applyEffectId = builder.applyEffectId;
        this.areaRadius = builder.areaRadius;
        this.areaPercent = builder.areaPercent;
        this.streams = Collections.unmodifiableList(new ArrayList<>(builder.streams));
        this.finishingShot = builder.finishingShot;
        this.pulseTicks = Collections.unmodifiableList(new ArrayList<>(builder.pulseTicks));
        this.effectTicks = Collections.unmodifiableList(new ArrayList<>(builder.effectTicks));
        this.secondaryParticleEffectId = builder.secondaryParticleEffectId;
        this.effectOffsetX = builder.effectOffsetX;
        this.effectOffsetY = builder.effectOffsetY;
        this.attractToPlantLane = builder.attractToPlantLane;
        this.includeAdjacentLanes = builder.includeAdjacentLanes;
        this.fireLine = builder.fireLine;
        this.damageIntervalTicks = builder.damageIntervalTicks;
    }

    public PlantName getPlantName() {
        return plantName;
    }

    public PlantFoodAbilityType getAbilityType() {
        return abilityType;
    }

    public int getSunAmount() {
        return sunAmount;
    }

    public int getHealAmount() {
        return healAmount;
    }

    public boolean isRestoreFullHealth() {
        return restoreFullHealth;
    }

    public boolean isForceMaxGrowth() {
        return forceMaxGrowth;
    }

    public int getEffectDelayTicks() {
        return effectDelayTicks;
    }

    public int getAnimationDurationTicks() {
        return animationDurationTicks;
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    public int getFireIntervalTicks() {
        return fireIntervalTicks;
    }

    public int getFireStartDelayTicks() {
        return fireStartDelayTicks;
    }

    public int getMaxShotsPerStream() {
        return maxShotsPerStream;
    }

    public boolean isLaneFreeze() {
        return laneFreeze;
    }

    public String getFreezeEffectId() {
        return freezeEffectId;
    }

    public String getFreezeOffEffectId() {
        return freezeOffEffectId;
    }

    public float getFreezeHoldSeconds() {
        return freezeHoldSeconds;
    }

    public float getFreezeScale() {
        return freezeScale;
    }

    public float getParticleScale() {
        return particleScale;
    }

    public String getParticleEffectId() {
        return particleEffectId;
    }

    public String getApplyEffectId() {
        return applyEffectId;
    }

    public int getAreaRadius() {
        return areaRadius;
    }

    public float getAreaPercent() {
        return areaPercent;
    }

    public List<PlantFoodShot> getStreams() {
        return streams;
    }

    public boolean keepsNormalCombat() {
        return abilityType == PlantFoodAbilityType.EMPOWER;
    }

    public PlantFoodShot getEmpoweredAttackShot() {
        if (!keepsNormalCombat() || streams == null || streams.isEmpty()) {
            return null;
        }
        return streams.get(0);
    }

    public PlantFoodShot getFinishingShot() {
        return finishingShot;
    }

    public List<Integer> getPulseTicks() {
        return pulseTicks;
    }

    public List<Integer> getEffectTicks() {
        return effectTicks;
    }

    public String getSecondaryParticleEffectId() {
        return secondaryParticleEffectId;
    }

    public float getEffectOffsetX() {
        return effectOffsetX;
    }

    public float getEffectOffsetY() {
        return effectOffsetY;
    }

    public boolean hasPulseSchedule() {
        return pulseTicks != null && !pulseTicks.isEmpty();
    }

    public boolean hasEffectSchedule() {
        return effectTicks != null && !effectTicks.isEmpty();
    }

    public boolean isAttractToPlantLane() {
        return attractToPlantLane;
    }

    public boolean isIncludeAdjacentLanes() {
        return includeAdjacentLanes;
    }

    public boolean isFireLine() {
        return fireLine;
    }

    public int getDamageIntervalTicks() {
        return damageIntervalTicks;
    }

    public static Builder builder(PlantName plantName, PlantFoodAbilityType abilityType) {
        return new Builder(plantName, abilityType);
    }

    public static final class Builder {
        private final PlantName plantName;
        private final PlantFoodAbilityType abilityType;
        private int sunAmount;
        private int healAmount;
        private boolean restoreFullHealth;
        private boolean forceMaxGrowth;
        private int effectDelayTicks;
        private int animationDurationTicks;
        private int durationTicks;
        private int fireIntervalTicks;
        private int fireStartDelayTicks;
        private int maxShotsPerStream;
        private boolean laneFreeze;
        private String freezeEffectId;
        private String freezeOffEffectId;
        private float freezeHoldSeconds;
        private float freezeScale = 1f;
        private float particleScale = 1f;
        private String particleEffectId;
        private String applyEffectId = "plantfood pickup";
        private int areaRadius;
        private float areaPercent;
        private final List<PlantFoodShot> streams = new ArrayList<>();
        private PlantFoodShot finishingShot;
        private final List<Integer> pulseTicks = new ArrayList<>();
        private final List<Integer> effectTicks = new ArrayList<>();
        private String secondaryParticleEffectId;
        private float effectOffsetX;
        private float effectOffsetY;
        private boolean attractToPlantLane;
        private boolean includeAdjacentLanes;
        private boolean fireLine;
        private int damageIntervalTicks;

        private Builder(PlantName plantName, PlantFoodAbilityType abilityType) {
            this.plantName = plantName;
            this.abilityType = abilityType;
        }

        public Builder sunAmount(int sunAmount) {
            this.sunAmount = sunAmount;
            return this;
        }

        public Builder healAmount(int healAmount) {
            this.healAmount = Math.max(0, healAmount);
            return this;
        }

        public Builder restoreFullHealth(boolean restoreFullHealth) {
            this.restoreFullHealth = restoreFullHealth;
            return this;
        }

        public Builder forceMaxGrowth(boolean forceMaxGrowth) {
            this.forceMaxGrowth = forceMaxGrowth;
            return this;
        }

        public Builder effectDelayTicks(int effectDelayTicks) {
            this.effectDelayTicks = effectDelayTicks;
            return this;
        }

        public Builder animationDurationTicks(int animationDurationTicks) {
            this.animationDurationTicks = Math.max(0, animationDurationTicks);
            return this;
        }

        public Builder durationTicks(int durationTicks) {
            this.durationTicks = durationTicks;
            return this;
        }

        public Builder fireIntervalTicks(int fireIntervalTicks) {
            this.fireIntervalTicks = fireIntervalTicks;
            return this;
        }

        public Builder fireStartDelayTicks(int fireStartDelayTicks) {
            this.fireStartDelayTicks = fireStartDelayTicks;
            return this;
        }

        public Builder maxShotsPerStream(int maxShotsPerStream) {
            this.maxShotsPerStream = maxShotsPerStream;
            return this;
        }

        public Builder laneFreeze(boolean laneFreeze) {
            this.laneFreeze = laneFreeze;
            return this;
        }

        public Builder freezeEffectId(String freezeEffectId) {
            this.freezeEffectId = freezeEffectId;
            return this;
        }

        public Builder freezeOffEffectId(String freezeOffEffectId) {
            this.freezeOffEffectId = freezeOffEffectId;
            return this;
        }

        public Builder freezeHoldSeconds(float freezeHoldSeconds) {
            this.freezeHoldSeconds = freezeHoldSeconds;
            return this;
        }

        public Builder freezeScale(float freezeScale) {
            this.freezeScale = freezeScale <= 0f ? 1f : freezeScale;
            return this;
        }

        public Builder particleScale(float particleScale) {
            this.particleScale = particleScale <= 0f ? 1f : particleScale;
            return this;
        }

        public Builder particleEffectId(String particleEffectId) {
            this.particleEffectId = particleEffectId;
            return this;
        }

        public Builder applyEffectId(String applyEffectId) {
            this.applyEffectId = applyEffectId;
            return this;
        }

        public Builder areaRadius(int areaRadius) {
            this.areaRadius = Math.max(-1, areaRadius);
            return this;
        }

        public Builder areaPercent(float areaPercent) {
            if (areaPercent < 0f) {
                this.areaPercent = 0f;
            } else if (areaPercent > 0.5f) {
                this.areaPercent = 0.5f;
            } else {
                this.areaPercent = areaPercent;
            }
            return this;
        }

        public Builder addStream(PlantFoodShot shot) {
            if (shot != null) {
                this.streams.add(shot);
            }
            return this;
        }

        public Builder finishingShot(PlantFoodShot finishingShot) {
            this.finishingShot = finishingShot;
            return this;
        }

        public Builder pulseTicks(int... ticks) {
            pulseTicks.clear();
            if (ticks != null) {
                for (int tick : ticks) {
                    if (tick >= 0) {
                        pulseTicks.add(tick);
                    }
                }
            }
            return this;
        }

        public Builder effectTicks(int... ticks) {
            effectTicks.clear();
            if (ticks != null) {
                for (int tick : ticks) {
                    if (tick >= 0) {
                        effectTicks.add(tick);
                    }
                }
            }
            return this;
        }

        public Builder secondaryParticleEffectId(String secondaryParticleEffectId) {
            this.secondaryParticleEffectId = secondaryParticleEffectId;
            return this;
        }

        public Builder effectOffset(float offsetX, float offsetY) {
            this.effectOffsetX = offsetX;
            this.effectOffsetY = offsetY;
            return this;
        }

        public Builder attractToPlantLane(boolean attractToPlantLane) {
            this.attractToPlantLane = attractToPlantLane;
            return this;
        }

        public Builder includeAdjacentLanes(boolean includeAdjacentLanes) {
            this.includeAdjacentLanes = includeAdjacentLanes;
            return this;
        }

        public Builder fireLine(boolean fireLine) {
            this.fireLine = fireLine;
            return this;
        }

        public Builder damageIntervalTicks(int damageIntervalTicks) {
            this.damageIntervalTicks = Math.max(0, damageIntervalTicks);
            return this;
        }

        public PlantFoodDefinition build() {
            return new PlantFoodDefinition(this);
        }
    }
}
