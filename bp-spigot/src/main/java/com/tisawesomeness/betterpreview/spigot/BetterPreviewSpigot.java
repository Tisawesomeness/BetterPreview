package com.tisawesomeness.betterpreview.spigot;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.ClassicFormatter;
import com.tisawesomeness.betterpreview.format.FormatterRegistry;

import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterPreviewSpigot extends JavaPlugin {

    public static final String CHANNEL = BetterPreview.CHANNEL.asString();

    // Dummy example for now
    private final ChatFormatter chatFormatter = new ClassicFormatter('&');

    @Override
    public void onEnable() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
    }

    @Override
    public void onDisable() {
        // no-op
    }

    public void sendFormatter(Player player) {
        player.sendPluginMessage(this, CHANNEL, getFormatterData());
    }
    private byte[] getFormatterData() {
        var buf = Unpooled.buffer();
        FormatterRegistry.write(buf, chatFormatter);
        assert buf.hasArray();
        return buf.array();
    }

}
