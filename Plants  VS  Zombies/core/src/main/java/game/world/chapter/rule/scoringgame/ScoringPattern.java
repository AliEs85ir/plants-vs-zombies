package game.world.chapter.rule.scoringgame;

import app.controller.game.ScoringGameController;
import app.enums.plant.PlantName;
import app.view.hud.notification.GameplayNotifications;
import app.view.hud.notification.NotificationSpec;
import game.component.physic.PhysicService;
import game.engine.GameSimulationEngine;
import game.entity.Entity;
import game.entity.plant.Plant;
import game.entity.projectile.Projectile;
import game.entity.zombie.Zombie;
import game.world.GameWorld;
import game.world.chapter.rule.ChapterRule;
import game.world.playground.Cell;
import graphics.GraphicsEngine;

import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ScoringPattern implements ChapterRule {
    public static final ScoringGameController controller = new ScoringGameController();

    private int matchCount;
    private final Set<Zombie> seenDead = java.util.Collections.newSetFromMap(new IdentityHashMap<>());

    @Override
    public void onChapterStart(GameWorld world, GraphicsEngine graphicsEngine, PhysicService physicService) {
    }

    @Override
    public void onTick(GameWorld world, long currentTick) {
    }

    @Override
    public void onEngineStart(GameSimulationEngine engine) {
    }

    public void sendNotification(String text) {
        NotificationSpec spec = GameplayNotifications.scoringBonus(text);
        if (controller != null) {
            controller.notifyScoring(spec);
            return;
        }
        GameplayNotifications.show(spec);
    }

    protected void award(String message, int mioPoints) {
        matchCount++;
        sendNotification(message);
        if (mioPoints <= 0) {
            return;
        }
        ScoringSession.add(mioPoints);
        controller.notifyMioScore(ScoringSession.points());
    }

    public int getMatchCount() {
        return matchCount;
    }

    protected void forEachNewDead(GameWorld world, Consumer<Zombie> consumer) {
        if (world == null || world.getZombies() == null || consumer == null) {
            return;
        }
        for (Zombie zombie : world.getZombies()) {
            if (zombie != null && zombie.isDead() && seenDead.add(zombie)) {
                consumer.accept(zombie);
            }
        }
    }

    protected static Plant plantOf(Entity attacker) {
        if (attacker instanceof Plant plant) {
            return plant;
        }
        if (attacker instanceof Projectile projectile) {
            return projectile.getOwner();
        }
        return null;
    }

    protected static PlantName plantNameOf(Entity attacker) {
        Plant plant = plantOf(attacker);
        if (plant == null || plant.getDefinition() == null) {
            return null;
        }
        return plant.getDefinition().getName();
    }

    protected static int columnOf(GameWorld world, Zombie zombie) {
        if (world == null || world.getGrid() == null || zombie == null || zombie.getPosition() == null) {
            return -1;
        }
        Cell cell = world.getGrid().getCellAt(zombie.getPosition());
        return cell == null ? -1 : cell.getCol();
    }

    protected static int plantsEaten(GameWorld world) {
        if (world == null) {
            return 0;
        }
        if (world.getObserver() != null) {
            return world.getObserver().getLostPlantsCount();
        }
        return world.getTotalDeadPlants();
    }

    protected static int boundedCount(int count) {
        return Math.max(1, count);
    }
}
