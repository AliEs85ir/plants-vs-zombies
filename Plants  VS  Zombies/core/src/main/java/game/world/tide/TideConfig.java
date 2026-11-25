package game.world.tide;

import game.world.tide.ambush.LowTideAmbush;
import game.world.tide.ambush.SpawnZombiesAmbush;
import game.world.tide.visual.TideVisualConfig;

public final class TideConfig {
    private final TideLine tideLine;
    private final TideLevelMapping mapping;
    private final TideSchedule schedule;
    private final TideLevel initialLevel;
    private final TideVisualConfig visualConfig;

    public TideConfig(
        TideLine tideLine,
        TideLevelMapping mapping,
        TideSchedule schedule,
        TideLevel initialLevel,
        TideVisualConfig visualConfig
    ) {
        this.tideLine = tideLine;
        this.mapping = mapping;
        this.schedule = schedule;
        this.initialLevel = initialLevel;
        this.visualConfig = visualConfig;
    }

    public TideLine getTideLine() {
        return tideLine;
    }

    public TideLevelMapping getMapping() {
        return mapping;
    }

    public TideSchedule getSchedule() {
        return schedule;
    }

    public TideLevel getInitialLevel() {
        return initialLevel;
    }

    public TideVisualConfig getVisualConfig() {
        return visualConfig;
    }


    public static TideConfig bigWaveBeachChapter1() {
        TideLine tideLine = TideLine.afterAlwaysLandColumns(3);
        TideLevelMapping mapping = new WaterStartColumnMapping();
        LowTideAmbush mermaidAmbush = SpawnZombiesAmbush.mermaidImp();
        TideSchedule schedule = TideSchedule.builder(new TideLevel(4))
            .wave(1, 4)
            .wave(2, 3)
            .wave(3, 6)
            .wave(4, 7)
            .wave(5, 5)
            .lowTide(6, 8, mermaidAmbush)
            .lowTide(7, 8, mermaidAmbush)
            .wave(8, 3)
            .wave(9, 5)
            .wave(10, 4)
            .build();
        return new TideConfig(
            tideLine,
            mapping,
            schedule,
            new TideLevel(4),
            TideVisualConfig.beachDefault()
        );
    }

    public static TideConfig bigWaveBeachDefault() {
        return bigWaveBeachChapter1();
    }
}
