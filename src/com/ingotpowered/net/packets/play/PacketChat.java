package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class PacketChat extends Packet {

    public String message;
    public byte position = 0;

    public PacketChat() { }

    public PacketChat(String message) {
        this.message = message;
    }

    public void read(ByteBuf in) throws Exception {
        message = PacketConstants.readString(in);
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 2);
        PacketConstants.writeString(out, message);
        out.writeByte(position);
    }

    public void handle(PacketHandler handler) {
        handler.chat(this);
    }
}
