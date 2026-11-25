package server.izombie;

import com.esotericsoftware.kryonet.Connection;

public final class IZombieQueueEntry {
    private final Connection connection;
    private final String username;
    private final String chapterName;

    public IZombieQueueEntry(Connection connection, String username, String chapterName) {
        this.connection = connection;
        this.username = username;
        this.chapterName = chapterName;
    }

    public Connection connection() {
        return connection;
    }

    public String username() {
        return username;
    }

    public String chapterName() {
        return chapterName;
    }
}
