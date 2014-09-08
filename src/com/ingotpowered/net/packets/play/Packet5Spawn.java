package com.ingotpowered.net.packets.play;

import com.ingotpowered.api.Position;
import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet5Spawn extends Packet {

    public Position position;

    public Packet5Spawn(Position position) {
        this.position = position;
    }

    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out, 5);
        out.writeLong(Position.toLong(position));
    }

    public void handle(PacketHandler handler) { }
}
