package com.ingotpowered.net.packets.play;

import com.ingotpowered.net.PacketConstants;
import com.ingotpowered.net.PacketHandler;
import com.ingotpowered.net.packets.Packet;
import io.netty.buffer.ByteBuf;

/**
 * Created by Joe on 9/16/2014.
 */
public class Packet11EntityAnimationB extends Packet{    //TODO: Give me a better name!

    public Packet11EntityAnimationB(int entityId, byte animationId){
        this.entityId = entityId;
        this.animationId = animationId;
    }

    public int entityId;
    public byte animationId;

    @Override
    public void write(ByteBuf out) throws Exception {
        PacketConstants.writeVarInt(out,entityId);
        out.writeByte(animationId & 0xFF);
    }

    @Override
    public void handle(PacketHandler handler) {
        //Only outbound
    }



}
