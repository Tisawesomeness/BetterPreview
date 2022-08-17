package com.tisawesomeness.betterpreview.spigot.network;

import com.tisawesomeness.betterpreview.spigot.BetterPreviewSpigot;

import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

@AllArgsConstructor
public class ChannelListener implements Listener {

    private final BetterPreviewSpigot plugin;

    // Can't use join event because channel is registered after
    @EventHandler
    public void onRegisterChannel(PlayerRegisterChannelEvent e) {
        if (e.getChannel().equals(BetterPreviewSpigot.HELLO_CHANNEL)) {
            plugin.getPlayerStorage().registerHelloChannel(e.getPlayer());
        }
    }

}
