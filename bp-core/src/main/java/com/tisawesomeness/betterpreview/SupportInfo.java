package com.tisawesomeness.betterpreview;

import com.tisawesomeness.betterpreview.network.ByteBufs;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SupportInfo {
    /** Whether the client is up-to-date, outdated, or has limited support */
    @Getter private final SupportStatus status;
    /** The minimum version the client should be on */
    @Getter private final String requestedVersion;
    private final @Nullable String message;

    public static SupportInfo supported(String requestedVersion) {
        return new SupportInfo(SupportStatus.FULL_SUPPORT, requestedVersion, null);
    }
    public static SupportInfo unsupported(SupportStatus status, String requestedVersion, @Nullable String message) {
        if (status == SupportStatus.FULL_SUPPORT) {
            throw new IllegalArgumentException("Can't create unsupported support info with full support");
        }
        return new SupportInfo(status, requestedVersion, message);
    }

    public SupportInfo(ByteBuf buf) {
        status = ByteBufs.readEnum(buf, SupportStatus.class);
        requestedVersion = ByteBufs.readString(buf, BetterPreview.MAX_VERSION_LENGTH);
        message = status == SupportStatus.FULL_SUPPORT ? null : ByteBufs.readNullableStringAsEmpty(buf);
    }

    public void write(ByteBuf buf) {
        ByteBufs.writeEnum(buf, status);
        ByteBufs.writeString(buf, requestedVersion, BetterPreview.MAX_VERSION_LENGTH);
        if (status != SupportStatus.FULL_SUPPORT) {
            ByteBufs.writeNullableStringAsEmpty(buf, message);
        }
    }

    /**
     * Gets the message to display letting the user know their client is outdated.
     * @return the message, or empty if there is no message
     * @throws IllegalStateException if the version is up-to-date
     */
    public Optional<String> getMessage() {
        if (status == SupportStatus.FULL_SUPPORT) {
            throw new IllegalStateException("Up-to-date version info does not have a message");
        }
        return Optional.ofNullable(message);
    }
}
