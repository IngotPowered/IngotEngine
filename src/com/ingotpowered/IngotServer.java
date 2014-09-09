package com.ingotpowered;

import com.ingotpowered.api.Config;
import com.ingotpowered.api.Ingot;
import com.ingotpowered.world.ChunkThread;
import com.ingotpowered.net.NetManager;

import java.util.concurrent.ConcurrentHashMap;

public class IngotServer extends Ingot {

    public static IngotServer server = new IngotServer();

    public static void main(String[] args) {
        Ingot.setServer(server);
        server.init();
    }

    public Config config = new Config();
    public ConcurrentHashMap<String, IngotPlayer> playerMap = new ConcurrentHashMap<String, IngotPlayer>();
    public NetManager netManager = new NetManager(playerMap);
    public ChunkThread[] chunkThreads = new ChunkThread[Runtime.getRuntime().availableProcessors()];
    public ConsoleCommands consoleCommands = new ConsoleCommands();

    private void init() {
        config.load();
        netManager.start();
        for (int i = 0; i < chunkThreads.length; i++) {
            chunkThreads[i] = new ChunkThread();
            chunkThreads[i].start();
        }
        consoleCommands.startHandling();
    }

    public void dispatchCommand(String command) {
        consoleCommands.dispatchConsoleCommand(command);
    }

    public void stop() {
        netManager.shutdown();
        for (ChunkThread c : chunkThreads) {
            c.interrupt();
        }
    }

    public Config getConfig() {
        return config;
    }
}
