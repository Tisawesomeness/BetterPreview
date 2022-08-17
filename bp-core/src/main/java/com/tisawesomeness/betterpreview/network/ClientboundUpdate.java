package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.FormatterRegistry;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import net.kyori.adventure.key.Key;

import javax.annotation.Nullable;
import java.util.Optional;

@AllArgsConstructor
public class ClientboundUpdate implements Packet {
    private final @Nullable ChatFormatter formatter;

    public ClientboundUpdate(ByteBuf buf) {
        formatter = FormatterRegistry.read(buf).orElse(null);
    }

    @Override
    public void write(ByteBuf buf) {
        FormatterRegistry.write(buf, formatter);
    }

    @Override
    public Key getChannel() {
        return BetterPreview.UPDATE_CHANNEL;
    }

    public Optional<ChatFormatter> getFormatter() {
        return Optional.ofNullable(formatter);
    }
}
