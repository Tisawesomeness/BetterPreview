package com.tisawesomeness.betterpreview.spigot;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class PreviewCommand implements CommandExecutor {

    private final BetterPreviewSpigot plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }
        if (args[0].equals("refresh")) {
            refreshSubcommand(sender);
        } else {
            sendHelp(sender, label);
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        String version = plugin.getDescription().getVersion();
        plugin.sendMessage(sender, String.format("%sBetterPreview %sv%s", ChatColor.GOLD, ChatColor.GREEN, version));
        plugin.sendMessage(sender, String.format("/%s refresh %s- Refresh your chat formatting rules", label, ChatColor.GOLD));
    }

    private void refreshSubcommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            plugin.sendMessage(sender, ChatColor.RED + "Only players can use this command.");
            return;
        }
        if (Util.hasPermission(player, "betterpreview.refresh")) {
            plugin.sendFormatter(player);
            plugin.sendMessage(sender, "Refreshed preview.");
        } else {
            plugin.sendMessage(sender, ChatColor.RED + "You don't have permission to refresh preview.");
        }
    }

}
