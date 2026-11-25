package game.world.tide;

import game.world.tide.ambush.LowTideAmbush;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class TideSchedule {
    private final Map<Integer, WaveTideDefinition> byWave;
    private final TideLevel defaultLevel;

    private TideSchedule(Map<Integer, WaveTideDefinition> byWave, TideLevel defaultLevel) {
        this.byWave = Collections.unmodifiableMap(new LinkedHashMap<>(byWave));
        this.defaultLevel = defaultLevel;
    }

    public Optional<WaveTideDefinition> forWave(int waveNumber) {
        return Optional.ofNullable(byWave.get(waveNumber));
    }

    public TideLevel levelForWave(int waveNumber) {
        WaveTideDefinition definition = byWave.get(waveNumber);
        return definition != null ? definition.getTideLevel() : defaultLevel;
    }

    public TideLevel getDefaultLevel() {
        return defaultLevel;
    }

    public Map<Integer, WaveTideDefinition> getWaves() {
        return byWave;
    }

    public static Builder builder(TideLevel defaultLevel) {
        return new Builder(defaultLevel);
    }

    public static final class Builder {
        private final Map<Integer, WaveTideDefinition> byWave = new LinkedHashMap<>();
        private final TideLevel defaultLevel;

        private Builder(TideLevel defaultLevel) {
            this.defaultLevel = defaultLevel;
        }

        public Builder wave(int waveNumber, int tideLevel) {
            byWave.put(waveNumber, new WaveTideDefinition(waveNumber, new TideLevel(tideLevel)));
            return this;
        }

        public Builder lowTide(int waveNumber, int tideLevel, LowTideAmbush ambush) {
            byWave.put(waveNumber, new WaveTideDefinition(
                waveNumber,
                new TideLevel(tideLevel),
                TideEventType.LOW_TIDE,
                ambush
            ));
            return this;
        }

        public TideSchedule build() {
            return new TideSchedule(byWave, defaultLevel);
        }
    }
}
