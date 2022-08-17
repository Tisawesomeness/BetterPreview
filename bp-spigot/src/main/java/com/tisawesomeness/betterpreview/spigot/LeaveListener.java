package com.tisawesomeness.betterpreview.spigot;

import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class LeaveListener implements Listener {

    private final BetterPreviewSpigot plugin;

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        plugin.getPlayerStorage().remove(e.getPlayer());
    }

}
