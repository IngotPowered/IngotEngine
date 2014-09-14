package com.ingotpowered.net;

import com.ingotpowered.IngotPlayer;
import com.ingotpowered.IngotServer;
import com.ingotpowered.api.Ingot;
import com.ingotpowered.api.events.list.PlayerLoginAttemptEvent;
import com.ingotpowered.api.events.list.ServerPingEvent;
import com.ingotpowered.net.codec.AesCodec;
import com.ingotpowered.net.http.HttpHandler;
import com.ingotpowered.net.http.HttpPostRequest;
import com.ingotpowered.net.packets.handshake.Packet0Handshake;
import com.ingotpowered.net.packets.login.Packet0Disconnect;
import com.ingotpowered.net.packets.login.Packet0LoginStart;
import com.ingotpowered.net.packets.login.Packet1Encryption;
import com.ingotpowered.net.packets.ping.Packet0Status;
import com.ingotpowered.net.packets.ping.Packet1Ping;
import com.ingotpowered.net.packets.play.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.*;
import java.util.Random;
import java.util.regex.Pattern;

public class PacketHandler {

    public static final String SESSIONSERVER_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=${username}&serverId=${hash}";

    private static final PublicKey publicKey;
    private static final PrivateKey privateKey;
    private static Random random = new Random(System.currentTimeMillis());

    static {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024);
            KeyPair keypair = gen.genKeyPair();
            publicKey = keypair.getPublic();
            privateKey = keypair.getPrivate();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public IngotPlayer ingotPlayer;
    public int waitingPingId = -1;
    public long pingSentTimestamp = System.currentTimeMillis() + 10000;

    public PacketHandler(IngotPlayer ingotPlayer) {
        this.ingotPlayer = ingotPlayer;
    }

    public void handshake(Packet0Handshake packet) {
        ingotPlayer.hostname = packet.hostname;
        ingotPlayer.port = packet.port;
        if (packet.nextState == 1) {
            ingotPlayer.packetCodec.protoState = ProtoState.PING;
        } else if (packet.nextState == 2) {
            ingotPlayer.packetCodec.protoState = ProtoState.LOGIN;
        } else {
            ingotPlayer.kick("Invalid packet state!");
        }
    }

    // -- BEGIN Server List Ping --
    public void statusRequest(final Packet0Status packet) {
        // IngotServer event
        final ServerPingEvent event = new ServerPingEvent(Ingot.VERSION_NAME, Ingot.PROTOCOL_VERSION, 0, IngotServer.server.config.getMaxPlayers(), IngotServer.server.config.getMOTD());
        IngotServer.server.eventFactory.callEvent(event, new Runnable() {
            public void run() {
                packet.description = event.getMOTD();
                packet.maxPlayers = event.getMaxPlayers();
                packet.onlineCount = event.getOnlinePlayersDisplayCount();
                packet.protocol = event.getProtocol();
                packet.version = event.getVersionName();
                ingotPlayer.channel.pipeline().writeAndFlush(packet);
            }
        });
    }

    public void ping(Packet1Ping packet) {
        ingotPlayer.channel.pipeline().writeAndFlush(packet);
    }

    // -- BEGIN Login Authentication --
    public void loginStart(Packet0LoginStart packet) {
        if (ingotPlayer.username != null) {
            ingotPlayer.channel.pipeline().writeAndFlush(new Packet0Disconnect("Stop repeating yourself"));
            ingotPlayer.channel.close();
            return;
        }
        ingotPlayer.username = packet.name;
        // IngotServer Event
        final PlayerLoginAttemptEvent event = new PlayerLoginAttemptEvent(ingotPlayer.username, ingotPlayer.hostname, (short) ingotPlayer.port);
        IngotServer.server.eventFactory.callEvent(event, new Runnable() {
            public void run() {
                if (event.isCancelled()) {
                    ingotPlayer.kick(event.getDisconnectMessage());
                    return;
                }
                if (IngotServer.server.config.isOnlineMode()) {
                    Packet1Encryption response = new Packet1Encryption();
                    response.publicKey = PacketHandler.publicKey.getEncoded();
                    byte[] verify = new byte[16];
                    random.nextBytes(verify);
                    response.verifyToken = verify;
                    ingotPlayer.channel.pipeline().writeAndFlush(response);
                } else {
                    ingotPlayer.playerAuthenticated();
                }
            }
        });
    }

    public void startEncryption(Packet1Encryption packet) {
        if (ingotPlayer.username == null) {
            ingotPlayer.kick("Identify yourself, stranger!");
            return;
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, PacketHandler.privateKey);
            byte[] iv = cipher.doFinal(packet.publicKey);
            ingotPlayer.channel.pipeline().addFirst(new AesCodec(iv));

            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.reset();
            sha1.update("".getBytes("ISO_8859_1"));
            sha1.update(iv);
            sha1.update(publicKey.getEncoded());
            String hash = URLEncoder.encode(new BigInteger(sha1.digest()).toString(16), "UTF-8");

            new HttpPostRequest(SESSIONSERVER_URL.replace("${username}", ingotPlayer.username).replace("${hash}", hash), new HttpHandler() {
                public void onError(Channel channel, Throwable cause) {
                    channel.close();
                    cause.printStackTrace();
                }
                public void onSuccess(ChannelHandlerContext context, String data) {
                    String[] hackSplit = data.split(Pattern.quote("\""));
                    ingotPlayer.username = hackSplit[7];
                    ingotPlayer.base64Skin = hackSplit[17];
                    String uuid = hackSplit[3];
                    ingotPlayer.uuid = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32);
                    ingotPlayer.playerAuthenticated();
                }
            });
        } catch (Exception ex) {
            ingotPlayer.kick("Handshake error: Could not enable encryption!");
            ex.printStackTrace();
        }
    }

    // -- BEGIN Player Play message --
    public void keepAlive(Packet0KeepAlive packet) {
        if (packet.id == waitingPingId) {
            ingotPlayer.ping = System.currentTimeMillis() - pingSentTimestamp;
            ingotPlayer.packetHandler.waitingPingId = -1;
        }
    }

    public void clientSettings(Packet15ClientSettings packet) {
        ingotPlayer.locale = packet.locale;
        ingotPlayer.viewDistance = Math.min(packet.viewDistance, IngotServer.server.config.getViewDistance());
        ingotPlayer.chatFlags = packet.chatFlags;
        ingotPlayer.showingColors = packet.showChatColors;
        ingotPlayer.displaySkinParts = packet.displaySkinParts;
    }

    public void chat(PacketChat packet) {
        ingotPlayer.playerChat(packet.message);
    }

    public void pluginMessage(PacketPluginMessage packet) {

    }

    public void positionUpdate(Packet4Position packet) {
        ingotPlayer.updatePositionAndOrientation(packet.x, packet.feetY, packet.z, ingotPlayer.yaw, ingotPlayer.pitch);
    }

    public void positionAndOrientationUpdate(PacketPlayerPosLook packet) {
        ingotPlayer.updatePositionAndOrientation(packet.x, packet.feetY, packet.z, packet.yaw, packet.pitch);
    }

    public void heldItemChange(Packet9HeldItem packet) {

    }

    public void playerLook(Packet5PlayerLook packet) {
        ingotPlayer.groundStateChange(packet.onGround);
        ingotPlayer.yaw = packet.yaw;
        ingotPlayer.pitch = packet.pitch;
    }

    public void groundStatus(Packet3GroundStatus packet) {
        ingotPlayer.groundStateChange(packet.onGround);
    }

    public void entityAction(Packet11EntityAction packet) {
        switch (packet.actionId) {
            case 0:
                ingotPlayer.crouched = true; break;
            case 1:
                ingotPlayer.crouched = false; break;
            case 2:
                /* Leave Bed */ break;
            case 3:
                ingotPlayer.sprinting = true; break;
            case 4:
                ingotPlayer.sprinting = false; break;
            case 5:
                /* Jump w/ horse */ break;
            case 6:
                /* Open inventory */ break;
            default:
                ingotPlayer.kick("Unknown EntityAction " + (int) packet.actionId); break;
        }
    }

    public void playerAnimation(Packet10Animation packet) {
        ingotPlayer.sendMessage("You clicked something doe!");
    }
}
