package com.tisawesomeness.betterpreview.spigot;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.format.FormatterStatus;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;
import com.tisawesomeness.betterpreview.network.ByteBufs;
import com.tisawesomeness.betterpreview.network.ClientboundUpdate;
import com.tisawesomeness.betterpreview.network.Packet;
import com.tisawesomeness.betterpreview.spigot.adapter.EssentialsChatAdapter;
import com.tisawesomeness.betterpreview.spigot.adapter.FormatAdapter;
import com.tisawesomeness.betterpreview.spigot.network.ChannelListener;
import com.tisawesomeness.betterpreview.spigot.network.PacketListener;
import com.tisawesomeness.betterpreview.spigot.network.PlayerStorage;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class BetterPreviewSpigot extends JavaPlugin {

    private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.GREEN + "BP" +
            ChatColor.GRAY + "]" + ChatColor.RESET + " ";

    public static final String HELLO_CHANNEL = BetterPreview.HELLO_CHANNEL.asString();
    public static final String UPDATE_CHANNEL = BetterPreview.UPDATE_CHANNEL.asString();

    @Getter private final PlayerStorage playerStorage = new PlayerStorage(this);
    private @Nullable FormatAdapter adapter;

    @Override
    public void onEnable() {
        boolean hasEssentialsChat = Bukkit.getPluginManager().getPlugin("EssentialsChat") != null;
        if (hasEssentialsChat) {
            adapter = new EssentialsChatAdapter();
            getLogger().info("Found chat plugin: EssentialsChat");
        }

        getServer().getMessenger().registerOutgoingPluginChannel(this, UPDATE_CHANNEL);
        getServer().getMessenger().registerOutgoingPluginChannel(this, HELLO_CHANNEL);
        new PacketListener(this);

        getServer().getPluginManager().registerEvents(new ChannelListener(this), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(this), this);

        Objects.requireNonNull(getCommand("betterpreview")).setExecutor(new PreviewCommand(this));
    }

    public FormatterUpdate getFormatterUpdate(Player player) {
        if (!Util.hasPermission(player, "betterpreview.preview")) {
            return FormatterUpdate.disabled(FormatterStatus.NO_PERMISSION);
        }
        var formatter = adapter == null ? null : adapter.buildChatFormatter(player).orElse(null);
        return FormatterUpdate.enabled(formatter);
    }

    public Optional<Player> getPlayer(String playerNameOrUUID) {
        // Longer usernames are not allowed in (at least) 1.19
        if (playerNameOrUUID.length() <= 16) {
            return Optional.ofNullable(getServer().getPlayer(playerNameOrUUID));
        }
        try {
            return Optional.ofNullable(getServer().getPlayer(UUID.fromString(playerNameOrUUID)));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    public void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(PREFIX + msg);
    }

    public void sendPacket(Player player, Packet packet) {
        var buf = ByteBufs.create();
        packet.write(buf);
        String channel = packet.getChannel().asString();

        if (getLogger().isLoggable(Level.FINER)) {
            getLogger().finer(() -> "Sending packet to " + player.getName() + " on channel " + channel +
                    " with data " + ByteBufs.debug(buf));
        } else {
            getLogger().fine(() -> "Sending packet to " + player.getName() + " on channel " + channel);
        }

        player.sendPluginMessage(this, channel, ByteBufs.asArray(buf));
    }

    public void updateFormatter(Player player) {
        var packet = new ClientboundUpdate(getFormatterUpdate(player));
        sendPacket(player, packet);
    }

}
