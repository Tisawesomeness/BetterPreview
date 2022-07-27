package com.tisawesomeness.betterpreview.spigot.adapter;

import com.tisawesomeness.betterpreview.format.ChatFormatter;

import org.bukkit.entity.Player;

/**
 * Adapts a plugin's chat system to a BetterPreview chat formatter.
 */
public interface FormatAdapter {

    /**
     * Builds a chat formatter for the player based on the player's permissions and the chat plugin config.
     * @param player the player who will be sent the formatter
     * @return the formatter
     */
    ChatFormatter buildChatFormatter(Player player);

}
