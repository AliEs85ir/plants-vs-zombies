package game.world.tide;

import game.world.tide.ambush.LowTideAmbush;

public final class WaveTideDefinition {
    private final int waveNumber;
    private final TideLevel tideLevel;
    private final TideEventType eventType;
    private final LowTideAmbush ambush;

    public WaveTideDefinition(int waveNumber, TideLevel tideLevel) {
        this(waveNumber, tideLevel, TideEventType.NORMAL, null);
    }

    public WaveTideDefinition(
        int waveNumber,
        TideLevel tideLevel,
        TideEventType eventType,
        LowTideAmbush ambush
    ) {
        this.waveNumber = waveNumber;
        this.tideLevel = tideLevel;
        this.eventType = eventType != null ? eventType : TideEventType.NORMAL;
        this.ambush = ambush;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public TideLevel getTideLevel() {
        return tideLevel;
    }

    public TideEventType getEventType() {
        return eventType;
    }

    public boolean isLowTide() {
        return eventType == TideEventType.LOW_TIDE;
    }

    public LowTideAmbush getAmbush() {
        return ambush;
    }
}
