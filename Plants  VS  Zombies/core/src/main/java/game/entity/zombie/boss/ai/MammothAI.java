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
import game.entity.obstacle.Obstacle;
import game.entity.obstacle.ObstacleFactory;
import game.entity.obstacle.ability.BossIceAbility;
import game.entity.obstacle.ability.BossProjectileAbility;
import game.entity.obstacle.ability.GlacierAbility;
import game.entity.plant.Plant;
import game.entity.zombie.boss.boss.Mammoth;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.MapConfig;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.GameplayEffects;
import graphics.general.effects.anchers.WorldAnchor;
import graphics.general.event.ObstacleVisualEventType;
import graphics.general.event.ZombieVisualEventType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MammothAI {
    private static final Random RANDOM = new Random();

    public static BehaviorTree<Mammoth> buildTree(Mammoth boss) {
        Selector<Mammoth> root = new Selector<>();
        Sequence<Mammoth> introSequence = new Sequence<>();
        Sequence<Mammoth> deathSequence = new Sequence<>();
        Sequence<Mammoth> stunSequence = new Sequence<>();
        Sequence<Mammoth> slingshotSequence = new Sequence<>();
        Selector<Mammoth> mainSelector = new Selector<>();
        Sequence<Mammoth> attackSequence = new Sequence<>();
        RandomSelector<Mammoth> attackRandomSelector = new RandomSelector<>();
        RandomSelector<Mammoth> windRandomSelector = new RandomSelector<>();
        Sequence<Mammoth> idleSequence = new Sequence<>();
        Selector<Mammoth> iceFloorSelector = new Selector<>();

        Sequence<Mammoth> iceColumn1 = new Sequence<>();
        Sequence<Mammoth> iceColumn2 = new Sequence<>();
        Sequence<Mammoth> iceColumn3 = new Sequence<>();
        Sequence<Mammoth> iceColumn4 = new Sequence<>();


        Sequence<Mammoth> iceWind1 = new Sequence<>();
        Sequence<Mammoth> iceWind2 = new Sequence<>();
        Sequence<Mammoth> iceWind3 = new Sequence<>();
        Sequence<Mammoth> iceWind4 = new Sequence<>();

        root.addChild(introSequence);
        root.addChild(deathSequence);
        root.addChild(stunSequence);
        root.addChild(mainSelector);


        introSequence.addChild(new CheckNotIntroduced());
        introSequence.addChild(new IntroAction());
        introSequence.addChild(new WaitTask(4f));

        deathSequence.addChild(new CheckDead());
        deathSequence.addChild(new BossDeathTasks.PlayDeathClip<>(ZombieVisualEventType.MAMMOTH_DIE));
        deathSequence.addChild(new WaitTask(4.03f));
        deathSequence.addChild(new BossDeathTasks.StartDeathTalk<>(ZombieVisualEventType.MAMMOTH_TALK));
        deathSequence.addChild(new BossDeathTasks.WaitForOutroDialogue<>());
        deathSequence.addChild(new BossDeathTasks.PlayDeathClip<>(ZombieVisualEventType.MAMMOTH_EXIT));
        deathSequence.addChild(new WaitTask(3.6f));
        deathSequence.addChild(new BossDeathTasks.FinishDeathSequence<>());

        stunSequence.addChild(new CheckStun());
        stunSequence.addChild(new StunBegin());
        stunSequence.addChild(new WaitTask(5f));
        stunSequence.addChild(new StunEnd());
        stunSequence.addChild(new WaitTask(3f));

        mainSelector.addChild(attackSequence);
        mainSelector.addChild(idleSequence);

        idleSequence.addChild(new IdleAction());
        idleSequence.addChild(new WaitTask(4));

        attackSequence.addChild(new CheckAttackChance());
        attackSequence.addChild(attackRandomSelector);
        attackSequence.addChild(new WaitTask(1));

        attackRandomSelector.addChild(iceFloorSelector);
        attackRandomSelector.addChild(windRandomSelector);
        attackRandomSelector.addChild(slingshotSequence);

        iceFloorSelector.addChild(iceColumn1);
        iceFloorSelector.addChild(iceColumn2);
        iceFloorSelector.addChild(iceColumn3);
        iceFloorSelector.addChild(iceColumn4);

        iceColumn1.addChild(new CheckIceColumn(5));
        iceColumn1.addChild(new IceColumnStart(1));
        iceColumn1.addChild(new WaitTask(2));
        iceColumn1.addChild(new IceColumnAction(5));


        iceColumn2.addChild(new CheckIceColumn(4));
        iceColumn2.addChild(new IceColumnStart(2));
        iceColumn2.addChild(new WaitTask(3f));
        iceColumn2.addChild(new IceColumnAction(4));


        iceColumn3.addChild(new CheckIceColumn(3));
        iceColumn3.addChild(new IceColumnStart(3));
        iceColumn3.addChild(new WaitTask(3f));
        iceColumn3.addChild(new IceColumnAction(3));


        iceColumn4.addChild(new CheckIceColumn(2));
        iceColumn4.addChild(new IceColumnStart(4));
        iceColumn4.addChild(new WaitTask(3f));
        iceColumn4.addChild(new IceColumnAction(2));

        windRandomSelector.addChild(iceWind1);
        windRandomSelector.addChild(iceWind2);
        windRandomSelector.addChild(iceWind3);
        windRandomSelector.addChild(iceWind4);

        iceWind1.addChild(new StartWind(1));
        iceWind1.addChild(new WaitTask(1f));
        iceWind1.addChild(new WindAction(1));

        iceWind2.addChild(new StartWind(2));
        iceWind2.addChild(new WaitTask(1f));
        iceWind2.addChild(new WindAction(2));

        iceWind3.addChild(new StartWind(3));
        iceWind3.addChild(new WaitTask(1f));
        iceWind3.addChild(new WindAction(3));

        iceWind4.addChild(new StartWind(4));
        iceWind4.addChild(new WaitTask(1f));
        iceWind4.addChild(new WindAction(4));

        slingshotSequence.addChild(new CheckShot());
        slingshotSequence.addChild(new SlingshotStart());
        slingshotSequence.addChild(new WaitTask(5));
        slingshotSequence.addChild(new SlingshotAction());

        return new BehaviorTree<>(root, boss);
    }

    public static class CheckAttackChance extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            float chance = getObject().getAttackChance();
            if (RANDOM.nextFloat() < chance) return Status.SUCCEEDED;
            return Status.FAILED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }
    public static class CheckShot extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            if(getObject().getWorld().getPlants().isEmpty()) {
                return Status.FAILED;
            }
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }

    public static class SlingshotAction extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            ArrayList<Plant> plants = getObject().getWorld().getPlants();
            if(plants.isEmpty()) {
                return Status.FAILED;
            }
            Plant plant = plants.get(0);
            Grid grid = getObject().getWorld().getGrid();
            Cell cell = grid.getCellAt(plant.getPosition());
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.SLINGSHOT,
                20,
                20,
                cell.getWorldX(),
                cell.getWorldY() + grid.getConfig().getMapHeight(),
                List.of(new BossProjectileAbility(cell, 7f))
            );

            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }
    public static class SlingshotStart extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.MAMMOTH_SLINGSHOT);
            Plant plant = getObject().getWorld().getPlants().get(0);
            Cell cell = getObject().getWorld().getGrid().getCellAt(plant.getPosition());
            BossProjectileAbility.SpawnTargetEffect(cell);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }

    public static class IdleAction extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }
    public static class StartWind extends LeafTask<Mammoth> {
        private int windNumber;

        public StartWind(int windNumber) {
            this.windNumber = windNumber;
        }

        @Override
        public Status execute() {
            switch (windNumber) {
                case 1 -> getObject().pushEvent(ZombieVisualEventType.MAMMOTH_WIND1);
                case 2 -> getObject().pushEvent(ZombieVisualEventType.MAMMOTH_WIND2);
                case 3 -> getObject().pushEvent(ZombieVisualEventType.MAMMOTH_WIND3);
                case 4 -> getObject().pushEvent(ZombieVisualEventType.MAMMOTH_WIND4);
            }
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }
    public static class WindAction extends LeafTask<Mammoth> {
        private int windNumber;

        public WindAction(int windNumber) {
            this.windNumber = windNumber;
            System.err.println("WIND " + windNumber);
        }

        @Override
        public Status execute() {
            Grid grid = getObject().getWorld().getGrid();
            for(Cell cell : grid.getRowCells(windNumber - 1)) {
                for(Plant plant : getObject().getWorld().getPlants()) {
                    if(cell.equals(grid.getCellAt(plant.getPosition()))) {
                        plant.addFreezeLevel();
                        System.err.println("ADDED FREEZE");
                    }
                }
            }
            for(Cell cell : grid.getRowCells(windNumber)) {
                for(Plant plant : getObject().getWorld().getPlants()) {
                    if(cell.equals(grid.getCellAt(plant.getPosition()))) {
                        plant.addFreezeLevel();
                        System.err.println("ADDED FREEZE");
                    }
                }
            }
            MapConfig config = grid.getConfig();
            Vector2 effectPos1 = new Vector2(
                config.getPlaygroundX() + config.getPlaygroundWidth(),
                config.getPlaygroundY() + (config.getRows() - windNumber) * (config.getCellHeight())
                );
            EffectRequest<Object> request = new EffectRequest<>(
                "ice wind",
                null,
                new WorldAnchor(effectPos1),
                EffectLayer.FRONT
            );
            Vector2 effectPos2 = new Vector2(
                config.getPlaygroundX() + config.getPlaygroundWidth(),
                config.getPlaygroundY() + (config.getRows() - windNumber + 1) * (config.getCellHeight())
            );
            EffectRequest<Object> request2 = new EffectRequest<>(
                "ice wind",
                null,
                new WorldAnchor(effectPos2),
                EffectLayer.FRONT
            );

            App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request);
            App.getRealTimeEngine().getGraphicsEngine().getEffectManager().spawn(request2);

            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }
    public static class CheckIceColumn extends LeafTask<Mammoth> {
        private int columnNumber;

        public CheckIceColumn(int columnNumber) {
            this.columnNumber = columnNumber;
        }

        @Override
        public Status execute() {
            Grid grid = getObject().getWorld().getGrid();
            for(Cell cell : grid.getColumnCells(columnNumber)) {
                if(!cell.hasObstacle()) {
                    return Status.SUCCEEDED;
                }
            }
            return Status.FAILED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }
    public static class IceColumnAction extends LeafTask<Mammoth> {
        private int columnNumber;

        public IceColumnAction(int columnNumber) {
            this.columnNumber = columnNumber;
        }

        @Override
        public Status execute() {
            Grid grid = getObject().getWorld().getGrid();
            for (int i = 0; i < grid.getColumnCells(columnNumber).size(); i++) {
                Cell cell = grid.getColumnCells(columnNumber).get(i);
                if(!cell.hasObstacle()) {
                    App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                        GameClock.currentTick,
                        4 * (i + 1),
                        () -> spawnIce(cell)
                    );
                }
            }
            return Status.SUCCEEDED;
        }

        private static void spawnIce(Cell cell) {
            ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.BOSS_ICE,
                cell.getWidth() / 2,
                cell.getHeight() / 2,
                cell.getWorldX(),
                cell.getWorldY(),
                List.of(new BossIceAbility(300, cell))
            );
            PlayScreen.shake(0.35f, 12f);
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }

    public static class StunBegin extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            getObject().beginStun();
            getObject().pushEvent(ZombieVisualEventType.MAMMOTH_REVEAL);
            getObject().pushEvent(ZombieVisualEventType.STUN_LOOP);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }
    public static class IceColumnStart extends LeafTask<Mammoth> {
        private int num;

        public IceColumnStart(int num) {
            this.num = num;
        }

        @Override
        public Status execute() {
            switch (num) {
                case 1 -> getObject().pushEvent(ZombieVisualEventType.MAMMOTH_COLUMN1);
                case 2 -> getObject().pushEvent(ZombieVisualEventType.MAMMOTH_COLUMN2);
                case 3 -> getObject().pushEvent(ZombieVisualEventType.MAMMOTH_COLUMN3);
                case 4 -> getObject().pushEvent(ZombieVisualEventType.MAMMOTH_COLUMN4);
            }
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }
    public static class CheckDead extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            if(getObject().isDead()) {
                return Status.SUCCEEDED;
            }
            return Status.FAILED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }

    public static class CheckStun extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            if (getObject().shouldEnterStun()) {
                return Status.SUCCEEDED;
            }
            return Status.FAILED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }


    public static class CheckNotIntroduced extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            if (getObject().isIntroduced()) return Status.FAILED;
            getObject().setIntroduced(true);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }
    public static class StunEnd extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.MAMMOTH_COVER_UP);
            getObject().pushEvent(ZombieVisualEventType.IDLE);
            App.getCurrentGameEngine().getTaskManager().scheduleRelative(
                GameClock.currentTick,
                10,
                this::spawnGlacier
            );
            return Status.SUCCEEDED;
        }

        private void spawnGlacier() {
            Vector2 position = getObject().getWorld().getMap().getZombieSpawns().get(0);
            Obstacle obstacle =
                ObstacleFactory.getInstance().createRuntimeObstacle(
                ObstacleType.MAMMOTH_GLACIER,
                100,
                3 * (getObject().getWorld().getGrid().getConfig().getCellHeight() / 2),
                position.x,
                position.y,
                List.of(new GlacierAbility(1000))
            );
            obstacle.pushEvent(ObstacleVisualEventType.GLACIER_SPAWN);
            GameplayEffects.spawn("zomboss glacier fog", obstacle.getPosition(), EffectLayer.FRONT);
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }

    public static class IntroAction extends LeafTask<Mammoth> {
        @Override
        public Status execute() {
            getObject().pushEvent(ZombieVisualEventType.INTRO);
            return Status.SUCCEEDED;
        }

        @Override
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            return task;
        }
    }

    public static class WaitTask extends LeafTask<Mammoth> {
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
        protected Task<Mammoth> copyTo(Task<Mammoth> task) {
            ((MammothAI.WaitTask)task).duration = duration;
            return task;
        }
    }
}
