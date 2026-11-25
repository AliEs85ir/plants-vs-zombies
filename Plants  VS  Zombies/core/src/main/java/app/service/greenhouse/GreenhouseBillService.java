package app.service.greenhouse;

import app.enums.greenhouse.BillAvailability;
import app.model.greenhouse.Greenhouse;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class GreenhouseBillService {
    private final Greenhouse greenhouse;
    private final float cooldownHours;

    public GreenhouseBillService(Greenhouse greenhouse) {
        this(greenhouse, GreenhouseGameplaySettings.current().billCooldownHours());
    }

    public GreenhouseBillService(Greenhouse greenhouse, float cooldownHours) {
        this.greenhouse = greenhouse;
        this.cooldownHours = cooldownHours <= 0f ? 2f : cooldownHours;
    }

    public boolean isAvailable() {
        return isAnyShovelAvailable(LocalDateTime.now());
    }

    public boolean isAvailable(LocalDateTime now) {
        return isAnyShovelAvailable(now);
    }

    public boolean isAnyShovelAvailable(LocalDateTime now) {
        List<LocalDateTime> shovels = greenhouse.getShovelLastUsedAt();
        for (LocalDateTime lastUsed : shovels) {
            if (lastUsed == null || !now.isBefore(lastUsed.plus(cooldownDuration()))) {
                return true;
            }
        }
        return false;
    }

    public Duration remainingCooldown() {
        return remainingCooldown(LocalDateTime.now());
    }

    public Duration remainingCooldown(LocalDateTime now) {
        Duration shortest = null;
        for (LocalDateTime lastUsed : greenhouse.getShovelLastUsedAt()) {
            Duration remaining = remainingForShovel(lastUsed, now);
            if (remaining.isZero()) {
                return Duration.ZERO;
            }
            if (shortest == null || remaining.compareTo(shortest) < 0) {
                shortest = remaining;
            }
        }
        return shortest == null ? Duration.ZERO : shortest;
    }

    public Duration remainingForShovelIndex(int index, LocalDateTime now) {
        List<LocalDateTime> shovels = greenhouse.getShovelLastUsedAt();
        if (index < 0 || index >= shovels.size()) {
            return Duration.ZERO;
        }
        return remainingForShovel(shovels.get(index), now);
    }

    private Duration remainingForShovel(LocalDateTime lastUsed, LocalDateTime now) {
        if (lastUsed == null) {
            return Duration.ZERO;
        }
        LocalDateTime readyAt = lastUsed.plus(cooldownDuration());
        if (!now.isBefore(readyAt)) {
            return Duration.ZERO;
        }
        return Duration.between(now, readyAt);
    }

    public BillAvailability availability(boolean selected) {
        if (!isAvailable()) {
            return BillAvailability.UNAVAILABLE;
        }
        if (selected) {
            return BillAvailability.SELECTED;
        }
        return BillAvailability.AVAILABLE;
    }

    public void consume() {
        consume(LocalDateTime.now());
    }

    public void consume(LocalDateTime at) {
        consumeFirstAvailable(at == null ? LocalDateTime.now() : at);
    }

    public void consumeFirstAvailable(LocalDateTime at) {
        List<LocalDateTime> shovels = greenhouse.getShovelLastUsedAt();
        for (int index = 0; index < shovels.size(); index++) {
            LocalDateTime lastUsed = shovels.get(index);
            if (lastUsed == null || !at.isBefore(lastUsed.plus(cooldownDuration()))) {
                shovels.set(index, at);
                greenhouse.setShovelLastUsedAt(shovels);
                return;
            }
        }
        if (!shovels.isEmpty()) {
            shovels.set(0, at);
            greenhouse.setShovelLastUsedAt(shovels);
        }
    }

    public void skip(Duration amount) {
        if (amount == null || amount.isZero() || amount.isNegative()) {
            return;
        }
        List<LocalDateTime> shovels = greenhouse.getShovelLastUsedAt();
        for (int index = 0; index < shovels.size(); index++) {
            LocalDateTime lastUsed = shovels.get(index);
            if (lastUsed != null) {
                shovels.set(index, lastUsed.minus(amount));
            }
        }
        greenhouse.setShovelLastUsedAt(shovels);
    }

    public float cooldownHours() {
        return cooldownHours;
    }

    private Duration cooldownDuration() {
        long seconds = Math.max(1L, Math.round(cooldownHours * 3600d));
        return Duration.ofSeconds(seconds);
    }
}
