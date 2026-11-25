package game.world.minigame.beghouled;

public record BeghouledScore(int packs, int combos) {
    public int sun() {
        return packs * BeghouledScorer.SUN_PER_PACK;
    }
}
