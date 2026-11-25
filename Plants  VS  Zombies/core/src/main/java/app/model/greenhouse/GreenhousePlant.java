package app.model.greenhouse;

import app.enums.greenhouse.GreenhousePlantType;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class GreenhousePlant {

    protected final GreenhousePlantType GreenhousePlantType;
    protected final LocalDateTime plantedAt;
    protected LocalDateTime readyAt;

    public GreenhousePlant(GreenhousePlantType GreenhousePlantType,
                           LocalDateTime plantedAt,
                           Duration growthDuration) {

        this.GreenhousePlantType = GreenhousePlantType;
        this.plantedAt = plantedAt;
        this.readyAt = plantedAt.plus(growthDuration);
    }

    public GreenhousePlant(GreenhousePlantType GreenhousePlantType,
                           LocalDateTime plantedAt,
                           LocalDateTime readyAt) {
        this.GreenhousePlantType = GreenhousePlantType;
        this.plantedAt = plantedAt;
        this.readyAt = readyAt;
    }

    public GreenhousePlantType getGreenhousePlantType() {
        return GreenhousePlantType;
    }

    public LocalDateTime getPlantedAt() {
        return plantedAt;
    }

    public LocalDateTime getReadyAt() {
        return readyAt;
    }

    public boolean isReady() {
        return !LocalDateTime.now().isBefore(readyAt);
    }

    public Duration getRemainingTime() {
        if (isReady()) {
            return Duration.ZERO;
        }
        return Duration.between(LocalDateTime.now(), readyAt);
    }

    public String getRemainingTimeText() {

        if (isReady()) {
            return "The plant is ready for harvest.";
        }

        Duration remaining = getRemainingTime();

        long totalSeconds = remaining.getSeconds();
        long minutesRounded = (long) Math.ceil(totalSeconds / 60.0);

        long hours = minutesRounded / 60;
        long minutes = minutesRounded % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours)
                    .append(" ")
                    .append(hours == 1 ? "hour" : "hours");
        }

        if (minutes > 0) {
            if (hours > 0) {
                sb.append(" and ");
            }
            sb.append(minutes)
                    .append(" ")
                    .append(minutes == 1 ? "minute" : "minutes");
        }

        sb.append(" remaining.");

        return sb.toString();
    }

    public void completeGrowth() {
        this.readyAt = LocalDateTime.now();
    }

    public void reduceRemainingTime(long hours) {
        if (hours <= 0) return;
        this.readyAt = this.readyAt.minusHours(hours);
        if (this.readyAt.isBefore(LocalDateTime.now())) {
            this.readyAt = LocalDateTime.now();
        }
    }

    public abstract String getPlantName();
}
