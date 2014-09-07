package com.ingotpowered;

import com.ingotpowered.api.Player;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.ProtoState;
import com.ingotpowered.net.codec.PacketCodec;
import com.ingotpowered.net.packets.login.Packet0Disconnect;
import com.ingotpowered.net.packets.login.Packet2LoginSuccess;
import com.ingotpowered.net.packets.login.Packet3Compression;
import com.ingotpowered.net.packets.play.Packet64Disconnect;
import io.netty.channel.socket.SocketChannel;

public class IngotPlayer implements Player {

    public static final String JSON_CHAT_MESSAGE_BASE = "{\"text\":\"${message}\"}";

    public SocketChannel channel;
    public PacketHandler packetHandler;
    public PacketCodec packetCodec;
    public String uuid;
    public String username;
    public String base64Skin;

    public IngotPlayer(SocketChannel channel) {
        this.channel = channel;
        this.packetHandler = new PacketHandler(this);
        this.packetCodec = new PacketCodec(this.packetHandler);
    }

    public void playerAuthenticated() {
        synchronized (IngotServer.server.playerMap) {
            IngotServer.server.playerMap.put(username, this);
        }
        channel.write(new Packet2LoginSuccess());
        channel.writeAndFlush(new Packet3Compression());
        packetCodec.protoState = ProtoState.PLAY;
        System.out.println(username + " connected to the server");
    }

    public void playerDisconnected() {
        synchronized (IngotServer.server.playerMap) {
            IngotServer.server.playerMap.remove(username);
        }
        System.out.println(username + " disconnected from the server");
    }

    public String getUsername() {
        return username;
    }

    public String getUUID() {
        return null;
    }

    public void sendMessage(String message) {
        sendJSONMessage(JSON_CHAT_MESSAGE_BASE.replace("${message}", message));
    }

    public void sendJSONMessage(String json) {

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
