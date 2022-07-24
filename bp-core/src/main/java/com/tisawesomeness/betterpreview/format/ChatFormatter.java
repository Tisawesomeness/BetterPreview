package com.tisawesomeness.betterpreview.format;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;

/**
 * Formats raw input text into a styled chat message.
 * Should contain a constructor that can create a formatter from a packet created in {@link #write(ByteBuf)}.
 * Reading and writing is done in FIFO order.
 */
public interface ChatFormatter {

    /**
     * Formats the raw input text into a styled chat message.
     * @param rawInput the text that a player types
     * @return the styled text to display in the chat preview
     */
    Component format(String rawInput);

    /**
     * Writes the formatter config to the buffer.
     * This buffer should create the exact same formatter when sent to the client.
     * @param buf the buffer
     */
    void write(ByteBuf buf);

}
