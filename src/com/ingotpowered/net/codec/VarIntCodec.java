package com.ingotpowered.net.codec;

import com.ingotpowered.net.PacketConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class VarIntCodec extends ByteToMessageCodec<ByteBuf> {

    private int length = -1;

    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int bodyLen = msg.readableBytes();
        int headerLen = varintSize(bodyLen);
        out.ensureWritable(headerLen + bodyLen);
        PacketConstants.writeVarInt(out, bodyLen);
        out.writeBytes(msg);
    }

    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        if (length == -1) {
            final byte[] buf = new byte[3];
            for (int i = 0; i < buf.length; i++) {
                if (!in.isReadable()) {
                    in.resetReaderIndex();
                    return;
                }
                buf[i] = in.readByte();
                if (buf[i] >= 0) {
                    length = PacketConstants.readVarInt(Unpooled.wrappedBuffer(buf));
                    in.markReaderIndex();
                    if (in.readableBytes() < length) {
                        in.resetReaderIndex();
                        return;
                    } else {
                        out.add(in.readBytes(length));
                        length = -1;
                        return;
                    }
                }
            }
            throw new CorruptedFrameException( "length wider than 21-bit" );
        } else {
            if (in.readableBytes() < length) {
                return;
            } else {
                out.add(in.readBytes(length));
                length = -1;
            }
        }
    }

    private static int varintSize(int paramInt) {
        if ((paramInt & 0xFFFFFF80) == 0) {
            return 1;
        }
        if ((paramInt & 0xFFFFC000) == 0) {
            return 2;
        }
        if ((paramInt & 0xFFE00000) == 0) {
            return 3;
        }
        if ((paramInt & 0xF0000000) == 0) {
            return 4;
        }
        return 5;
    }
}
