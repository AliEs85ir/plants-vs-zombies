package game.world.minigame.beghouled;

import java.util.List;

public final class BeghouledScorer {
    public static final int SUN_PER_PACK = 50;

    private BeghouledScorer() {}

    public static BeghouledScore score(List<BeghouledMatch> matches, boolean cascade) {
        if (matches == null || matches.isEmpty()) {
            return new BeghouledScore(0, 0);
        }
        int packs = 0;
        int combos = 0;
        for (BeghouledMatch match : matches) {
            if (match == null || match.length() < 3) {
                continue;
            }
            int groupPacks = 1 + (match.length() - 3);
            if (cascade) {
                groupPacks += 1;
            }
            packs += groupPacks;
            combos += 1;
        }
        return new BeghouledScore(packs, combos);
    }
}
