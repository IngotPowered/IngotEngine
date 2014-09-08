package com.ingotpowered.net.packets;

import com.ingotpowered.net.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;

public abstract class Packet {

    public void read(ByteBuf buf) throws Exception { }

    public void write(ByteBuf out) throws Exception { }

    public abstract void handle(PacketHandler handler);

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName() + "[  ");
        try {
            Field[] fields = this.getClass().getFields();
            for (Field f : fields) {
                sb.append(f.getName() + "=" + f.get(this) + ", ");
            }
            sb.setLength(sb.length() - 2);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        sb.append("  ]");
        return sb.toString();
    }
}
