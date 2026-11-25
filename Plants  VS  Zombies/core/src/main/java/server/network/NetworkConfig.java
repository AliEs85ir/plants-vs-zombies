package server.network;

public class NetworkConfig {
    public static final String HOST = "localhost";
    public static final int PORT = 54555;
    public static final int CONNECT_TIMEOUT_MS = 5000;

    
    public static final int WRITE_BUFFER_SIZE = 256 * 1024;
    public static final int OBJECT_BUFFER_SIZE = 128 * 1024;

    private NetworkConfig() {}
}
