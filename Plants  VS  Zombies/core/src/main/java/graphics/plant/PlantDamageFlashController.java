package graphics.plant;

public final class PlantDamageFlashController {

    private boolean damaged;
    private float timeSinceLastDamage;
    private float cycleElapsed;
    private float flashRemaining;

    public void notifyDamaged() {
        timeSinceLastDamage = 0f;
        if (damaged) {
            return;
        }
        damaged = true;
        startFlash();
    }

    public void update(float delta) {
        if (!damaged) {
            return;
        }

        timeSinceLastDamage += delta;
        if (timeSinceLastDamage >= PlantDamageFlashConfig.STATE_TIMEOUT) {
            clearState();
            return;
        }

        cycleElapsed += delta;
        if (flashRemaining > 0f) {
            flashRemaining = Math.max(0f, flashRemaining - delta);
        }
        if (cycleElapsed >= PlantDamageFlashConfig.INTERVAL) {
            startFlash();
        }
    }

    public boolean isDamaged() {
        return damaged;
    }

    public boolean isActive() {
        return flashRemaining > 0f;
    }

    public float getOverlayAlpha() {
        if (flashRemaining <= 0f) {
            return 0f;
        }
        return PlantDamageFlashConfig.ALPHA;
    }

    private void startFlash() {
        flashRemaining = PlantDamageFlashConfig.DURATION;
        cycleElapsed = 0f;
    }

    private void clearState() {
        damaged = false;
        timeSinceLastDamage = 0f;
        cycleElapsed = 0f;
        flashRemaining = 0f;
    }
}
