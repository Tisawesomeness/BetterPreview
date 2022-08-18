package com.tisawesomeness.betterpreview.format;

import com.tisawesomeness.betterpreview.network.ByteBufs;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Keeps a list of all possible formatters.
 */
public class FormatterRegistry {

    private static final Map<Class<? extends ChatFormatter>, Integer> formatterToId = new HashMap<>();
    // index is id
    private static final List<FormatterReader> formatters = new ArrayList<>();

    // must keep order when updating to a new version
    static {
        // id 0 for no formatter
        register(NopFormatter.class, buf -> new NopFormatter());
        register(ClassicFormatter.class, ClassicFormatter::new);
    }

    private static void register(Class<? extends ChatFormatter> clazz, FormatterReader reader) {
        int id = formatters.size() + 1; // Add 1 to account for 0 id
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
        int id = ByteBufs.readVarInt(buf);
        if (id == 0) {
            return Optional.empty();
        }
        if (id < 0 || formatters.size() < id) {
            throw new IllegalArgumentException("Invalid formatter id: " + id);
        }
        int idx = id - 1;
        return Optional.of(formatters.get(idx).read(buf));
    }

    /**
     * Writes the formatter id and the formatter config to the buffer.
     * @param buf the buffer
     * @param formatter the formatter, or null to disable formatting
     */
    public static void write(ByteBuf buf, @Nullable ChatFormatter formatter) {
        if (formatter == null) {
            ByteBufs.writeVarInt(buf, 0);
        } else {
            int id = formatterToId.get(formatter.getClass());
            ByteBufs.writeVarInt(buf, id);
            formatter.write(buf);
        }
    }

    @FunctionalInterface
    private interface FormatterReader {
        ChatFormatter read(ByteBuf buf);
    }

}
