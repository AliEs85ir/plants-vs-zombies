package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ServerListener extends Listener {
    private final GameServer gameServer;

    public ServerListener(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    @Override
    public void connected(Connection connection) {
        gameServer.addClient(connection);
        System.out.println("[Server] Client connected: " + connection.getRemoteAddressTCP());
    }

    @Override
    public void disconnected(Connection connection) {
        gameServer.removeClient(connection);
        System.out.println("[Server] Client disconnected: " + connection.getRemoteAddressTCP());
    }

    @Override
    public void received(Connection connection, Object object) {
        gameServer.handle(connection, object);
    }
}
