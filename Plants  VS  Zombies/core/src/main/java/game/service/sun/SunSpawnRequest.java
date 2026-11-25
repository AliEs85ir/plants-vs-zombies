package game.service.sun;

import common.model.Position;

/**
 * Generic sun spawn request. Source-agnostic: any caller can supply an optional
 * world-space source and a landing. Missing source means Direct (no arc).
 */
public final class SunSpawnRequest {
    private final int amount;
    private final String producerLabel;
    private final Position landing;
    private final Position sourceWorld;
    private final Integer durationTicks;
    private final Double arcHeight;

    private SunSpawnRequest(Builder builder) {
        this.amount = builder.amount;
        this.producerLabel = builder.producerLabel;
        this.landing = builder.landing;
        this.sourceWorld = builder.sourceWorld;
        this.durationTicks = builder.durationTicks;
        this.arcHeight = builder.arcHeight;
    }

    public int getAmount() {
        return amount;
    }

    public String getProducerLabel() {
        return producerLabel;
    }

    public Position getLanding() {
        return landing;
    }

    public Position getSourceWorld() {
        return sourceWorld;
    }

    public Integer getDurationTicks() {
        return durationTicks;
    }

    public Double getArcHeight() {
        return arcHeight;
    }

    public boolean isLaunch() {
        return sourceWorld != null && landing != null;
    }

    public static Builder builder(int amount, Position landing) {
        return new Builder(amount, landing);
    }

    public static final class Builder {
        private final int amount;
        private final Position landing;
        private String producerLabel = "sun";
        private Position sourceWorld;
        private Integer durationTicks;
        private Double arcHeight;

        private Builder(int amount, Position landing) {
            this.amount = amount;
            this.landing = landing;
        }

        public Builder producerLabel(String producerLabel) {
            this.producerLabel = producerLabel;
            return this;
        }

        public Builder sourceWorld(Position sourceWorld) {
            this.sourceWorld = sourceWorld;
            return this;
        }

        public Builder durationTicks(int durationTicks) {
            this.durationTicks = durationTicks;
            return this;
        }

        public Builder arcHeight(double arcHeight) {
            this.arcHeight = arcHeight;
            return this;
        }

        public SunSpawnRequest build() {
            return new SunSpawnRequest(this);
        }
    }
}
