package com.tisawesomeness.betterpreview.network;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.key.Key;

public interface Packet {

    /**
     * Writes the packet to the buffer.
     * @param buf the buffer
     */
    void write(ByteBuf buf);

    /**
     * Gets the channel identifier the packet is sent on.
     * Up to one clientbound and one serverbound packet should be used per channel.
     * @return the channel
     */
    Key getChannel();

}
