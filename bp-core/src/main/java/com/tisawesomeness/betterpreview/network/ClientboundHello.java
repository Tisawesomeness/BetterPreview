package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.SupportInfo;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.key.Key;

import javax.annotation.Nullable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientboundHello implements Packet {
    @Getter private final String serverVersion;
    /** Carries information about whether the client needs to update */
    @Getter private final SupportInfo supportInfo;
    private final @Nullable FormatterUpdate update;

    public static ClientboundHello withUpdate(String serverVersion, SupportInfo supportInfo, FormatterUpdate update) {
        if (!supportInfo.getStatus().supportsPreviews()) {
            throw new IllegalStateException("Can't create preview clientbound hello with unsupported info");
        }
        return new ClientboundHello(serverVersion, supportInfo, update);
    }
    public static ClientboundHello withoutUpdate(String serverVersion, SupportInfo supportInfo) {
        if (supportInfo.getStatus().supportsPreviews()) {
            throw new IllegalStateException("Can't create non-preview clientbound hello with supported info");
        }
        return new ClientboundHello(serverVersion, supportInfo, null);
    }

    public ClientboundHello(ByteBuf buf) {
        serverVersion = ByteBufs.readString(buf, BetterPreview.MAX_VERSION_LENGTH);
        supportInfo = new SupportInfo(buf);
        update = supportInfo.getStatus().supportsPreviews() ? new FormatterUpdate(buf) : null;
    }

    @Override
    public void write(ByteBuf buf) {
        ByteBufs.writeString(buf, serverVersion, BetterPreview.MAX_VERSION_LENGTH);
        supportInfo.write(buf);
        if (supportInfo.getStatus().supportsPreviews()) {
            assert update != null;
            update.write(buf);
        }
    }

    /**
     * Gets the initial formatter to use on join.
     * @return the formatter update
     * @throws IllegalStateException if the client is outdated
     */
    public FormatterUpdate getUpdate() {
        if (update == null) {
            throw new IllegalStateException("No formatter update available");
        }
        return update;
    }

    @Override
    public Key getChannel() {
        return BetterPreview.HELLO_CHANNEL;
    }
}
