package game.entity.zombie.boss.ai;

import app.model.App;
import app.view.game.PlayScreen;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.RandomSelector;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import game.component.contacts.ContactBits;
import game.component.physic.PhysicService;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.boss.boss.Shark;
import game.entity.zombie.data.properties.Property;
import game.service.Zombie.ZombieService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.ZombieVisualEventType;
import graphics.general.modifier.visualModifiers.WhiteModifier;
import graphics.zombie.ZombieRenderInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SharkAI {
    private static final Random RANDOM = new Random();

    public static BehaviorTree<Shark> buildTree(Shark boss) {
        Selector<Shark> root = new Selector<>();
        Sequence<Shark> introSequence = new Sequence<>();
        Sequence<Shark> deathSequence = new Sequence<>();
        Sequence<Shark> stunSequence = new Sequence<>();
        Selector<Shark> mainSelector = new Selector<>();
        Sequence<Shark> attackSequence = new Sequence<>();
        RandomSelector<Shark> nonAttackRandomSelector = new RandomSelector<>();
        RandomSelector<Shark> attackRandomSelector = new RandomSelector<>();
        Sequence<Shark> idleSequence = new Sequence<>();
        Sequence<Shark> submergeSequence = new Sequence<>();
        Sequence<Shark> suctionSequence = new Sequence<>();
        Sequence<Shark> summonSequence = new Sequence<>();
        Sequence<Shark> sharkSequence = new Sequence<>();

        root.addChild(introSequence);
        root.addChild(deathSequence);
        root.addChild(stunSequence);
        root.addChild(mainSelector);

        stunSequence.addChild(new CheckStun());
        stunSequence.addChild(new StunBegin());
        stunSequence.addChild(new WaitTask(5f));
        stunSequence.addChild(new StunEnd());
        stunSequence.addChild(new WaitTask(1.5f));

        introSequence.addChild(new CheckNotIntroduced());
        introSequence.addChild(new IntroAction());
        introSequence.addChild(new WaitTask(4f));

        deathSequence.addChild(new CheckDead());
        deathSequence.addChild(new BossDeathTasks.PlayDeathClip<>(ZombieVisualEventType.SHARK_DIE));
        deathSequence.addChild(new WaitTask(5.07f));
        deathSequence.addChild(new BossDeathTasks.StartDeathTalk<>(ZombieVisualEventType.SHARK_TALK));
        deathSequence.addChild(new BossDeathTasks.WaitForOutroDialogue<>());
        deathSequence.addChild(new BossDeathTasks.PlayDeathClip<>(ZombieVisualEventType.SHARK_EXIT));
        deathSequence.addChild(new WaitTask(7.67f));
        deathSequence.addChild(new BossDeathTasks.FinishDeathSequence<>());

        mainSelector.addChild(attackSequence);
        mainSelector.addChild(nonAttackRandomSelector);

        nonAttackRandomSelector.addChild(idleSequence);
        nonAttackRandomSelector.addChild(submergeSequence);

        attackSequence.addChild(new CheckAttackChance());
        attackSequence.addChild(attackRandomSelector);

        attackRandomSelector.addChild(suctionSequence);
        attackRandomSelector.addChild(summonSequence);
        attackRandomSelector.addChild(sharkSequence);

        idleSequence.addChild(new IdleAction());
        idleSequence.addChild(new WaitTask(2));

        submergeSequence.addChild(new SubmergeTask());
        submergeSequence.addChild(new WaitTask(3f));
        submergeSequence.addChild(new EmergeTask());
        submergeSequence.addChild(new WaitTask(2f));

        suctionSequence.addChild(new SuctionStart());
        suctionSequence.addChild(new WaitTask(2f));
        suctionSequence.addChild(new SuctionAction());
        suctionSequence.addChild(new WaitTask(7f));
        suctionSequence.addChild(new SuctionEnd());
        suctionSequence.addChild(new WaitTask(2));

        summonSequence.addChild(new SummonStart());
        summonSequence.addChild(new WaitTask(2));
        summonSequence.addChild(new SummonAction());
        summonSequence.addChild(new WaitTask(5));
        summonSequence.addChild(new SummonEnd());
        summonSequence.addChild(new WaitTask(3f));

        sharkSequence.addChild(new SharkAction());
        sharkSequence.addChild(new WaitTask(6f));
        return new BehaviorTree<>(root, boss);
    }

    public static class IdleAction extends LeafTask<Shark> {
        @Override
        public Status execute() {
            System.err.println("I CHOOSE IDLE");
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }
    public static class SharkAction extends LeafTask<Shark> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            List<Cell> cells = new ArrayList<>();
            Grid grid = getObject().getWorld().getGrid();
            for(Cell cell : grid.getAllCells()) {
                if(cell.isWater()) {
                    cells.add(cell);
                }
            }
            List<Plant> plants = new ArrayList<>();
            for(Plant plant : getObject().getWorld().getPlants()) {
                if(cells.contains(grid.getCellAt(plant.getPosition()))) {
                    plants.add(plant);
                }
            }
            int num = RANDOM.nextInt(4) + 2;
            int killed = 0;
            Collections.shuffle(plants);
            for(Plant plant : plants) {
                if(killed >= num) {
                    break;
                }
                App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                    GameClock.currentTick,
                    (killed+1) * 10L,
                    () -> kill(plant, getObject().getWorld())
                );
                killed++;
            }
            return Status.SUCCEEDED;
        }

        private void kill(Plant plant, GameWorld world) {
            if (plant == null || plant.isDead()) {
                return;
            }
            plant.markDead(world);
            PlayScreen.shake(0.35f, 12f);
            EffectRequest<Plant> request = new EffectRequest<>(
                "shark attack",
                null,
                new WorldAnchor(plant.getPosition()),
                EffectLayer.FRONT
            );
            App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }
    public static class SuctionStart extends LeafTask<Shark> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.SUCTION_START);
            getObject().pushEvent(ZombieVisualEventType.SUCTION_LOOP);
            PlayScreen.shake(0.8f, 14f);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }
    public static class SuctionAction extends LeafTask<Shark> {
        @Override
        public Status execute() {
            List<Cell> cells = findCells();
            Grid grid = getObject().getWorld().getGrid();
            for(Plant plant : getObject().getWorld().getPlants()) {
                if(cells.contains(grid.getCellAt(plant.getPosition()))) {
                    plant.getBody().setType(BodyDef.BodyType.DynamicBody);
                    plant.getBody().setLinearVelocity(3,0);

                }
            }
            PlayScreen.shake(0.55f, 18f);
            return Status.SUCCEEDED;
        }
        private List<Cell> findCells() {
            Grid grid = getObject().getWorld().getGrid();

            int laneMask = getObject().getLaneMask();
            int rowCount = grid.getRowCount();
            int colCount = grid.getColCount();

            int columnsToAttack = Math.min(5, colCount);
            List<Cell> cells = new ArrayList<>();

            for (int row = 0; row < rowCount; row++) {
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
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }public static class SummonStart extends LeafTask<Shark> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.SHARK_SPAWN);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }public static class SummonAction extends LeafTask<Shark> {
        @Override
        public Status execute() {
            List<Cell> cells = new ArrayList<>();
            Grid grid = getObject().getWorld().getGrid();
            for(Cell cell : grid.getAllCells()) {
                if(cell.isWater()) {
                    cells.add(cell);
                }
            }
            Collections.shuffle(cells);
            int zombie = RANDOM.nextInt(6);
            ZombieService service = new ZombieService(getObject().getWorld());
            int placed = 0;
            for(Cell cell : cells) {
                if(placed >= zombie) {
                    break;
                }
                Zombie z = spawnRandomZombie(getObject().getWorld(), service, cell);
                App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                    GameClock.currentTick,
                    2,
                    () -> addModifier(z)
                );
                placed++;
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

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }public static class SummonEnd extends LeafTask<Shark> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.SUCTION_END);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }public static class SuctionEnd extends LeafTask<Shark> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.SUCTION_END);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }
    public static class SubmergeTask extends LeafTask<Shark> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.SUBMERGE);
            Fixture fixture = getObject().getBody().getFixtureList().first();
            Filter filter = fixture.getFilterData();
            filter.maskBits = 0;
            fixture.setFilterData(filter);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }
    public static class EmergeTask extends LeafTask<Shark> {
        @Override
        public Status execute() {
            Shark shark = getObject();
            Grid grid = shark.getWorld().getGrid();

            // 1. Determine current cell and column
            Cell currentCell = grid.getCellAt(shark.getPosition());

            if (currentCell != null) {
                List<Cell> colCells = grid.getColumnCells(currentCell.getCol());
                List<Cell> candidateCells = new ArrayList<>();

                // 2. Pick any cell in the same column EXCEPT the current one
                for (Cell cell : colCells) {
                    if (cell.getRow() != currentCell.getRow()) {
                        candidateCells.add(cell);
                    }
                }

                System.err.println("EMERGE — running, cell=" + currentCell
                    + " candidates=" + candidateCells.size());

                // 3. Only teleport if there's a valid target cell
                if (!candidateCells.isEmpty()) {
                    Cell targetCell = candidateCells.get(RANDOM.nextInt(candidateCells.size()));
                    Vector2 targetWorldPos = targetCell.getWorldPosition();

                    if (shark.getBody() != null) {
                        shark.getBody().setTransform(
                            targetWorldPos.x / PhysicService.PIXEL_PER_METER,
                            targetWorldPos.y / PhysicService.PIXEL_PER_METER,
                            shark.getBody().getAngle()
                        );

                        shark.getBody().setLinearVelocity(0f, 0f);
                    }

                    if (shark.getPosition() != null) {
                        shark.getPosition().setX(targetWorldPos.x);
                        shark.getPosition().setY(targetWorldPos.y);
                    }
                } else {
                    System.err.println("EMERGE — no candidate cell, staying in place");
                }
            }

            // 4. Trigger emerge visual event & restore collision mask bits
            shark.pushEvent(ZombieVisualEventType.EMERGE);

            if (shark.getBody() != null && shark.getBody().getFixtureList().size > 0) {
                Fixture fixture = shark.getBody().getFixtureList().first();
                Filter filter = fixture.getFilterData();
                filter.maskBits = ContactBits.PROJECTILE | ContactBits.PLANT;
                fixture.setFilterData(filter);
            }

            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }

    public static class CheckNotIntroduced extends LeafTask<Shark> {
        @Override
        public Status execute() {
            if (getObject().isIntroduced()) return Status.FAILED;
            getObject().setIntroduced(true);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }

    public static class WaitTask extends LeafTask<Shark> {
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
        protected Task<Shark> copyTo(Task<Shark> task) {
            ((SharkAI.WaitTask)task).duration = duration;
            return task;
        }
    }
    public static class IntroAction extends LeafTask<Shark> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.INTRO);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }

    public static class CheckDead extends LeafTask<Shark> {
        @Override
        public Status execute() {
            if(getObject().isDead()) {
                return Status.SUCCEEDED;
            }
            return Status.FAILED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }

    public static class CheckStun extends LeafTask<Shark> {
        @Override
        public Status execute() {
            if (getObject().shouldEnterStun()) {
                return Status.SUCCEEDED;
            }
            return Status.FAILED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }

    public static class StunBegin extends LeafTask<Shark> {
        @Override
        public Status execute() {
            getObject().beginStun();
            getObject().pushEvent(ZombieVisualEventType.STUN_START);
            getObject().pushEvent(ZombieVisualEventType.STUN_LOOP);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }

    public static class StunEnd extends LeafTask<Shark> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.STUN_END);
            getObject().endStun();
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }
    public static class CheckAttackChance extends LeafTask<Shark> {
        @Override
        public Status execute() {
            float chance = getObject().getAttackChance();
            if (RANDOM.nextFloat() < chance) return Status.SUCCEEDED;
            return Status.FAILED;
        }

        @Override
        protected Task<Shark> copyTo(Task<Shark> task) {
            return task;
        }
    }

}
