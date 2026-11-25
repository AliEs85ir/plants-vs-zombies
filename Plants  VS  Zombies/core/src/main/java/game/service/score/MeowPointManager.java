package game.service.score;

import java.util.HashMap;
import java.util.Map;

public class MeowPointManager {
    private int totalMeowPoints;
    private final Map<String, Integer> patternScores;

    public MeowPointManager() {
        this.totalMeowPoints = 0;
        this.patternScores = new HashMap<>();
    }

    public void addPoints(String patternName, int points) {
    }

    public int getTotalMeowPoints() {
        return totalMeowPoints;
    }

    public void setTotalMeowPoints(int totalMeowPoints) {
        this.totalMeowPoints = totalMeowPoints;
    }

    public void reset() {
    }
}
