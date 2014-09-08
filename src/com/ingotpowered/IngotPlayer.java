package com.ingotpowered;

import com.ingotpowered.api.Player;
import com.ingotpowered.api.Position;
import com.ingotpowered.api.definitions.Difficulty;
import com.ingotpowered.api.definitions.Dimension;
import com.ingotpowered.api.definitions.GameMode;
import com.ingotpowered.api.definitions.LevelType;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.ProtoState;
import com.ingotpowered.net.codec.PacketCodec;
import com.ingotpowered.net.packets.login.Packet0Disconnect;
import com.ingotpowered.net.packets.login.Packet2LoginSuccess;
import com.ingotpowered.net.packets.play.*;
import io.netty.channel.socket.SocketChannel;

public class IngotPlayer implements Player {

    public static final String JSON_CHAT_MESSAGE_BASE = "{\"text\":\"${message}\"}";

    public SocketChannel channel;
    public PacketHandler packetHandler;
    public PacketCodec packetCodec;
    public String uuid;
    public String username;
    public String base64Skin;
    public Position compassSpawnPosition = new Position(0, 0, 0);

    public IngotPlayer(SocketChannel channel) {
        this.channel = channel;
        this.packetHandler = new PacketHandler(this);
        this.packetCodec = new PacketCodec(this.packetHandler);
    }

    public void playerAuthenticated() {
        synchronized (IngotServer.server.playerMap) {
            IngotServer.server.playerMap.put(username, this);
        }
        Packet2LoginSuccess response = new Packet2LoginSuccess();
        response.username = username;
        response.uuid = uuid;
        System.out.println("UUID: " + uuid);
        channel.pipeline().write(response);
        packetCodec.protoState = ProtoState.PLAY;
        channel.write(new Packet1JoinGame(1, GameMode.SURVIVAL, Dimension.OVERWORLD, Difficulty.EASY, 80, LevelType.DEFAULT, true));
        PacketPluginMessage serverType = new PacketPluginMessage();
        serverType.channel = "MC|Brand";
        serverType.message = new byte[] { 1, 3, 3, 7 };
        channel.pipeline().writeAndFlush(serverType);
        System.out.println(username + " connected to the server");
    }

    public void playerDisconnected() {
        synchronized (IngotServer.server.playerMap) {
            IngotServer.server.playerMap.remove(username);
        }
        System.out.println(username + " disconnected from the server");
    }

    public void sendMessage(String message) {
        sendJSONMessage(JSON_CHAT_MESSAGE_BASE.replace("${message}", message));
    }

    public void sendJSONMessage(String json) {

    }

    public void setCompassSpawn(Position compassSpawnPosition) {
        this.compassSpawnPosition = compassSpawnPosition;
        channel.pipeline().writeAndFlush(new Packet5Spawn(compassSpawnPosition));
    }

    public String getUsername() {
        return username;
    }

    public String getUUID() {
        return null;
    }

    public String getBase64EncodedSkin() {
        return base64Skin;
    }

    public void kick() {
        this.kick("You have been kicked from the server!");
    }

    public void kick(String reason) {
        if (packetCodec.protoState == ProtoState.LOGIN) {
            channel.pipeline().writeAndFlush(new Packet0Disconnect(reason));
        } else if (packetCodec.protoState == ProtoState.PLAY) {
            channel.pipeline().writeAndFlush(new Packet64Disconnect(reason));
        }
        channel.close();
    }
}
