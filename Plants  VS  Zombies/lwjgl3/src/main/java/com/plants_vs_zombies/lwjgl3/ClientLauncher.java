package com.plants_vs_zombies.lwjgl3;

import app.service.AppService;
import server.EmbeddedServer;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import plants_vs_zombies.PlantsVSZombies;

public class ClientLauncher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) {
            return;
        }

        if (EmbeddedServer.shouldEmbed()) {
            Thread starter = new Thread(EmbeddedServer::startIfNeeded, "embedded-server-start");
            starter.setDaemon(true);
            starter.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                AppService.syncGameDataIfDirty();
            } catch (RuntimeException ignored) {
            }

            if (AppService.client() != null) {
                AppService.client().close();
            }
            EmbeddedServer.stopIfOwned();
        }, "game-shutdown-hook"));
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(
            new PlantsVSZombies(),
            getDefaultConfiguration()
        );
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration =
            new Lwjgl3ApplicationConfiguration();

        configuration.setTitle("Plants VS Zombies");
        configuration.useVsync(true);

        configuration.setForegroundFPS(
            Lwjgl3ApplicationConfiguration
                .getDisplayMode()
                .refreshRate + 1
        );

        configuration.setWindowedMode(640, 480);

        configuration.setWindowIcon(
            "logo/app_icon_128.png",
            "logo/app_icon_64.png",
            "logo/app_icon_32.png",
            "logo/app_icon_16.png"
        );

        return configuration;
    }
}
