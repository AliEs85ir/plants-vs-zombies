package server;

public class UpdateThread implements Runnable {
    private static final long TICK_INTERVAL_MS = 50L;

    private final GameServer gameServer;
    private volatile boolean running = true;

    public UpdateThread(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                gameServer.tickMatches();
                Thread.sleep(TICK_INTERVAL_MS);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception exception) {
                System.err.println("[Server] Update thread error: " + exception.getMessage());
            }
        }
    }
}
