package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class PacketPluginMessage extends Packet {

    public String channel;
    public byte[] message;

    public PacketPluginMessage() { }

    public PacketPluginMessage(String channel, byte[] message) {
        this.channel = channel;
        this.message = message;
    }

    public void read(ByteBuf in) throws Exception {
        channel = PacketConstants.readString(in);
        message = new byte[in.readableBytes()];
        in.readBytes(message);
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 63);
        PacketConstants.writeString(out, channel);
        out.writeBytes(message);
    }

    public void handle(PacketHandler handler) {
        handler.pluginMessage(this);
    }
}
