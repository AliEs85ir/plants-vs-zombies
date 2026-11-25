package game.world.minigame.beghouled;

import game.entity.plant.Plant;

final class BeghouledSlide {
    private final Plant plant;
    private final float startX;
    private final float startY;
    private final float endX;
    private final float endY;
    private final float duration;
    private float elapsed;

    BeghouledSlide(Plant plant, float startX, float startY, float endX, float endY, float duration) {
        this.plant = plant;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.duration = Math.max(0.08f, duration);
    }

    boolean advance(float delta) {
        if (plant == null || plant.isDead()) {
            return true;
        }
        elapsed += Math.max(0f, delta);
        float t = Math.min(1f, elapsed / duration);
        t = t * t * (3f - 2f * t);
        BeghouledPlantOps.setWorldPos(
            plant,
            startX + (endX - startX) * t,
            startY + (endY - startY) * t
        );
        if (elapsed >= duration) {
            BeghouledPlantOps.setWorldPos(plant, endX, endY);
            return true;
        }
        return false;
    }
}
