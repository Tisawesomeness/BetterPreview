package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.key.Key;

@AllArgsConstructor
public class ClientboundUpdate implements Packet {
    @Getter private final FormatterUpdate update;

    public ClientboundUpdate(ByteBuf buf) {
        update = new FormatterUpdate(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        update.write(buf);
    }

    @Override
    public Key getChannel() {
        return BetterPreview.UPDATE_CHANNEL;
    }
}
