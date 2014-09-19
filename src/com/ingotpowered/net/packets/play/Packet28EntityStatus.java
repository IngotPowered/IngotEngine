package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

public class Packet28EntityStatus extends Packet {

    public Packet28EntityStatus(int id, byte status){
        this.id = id;
        this.status = status;
    }

    public int id;
    public byte status;

    @Override
    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out,28);
        PacketConstants.writeVarInt(out,id);
        out.writeByte(status);
    }

    @Override
    public void handle(PacketHandler handler) {

    }
}
