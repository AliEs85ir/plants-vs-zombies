package game.world.tide.ambush;

public final class AmbushSpawnSpec {
    private final String zombieAlias;
    private final int count;
    private final AmbushPlacement placement;

    public AmbushSpawnSpec(String zombieAlias, int count, AmbushPlacement placement) {
        this.zombieAlias = zombieAlias;
        this.count = Math.max(0, count);
        this.placement = placement != null ? placement : new RandomExposedLandPlacement();
    }

    public String getZombieAlias() {
        return zombieAlias;
    }

    public int getCount() {
        return count;
    }

    public AmbushPlacement getPlacement() {
        return placement;
    }
}
