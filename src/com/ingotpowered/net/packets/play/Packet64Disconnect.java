package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet64Disconnect extends Packet {

    private static final String DISCONNECT_STRING = "{\"text\":\"${message}\"}";

    public String message;

    public Packet64Disconnect(String data) {
        this.message = data;
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 64);
        PacketConstants.writeString(out, DISCONNECT_STRING.replace("${message}", message));
    }

    public void handle(PacketHandler handler) { }
}

