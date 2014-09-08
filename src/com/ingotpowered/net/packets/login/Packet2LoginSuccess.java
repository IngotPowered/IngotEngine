package com.ingotpowered.net.packets.login;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet2LoginSuccess extends Packet {

    public String username;
    public String uuid;

    public void write(ByteBuf out) throws Exception {
        if (username == null || uuid == null) {
            System.out.println("FUCK");
        }
        PacketConstants.writeVarInt(out, 2);
        PacketConstants.writeString(out, uuid);
        PacketConstants.writeString(out, username);
    }

    public void handle(PacketHandler handler) { }
}
