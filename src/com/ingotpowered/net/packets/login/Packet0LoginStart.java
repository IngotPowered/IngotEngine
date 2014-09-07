package com.ingotpowered.net.packets.login;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet0LoginStart extends Packet {

    public String name;

    public void handle(PacketHandler handler) {
        handler.loginStart(this);
    }

    public void read(ByteBuf in) throws Exception {
        this.name = PacketConstants.readString(in);
    }
}
