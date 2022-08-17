package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.FormatterRegistry;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.key.Key;

import javax.annotation.Nullable;
import java.util.Optional;

@AllArgsConstructor
public class ClientboundHello implements Packet {
    @Getter private final String serverVersion;
    private final @Nullable ChatFormatter formatter;

    public ClientboundHello(ByteBuf buf) {
        serverVersion = ByteBufs.readString(buf);
        formatter = FormatterRegistry.read(buf).orElse(null);
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufs.writeString(buf, serverVersion);
        FormatterRegistry.write(buf, formatter);
    }

    @Override
    public Key getChannel() {
        return BetterPreview.HELLO_CHANNEL;
    }

    public Optional<ChatFormatter> getFormatter() {
        return Optional.ofNullable(formatter);
    }
}
