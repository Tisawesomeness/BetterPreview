package com.tisawesomeness.betterpreview.spigot.network;

import com.tisawesomeness.betterpreview.network.ByteBufs;
import com.tisawesomeness.betterpreview.spigot.BetterPreviewSpigot;

import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;
import java.util.Map;

public class PacketListener implements PluginMessageListener {

    private final BetterPreviewSpigot plugin;
    private final Map<String, PacketHandler> handlers = new HashMap<>();

    public PacketListener(BetterPreviewSpigot plugin) {
        this.plugin = plugin;
        register(BetterPreviewSpigot.HELLO_CHANNEL, this::receiveHello);
    }
    private void register(String channel, PacketHandler handler) {
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, this);
        handlers.put(channel, handler);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        var handler = handlers.get(channel);
        if (handler != null) {
            handler.handle(player, ByteBufs.fromArray(message));
        }
    }

    private void receiveHello(Player player, ByteBuf buf) {
        String version = ByteBufs.readString(buf);
        plugin.getLogger().fine(() -> "Received C2S hello from " + player.getName() + " with version " + version);
        plugin.getPlayerStorage().acceptVersion(player, version);
    }

    @FunctionalInterface
    private interface PacketHandler {
        void handle(Player player, ByteBuf buf);
    }

}
