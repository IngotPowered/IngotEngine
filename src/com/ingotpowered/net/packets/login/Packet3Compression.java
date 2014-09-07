package com.ingotpowered.net.packets.login;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet3Compression extends Packet {

    public int compressionLevel = -1;

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 3);
        PacketConstants.writeVarInt(out, compressionLevel);
    }

    public void handle(PacketHandler handler) { }
}
