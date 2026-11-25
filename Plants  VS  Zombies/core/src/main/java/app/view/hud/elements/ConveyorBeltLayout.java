package app.view.hud.elements;

public class ConveyorBeltLayout {

    public float cardScale = 1f;
    public float cardSpacing = 8f;
    public float cardWidthRatio = 0.90f;
    public float padLeft = 4f;
    public float padTop = 8f;
    public float padBottom = 8f;
    public float maxBarHeight = 520f;

    public float scrollSpeed = 120f;
    public float cardMoveSpeed = 120f;
    public float minMoveDuration = 0.08f;
    public float enterFadeDuration = 0.18f;
    public float exitDuration = 0.14f;

    public float hoverWaveAmplitude = 10f;
    public float hoverWaveSpeed = 7.5f;
    public float hoverLerpSpeed = 12f;
    public float selectBorderPad = 3f;

    public static ConveyorBeltLayout defaults() {
        return new ConveyorBeltLayout();
    }

    public float moveDuration(float fromY, float toY) {
        float distance = Math.abs(toY - fromY);
        if (cardMoveSpeed <= 0f) {
            return minMoveDuration;
        }
        return Math.max(minMoveDuration, distance / cardMoveSpeed);
    }
}
