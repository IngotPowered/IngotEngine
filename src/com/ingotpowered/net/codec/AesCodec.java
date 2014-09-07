package com.ingotpowered.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

public class AesCodec extends ByteToMessageCodec<ByteBuf> {

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public AesCodec(byte[] iv) throws Exception {
        SecretKeySpec secret = new SecretKeySpec(iv, "AES");
        encryptCipher = Cipher.getInstance("AES");
        decryptCipher = Cipher.getInstance("AES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, secret);
        decryptCipher.init(Cipher.DECRYPT_MODE, secret);
    }

    protected void encode(ChannelHandlerContext context, ByteBuf in, ByteBuf out) throws Exception {
        byte[] b = new byte[in.readableBytes()];
        in.readBytes(b);
        b = encryptCipher.doFinal(b);
        out.writeBytes(b);
    }

    protected void decode(ChannelHandlerContext context, ByteBuf in, List<Object> out) throws Exception {
        byte[] b = new byte[in.readableBytes()];
        in.readBytes(b);
        b = decryptCipher.doFinal(b);
        ByteBuf buf = context.alloc().buffer(b.length);
        buf.writeBytes(b);
    }
}
