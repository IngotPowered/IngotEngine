package com.ingotpowered;

import com.ingotpowered.api.Orientation;
import com.ingotpowered.api.Player;
import com.ingotpowered.api.Position;
import com.ingotpowered.api.definitions.Difficulty;
import com.ingotpowered.api.definitions.Dimension;
import com.ingotpowered.api.definitions.GameMode;
import com.ingotpowered.api.definitions.LevelType;
import com.ingotpowered.api.entity.EntityAnimation;
import com.ingotpowered.api.entity.Rideable;
import com.ingotpowered.api.events.list.*;
<<<<<<< HEAD
import com.ingotpowered.api.world.World;
=======
import com.ingotpowered.api.world.ChunkPosition;
>>>>>>> 7c14b65633ebe1efba9deda2ff615032cc740b21
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.ProtoState;
import com.ingotpowered.net.codec.PacketCodec;
import com.ingotpowered.net.packets.login.Packet0Disconnect;
import com.ingotpowered.net.packets.login.Packet2LoginSuccess;
import com.ingotpowered.net.packets.play.*;
import com.ingotpowered.world.IngotChunk;
import io.netty.channel.socket.SocketChannel;

import java.nio.charset.Charset;

public class IngotPlayer implements Player {

    public static final String JSON_CHAT_MESSAGE_BASE = "{\"text\":\"${message}\"}";

    public SocketChannel channel;
    public PacketHandler packetHandler;
    public PacketCodec packetCodec;
    public String uuid;
    public String username;
    public String hostname;
    public int port;
    public String base64Skin;
    public Position compassSpawnPosition = new Position(0, 0, 0);
    public String locale = "en_US";
    public int viewDistance = 10;
    public byte chatFlags = 0;
    public boolean showingColors = true;
    public byte displaySkinParts;
    public long ping = 0;
    public boolean onGround = false;
    public double x = 0d;
    public double y = 0d;
    public double z = 0d;
    public float yaw = 0f;
    public float pitch = 0f;
    public boolean crouched = false;
    public boolean sprinting = false;

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
        channel.pipeline().write(response);
        packetCodec.protoState = ProtoState.PLAY;

        IngotChunk testChunk = new IngotChunk(new ChunkPosition(0, 0));
        channel.pipeline().writeAndFlush(new Packet38ChunkBulk(true, 1, 0, 0, (short) ((1 << 16) - 1), testChunk.getChunkData()));

        channel.pipeline().write(new PacketPluginMessage("MC|Brand", "Ingot".getBytes(Charset.forName("UTF-8"))));
        channel.pipeline().write(new Packet5Spawn(new Position(0, 6, 0)));
        channel.pipeline().write(new Packet57ClientAbilities(false, true, true, false, 2F, 2F));
        channel.pipeline().write(new Packet1JoinGame(89, GameMode.SURVIVAL, Dimension.OVERWORLD, Difficulty.EASY, 80, LevelType.DEFAULT, true));
        channel.pipeline().writeAndFlush(new PacketPlayerPosLook(0, 16, 0, 20, 20, (byte) 0)); // We're ready to spawn!

        // IngotServer Event
        final PlayerLoginEvent event = new PlayerLoginEvent(this);
        IngotServer.server.eventFactory.callEvent(event, null);



        // channel.pipeline().write(new Packet38ChunkBulk(false, 0, 0, 0, (short) 0, new byte[Short.MAX_VALUE]));
        // channel.pipeline().write(new Packet38ChunkBulk(false, 0, 1, 0, (short) 0, new byte[Short.MAX_VALUE]));
        // channel.pipeline().write(new Packet38ChunkBulk(false, 0, 1, 1, (short) 0, new byte[] { }));
        // channel.pipeline().writeAndFlush(new Packet38ChunkBulk(false, 0, 0, 1, (short) 0, new byte[] { }));
    }

    public void playerDisconnected() {
        synchronized (IngotServer.server.playerMap) {
            IngotServer.server.playerMap.remove(username);
        }
        System.out.println(username + " disconnected from the server");
    }

    public void groundStateChange(boolean onGround) {
        // IngotServer Event
        PlayerGroundStateEvent event = new PlayerGroundStateEvent(this, this.onGround, onGround);
        IngotServer.server.eventFactory.callEvent(event, null);
        this.onGround = onGround;
    }

    public void updatePositionAndOrientation(final double x, final double y, final double z, final float yaw, final float pitch) {
        final PlayerMoveEvent event = new PlayerMoveEvent(this, new Position(this.x, this.y, this.z), new Orientation(this.yaw, this.pitch), new Position(x, y, z), new Orientation(yaw, pitch));
        IngotServer.server.eventFactory.callEvent(event, new Runnable() {
            public void run() {
                if (event.isCancelled()) {
                    channel.pipeline().writeAndFlush(new PacketPlayerPosLook(IngotPlayer.this.x, IngotPlayer.this.y, IngotPlayer.this.z, IngotPlayer.this.yaw, IngotPlayer.this.pitch, (byte) 0));
                } else {
                    IngotPlayer.this.x = x;
                    IngotPlayer.this.y = y;
                    IngotPlayer.this.z = z;
                    IngotPlayer.this.yaw = yaw;
                    IngotPlayer.this.pitch = pitch;
                }
            }
        });
    }

    public void playerChat(String message) {
        if (message.trim().equals("")) {
            return;
        }
        if (message.startsWith("/")) {
            String[] split = message.split(" ");
            final String command = split[0].substring(1);
            final String[] args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, args.length);
            final PlayerCommandEvent event = new PlayerCommandEvent(this, command, args);
            IngotServer.server.eventFactory.callEvent(event, new Runnable() {
                public void run() {
                    if (event.isCancelled()) {
                        return;
                    }
                    IngotServer.server.commandRegistry.runPlayerCommand(IngotPlayer.this, command, args);
                }
            });
            return;
        }
        final PlayerChatEvent event = new PlayerChatEvent(this, message);
        IngotServer.server.eventFactory.callEvent(event, new Runnable() {
            public void run() {
                if (event.isCancelled()) {
                    return;
                }
                String finalMessage = JSON_CHAT_MESSAGE_BASE.replace("${message}", event.getFormat().replace("${0}", username).replace("${1}", event.getMessage()));
                IngotServer.server.sendGlobalPacket(new PacketChat(finalMessage));
            }
        });
    }

    public void sendMessage(String message) {
        sendJSONMessage(JSON_CHAT_MESSAGE_BASE.replace("${message}", message));
    }

    public void sendJSONMessage(String json) {
        channel.pipeline().writeAndFlush(new PacketChat(json));
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
            // IngotServer event
            final PlayerKickEvent event = new PlayerKickEvent(this, reason);
            IngotServer.server.eventFactory.callEvent(event, new Runnable() {
                public void run() {
                    if (!event.isCancelled()) {
                        channel.pipeline().writeAndFlush(new Packet64Disconnect(event.getKickMessage()));
                        channel.close();
                    }
                }
            });
            return;
        }
        channel.close();
    }

    public void teleport(Position position) {

    }

    public void teleport(Position position, World world) {

    }

    public void teleport(Position position, Orientation orientation) {

    }

    public void teleport(Position position, World world, Orientation orientation) {

    }

    public void mount(Rideable rideable) {

    }

    public void dismount() {

    }

    public void moveRelative(double x, double y, double z) {

    }

    public void setOrientation(Orientation orientation) {

    }

    public void moveRelative(double x, double y, double z, Orientation orientation) {

    }

    public void sendStatus(EntityAnimation animation) {

    }

    public int getId() {
        return -1;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public long getPing() {
        return ping;
    }

    public String getLocale() {
        return locale;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public Position getCompassSpawnPosition() {
        return compassSpawnPosition;
    }

    public boolean isAlive() {
        return true;
    }

    public Position getPosition() {
        return new Position(x, y, z);
    }

    public Orientation getOrientation() {
        return new Orientation(yaw, pitch);
    }
}
