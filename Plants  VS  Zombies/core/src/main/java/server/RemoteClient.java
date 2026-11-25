package server;


import com.esotericsoftware.kryonet.Connection;
import server.network.ClientState;

public class RemoteClient {
    private final Connection connection;
    private ClientState clientState;
    private String username;   // logged-in username, null until authenticated
    private String sessionToken;

    public RemoteClient(Connection connection, ClientState clientState) {
        this.connection = connection;
        this.clientState = clientState;
    }

    public Connection getConnection() { return connection; }
    public ClientState getClientState() { return clientState; }
    public void setClientState(ClientState s) { this.clientState = s; }
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String t) { this.sessionToken = t; }

    public boolean isAuthenticated() { return username != null; }
}
