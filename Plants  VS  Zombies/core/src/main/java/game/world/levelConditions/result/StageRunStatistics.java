package game.world.levelConditions.result;

public final class StageRunStatistics {
    private final int zombiesKilled;
    private final int plantsPlanted;
    private final int sunsCollected;
    private final double playTimeSeconds;

    public StageRunStatistics(
        int zombiesKilled,
        int plantsPlanted,
        int sunsCollected,
        double playTimeSeconds
    ) {
        this.zombiesKilled = Math.max(0, zombiesKilled);
        this.plantsPlanted = Math.max(0, plantsPlanted);
        this.sunsCollected = Math.max(0, sunsCollected);
        this.playTimeSeconds = Math.max(0d, playTimeSeconds);
    }

    public static StageRunStatistics fromWorld(game.world.GameWorld world) {
        if (world == null) {
            return new StageRunStatistics(0, 0, 0, 0d);
        }
        double playTime = 0d;
        if (app.model.App.getCurrentGameEngine() != null
            && app.model.App.getCurrentGameEngine().getGameClock() != null) {
            playTime = app.model.App.getCurrentGameEngine().getGameClock().getCurrentSeconds();
        }
        return new StageRunStatistics(
            world.getTotalDeadZombies(),
            world.getTotalPlantsPlanted(),
            world.getTotalSunsCollected(),
            playTime
        );
    }

    public int getZombiesKilled() {
        return zombiesKilled;
    }

    public int getPlantsPlanted() {
        return plantsPlanted;
    }

    public int getSunsCollected() {
        return sunsCollected;
    }

    public double getPlayTimeSeconds() {
        return playTimeSeconds;
    }

    public String formattedPlayTime() {
        int total = (int) Math.floor(playTimeSeconds);
        int minutes = total / 60;
        int seconds = total % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
