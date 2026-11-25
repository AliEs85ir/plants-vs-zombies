package server.izombie;

import com.esotericsoftware.kryonet.Connection;
import game.world.minigame.izombie.IZombieSide;
import server.network.Packets;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class IZombieMatch {
    private final String matchId;
    private final String chapterName;
    private final long layoutSeed;
    private final Connection host;
    private final Connection guest;
    private final String hostUsername;
    private final String guestUsername;
    private final IZombieSide hostSide;
    private final Set<String> occupiedCells = ConcurrentHashMap.newKeySet();
    private final ConcurrentLinkedQueue<Object> outbound = new ConcurrentLinkedQueue<>();
    private volatile boolean ended;

    public IZombieMatch(
        String matchId,
        String chapterName,
        long layoutSeed,
        Connection host,
        String hostUsername,
        Connection guest,
        String guestUsername,
        IZombieSide hostSide
    ) {
        this.matchId = matchId;
        this.chapterName = chapterName;
        this.layoutSeed = layoutSeed;
        this.host = host;
        this.hostUsername = hostUsername;
        this.guest = guest;
        this.guestUsername = guestUsername;
        this.hostSide = hostSide != null ? hostSide : IZombieSide.ZOMBIES;
    }

    public String matchId() {
        return matchId;
    }

    public String chapterName() {
        return chapterName;
    }

    public long layoutSeed() {
        return layoutSeed;
    }

    public Connection host() {
        return host;
    }

    public Connection guest() {
        return guest;
    }

    public String hostUsername() {
        return hostUsername;
    }

    public String guestUsername() {
        return guestUsername;
    }

    public IZombieSide hostSide() {
        return hostSide;
    }

    public IZombieSide sideOf(Connection connection) {
        if (connection == host) {
            return hostSide;
        }
        if (connection == guest) {
            return hostSide.opponent();
        }
        return null;
    }

    public boolean contains(Connection connection) {
        return connection == host || connection == guest;
    }

    public Connection opponentOf(Connection connection) {
        if (connection == host) {
            return guest;
        }
        if (connection == guest) {
            return host;
        }
        return null;
    }

    public String usernameOf(Connection connection) {
        if (connection == host) {
            return hostUsername;
        }
        if (connection == guest) {
            return guestUsername;
        }
        return null;
    }

    public boolean tryOccupy(int row, int col) {
        return occupiedCells.add(row + "," + col);
    }

    public void enqueue(Object event) {
        if (!ended && event != null) {
            outbound.add(event);
        }
    }

    public Object pollOutbound() {
        return outbound.poll();
    }

    public boolean isEnded() {
        return ended;
    }

    public void markEnded() {
        ended = true;
        outbound.clear();
    }
}
