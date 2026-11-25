package server.network.client;

import app.controller.game.izombie.IZombieNetworkListener;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import server.network.NetworkConfig;
import server.network.NetworkHandler;
import server.network.Packets;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class GameClient implements AutoCloseable{
    private final Client client = new Client(
        NetworkConfig.WRITE_BUFFER_SIZE,
        NetworkConfig.OBJECT_BUFFER_SIZE
    );
    private final AtomicBoolean connecting = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "net-client");
        t.setDaemon(true);
        return t;
    });
    private volatile boolean connected = false;

    private Consumer<Packets.RegisterResponse> registerCallback;
    private Consumer<Packets.LoginResponse> loginCallback;
    private Consumer<Packets.LoginResponse> restoreCallback;
    private Consumer<Packets.ProfileResponse> profileCallback;
    private Consumer<Packets.ForgotPasswordResponse> forgotCallback;
    private Consumer<Packets.ResetPasswordResponse> resetCallback;
    private Consumer<Packets.ProfileResponse> updateCallback;
    private Consumer<Packets.ProfileResponse> syncCallback;
    private Consumer<Packets.LogoutResponse> logoutCallback;
    private Consumer<Packets.LeaderboardResponse> leaderboardCallback;
    private volatile IZombieNetworkListener izombieListener;

    public GameClient() {
        NetworkHandler.register(client);
        client.addListener(new ClientListener(this));
    }

    public void sendRegister(Packets.RegisterRequest req, Consumer<Packets.RegisterResponse> cb) {
        registerCallback = cb;
        sendGuarded(req, () -> cb.accept(Packets.RegisterResponse.fail("Could not connect to server.")));
    }

    public void sendLogin(Packets.LoginRequest req, Consumer<Packets.LoginResponse> cb) {
        loginCallback = cb;
        sendGuarded(req, () -> cb.accept(Packets.LoginResponse.fail("Could not connect to server.")));
    }

    public void sendRestore(Packets.SessionRestoreRequest req, Consumer<Packets.LoginResponse> cb) {
        restoreCallback = cb;
        sendGuarded(req, () -> cb.accept(Packets.LoginResponse.fail("Could not connect to server.")));
    }

    public void sendProfile(Packets.ProfileRequest req, Consumer<Packets.ProfileResponse> cb) {
        profileCallback = cb;
        sendGuarded(req, () -> cb.accept(Packets.ProfileResponse.fail("Could not connect to server.")));
    }

    public void sendForgot(Packets.ForgotPasswordRequest request, Consumer<Packets.ForgotPasswordResponse> cb) {
        forgotCallback = cb;
        sendGuarded(request, () -> {
            Packets.ForgotPasswordResponse fail = new Packets.ForgotPasswordResponse();
            fail.success = false;
            fail.message = "Could not connect to server.";
            cb.accept(fail);
        });
    }

    public void sendReset(Packets.ResetPasswordRequest request, Consumer<Packets.ResetPasswordResponse> cb) {
        resetCallback = cb;
        sendGuarded(request, () -> {
            Packets.ResetPasswordResponse fail = new Packets.ResetPasswordResponse();
            fail.success = false;
            fail.message = "Could not connect to server.";
            cb.accept(fail);
        });
    }

    public void sendProfileUpdate(Packets.ProfileUpdateRequest request, Consumer<Packets.ProfileResponse> cb) {
        updateCallback = cb;
        sendGuarded(request, () -> cb.accept(Packets.ProfileResponse.fail("Could not connect to server.")));
    }

    public void sendSync(Packets.GameDataSyncRequest req, Consumer<Packets.ProfileResponse> cb) {
        syncCallback = cb;
        sendGuarded(req, () -> cb.accept(Packets.ProfileResponse.fail("Could not connect to server.")));
    }

    public void sendLogout(Consumer<Packets.LogoutResponse> cb) {
        logoutCallback = cb;
        sendGuarded(new Packets.LogoutRequest(), () -> cb.accept(Packets.LogoutResponse.ok()));
    }

    public void sendLeaderboard(Consumer<Packets.LeaderboardResponse> cb) {
        leaderboardCallback = cb;
        sendGuarded(
            new Packets.LeaderboardRequest(),
            () -> cb.accept(Packets.LeaderboardResponse.fail("Could not connect to server."))
        );
    }

    public void setIZombieListener(IZombieNetworkListener listener) {
        this.izombieListener = listener;
    }

    public void sendIZombie(Object packet) {
        sendGuarded(packet, () -> {});
    }

    public void sendIZombie(Object packet, Runnable onFailure) {
        sendGuarded(packet, onFailure == null ? () -> {} : onFailure);
    }

    private void sendGuarded(Object packet, Runnable onFailure) {
        executor.submit(() -> {
            if (!ensureConnected()) {
                toMainThread(onFailure);
                return;
            }
            try {
                client.sendTCP(packet);
            } catch (Exception e) {
                toMainThread(onFailure);
            }
        });
    }

    public void connect() {
        if (connected || connecting.get()) return;
        executor.submit(this::ensureConnected);
    }

    private synchronized boolean ensureConnected() {
        if (connected && client.isConnected()) {
            return true;
        }
        long waitDeadline = System.currentTimeMillis() + NetworkConfig.CONNECT_TIMEOUT_MS + 1000L;
        while (connecting.get()) {
            if (connected && client.isConnected()) {
                return true;
            }
            if (System.currentTimeMillis() >= waitDeadline) {
                return false;
            }
            try {
                wait(50L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        if (connected && client.isConnected()) {
            return true;
        }
        if (!connecting.compareAndSet(false, true)) {
            return ensureConnected();
        }
        try {
            if (!client.isConnected()) {
                try {
                    client.start();
                } catch (Exception ignored) {
                }
                client.connect(NetworkConfig.CONNECT_TIMEOUT_MS, NetworkConfig.HOST, NetworkConfig.PORT, NetworkConfig.PORT);
            }
            connected = true;
            return true;
        } catch (IOException e) {
            connected = false;
            return false;
        } finally {
            connecting.set(false);
            notifyAll();
        }
    }

    void onDisconnected() {
        connected = false;
        failPendingRequests("Could not connect to server.");
    }

    private synchronized void failPendingRequests(String message) {
        Consumer<Packets.RegisterResponse> register = registerCallback;
        registerCallback = null;
        Consumer<Packets.LoginResponse> login = loginCallback;
        loginCallback = null;
        Consumer<Packets.LoginResponse> restore = restoreCallback;
        restoreCallback = null;
        Consumer<Packets.ProfileResponse> profile = profileCallback;
        profileCallback = null;
        Consumer<Packets.ForgotPasswordResponse> forgot = forgotCallback;
        forgotCallback = null;
        Consumer<Packets.ResetPasswordResponse> reset = resetCallback;
        resetCallback = null;
        Consumer<Packets.ProfileResponse> update = updateCallback;
        updateCallback = null;
        Consumer<Packets.ProfileResponse> sync = syncCallback;
        syncCallback = null;
        Consumer<Packets.LogoutResponse> logout = logoutCallback;
        logoutCallback = null;
        Consumer<Packets.LeaderboardResponse> leaderboard = leaderboardCallback;
        leaderboardCallback = null;

        if (restore != null) {
            if (Gdx.app != null) {
                Gdx.app.error("GameClient", "Pending session restore failed: " + message);
            }
            toMainThread(() -> restore.accept(Packets.LoginResponse.fail(message)));
        }
        if (register != null) {
            toMainThread(() -> register.accept(Packets.RegisterResponse.fail(message)));
        }
        if (login != null) {
            toMainThread(() -> login.accept(Packets.LoginResponse.fail(message)));
        }
        if (profile != null) {
            toMainThread(() -> profile.accept(Packets.ProfileResponse.fail(message)));
        }
        if (forgot != null) {
            toMainThread(() -> {
                Packets.ForgotPasswordResponse fail = new Packets.ForgotPasswordResponse();
                fail.success = false;
                fail.message = message;
                forgot.accept(fail);
            });
        }
        if (reset != null) {
            toMainThread(() -> {
                Packets.ResetPasswordResponse fail = new Packets.ResetPasswordResponse();
                fail.success = false;
                fail.message = message;
                reset.accept(fail);
            });
        }
        if (update != null) {
            toMainThread(() -> update.accept(Packets.ProfileResponse.fail(message)));
        }
        if (sync != null) {
            toMainThread(() -> sync.accept(Packets.ProfileResponse.fail(message)));
        }
        if (logout != null) {
            toMainThread(() -> logout.accept(Packets.LogoutResponse.ok()));
        }
        if (leaderboard != null) {
            toMainThread(() -> leaderboard.accept(Packets.LeaderboardResponse.fail(message)));
        }
    }

    void dispatch(Object packet) {
        if (packet instanceof Packets.RegisterResponse r && registerCallback != null) {
            Consumer<Packets.RegisterResponse> cb = registerCallback;
            registerCallback = null;
            cb.accept(r);
        } else if (packet instanceof Packets.LoginResponse r) {
            if (restoreCallback != null) {
                Consumer<Packets.LoginResponse> cb = restoreCallback;
                restoreCallback = null;
                cb.accept(r);
            } else if (loginCallback != null) {
                Consumer<Packets.LoginResponse> cb = loginCallback;
                loginCallback = null;
                cb.accept(r);
            }
        } else if (packet instanceof Packets.ForgotPasswordResponse r && forgotCallback != null) {
            Consumer<Packets.ForgotPasswordResponse> cb = forgotCallback;
            forgotCallback = null;
            cb.accept(r);
        } else if (packet instanceof Packets.ResetPasswordResponse r && resetCallback != null) {
            Consumer<Packets.ResetPasswordResponse> cb = resetCallback;
            resetCallback = null;
            cb.accept(r);
        } else if (packet instanceof Packets.LogoutResponse r && logoutCallback != null) {
            Consumer<Packets.LogoutResponse> cb = logoutCallback;
            logoutCallback = null;
            cb.accept(r);
        } else if (packet instanceof Packets.LeaderboardResponse r && leaderboardCallback != null) {
            Consumer<Packets.LeaderboardResponse> cb = leaderboardCallback;
            leaderboardCallback = null;
            cb.accept(r);
        } else if (packet instanceof Packets.ProfileResponse r) {
            if (updateCallback != null) {
                Consumer<Packets.ProfileResponse> cb = updateCallback;
                updateCallback = null;
                cb.accept(r);
            } else if (syncCallback != null) {
                Consumer<Packets.ProfileResponse> cb = syncCallback;
                syncCallback = null;
                cb.accept(r);
            } else if (profileCallback != null) {
                Consumer<Packets.ProfileResponse> cb = profileCallback;
                profileCallback = null;
                cb.accept(r);
            }
        } else {
            dispatchIZombie(packet);
        }
    }

    private void dispatchIZombie(Object packet) {
        IZombieNetworkListener listener = izombieListener;
        if (listener == null) {
            return;
        }
        if (packet instanceof Packets.IZombieOnlinePlayersResponse response) {
            listener.onOnlinePlayers(response);
        } else if (packet instanceof Packets.IZombieInviteResult result) {
            listener.onInviteResult(result);
        } else if (packet instanceof Packets.IZombieInviteIncoming incoming) {
            listener.onInviteIncoming(incoming);
        } else if (packet instanceof Packets.IZombieQueueStatus status) {
            listener.onQueueStatus(status);
        } else if (packet instanceof Packets.IZombieMatchFound found) {
            listener.onMatchFound(found);
        } else if (packet instanceof Packets.IZombiePlaceZombieEvent event) {
            listener.onPlaceZombie(event);
        } else if (packet instanceof Packets.IZombiePlacePlantEvent event) {
            listener.onPlacePlant(event);
        } else if (packet instanceof Packets.IZombieChatEvent event) {
            listener.onChat(event);
        } else if (packet instanceof Packets.IZombieMatchEnded ended) {
            listener.onMatchEnded(ended);
        }
    }

    private static void toMainThread(Runnable r) {
        if (Gdx.app != null) {
            Gdx.app.postRunnable(r);
        } else {
            r.run();
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
        try {
            client.stop();
        } catch (Exception ignored) {
        }
    }
}
