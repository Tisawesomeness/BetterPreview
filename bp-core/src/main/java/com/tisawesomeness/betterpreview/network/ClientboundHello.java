package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.key.Key;

@AllArgsConstructor
public class ClientboundHello implements Packet {
    @Getter private final String serverVersion;
    @Getter private final FormatterUpdate update;

    public ClientboundHello(ByteBuf buf) {
        serverVersion = ByteBufs.readString(buf);
        update = new FormatterUpdate(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufs.writeString(buf, serverVersion);
        update.write(buf);
    }

    @Override
    public Key getChannel() {
        return BetterPreview.HELLO_CHANNEL;
    }
}
