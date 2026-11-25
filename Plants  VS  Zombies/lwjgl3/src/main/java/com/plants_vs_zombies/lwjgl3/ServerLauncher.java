package com.plants_vs_zombies.lwjgl3;

import server.GameServer;
import server.network.NetworkConfig;

import java.io.IOException;

public class ServerLauncher {
    public static void main(String[] args) {
        GameServer gameServer = new GameServer();
        try {
            gameServer.start();
            System.out.println("[Server] Listening on localhost:" + NetworkConfig.PORT);
        }
        catch(IOException ioException) {
            System.err.println("Failed to start server: " + ioException.getMessage());
            ioException.printStackTrace();
        }

    }
}
