package com.tisawesomeness.betterpreview.spigot;

import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

@AllArgsConstructor
public class LoadListener implements Listener {

    private final BetterPreviewSpigot plugin;

    @EventHandler
    public void onLoad(ServerLoadEvent e) {
        if (e.getType() == ServerLoadEvent.LoadType.RELOAD) {
            plugin.getLogger().severe("BetterPreview does not support the /reload command.");
        }
    }

}
