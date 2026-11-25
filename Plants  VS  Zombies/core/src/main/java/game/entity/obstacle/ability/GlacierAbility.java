package game.entity.obstacle.ability;

import app.model.App;
import game.entity.obstacle.Obstacle;
import game.service.obstacle.ObstacleService;
import graphics.general.event.ObstacleVisualEventType;
import graphics.general.instance.VisibilityController;
import graphics.obstacle.ObstacleRenderInstance;

public class GlacierAbility implements ObstacleAbility{
    private static final int LAST_STAGE = 5;
    private static final String BASE_LAYER = "damage_00";

    private final double maxHealth;
    private double currentHealth;
    private int damageStage;
    private boolean dead;
    private boolean applied;

    public GlacierAbility(double currentHealth) {
        this.maxHealth = currentHealth;
        this.currentHealth = currentHealth;
        this.damageStage = 0;
        this.dead = false;
        this.applied = false;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(dead) {
            if (!applied) {
                obstacle.die();
                obstacle.pushEvent(ObstacleVisualEventType.DEATH);
                applied = true;
            }
            return;
        }
        applyDamageLayers(obstacle);
    }

    @Override
    public void onDeath(Obstacle obstacle) {

    }


    public void takeDamage(double amount, Obstacle obstacle) {
        currentHealth -= amount;
        damageStage = stageFromHealth();
        applyDamageLayers(obstacle);
        if(currentHealth <= 0) {
            dead = true;
        }
    }

    private int stageFromHealth() {
        if (maxHealth <= 0) {
            return 0;
        }
        double ratio = Math.max(0d, currentHealth) / maxHealth;
        return Math.min(LAST_STAGE, (int) ((1d - ratio) * (LAST_STAGE + 1)));
    }

    private void applyDamageLayers(Obstacle obstacle) {
        VisibilityController visibility = visibilityOf(obstacle);
        if (visibility == null) {
            return;
        }
        visibility.setVisible(BASE_LAYER);
        for (int i = 1; i <= LAST_STAGE; i++) {
            String layer = "damage_0" + i;
            if (i == damageStage) {
                visibility.setVisible(layer);
            } else {
                visibility.setInvisible(layer);
            }
        }
    }

    private static VisibilityController visibilityOf(Obstacle obstacle) {
        if (obstacle == null || App.getRealTimeEngine() == null) {
            return null;
        }
        ObstacleRenderInstance instance = App.getRealTimeEngine()
            .getGraphicsEngine()
            .getObstacleInstance(obstacle);
        return instance == null ? null : instance.getVisibilityController();
    }

    public boolean isDead() {
        return dead;
    }
}
