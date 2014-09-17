package com.ingotpowered;

import com.ingotpowered.api.Config;
import com.ingotpowered.api.Ingot;
import com.ingotpowered.api.commands.CommandRegistry;
import com.ingotpowered.api.events.EventFactory;
import com.ingotpowered.net.NetManager;
import com.ingotpowered.net.packets.Packet;
import com.ingotpowered.net.packets.play.PacketChat;
import com.ingotpowered.world.ChunkThread;
<<<<<<< HEAD
=======
import com.ingotpowered.net.NetManager;
import com.ingotpowered.world.IngotWorldManager;
>>>>>>> upstream/master

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class IngotServer extends Ingot {

    public static IngotServer server = new IngotServer();

    public static void main(String[] args) {
        Ingot.setServer(server);
        server.init();
    }

    public Config config = new Config();
    public ConcurrentHashMap<String, IngotPlayer> playerMap = new ConcurrentHashMap<String, IngotPlayer>();
    public IngotWorldManager worldManager;
    public NetManager netManager = new NetManager(playerMap);
    public ThreadKeepAlive threadKeepAlive = new ThreadKeepAlive();
    public ChunkThread[] chunkThreads = new ChunkThread[Runtime.getRuntime().availableProcessors()];
    public EventFactory eventFactory;
    public PluginLoader pluginLoader = new PluginLoader();
    public ConsoleCommands consoleCommands = new ConsoleCommands();
    public CommandRegistry commandRegistry = new CommandRegistry();

    private void init() {
        config.load();
        netManager.start();
        threadKeepAlive.start();
        for (int i = 0; i < chunkThreads.length; i++) {
            chunkThreads[i] = new ChunkThread();
            chunkThreads[i].start();
        }
        eventFactory = new EventFactory();
        pluginLoader.load();
        consoleCommands.startHandling();
    }

    public void dispatchCommand(String command) {
        consoleCommands.dispatchConsoleCommand(command);
    }

    public void sendGlobalPacket(Packet packet) {
        Iterator<IngotPlayer> iterator = playerMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().channel.pipeline().writeAndFlush(packet);
        }
    }

    public IngotPlayer getPlayer(String name) {
        return playerMap.get(name);
    }

    public void stop() {
        consoleCommands.stopped = true;
        netManager.shutdown();
        threadKeepAlive.interrupt();
        try {
            threadKeepAlive.join();
        } catch (Exception ex) { }
        for (ChunkThread c : chunkThreads) {
            c.interrupt();
        }
        pluginLoader.unload();
        eventFactory.destroyThreadPool();
        System.exit(0);
    }

    public void broadcastMessage(String message) {
        PacketChat packet = new PacketChat(IngotPlayer.JSON_CHAT_MESSAGE_BASE.replace("${message}", message));
        sendGlobalPacket(packet);
    }

    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }

    public Config getConfig() {
        return config;
    }

    public EventFactory getEventFactory() {
        return eventFactory;
    }

    public IngotWorldManager getWorldManager() {
        return worldManager;
    }
}
