package com.tisawesomeness.betterpreview.spigot;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.FormatterRegistry;
import com.tisawesomeness.betterpreview.spigot.adapter.EssentialsChatAdapter;
import com.tisawesomeness.betterpreview.spigot.adapter.FormatAdapter;

import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BetterPreviewSpigot extends JavaPlugin {

    public static final String CHANNEL = BetterPreview.CHANNEL.asString();

    private @Nullable FormatAdapter adapter;

    @Override
    public void onEnable() {
        boolean hasEssentialsChat = Bukkit.getPluginManager().getPlugin("EssentialsChat") != null;
        if (hasEssentialsChat) {
            adapter = new EssentialsChatAdapter();
            getLogger().info("Found chat plugin: EssentialsChat");
        }

        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
    }

    @Override
    public void onDisable() {
        // no-op
    }

    public void sendFormatter(Player player) {
        var buf = Unpooled.buffer();
        FormatterRegistry.write(buf, getFormatter(player).orElse(null));
        assert buf.hasArray();
        player.sendPluginMessage(this, CHANNEL, buf.array());
    }
    private Optional<ChatFormatter> getFormatter(Player player) {
        if (adapter == null) {
            return Optional.empty();
        }
        return adapter.buildChatFormatter(player);
    }

}
