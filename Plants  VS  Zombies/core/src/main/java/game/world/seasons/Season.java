package game.world.seasons;

import app.enums.SeasonType;
import game.entity.obstacle.Obstacle;
import game.service.season.SeasonService;
import game.world.GameWorld;
import game.world.chapter.Chapter;
import game.world.playground.Grid;
import game.world.tide.TideController;

import java.util.ArrayList;
import java.util.List;

public abstract class Season {

    protected String seasonName;
    private ArrayList<Obstacle> seasonObstacles;
    protected List<Chapter> chapters = new ArrayList<>();


    public abstract void updateEnvironment(long currentTick, SeasonService service);
    public abstract void setupMap(Grid grid);
    public abstract SeasonType getSeasonType();
    public List<Chapter> getChapters() {
        return chapters;
    }
    public Chapter getChapter(int number) {
        for (Chapter chapter : chapters) {
            if (chapter.getChapterNumber() == number) {
                return chapter;
            }
        }
        return null;
    }

    public abstract String getGrassDisplay();

    public abstract String getWaterDisplay();

    public String getName() {
        return seasonName;
    }

    public abstract void onWaveStart(GameWorld world, int waveNumber);

    public TideController getTideController() {
        return null;
    }
}
