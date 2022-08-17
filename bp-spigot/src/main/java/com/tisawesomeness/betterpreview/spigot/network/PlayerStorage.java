package com.tisawesomeness.betterpreview.spigot.network;

import com.tisawesomeness.betterpreview.network.ByteBufs;
import com.tisawesomeness.betterpreview.network.ClientboundHello;
import com.tisawesomeness.betterpreview.spigot.BetterPreviewSpigot;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class PlayerStorage {

    private final BetterPreviewSpigot plugin;

    private final Map<UUID, PlayerData> storage = new HashMap<>();

    public PlayerData get(Player player) {
        return storage.get(player.getUniqueId());
    }
    public void acceptVersion(Player player, String version) {
        plugin.getLogger().fine("Player " + player.getName() + " joined with BP version " + version);
        storage.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()).acceptVersion(player, version);
    }
    public void registerHelloChannel(Player player) {
        plugin.getLogger().fine("Player " + player.getName() + " registered channel");
        storage.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData()).register(player);
    }
    public void remove(Player player) {
        storage.remove(player.getUniqueId());
    }

    private class PlayerData {
        private @Nullable String version;
        private boolean registered;

        public void acceptVersion(Player player, String version) {
            this.version = version;
            onChange(player);
        }
        public void register(Player player) {
            registered = true;
            onChange(player);
        }
        private void onChange(Player player) {
            if (version != null && registered) {
                plugin.getLogger().fine(player.getName() + " data completed, sending hello packet");
                sendHello(player);
            }
        }
        private void sendHello(Player player) {
            String version = plugin.getDescription().getVersion();
            var buf = ByteBufs.create();
            new ClientboundHello(version, plugin.getFormatter(player).orElse(null)).write(buf);
            plugin.sendPacket(player, BetterPreviewSpigot.HELLO_CHANNEL, buf);
        }
    }

}
