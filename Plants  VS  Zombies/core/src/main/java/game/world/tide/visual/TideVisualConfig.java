package game.world.tide.visual;

public final class TideVisualConfig {
    public static final String TIDE_LINE_PAM = "768/FULL/BACKGROUNDS/WATER_TIDE_LINE/WATER_TIDE_LINE.PAM";
    public static final String TIDE_LINE_CLIP = "idle";
    public static final String WATER_PAM = "768/FULL/BACKGROUNDS/WAVE_UPPERLAYER/WAVE_UPPERLAYER.PAM";
    public static final String WATER_CLIP = "water";

    private final String tideLinePamPath;
    private final String tideLineClip;
    private final OverlayTransform tideLineTransform;

    private final String waterPamPath;
    private final String waterClip;
    private final OverlayTransform waterTransform;

    private final float waterMoveSpeedPixelsPerSecond;

    public TideVisualConfig(
        String tideLinePamPath,
        String tideLineClip,
        OverlayTransform tideLineTransform,
        String waterPamPath,
        String waterClip,
        OverlayTransform waterTransform,
        float waterMoveSpeedPixelsPerSecond
    ) {
        this.tideLinePamPath = tideLinePamPath;
        this.tideLineClip = tideLineClip;
        this.tideLineTransform = tideLineTransform;
        this.waterPamPath = waterPamPath;
        this.waterClip = waterClip;
        this.waterTransform = waterTransform;
        this.waterMoveSpeedPixelsPerSecond = waterMoveSpeedPixelsPerSecond;
    }

    public static TideVisualConfig beachDefault() {
        return new TideVisualConfig(
            TIDE_LINE_PAM,
            TIDE_LINE_CLIP,
            new OverlayTransform(
                BeachOverlayTuning.TIDE_LINE_OFFSET_X,
                BeachOverlayTuning.TIDE_LINE_OFFSET_Y,
                BeachOverlayTuning.TIDE_LINE_SCALE_X,
                BeachOverlayTuning.TIDE_LINE_SCALE_Y
            ),
            WATER_PAM,
            WATER_CLIP,
            new OverlayTransform(
                BeachOverlayTuning.WATER_OFFSET_X,
                BeachOverlayTuning.WATER_OFFSET_Y,
                BeachOverlayTuning.WATER_SCALE_X,
                BeachOverlayTuning.WATER_SCALE_Y
            ),
            180f
        );
    }

    public String getTideLinePamPath() {
        return tideLinePamPath;
    }

    public String getTideLineClip() {
        return tideLineClip;
    }

    public OverlayTransform getTideLineTransform() {
        return tideLineTransform;
    }

    public String getWaterPamPath() {
        return waterPamPath;
    }

    public String getWaterClip() {
        return waterClip;
    }

    public OverlayTransform getWaterTransform() {
        return waterTransform;
    }

    public float getWaterMoveSpeedPixelsPerSecond() {
        return waterMoveSpeedPixelsPerSecond;
    }
}
