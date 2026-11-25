package game.entity.zombie.boss.ai;

import app.enums.obstacle.ObstacleType;
import app.model.App;
import app.view.game.PlayScreen;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.RandomSelector;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.math.Vector2;
import game.engine.time.GameClock;
import game.entity.DeathStyle;
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.BossProjectileAbility;
import game.entity.obstacle.ability.TimerdLifeAbility;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.boss.boss.DarkDragon;
import game.entity.zombie.data.properties.Property;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.effects.EffectConditions;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.ZombieVisualEventType;
import graphics.general.modifier.visualModifiers.WhiteModifier;
import graphics.zombie.ZombieRenderInstance;

import java.util.*;


public class DarkDragonAI {
    private static final Random RANDOM = new Random();

    private static List<Cell> pickFireballTargets(DarkDragon dragon) {
        List<Cell> cells = new ArrayList<>(dragon.getWorld().getGrid().getFirstColumns(5));
        Collections.shuffle(cells);
        int count = Math.min(RANDOM.nextInt(5), cells.size());
        List<Cell> picked = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            picked.add(cells.get(i));
        }
        return picked;
    }

    public static BehaviorTree<DarkDragon> buildTree(DarkDragon boss) {
        Selector<DarkDragon> root = new Selector<>();
        Selector<DarkDragon> mainSelector = new Selector<>();
        Sequence<DarkDragon> attackSequence = new Sequence<>();
        RandomSelector<DarkDragon> randomAttackSelector = new RandomSelector<>();
        Sequence<DarkDragon> summonSequence = new Sequence<>();
        Sequence<DarkDragon> fireAttackSequence = new Sequence<>();
        Sequence<DarkDragon> fireBombSequence = new Sequence<>();
        Sequence<DarkDragon> vulnerabilitySequence = new Sequence<>();
        Sequence<DarkDragon> stunSequence = new Sequence<>();
        RandomSelector<DarkDragon> nonAttackRandomSelector = new RandomSelector<>();
        Sequence<DarkDragon> idleSequence = new Sequence<>();
        Sequence<DarkDragon> walkUpSequence = new Sequence<>();
        Sequence<DarkDragon> walkDownSequence = new Sequence<>();
        RandomSelector<DarkDragon> walkSelector = new RandomSelector<>();

        // Intro sequence (runs once)
        Sequence<DarkDragon> introSequence = new Sequence<>();
        introSequence.addChild(new CheckNotIntroduced());
        introSequence.addChild(new IntroAction());
        introSequence.addChild(new WaitTask(8f));
        introSequence.addChild(new SpawnPermanentScorch());
        introSequence.addChild(new WaitTask(3));

        // Death check
        Sequence<DarkDragon> deathSequence = new Sequence<>();
        deathSequence.addChild(new CheckDead());
        deathSequence.addChild(new StartDeath());
        deathSequence.addChild(new WaitTask(5.67f));
        deathSequence.addChild(new BossDeathTasks.StartDeathTalk<>(ZombieVisualEventType.DRAGON_TALK));
        deathSequence.addChild(new BossDeathTasks.WaitForOutroDialogue<>());
        deathSequence.addChild(new BossDeathTasks.PlayDeathClip<>(ZombieVisualEventType.DRAGON_EXIT));
        deathSequence.addChild(new WaitTask(6.17f));
        deathSequence.addChild(new BossDeathTasks.FinishDeathSequence<>());


        walkUpSequence.addChild(new walkUp());
        walkUpSequence.addChild(new Stop());

        walkDownSequence.addChild(new walkDown());
        walkDownSequence.addChild(new Stop());

        walkSelector.addChild(walkUpSequence);
        walkSelector.addChild(walkDownSequence);

        stunSequence.addChild(new CheckStun());
        stunSequence.addChild(new StunBegin());
        stunSequence.addChild(new WaitTask(5f));
        stunSequence.addChild(new StunEnd());
        stunSequence.addChild(new WaitTask(1.5f));

        summonSequence.addChild(new StartSummon());
        summonSequence.addChild(new WaitTask(0.5f));
        summonSequence.addChild(new SummonAction());

        fireAttackSequence.addChild(new StartFireAttack());
        fireAttackSequence.addChild(new WaitTask(1.8f));
        fireAttackSequence.addChild(new FireAttackAction());
        fireAttackSequence.addChild(new WaitTask(3));
        fireAttackSequence.addChild(new EndFireAttack());

        fireBombSequence.addChild(new StartFireBomb());
        fireBombSequence.addChild(new WaitTask(4));
        fireBombSequence.addChild(new FireBombAction());

        vulnerabilitySequence.addChild(new VulnerabilityAction());
        vulnerabilitySequence.addChild(new WaitTask(3));
        vulnerabilitySequence.addChild(new VulnerabilityEnd());

        randomAttackSelector.addChild(fireAttackSequence);
        randomAttackSelector.addChild(fireBombSequence);
        randomAttackSelector.addChild(summonSequence);
        randomAttackSelector.addChild(vulnerabilitySequence);

        attackSequence.addChild(new CheckAttackChance());
        attackSequence.addChild(randomAttackSelector);

        mainSelector.addChild(attackSequence);
        mainSelector.addChild(nonAttackRandomSelector);

        nonAttackRandomSelector.addChild(idleSequence);
        nonAttackRandomSelector.addChild(walkSelector);

        idleSequence.addChild(new IdleAction());
        idleSequence.addChild(new WaitTask(3));

        root.addChild(introSequence);
        root.addChild(deathSequence);
        root.addChild(stunSequence);
        root.addChild(mainSelector);

        return new BehaviorTree<>(root, boss);
    }

    public static class StunEnd extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.STUN_END);
            getObject().endStun();
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class CheckStun extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            if (getObject().shouldEnterStun()) {
                return Status.SUCCEEDED;
            }
            return Status.FAILED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }
    public static class walkDown extends LeafTask<DarkDragon> {
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
            if (!canWalk) {
                return Status.FAILED;
            }
            if (!getObject().canMoveInsideMap(-1, GameClock.SECONDS_PER_TICK)) {
                getObject().stopMovement();
                getObject().clampBodyToPlayground();
                return Status.SUCCEEDED;
            }
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            Vector2 vel = getObject().getBody().getLinearVelocity();
            getObject().getBody().setLinearVelocity(vel.x,
                (float) - getObject().getPropertyData().getSpeed());
            timer -= GameClock.SECONDS_PER_TICK;
            if (timer <= 0) {
                return Status.SUCCEEDED;
            }
            return Status.RUNNING;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class StunBegin extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().beginStun();
            getObject().pushEvent(ZombieVisualEventType.STUN_START);
            getObject().pushEvent(ZombieVisualEventType.STUN_LOOP);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class StartSummon extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.DRAGON_SUMMONING);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }
    public static class SpawnPermanentScorch extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            Grid grid = getObject().getWorld().getGrid();
            List<Cell> cells = grid.getLastColumns(2);
            for(Cell cell : cells) {
                ObstacleFactory.getInstance().createRuntimeObstacle(
                    ObstacleType.PERMANENT_SCORCHED,
                    5,
                    5,
                    cell.getWorldX(),
                    cell.getWorldY(),
                    List.of()
                );
            }
            PlayScreen.shake(0.8f, 24f);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class walkUp extends LeafTask<DarkDragon> {
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
            if (!canWalk) {
                return Status.FAILED;
            }
            if (!getObject().canMoveInsideMap(1, GameClock.SECONDS_PER_TICK)) {
                getObject().stopMovement();
                getObject().clampBodyToPlayground();
                return Status.SUCCEEDED;
            }
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            Vector2 vel = getObject().getBody().getLinearVelocity();
            getObject().getBody().setLinearVelocity(vel.x,
                (float) getObject().getPropertyData().getSpeed());
            timer -= GameClock.SECONDS_PER_TICK;
            if (timer <= 0) {
                return Status.SUCCEEDED;
            }
            return Status.RUNNING;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }
    public static class Stop extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().stopMovement();
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }
    public static class StartFireAttack extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.DRAGON_FIRE_START);
            getObject().pushEvent(ZombieVisualEventType.DRAGON_FIRE);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }
    public static class VulnerabilityEnd extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.DRAGON_VULNERABLE_END);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }
    public static class StartFireBomb extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.DRAGON_BOMB_START);
            getObject().pushEvent(ZombieVisualEventType.DRAGON_BOMB);
            List<Cell> targets = pickFireballTargets(getObject());
            getObject().setPendingFireballTargets(targets);
            for (Cell cell : targets) {
                getObject().spawnFireballTargetEffect(cell);
            }
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }
    public static class EndFireAttack extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.DRAGON_FIRE_END);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }



    public static class CheckNotIntroduced extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            if (getObject().isIntroduced()) return Status.FAILED;
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class IntroAction extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.INTRO);
            getObject().setIntroduced(true);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class CheckDead extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            if (getObject().isDead()) return Status.SUCCEEDED;
            return Status.FAILED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class StartDeath extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.DRAGON_DIE);
            PlayScreen.shake(0.75f, 22f);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class CheckAttackChance extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            float chance = getObject().getAttackChance();
            if (RANDOM.nextFloat() < chance) return Status.SUCCEEDED;
            return Status.FAILED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }


    public static class FireAttackAction extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            List<Cell> cells = findCells();
            int i = 0;
            for(Cell cell : cells) {
                EffectRequest<Object> request = new EffectRequest<>(
                    "fire impact start",
                    null,
                    new WorldAnchor(cell.getWorldPosition()),
                    EffectLayer.FRONT,
                    EffectConditions.withinSeconds(3)
                );
                App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
                for(Plant plant : getObject().getWorld().getPlants()) {
                    if(cell.equals(getObject().getWorld()
                        .getGrid().getCellAt(plant.getPosition()))) {
                        plant.markDead(getObject().getWorld());
                    }
                }
                for(Zombie zombie : getObject().getWorld().getZombies()) {
                    if(cell.equals(getObject().getWorld()
                        .getGrid().getCellAt(zombie.getPosition()))) {
                        zombie.setPendingDeathStyle(DeathStyle.ASH);
                        zombie.die();
                    }
                }
                App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                    GameClock.currentTick,
                    (i+1) * 5L,
                    () -> spawnScorch(cell)
                );
                i++;
            }
            PlayScreen.shake(0.7f, 22f);
            return Status.SUCCEEDED;
        }

        private static void spawnScorch(Cell cell) {
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.TEMPORARY_SCORCH,
                cell.getWidth() / 2,
                cell.getHeight() / 2,
                cell.getWorldX(),
                cell.getWorldY(),
                List.of(new TimerdLifeAbility(4))
            );
        }

        private List<Cell> findCells() {
            Grid grid = getObject().getWorld().getGrid();

            int laneMask = getObject().getLaneMask();
            int rowCount = grid.getRowCount();
            int colCount = grid.getColCount();

            int columnsToAttack = Math.min(5, colCount);
            List<Cell> cells = new ArrayList<>();

            for (int row = 0; row < rowCount; row++) {
                // Check whether this lane is enabled in the mask.
                if ((laneMask & (1 << row)) == 0) {
                    continue;
                }

                for (int col = 0; col < columnsToAttack; col++) {
                    Cell cell = grid.getCell(row, col);

                    if (cell != null) {
                        cells.add(cell);
                    }
                }
            }
            return cells;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class FireBombAction extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            Grid grid = getObject().getWorld().getGrid();
            List<Cell> cells = getObject().getPendingFireballTargets();
            if (cells.isEmpty()) {
                cells = pickFireballTargets(getObject());
            }
            getObject().pushEvent(ZombieVisualEventType.DRAGON_BOMB_END);
            for (Cell cell : cells) {
                Vector2 cellPos = cell.getWorldPosition();
                ObstacleFactory.getInstance().createRuntimeObstacle(
                    ObstacleType.FIREBALL,
                    20,
                    20,
                    cellPos.x,
                    cellPos.y + grid.getConfig().getMapHeight(),
                    List.of(new BossProjectileAbility(cell, 7f))
                );
            }
            getObject().clearPendingFireballTargets();
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class SummonAction extends LeafTask<DarkDragon> {

        @Override
        public Status execute() {
            List<Cell> cells = findCells();
            ZombieService service = new ZombieService(getObject().getWorld());
            int randomNumber = RANDOM.nextInt(cells.size());
            Collections.shuffle(cells);
            for (int i = 0; i < randomNumber; i++) {
                Cell cell = cells.get(i);
                EffectRequest<Object> request = new EffectRequest<>(
                    "dirt spawn",
                    null,
                    new WorldAnchor(cell.getWorldPosition()),
                    EffectLayer.FRONT
                );
                App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
                Zombie z = spawnRandomZombie(getObject().getWorld(), service, cell);
                App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                    GameClock.currentTick,
                    2,
                    () -> addModifier(z)
                );

            }
            return Status.SUCCEEDED;
        }
        private Zombie spawnRandomZombie(GameWorld world, ZombieService service, Cell cell) {
            List<Property> availableZombies = world.getCurrentChapter().getAvailableZombies();
            Property random = availableZombies.get(RANDOM.nextInt(availableZombies.size()));
            return service.spawnZombie(random.getAlias(), cell.getWorldPosition());
        }

        private void addModifier(Zombie zombie) {
            ZombieRenderInstance instance = App.getRealTimeEngine().
                getGraphicsEngine().getZombieInstance(zombie);
            if(instance == null) {
                return;
            }
            instance.getModifierController().add(new WhiteModifier());
        }

        private List<Cell> findCells() {
            var grid = getObject().getWorld().getGrid();

            int rowCount = grid.getRowCount();
            int colCount = grid.getColCount();

            List<Cell> obstacleCells = new ArrayList<>();

            int secondLastColumn = colCount - 3;
            int thirdLastColumn = colCount - 4;

            for (int row = 0; row < rowCount; row++) {
                Cell secondLastCell = grid.getCell(row, secondLastColumn);
                Cell thirdLastCell = grid.getCell(row, thirdLastColumn);

                if (secondLastCell != null) {
                    obstacleCells.add(secondLastCell);
                }

                if (thirdLastCell != null) {
                    obstacleCells.add(thirdLastCell);
                }
            }
            return obstacleCells;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class VulnerabilityAction extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.DRAGON_VULNERABLE_START);
            getObject().pushEvent(ZombieVisualEventType.DRAGON_VULNERABLE);
            // todo -> do action.
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class IdleAction extends LeafTask<DarkDragon> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            return task;
        }
    }

    public static class WaitTask extends LeafTask<DarkDragon> {
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
        protected Task<DarkDragon> copyTo(Task<DarkDragon> task) {
            ((WaitTask)task).duration = duration;
            return task;
        }
    }
}
