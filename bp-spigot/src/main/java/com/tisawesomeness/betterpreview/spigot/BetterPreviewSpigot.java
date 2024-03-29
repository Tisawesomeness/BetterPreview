package com.tisawesomeness.betterpreview.spigot;

import com.tisawesomeness.betterpreview.BetterPreview;
import com.tisawesomeness.betterpreview.SupportInfo;
import com.tisawesomeness.betterpreview.format.FormatterStatus;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;
import com.tisawesomeness.betterpreview.network.ByteBufs;
import com.tisawesomeness.betterpreview.network.ClientboundHello;
import com.tisawesomeness.betterpreview.network.ClientboundUpdate;
import com.tisawesomeness.betterpreview.network.Packet;
import com.tisawesomeness.betterpreview.spigot.adapter.EssentialsChatAdapter;
import com.tisawesomeness.betterpreview.spigot.adapter.FormatAdapter;
import com.tisawesomeness.betterpreview.spigot.adapter.LuckPermsChatAdapter;
import com.tisawesomeness.betterpreview.spigot.network.ChannelListener;
import com.tisawesomeness.betterpreview.spigot.network.PacketListener;
import com.tisawesomeness.betterpreview.spigot.network.PlayerStorage;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class BetterPreviewSpigot extends JavaPlugin {

    public static final String HELLO_CHANNEL = BetterPreview.HELLO_CHANNEL.asString();
    public static final String UPDATE_CHANNEL = BetterPreview.UPDATE_CHANNEL.asString();

    @Getter private final PlayerStorage playerStorage = new PlayerStorage(this);
    private @Nullable FormatAdapter adapter;

    @Override
    public void onEnable() {
        adapter = findAdapter();

        getServer().getMessenger().registerOutgoingPluginChannel(this, UPDATE_CHANNEL);
        getServer().getMessenger().registerOutgoingPluginChannel(this, HELLO_CHANNEL);
        new PacketListener(this);

        getServer().getPluginManager().registerEvents(new ChannelListener(this), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new LoadListener(this), this);

        Objects.requireNonNull(getCommand("betterpreview")).setExecutor(new PreviewCommand(this));
    }
    private @Nullable FormatAdapter findAdapter() {
        boolean hasEssentialsChat = getServer().getPluginManager().getPlugin("EssentialsChat") != null;
        if (hasEssentialsChat) {
            getLogger().info("Found chat plugin: EssentialsChat");
            return new EssentialsChatAdapter();
        }
        boolean hasLuckPermsChat = getServer().getPluginManager().getPlugin("LPC") != null;
        if (hasLuckPermsChat) {
            getLogger().info("Found chat plugin: LPC");
            return new LuckPermsChatAdapter();
        }
        getLogger().warning("No supported chat plugin found. Player previews will have no formatting.");
        return null;
    }

    public void sendHello(Player player) {
        String version = getDescription().getVersion();
        var supportInfo = SupportInfo.supported("0.2.0");
        var update = getFormatterUpdate(player);
        var packet = ClientboundHello.withUpdate(version, supportInfo, update);
        sendPacket(player, packet);
    }

    public void updateFormatter(Player player) {
        var packet = new ClientboundUpdate(getFormatterUpdate(player));
        sendPacket(player, packet);
    }

    private FormatterUpdate getFormatterUpdate(Player player) {
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
        sender.sendMessage(BetterPreview.MESSAGE_PREFIX_RAW + msg);
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

}
