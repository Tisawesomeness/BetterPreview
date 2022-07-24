package com.tisawesomeness.betterpreview.format;

import net.minecraft.network.PacketByteBuf;

import java.util.*;

/**
 * Keeps a list of all possible formatters.
 */
public class FormatterRegistry {

    private static final Map<Class<? extends ChatFormatter>, Byte> formatterToId = new HashMap<>();
    // index is id
    private static final List<FormatterReader> formatters = new ArrayList<>();

    // limited to 128 formatters, ids 0-127
    // should be way more than enough
    // must keep order when updating to a new version
    static {
        register(NopFormatter.class, buf -> new NopFormatter());
        register(ClassicFormatter.class, ClassicFormatter::new);
    }

    private static void register(Class<? extends ChatFormatter> clazz, FormatterReader reader) {
        assert formatters.size() <= Byte.MAX_VALUE;
        byte id = (byte) formatters.size();
        formatters.add(reader);
        formatterToId.put(clazz, id);
    }

    /**
     * Reads the formatter id, finds the corresponding formatter, and creates the formatter from the buffer.
     * @param buf the buffer
     * @return the formatter, or empty if sent an invalid id
     */
    public static Optional<ChatFormatter> read(PacketByteBuf buf) {
        byte id = buf.readByte();
        assert formatters.size() <= Byte.MAX_VALUE;
        if (id >= formatters.size()) {
            return Optional.empty();
        }
        return Optional.of(formatters.get(id).read(buf));
    }

    /**
     * Writes the formatter id and the formatter config to the buffer.
     * @param buf the buffer
     * @param formatter the formatter
     */
    public static void write(PacketByteBuf buf, ChatFormatter formatter) {
        byte id = formatterToId.get(formatter.getClass());
        buf.writeByte(id);
        formatter.write(buf);
    }

    @FunctionalInterface
    private interface FormatterReader {
        ChatFormatter read(PacketByteBuf buf);
    }

}
