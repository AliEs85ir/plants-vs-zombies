package app.model.user;

public class StageProgress {
    private boolean unlocked;
    private boolean completed;
    private boolean played;
    private boolean lastResultWin;
    private int playCount;
    private int winCount;
    private int lossCount;
    private long unlockedAtEpochMs;
    private long completedAtEpochMs;
    private boolean firstClearRewardClaimed;

    public StageProgress() {
    }

    public static StageProgress unlockedInitial() {
        StageProgress progress = new StageProgress();
        progress.unlocked = true;
        progress.unlockedAtEpochMs = System.currentTimeMillis();
        return progress;
    }

    public boolean isFirstClearRewardClaimed() {
        return firstClearRewardClaimed;
    }

    public void setFirstClearRewardClaimed(boolean firstClearRewardClaimed) {
        this.firstClearRewardClaimed = firstClearRewardClaimed;
    }

    public void markFirstClearRewardClaimed() {
        this.firstClearRewardClaimed = true;
    }

    public void markBuyWinConverted() {
        winCount++;
        lastResultWin = true;
        completed = true;
        completedAtEpochMs = System.currentTimeMillis();
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public boolean isLastResultWin() {
        return lastResultWin;
    }

    public void setLastResultWin(boolean lastResultWin) {
        this.lastResultWin = lastResultWin;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = Math.max(0, playCount);
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = Math.max(0, winCount);
    }

    public int getLossCount() {
        return lossCount;
    }

    public void setLossCount(int lossCount) {
        this.lossCount = Math.max(0, lossCount);
    }

    public long getUnlockedAtEpochMs() {
        return unlockedAtEpochMs;
    }

    public void setUnlockedAtEpochMs(long unlockedAtEpochMs) {
        this.unlockedAtEpochMs = unlockedAtEpochMs;
    }

    public long getCompletedAtEpochMs() {
        return completedAtEpochMs;
    }

    public void setCompletedAtEpochMs(long completedAtEpochMs) {
        this.completedAtEpochMs = completedAtEpochMs;
    }

    public void markPlayed() {
        played = true;
        playCount++;
    }

    public void markWin() {
        markPlayed();
        winCount++;
        lastResultWin = true;
        completed = true;
        completedAtEpochMs = System.currentTimeMillis();
    }

    public void markLoss() {
        markPlayed();
        lossCount++;
        lastResultWin = false;
    }

    public void unlockNow() {
        if (unlocked) {
            return;
        }
        unlocked = true;
        unlockedAtEpochMs = System.currentTimeMillis();
    }
}
