package game.world.seasons;

import app.enums.SeasonType;
import app.view.hud.notification.GameplayNotifications;
import game.entity.zombie.Zombie;
import game.entity.zombie.ability.SandStormAbility;
import game.service.season.SeasonService;
import game.world.GameWorld;
import game.world.playground.Cell;
import game.world.playground.Grid;
import game.world.playground.celltypes.GrassCellType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AncientEgyptSeason extends Season{
    private final Random random = new Random();

    private final int RANDOM_TOMB_COUNT = 3;

    public AncientEgyptSeason() {
        seasonName = "Ancient Egypt 🏜️";
    }

    @Override
    public String getGrassDisplay() {
        return Color.YELLOW + "░" + Color.RESET;
    }

    @Override
    public String getWaterDisplay() {
        return Color.BLUE + "≈" + Color.RESET;
    }

    @Override
    public void updateEnvironment(long currentTick, SeasonService service) {

    }

    @Override
    public void onWaveStart(GameWorld world, int waveNumber) {
        GameplayNotifications.showSeasonHazard("Sand Storm!");
        game.audio.Sfx.egyptSandstorm();
        List<Zombie> zombies = world.getZombies();

        if (zombies == null || zombies.isEmpty()) {
            return;
        }
        int selectedCount = random.nextInt(3) + 1;
        selectedCount = Math.min(selectedCount, zombies.size());
        List<Zombie> shuffledZombies = new ArrayList<>(zombies);
        Collections.shuffle(shuffledZombies, random);

        for (int i = 0; i < selectedCount; i++) {
            Zombie selectedZombie = shuffledZombies.get(i);
            selectedZombie.addAbility(new SandStormAbility());
        }
    }
    @Override
    public void setupMap(Grid grid) {
        for (Cell cell : grid.getAllCells()) {
            cell.setCellType(new GrassCellType());
        }

        placeRandomGraves(grid, RANDOM_TOMB_COUNT);
    }

    private void placeRandomGraves(Grid grid, int count) {
        Random random = new Random();

        int placedGrave = 0;
        while (placedGrave < count) {
            int row = random.nextInt(grid.getRowCount());
            int col = random.nextInt(grid.getColCount());

            Cell cell = grid.getCell(row, col);

            if (cell.getObstacle() != null)
                continue;

            //cell.setObstacle(new TombStone(cell.getPosition()));

            placedGrave++;
        }
    }

    @Override
    public SeasonType getSeasonType() {
        return SeasonType.ANCIENT_EGYPT;
    }


}
