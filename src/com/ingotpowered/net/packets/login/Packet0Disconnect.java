package com.ingotpowered.net.packets.login;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet0Disconnect extends Packet {

    private static final String DISCONNECT_STRING = "{\"text\":\"${message}\"}";

    public String message;

    public Packet0Disconnect(String data) {
        this.message = data;
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 0);
        PacketConstants.writeString(out, DISCONNECT_STRING.replace("${message}", message));
    }

    public void handle(PacketHandler handler) { }
}
