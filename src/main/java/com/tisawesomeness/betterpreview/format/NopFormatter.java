package com.tisawesomeness.betterpreview.format;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

/**
 * Formatter that does nothing.
 */
public class NopFormatter implements ChatFormatter {

    @Override
    public Text format(String rawInput) {
        return Text.literal(rawInput);
    }

    @Override
    public void write(PacketByteBuf buf) {
        // no-op
    }

}
