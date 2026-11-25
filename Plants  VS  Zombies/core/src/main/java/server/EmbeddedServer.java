package server;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class EmbeddedServer {
    public static final String PROPERTY = "pvz.embedServer";

    private static final AtomicReference<GameServer> INSTANCE = new AtomicReference<>();
    private static final AtomicBoolean OWNED = new AtomicBoolean(false);
    private static final AtomicReference<String> FAILURE = new AtomicReference<>();

    private EmbeddedServer() {
    }

    public static boolean shouldEmbed() {
        return Boolean.parseBoolean(System.getProperty(PROPERTY, "false"));
    }

    public static boolean startIfNeeded() {
        if (!shouldEmbed()) {
            return true;
        }
        GameServer existing = INSTANCE.get();
        if (existing != null && existing.isReady()) {
            return true;
        }
        try {
            GameServer server = new GameServer();
            server.start();
            INSTANCE.set(server);
            OWNED.set(true);
            FAILURE.set(null);
            return server.isReady();
        } catch (IOException exception) {
            String message = exception.getMessage() == null ? "Could not start server." : exception.getMessage();
            if (message.contains("bind") || message.contains("Address already in use")) {
                FAILURE.set(null);
                OWNED.set(false);
                return true;
            }
            FAILURE.set(message);
            return false;
        }
    }

    public static boolean isReady() {
        GameServer server = INSTANCE.get();
        return (server != null && server.isReady()) || (!shouldEmbed()) || (!OWNED.get() && FAILURE.get() == null);
    }

    public static String failureMessage() {
        return FAILURE.get();
    }

    public static void stopIfOwned() {
        if (!OWNED.getAndSet(false)) {
            return;
        }
        GameServer server = INSTANCE.getAndSet(null);
        if (server != null) {
            server.stop();
        }
    }
}
