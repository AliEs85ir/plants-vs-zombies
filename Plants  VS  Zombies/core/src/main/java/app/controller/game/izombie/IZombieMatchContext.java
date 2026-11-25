package app.controller.game.izombie;

import game.world.minigame.izombie.IZombieSide;

public final class IZombieMatchContext {
    private static IZombieMatchContext active;

    private final String matchId;
    private final String chapterName;
    private final String opponentUsername;
    private final long layoutSeed;
    private final boolean host;
    private final IZombieSide localSide;
    private volatile boolean applyingRemotePlacement;

    private IZombieMatchContext(
        String matchId,
        String chapterName,
        String opponentUsername,
        long layoutSeed,
        boolean host,
        IZombieSide localSide
    ) {
        this.matchId = matchId;
        this.chapterName = chapterName;
        this.opponentUsername = opponentUsername;
        this.layoutSeed = layoutSeed;
        this.host = host;
        this.localSide = localSide != null ? localSide : IZombieSide.ZOMBIES;
    }

    public static void activate(
        String matchId,
        String chapterName,
        String opponentUsername,
        long layoutSeed,
        boolean host,
        IZombieSide localSide
    ) {
        active = new IZombieMatchContext(
            matchId,
            chapterName,
            opponentUsername,
            layoutSeed,
            host,
            localSide
        );
    }

    public static void clear() {
        active = null;
    }

    public static boolean isActive() {
        return active != null;
    }

    public static boolean isActiveFor(String chapterName) {
        return active != null && active.chapterName != null && active.chapterName.equals(chapterName);
    }

    public static IZombieMatchContext get() {
        return active;
    }

    public String matchId() {
        return matchId;
    }

    public String chapterName() {
        return chapterName;
    }

    public String opponentUsername() {
        return opponentUsername;
    }

    public long layoutSeed() {
        return layoutSeed;
    }

    public boolean host() {
        return host;
    }

    public IZombieSide localSide() {
        return localSide;
    }

    public boolean isApplyingRemotePlacement() {
        return applyingRemotePlacement;
    }

    public void beginRemotePlacement() {
        applyingRemotePlacement = true;
    }

    public void endRemotePlacement() {
        applyingRemotePlacement = false;
    }

    public void applyRemote(Runnable action) {
        beginRemotePlacement();
        try {
            action.run();
        } finally {
            endRemotePlacement();
        }
    }
}
