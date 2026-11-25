package server.network.client;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ClientListener extends Listener {
    private final GameClient gameClient;

    public ClientListener(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    @Override
    public void connected(Connection connection) {
        System.out.println("[Client] Connected to server: " + connection.getRemoteAddressTCP());
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("[Client] Disconnected from server");
        gameClient.onDisconnected();
    }

    @Override
    public void received(Connection connection, Object object) {
        Gdx.app.postRunnable(() -> gameClient.dispatch(object));
    }

}
