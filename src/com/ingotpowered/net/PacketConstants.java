package com.ingotpowered.net;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;

public class PacketConstants {

    public static int readVarInt(ByteBuf buf) {
        int value = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = buf.readByte();
            value |= (in & 0x7F) << (bytes++ * 7);
            if (bytes > 32) {
                throw new IllegalArgumentException("VarInt is too long: " + bytes);
            }
            if ((in & 0x80) == 0x80) {
                continue;
            }
            break;
        }
        return value;
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        byte in;
        while (true) {
            in = (byte) (value & 0x7F);
            value >>>= 7;
            if (value != 0) {
                in |= 0x80;
            }
            buf.writeByte(in);
            if (value != 0) {
                continue;
            }
            break;
        }
    }

    public static String readString(ByteBuf buffer) {
        byte[] bytes = new byte[readVarInt(buffer)];
        buffer.readBytes(bytes);
        return new String(bytes, CharsetUtil.UTF_8);
    }

    public static void writeString(ByteBuf buffer, String string) {
        byte[] bytes = string.getBytes(CharsetUtil.UTF_8);
        writeVarInt(buffer, bytes.length);
        buffer.writeBytes(bytes);
    }
}
