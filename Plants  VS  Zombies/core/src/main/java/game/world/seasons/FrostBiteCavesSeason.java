package game.world.seasons;

import app.enums.SeasonType;
import app.model.App;
import app.view.hud.notification.GameplayNotifications;
import com.badlogic.gdx.math.Vector2;
import game.engine.time.GameClock;
import game.entity.plant.Plant;
import game.entity.zombie.Zombie;
import game.entity.zombie.ability.SnowStormAbility;
import game.service.season.SeasonService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.celltypes.GrassCellType;
import game.world.playground.celltypes.IceCellType;
import graphics.general.effects.EffectLayer;
import graphics.general.effects.EffectRequest;
import graphics.general.effects.anchers.WorldAnchor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FrostBiteCavesSeason extends Season{

    private final int RANDOM_ICE_COUNT = 5;
    private final int RANDOM_ZOMBIE_COUNT = 3;
    private boolean spawnedFreezedZombie = false;

    public FrostBiteCavesSeason() {
        seasonName = "Frostbite Caves ❄️";
    }


    @Override
    public String getGrassDisplay() {
        return Color.WHITE + "░" + Color.RESET;
    }

    @Override
    public String getWaterDisplay() {
        return "";
    }

    @Override
    public void onWaveStart(GameWorld world, int waveNumber) {
        GameplayNotifications.showSeasonHazard("Snow Storm!");
        game.audio.Sfx.snowStorm();
        attachSnowStorm(world);
        Grid grid = world.getGrid();
        Random random = new Random();

        int rowsToPick = 1+ random.nextInt(grid.getRowCount() - 1);

        List<Integer> availableRows = new ArrayList<>();
        for (int row = 0; row < grid.getRowCount(); row++) {
            availableRows.add(row);
        }

        Collections.shuffle(availableRows);

        for (int i = 0; i < rowsToPick; i++) {
            EffectRequest<Object> request = getObjectEffectRequest(availableRows, i, grid);
            App.getRealTimeEngine().getGraphicsEngine()
                .getEffectManager().spawn(request);
            applyIceWindEffect(i, world);
        }
    }

    private static EffectRequest<Object> getObjectEffectRequest(List<Integer> availableRows, int i, Grid grid) {
        int row = availableRows.get(i);

        float offsetX = -200f;
        float offsetY = 0f;

        float centerX = grid.getConfig().getPlaygroundX()
            + grid.getConfig().getPlaygroundWidth() / 2f
            + offsetX;

        float centerY = grid.getConfig().getPlaygroundY()
            + (row + 0.5f) * grid.getConfig().getCellHeight()
            + offsetY;

        Vector2 rowCenter = new Vector2(centerX, centerY);
        EffectRequest<Object> request = new EffectRequest<>(
            "ice wind",
            null,
            new WorldAnchor(rowCenter),
            EffectLayer.FRONT
        );
        return request;
    }

    private void attachSnowStorm(GameWorld world) {
        List<Zombie> zombies = world.getZombies();
        if (zombies == null || zombies.isEmpty()) {
            return;
        }
        Random random = new Random();
        int selectedCount = random.nextInt(3) + 1;
        selectedCount = Math.min(selectedCount, zombies.size());
        List<Zombie> shuffledZombies = new ArrayList<>(zombies);
        Collections.shuffle(shuffledZombies, random);
        for (int i = 0; i < selectedCount; i++) {
            shuffledZombies.get(i).addAbility(new SnowStormAbility());
        }
    }

    private void applyIceWindEffect(int rowNumber, GameWorld world) {
        for(Plant plant : world.getPlants()) {
            if(world.getGrid().getEntityLane(plant) == rowNumber) {
                plant.addFreezeLevel();
            }
        }
    }


    @Override
    public void setupMap(Grid grid) {
        for (Cell cell : grid.getAllCells()) {
            cell.setCellType(new GrassCellType());
        }

        placeRandomIces(grid, RANDOM_ICE_COUNT);

    }



    public void placeRandomIces(Grid grid, int count) {
        Random random = new Random();

        int placed = 0;
        while (placed < count) {

            int row = random.nextInt(grid.getRowCount());
            int col = random.nextInt(grid.getColCount());

            Cell cell = grid.getCell(row, col);

            if (cell.getCellType() instanceof IceCellType)
                continue;

            cell.setCellType(new IceCellType());
          //  cell.setObstacle(new IceBlock(cell.getPosition()));

            placed++;
        }
    }

    @Override
    public void updateEnvironment(long currentTick, SeasonService service) {

    }

    @Override
    public SeasonType getSeasonType() {
        return SeasonType.FROSTBITE_CAVES;
    }
}
