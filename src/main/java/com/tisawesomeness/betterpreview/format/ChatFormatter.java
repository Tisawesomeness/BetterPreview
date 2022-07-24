package com.tisawesomeness.betterpreview.format;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

/**
 * Formats raw input text into a styled chat message.
 * Should contain a constructor that can create a formatter from a packet created in {@link #write(PacketByteBuf)}.
 * Reading and writing is done in FIFO order.
 */
public interface ChatFormatter {

    /**
     * Formats the raw input text into a styled chat message.
     * @param rawInput the text that a player types
     * @return the styled text to display in the chat preview
     */
    Text format(String rawInput);

    /**
     * Writes the formatter config to the buffer.
     * This buffer should create the exact same formatter when sent to the client.
     * @param buf the buffer
     */
    void write(PacketByteBuf buf);

}
