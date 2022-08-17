package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.FormatterRegistry;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Optional;

@AllArgsConstructor
public class ClientboundHello {
    @Getter private final String serverVersion;
    private final @Nullable ChatFormatter formatter;

    public ClientboundHello(ByteBuf buf) {
        serverVersion = ByteBufs.readString(buf);
        formatter = FormatterRegistry.read(buf).orElse(null);
    }

    public void write(ByteBuf buf) {
        ByteBufs.writeString(buf, serverVersion);
        FormatterRegistry.write(buf, formatter);
    }

    public Optional<ChatFormatter> getFormatter() {
        return Optional.ofNullable(formatter);
    }
}
