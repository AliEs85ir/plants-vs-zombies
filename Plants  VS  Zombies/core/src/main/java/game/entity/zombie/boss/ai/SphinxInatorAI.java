package game.entity.zombie.boss.ai;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.RandomSelector;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.math.Vector2;
import common.model.Position;
import game.engine.time.GameClock;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.BossProjectileAbility;
import game.entity.zombie.Zombie;
import game.entity.zombie.boss.boss.SphinxInator;
import game.entity.zombie.data.properties.Property;
import game.service.Zombie.ZombieService;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.ZombieVisualEventType;
import graphics.general.modifier.visualModifiers.WhiteModifier;
import graphics.zombie.ZombieRenderInstance;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SphinxInatorAI {
    private static final Random RANDOM = new Random();

    public static BehaviorTree<SphinxInator> buildTree(SphinxInator boss) {
        Selector<SphinxInator> root = new Selector<>();
        Sequence<SphinxInator> introSequence = new Sequence<>();
        Sequence<SphinxInator> deathSequence = new Sequence<>();
        Sequence<SphinxInator> stunSequence = new Sequence<>();
        Selector<SphinxInator> mainSelector = new Selector<>();
        Sequence<SphinxInator> attackSequence = new Sequence<>();
        RandomSelector<SphinxInator> nonAttackRandomSelector = new RandomSelector<>();
        RandomSelector<SphinxInator> attackRandomSelector = new RandomSelector<>();
        Sequence<SphinxInator> idleSequence = new Sequence<>();
        RandomSelector<SphinxInator> walkSelector = new RandomSelector<>();
        Sequence<SphinxInator> walkUpSequence = new Sequence<>();
        Sequence<SphinxInator> walkDownSequence = new Sequence<>();
        Sequence<SphinxInator> summonSequence = new Sequence<>();
        Sequence<SphinxInator> missileSequence = new Sequence<>();
        Sequence<SphinxInator> fastWalkSequence = new Sequence<>();

        root.addChild(introSequence);
        root.addChild(deathSequence);
        root.addChild(stunSequence);
        root.addChild(mainSelector);

        mainSelector.addChild(attackSequence);
        mainSelector.addChild(nonAttackRandomSelector);

        nonAttackRandomSelector.addChild(idleSequence);
        nonAttackRandomSelector.addChild(walkSelector);


        introSequence.addChild(new CheckNotIntroduced());
        introSequence.addChild(new IntroAction());
        introSequence.addChild(new WaitTask(4f));

        deathSequence.addChild(new CheckDead());
        deathSequence.addChild(new BossDeathTasks.PlayDeathClip<>(ZombieVisualEventType.EGYPT_DIE1));
        deathSequence.addChild(new WaitTask(0.27f));
        deathSequence.addChild(new BossDeathTasks.PlayDeathClip<>(ZombieVisualEventType.EGYPT_DIE2));
        deathSequence.addChild(new WaitTask(4.93f));
        deathSequence.addChild(new BossDeathTasks.StartDeathTalk<>(ZombieVisualEventType.EGYPT_TALK));
        deathSequence.addChild(new BossDeathTasks.WaitForOutroDialogue<>());
        deathSequence.addChild(new BossDeathTasks.PlayDeathClip<>(ZombieVisualEventType.EGYPT_EXIT));
        deathSequence.addChild(new WaitTask(7.3f));
        deathSequence.addChild(new BossDeathTasks.FinishDeathSequence<>());

        stunSequence.addChild(new CheckStun());
        stunSequence.addChild(new StunBegin());
        stunSequence.addChild(new WaitTask(5f));
        stunSequence.addChild(new StunEnd());
        stunSequence.addChild(new WaitTask(1.5f));

        attackSequence.addChild(new CheckAttackChance());
        attackSequence.addChild(attackRandomSelector);


        idleSequence.addChild(new IdleAction());
        idleSequence.addChild(new WaitTask(3));

        walkSelector.addChild(walkUpSequence);
        walkSelector.addChild(walkDownSequence);

        walkUpSequence.addChild(new walkUp());
        walkUpSequence.addChild(new Stop());

        walkDownSequence.addChild(new walkDown());
        walkDownSequence.addChild(new Stop());

        attackRandomSelector.addChild(summonSequence);
        attackRandomSelector.addChild(missileSequence);
        attackRandomSelector.addChild(fastWalkSequence);
        // todo -> add other attacks

        summonSequence.addChild(new StartSummon());
        summonSequence.addChild(new WaitTask(2));
        summonSequence.addChild(new SummonAction());
        summonSequence.addChild(new WaitTask(1.5f));
        summonSequence.addChild(new SummonAction());
        summonSequence.addChild(new WaitTask(1.5f));
        summonSequence.addChild(new SummonAction());
        summonSequence.addChild(new WaitTask(1.5f));
        summonSequence.addChild(new SummonAction());
        summonSequence.addChild(new WaitTask(1.5f));
        summonSequence.addChild(new SummonEnd());

        missileSequence.addChild(new MissileStart());
        missileSequence.addChild(new WaitTask(5f));
        missileSequence.addChild(new MissileAction());

        fastWalkSequence.addChild(new WalkForward());
        fastWalkSequence.addChild(new WalkBackward());

        return new BehaviorTree<>(root, boss);
    }

    public static class CheckNotIntroduced extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            if (getObject().isIntroduced()) return Status.FAILED;
            getObject().setIntroduced(true);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class WalkForward extends LeafTask<SphinxInator> {
        @Override
        public void start() {
            getObject().pushEvent(ZombieVisualEventType.WALK_FORWARD);
        }

        @Override
        public Status execute() {
            if (!getObject().canKeepMovingHorizontal(-1, 5f)) {
                getObject().stopMovement();
                getObject().clampBodyToPlayground();
                return Status.SUCCEEDED;
            }
            getObject().getBody().setLinearVelocity(-5, 0);
            return Status.RUNNING;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class WalkBackward extends LeafTask<SphinxInator> {
        @Override
        public void start() {
            getObject().pushEvent(ZombieVisualEventType.WALK_BACKWARD);
        }

        @Override
        public Status execute() {
            if (!getObject().canKeepMovingHorizontal(1, 5f)) {
                getObject().stopMovement();
                getObject().clampBodyToPlayground();
                return Status.SUCCEEDED;
            }
            getObject().getBody().setLinearVelocity(5, 0);
            return Status.RUNNING;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class MissileStart extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.MISSILE_START);
            getObject().pushEvent(ZombieVisualEventType.MISSILE);
            Grid grid = getObject().getWorld().getGrid();
            List<Cell> cells = grid.getFirstColumns(3);
            if (cells.isEmpty()) {
                getObject().setPendingMissileTarget(null);
                return Status.FAILED;
            }
            Collections.shuffle(cells);
            Cell cell = cells.get(0);
            getObject().setPendingMissileTarget(cell);
            BossProjectileAbility.SpawnTargetEffect(cell);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }

    public static class MissileAction extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            Grid grid = getObject().getWorld().getGrid();
            Cell cell = getObject().getPendingMissileTarget();
            if (cell == null) {
                List<Cell> cells = grid.getFirstColumns(3);
                if (cells.isEmpty()) {
                    return Status.FAILED;
                }
                Collections.shuffle(cells);
                cell = cells.get(0);
            }
            Vector2 cellPos = cell.getWorldPosition();
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.MISSILE,
                20,
                20,
                cellPos.x,
                cellPos.y + grid.getConfig().getMapHeight(),
                List.of(new BossProjectileAbility(cell, 7f))
            );
            getObject().setPendingMissileTarget(null);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class SummonEnd extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.PORTAL_END);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class StartSummon extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.PORTAL_START);
            getObject().pushEvent(ZombieVisualEventType.PORTAL_LOOP);
            game.audio.Sfx.play(game.audio.AudioId.SFX_PORTAL);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                GameClock.currentTick,
                25,
                this::spawnEffect
            );
            return Status.SUCCEEDED;
        }

        private void spawnEffect() {
            float offsetX = -100;
            float offsetY = 0;
            Position position = new Position(
                getObject().getPosition().getX() + offsetX,
                getObject().getPosition().getY() + offsetY
            );
            EffectRequest<Object> request = new EffectRequest<>(
                "portal",
                null,
                new WorldAnchor(position),
                EffectLayer.FRONT,
                EffectConditions.withinSeconds(6f)
            );
            App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class SummonAction extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {

            List<Property> allowable = getObject().getWorld().getCurrentChapter().getAvailableZombies();
            Property randomZombie = allowable.get(RANDOM.nextInt(allowable.size()));
            ZombieService service = new ZombieService(getObject().getWorld());
            Position spawnPos = new Position(getObject().getPosition().getX() - 300,
                                                getObject().getPosition().getY());
            Zombie zombie = service.spawnZombie(randomZombie.getAlias(), spawnPos);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                GameClock.currentTick,
                2,
                () -> addModifier(zombie)
            );
            return Status.SUCCEEDED;
        }

        public void addModifier(Zombie zombie) {
            ZombieRenderInstance instance = App.getRealTimeEngine().
                getGraphicsEngine().getZombieInstance(zombie);
            if (instance != null) {
                instance.getModifierController().add(new WhiteModifier());
            }
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class IntroAction extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.INTRO);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class IdleAction extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            System.err.println("I CHOOSE IDLE");
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class walkUp extends LeafTask<SphinxInator> {
        private float timer;
        private boolean canWalk;

        @Override
        public void start() {
            timer = 1 + RANDOM.nextFloat() * 5;
            canWalk = getObject().canMoveInsideMap(1, timer);
            System.err.println("WALK UP : " + canWalk);
        }

        @Override
        public Status execute() {
            if(!canWalk) {
                return Status.FAILED;
            }
            if (!getObject().canMoveInsideMap(1, GameClock.SECONDS_PER_TICK)) {
                getObject().stopMovement();
                getObject().clampBodyToPlayground();
                return Status.SUCCEEDED;
            }
            getObject().pushEvent(ZombieVisualEventType.WALK_UP);
            Vector2 vel = getObject().getBody().getLinearVelocity();
            getObject().getBody().setLinearVelocity(vel.x,
                (float) getObject().getPropertyData().getSpeed());
            timer -= GameClock.SECONDS_PER_TICK;
            if(timer <= 0) {
                return Status.SUCCEEDED;
            }
            return Status.RUNNING;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }

    public static class walkDown extends LeafTask<SphinxInator> {
        private float timer;
        private boolean canWalk;

        @Override
        public void start() {
            timer = 1 + RANDOM.nextFloat() * 5;
            canWalk = getObject().canMoveInsideMap(-1, timer);
            System.err.println("WALK DOWN : " + canWalk);
        }

        @Override
        public Status execute() {
            if(!canWalk) {
                return Status.FAILED;
            }
            if (!getObject().canMoveInsideMap(-1, GameClock.SECONDS_PER_TICK)) {
                getObject().stopMovement();
                getObject().clampBodyToPlayground();
                return Status.SUCCEEDED;
            }
            getObject().pushEvent(ZombieVisualEventType.WALK_DOWN);
            Vector2 vel = getObject().getBody().getLinearVelocity();
            getObject().getBody().setLinearVelocity(vel.x,
                (float) - getObject().getPropertyData().getSpeed());
            timer -= GameClock.SECONDS_PER_TICK;
            if(timer <= 0) {
                return Status.SUCCEEDED;
            }
            return Status.RUNNING;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class StunEnd extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.STUN_END);
            getObject().endStun();
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }

    public static class Stop extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            getObject().stopMovement();
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class StunBegin extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            getObject().beginStun();
            getObject().pushEvent(ZombieVisualEventType.STUN_START);
            getObject().pushEvent(ZombieVisualEventType.STUN_LOOP);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class CheckStun extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            if (getObject().shouldEnterStun()) {
                return Status.SUCCEEDED;
            }
            return Status.FAILED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
    public static class CheckDead extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            if(getObject().isDead()) {
                return Status.SUCCEEDED;
            }
            return Status.FAILED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }

    public static class WaitTask extends LeafTask<SphinxInator> {
        private float duration;
        private float timer;

        public WaitTask(float duration) {
            this.duration = duration;
        }

        @Override
        public void start() {
            timer = 0f;
        }

        @Override
        public Status execute() {
            timer += GameClock.SECONDS_PER_TICK;
            if(timer >= duration) {
                return Status.SUCCEEDED;
            }
            return Status.RUNNING;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            ((SphinxInatorAI.WaitTask)task).duration = duration;
            return task;
        }
    }

    public static class CheckAttackChance extends LeafTask<SphinxInator> {
        @Override
        public Status execute() {
            float chance = getObject().getAttackChance();
            if (RANDOM.nextFloat() < chance) return Status.SUCCEEDED;
            return Status.FAILED;
        }

        @Override
        protected Task<SphinxInator> copyTo(Task<SphinxInator> task) {
            return task;
        }
    }
}
