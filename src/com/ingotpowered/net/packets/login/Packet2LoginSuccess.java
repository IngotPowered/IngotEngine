package com.ingotpowered.net.packets.login;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet2LoginSuccess extends Packet {

    public String username;
    public String uuid;

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 2);
        PacketConstants.writeString(out, username);
        PacketConstants.writeString(out, uuid);
    }

    public void handle(PacketHandler handler) { }
}
