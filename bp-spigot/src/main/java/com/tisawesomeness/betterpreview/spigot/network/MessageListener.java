package com.tisawesomeness.betterpreview.spigot.network;

import com.tisawesomeness.betterpreview.network.ByteBufs;
import com.tisawesomeness.betterpreview.spigot.BetterPreviewSpigot;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

@AllArgsConstructor
public class MessageListener implements PluginMessageListener {

    private final BetterPreviewSpigot plugin;

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (BetterPreviewSpigot.HELLO_CHANNEL.equals(channel)) {
            var buf = ByteBufs.fromArray(message);
            String version = ByteBufs.readString(buf);
            plugin.getLogger().fine("Received C2S hello from " + player.getName() + " with version " + version);
            plugin.getPlayerStorage().acceptVersion(player, version);
        }
    }

}
