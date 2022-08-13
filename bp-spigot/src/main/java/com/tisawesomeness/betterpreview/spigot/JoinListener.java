package com.tisawesomeness.betterpreview.spigot;

import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

@AllArgsConstructor
public class JoinListener implements Listener {

    private final BetterPreviewSpigot plugin;

    // Can't use join event because channel is registered after
    @EventHandler
    public void onRegisterChannel(PlayerRegisterChannelEvent e) {
        if (BetterPreviewSpigot.CHANNEL.equals(e.getChannel())) {
            plugin.sendFormatterIfAllowed(e.getPlayer());
        }
    }

}
