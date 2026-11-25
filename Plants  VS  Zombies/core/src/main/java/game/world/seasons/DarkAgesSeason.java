package game.world.seasons;

import app.enums.SeasonType;
import app.view.hud.notification.GameplayNotifications;
import game.entity.obstacle.Obstacle;
import game.entity.zombie.Zombie;
import game.entity.zombie.ability.InfernoStormAbility;
import game.service.season.SeasonService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.celltypes.GrassCellType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DarkAgesSeason extends Season {

    private final int RANDOM_TOMB_COUNT = 3;
    private final int RANDOM_NECROMANCY_COUNT = 5;


    public DarkAgesSeason() {
        seasonName = "Dark Ages 🏰";
    }


    @Override
    public String getGrassDisplay() {
        return Color.GREEN + "▓" + Color.RESET;
    }

    @Override
    public String getWaterDisplay() {
        return "";
    }

    @Override
    public void setupMap(Grid grid) {
        for (Cell cell : grid.getAllCells()) {
            cell.setCellType(new GrassCellType());
        }
    }


    @Override
    public void onWaveStart(GameWorld world, int waveNumber) {
        GameplayNotifications.showSeasonHazard("Necromancy!");
        game.audio.Sfx.darkAgesWave(waveNumber);
        Grid grid = world.getGrid();
        for(Obstacle obstacle : world.getObstacles()) {
            obstacle.onWaveStart(world, waveNumber);
        }
        attachInfernoStorm(world);
    }

    private void attachInfernoStorm(GameWorld world) {
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
            shuffledZombies.get(i).addAbility(new InfernoStormAbility());
        }
    }

    @Override
    public void updateEnvironment(long currentTick, SeasonService service) {

    }

    @Override
    public SeasonType getSeasonType() {
        return SeasonType.DARK_AGES;
    }
}
