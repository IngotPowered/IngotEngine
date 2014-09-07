package com.ingotpowered.net.packets.handshake;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet0Handshake extends Packet {

    public int protocolVersion;
    public String hostname;
    public int port;
    public int nextState;

    public void read(ByteBuf in) throws Exception {
        protocolVersion = PacketConstants.readVarInt(in);
        hostname = PacketConstants.readString(in);
        port = in.readUnsignedShort();
        nextState = PacketConstants.readVarInt(in);
    }

    public void handle(PacketHandler handler) {
        handler.handshake(this);
    }
}
