package com.tisawesomeness.betterpreview.format;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;

/**
 * Formatter that does nothing.
 */
public class NopFormatter implements ChatFormatter {

    @Override
    public Component format(String rawInput) {
        return Component.text(rawInput);
    }

    @Override
    public void write(ByteBuf buf) {
        // no-op
    }

}
