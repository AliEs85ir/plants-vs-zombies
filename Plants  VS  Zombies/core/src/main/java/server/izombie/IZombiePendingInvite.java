package server.izombie;

import com.esotericsoftware.kryonet.Connection;

public final class IZombiePendingInvite {
    private final String inviteId;
    private final Connection from;
    private final Connection to;
    private final String fromUsername;
    private final String toUsername;
    private final String chapterName;
    private final long createdAtMillis;

    public IZombiePendingInvite(
        String inviteId,
        Connection from,
        String fromUsername,
        Connection to,
        String toUsername,
        String chapterName
    ) {
        this.inviteId = inviteId;
        this.from = from;
        this.fromUsername = fromUsername;
        this.to = to;
        this.toUsername = toUsername;
        this.chapterName = chapterName;
        this.createdAtMillis = System.currentTimeMillis();
    }

    public String inviteId() {
        return inviteId;
    }

    public Connection from() {
        return from;
    }

    public Connection to() {
        return to;
    }

    public String fromUsername() {
        return fromUsername;
    }

    public String toUsername() {
        return toUsername;
    }

    public String chapterName() {
        return chapterName;
    }

    public boolean isExpired(long nowMillis, long ttlMillis) {
        return nowMillis - createdAtMillis > ttlMillis;
    }
}
