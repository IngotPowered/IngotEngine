package com.ingotpowered.net;

import com.ingotpowered.IngotPlayer;
import com.ingotpowered.IngotServer;
import com.ingotpowered.net.codec.AesCodec;
import com.ingotpowered.net.http.HttpHandler;
import com.ingotpowered.net.http.HttpPostRequest;
import com.ingotpowered.net.packets.handshake.Packet0Handshake;
import com.ingotpowered.net.packets.login.Packet0Disconnect;
import com.ingotpowered.net.packets.login.Packet0LoginStart;
import com.ingotpowered.net.packets.login.Packet1Encryption;
import com.ingotpowered.net.packets.login.Packet2LoginSuccess;
import com.ingotpowered.net.packets.ping.Packet0Status;
import com.ingotpowered.net.packets.ping.Packet1Ping;
import com.ingotpowered.net.packets.play.PacketChat;
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

    public PacketHandler(IngotPlayer ingotPlayer) {
        this.ingotPlayer = ingotPlayer;
    }

    public void handshake(Packet0Handshake packet) {
        if (packet.nextState == 1) {
            ingotPlayer.packetCodec.protoState = ProtoState.PING;
        } else if (packet.nextState == 2) {
            ingotPlayer.packetCodec.protoState = ProtoState.LOGIN;
        } else {
            ingotPlayer.kick("Invalid packet state!");
        }
    }

    // -- BEGIN Server List Ping --
    public void statusRequest(Packet0Status packet) {
        packet.description = IngotServer.server.config.getMOTD();
        packet.maxPlayers = IngotServer.server.config.getMaxPlayers();
        packet.onlineCount = 1;
        packet.protocol = 47;
        packet.version = "Use 1.8 to connect!";
        ingotPlayer.channel.pipeline().writeAndFlush(packet);
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
        if (IngotServer.server.config.isOnlineMode()) {
            Packet1Encryption response = new Packet1Encryption();
            response.publicKey = PacketHandler.publicKey.getEncoded();
            byte[] verify = new byte[16];
            random.nextBytes(verify);
            response.verifyToken = verify;
            ingotPlayer.channel.pipeline().writeAndFlush(response);
        } else {
            Packet2LoginSuccess response = new Packet2LoginSuccess();
            response.username = ingotPlayer.username;
            ingotPlayer.channel.pipeline().writeAndFlush(response);
            ingotPlayer.playerAuthenticated();
        }
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
                    ingotPlayer.uuid = hackSplit[3];
                    ingotPlayer.base64Skin = hackSplit[17];
                    ingotPlayer.playerAuthenticated();
                }
            });
        } catch (Exception ex) {
            ingotPlayer.kick("Handshake error: Could not enable encryption!");
            ex.printStackTrace();
        }
    }

    // -- BEGIN Player Play message --
    public void chat(PacketChat chat) {

    }
}
