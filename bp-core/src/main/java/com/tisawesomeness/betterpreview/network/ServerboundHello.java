package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.BetterPreview;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.key.Key;

@AllArgsConstructor
public class ServerboundHello implements Packet {
    @Getter private final String clientVersion;

    public ServerboundHello(ByteBuf buf) {
        clientVersion = ByteBufs.readString(buf, BetterPreview.MAX_VERSION_LENGTH);
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufs.writeString(buf, clientVersion, BetterPreview.MAX_VERSION_LENGTH);
    }

    @Override
    public Key getChannel() {
        return BetterPreview.HELLO_CHANNEL;
    }
}
