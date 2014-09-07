package com.ingotpowered.net.packets.login;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet1Encryption extends Packet {

    public byte[] publicKey;
    public byte[] verifyToken;

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 1);
        PacketConstants.writeString(out, ""); // Empty as of 1.7.x
        PacketConstants.writeVarInt(out, publicKey.length);
        out.writeBytes(publicKey);
        PacketConstants.writeVarInt(out, verifyToken.length);
        out.writeBytes(verifyToken);
    }

    public void read(ByteBuf in) throws Exception {
        publicKey = new byte[PacketConstants.readVarInt(in)];
        in.readBytes(publicKey);
        verifyToken = new byte[PacketConstants.readVarInt(in)];
        in.readBytes(verifyToken);
    }

    public void handle(PacketHandler handler) {
        handler.startEncryption(this);
    }
}
