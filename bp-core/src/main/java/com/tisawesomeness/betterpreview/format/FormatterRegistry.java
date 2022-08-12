package com.tisawesomeness.betterpreview.format;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Keeps a list of all possible formatters.
 */
public class FormatterRegistry {

    private static final Map<Class<? extends ChatFormatter>, Byte> formatterToId = new HashMap<>();
    // index is id
    private static final List<FormatterReader> formatters = new ArrayList<>();

    // limited to 127 formatters, ids 0-126 (1-127 in packet)
    // should be way more than enough
    // must keep order when updating to a new version
    static {
        // id 0 for no formatter
        register(NopFormatter.class, buf -> new NopFormatter());
        register(ClassicFormatter.class, ClassicFormatter::new);
    }

    private static void register(Class<? extends ChatFormatter> clazz, FormatterReader reader) {
        assert formatters.size() <= Byte.MAX_VALUE - 1;
        byte id = (byte) (formatters.size() + 1);
        formatters.add(reader);
        formatterToId.put(clazz, id);
    }

    /**
     * Reads the formatter id, finds the corresponding formatter, and creates the formatter from the buffer.
     * @param buf the buffer
     * @return the formatter, or empty if sent a packet with id 0
     * @throws IllegalArgumentException if the formatter id is invalid
     */
    public static Optional<ChatFormatter> read(ByteBuf buf) {
        byte id = buf.readByte();
        if (id == 0) {
            return Optional.empty();
        }
        int idx = id - 1;
        assert formatters.size() <= Byte.MAX_VALUE - 1;
        if (idx >= formatters.size()) {
            throw new IllegalArgumentException("Invalid formatter id: " + id);
        }
        return Optional.of(formatters.get(idx).read(buf));
    }

    /**
     * Writes the formatter id and the formatter config to the buffer.
     * @param buf the buffer
     * @param formatter the formatter, or null to disable formatting
     */
    public static void write(ByteBuf buf, @Nullable ChatFormatter formatter) {
        if (formatter == null) {
            buf.writeByte(0);
        } else {
            byte id = formatterToId.get(formatter.getClass());
            buf.writeByte(id);
            formatter.write(buf);
        }
    }

    @FunctionalInterface
    private interface FormatterReader {
        ChatFormatter read(ByteBuf buf);
    }

}
