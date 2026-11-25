package game.entity.obstacle.ability;

import app.model.App;
import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import game.entity.zombie.data.properties.Property;
import game.service.Zombie.ZombieService;
import game.service.obstacle.ObstacleService;
import game.world.playground.Cell;
import graphics.general.instance.VisibilityController;
import graphics.general.modifier.visualModifiers.WhiteModifier;
import graphics.obstacle.ObstacleRenderInstance;
import graphics.zombie.ZombieRenderInstance;

import java.util.List;
import java.util.Random;

public class BossIceAbility implements ObstacleAbility{
    private static final int LAST_STAGE = 5;

    private final double maxHealth;
    private double currentHealth;
    private int damageStage;
    private boolean dead;
    private Cell myCell;
    private Zombie myZombie;

    public BossIceAbility(double currentHealth, Cell myCell) {
        this.maxHealth = currentHealth;
        this.currentHealth = currentHealth;
        this.damageStage = 0;
        this.dead = false;
        this.myCell = myCell;
    }

    @Override
    public void update(Obstacle obstacle, ObstacleService obstacleService, long currentTick) {
        if(dead) {
            obstacle.die();
            return;
        }
        applyDamageLayers(obstacle);
    }

    @Override
    public void onDeath(Obstacle obstacle) {
        Random random = new Random();
        List<Property> zombies = App.getCurrentGameEngine().getGameWorld()
            .getCurrentChapter().getAvailableZombies();
        Property zombie = zombies.get(random.nextInt(zombies.size()));
        ZombieService service = new ZombieService(App.getCurrentGameEngine().getGameWorld());
        myZombie = service.spawnZombie(zombie.getAlias(), myCell.getWorldPosition());
        App.getCurrentGameEngine().getTaskManager().scheduleRelative(
            GameClock.currentTick,
            2,
            () -> addModifier()
        );
    }

    private void addModifier() {
        ZombieRenderInstance instance = App.getRealTimeEngine().getGraphicsEngine().getZombieInstance(myZombie);
        if(instance == null) {
            return;
        }
        instance.getModifierController().add(new WhiteModifier());
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
        for (int i = 0; i <= LAST_STAGE; i++) {
            String layer = "glacier_block_damage" + i;
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
}
