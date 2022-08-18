package com.tisawesomeness.betterpreview.format;

import com.tisawesomeness.betterpreview.network.ByteBufs;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FormatterUpdate {
    /** Either {@link FormatterStatus#OK} or the reason a formatter is disabled */
    @Getter private final FormatterStatus status;
    private final @Nullable ChatFormatter formatter;

    public static FormatterUpdate enabled(@Nullable ChatFormatter formatter) {
        return new FormatterUpdate(FormatterStatus.OK, formatter);
    }
    public static FormatterUpdate disabled(FormatterStatus status) {
        if (status == FormatterStatus.OK) {
            throw new IllegalArgumentException("A disabled formatter update should not have an OK status");
        }
        return new FormatterUpdate(status, null);
    }

    public FormatterUpdate(ByteBuf buf) {
        status = ByteBufs.readEnum(buf, FormatterStatus.class);
        formatter = status == FormatterStatus.OK ? FormatterRegistry.read(buf).orElse(null) : null;
    }

    public void write(ByteBuf buf) {
        ByteBufs.writeEnum(buf, status);
        if (status == FormatterStatus.OK) {
            FormatterRegistry.write(buf, formatter);
        }
    }

    /**
     * Gets the formatter
     * @return the formatter, or empty for no formatter (if status OK) or disabled (if status not OK)
     */
    public Optional<ChatFormatter> getFormatter() {
        return Optional.ofNullable(formatter);
    }
}
