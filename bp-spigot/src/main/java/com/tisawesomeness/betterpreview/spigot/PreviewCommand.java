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
            refreshSubcommand(sender, args);
        } else {
            sendHelp(sender, label);
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        String version = plugin.getDescription().getVersion();
        plugin.sendMessage(sender, String.format("%sBetterPreview %sv%s", ChatColor.GOLD, ChatColor.GREEN, version));
        plugin.sendMessage(sender, String.format("/%s refresh %s[player] %s- Refresh chat formatting rules",
                label, ChatColor.YELLOW, ChatColor.GOLD));
    }

    private void refreshSubcommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, ChatColor.RED + "Only players can use this command.");
            return;
        }
        if (!Util.hasPermission(sender, "betterpreview.refresh")) {
            plugin.sendMessage(sender, ChatColor.RED + "You don't have permission to refresh preview.");
            return;
        }
        Player targetPlayer;
        if (args.length > 1) {
            if (!Util.hasPermission(sender, "betterpreview.refresh.others")) {
                plugin.sendMessage(sender, ChatColor.RED + "You don't have permission to refresh preview for other players.");
                return;
            }
            targetPlayer = plugin.getPlayer(args[1]);
            if (targetPlayer == null) {
                plugin.sendMessage(sender, ChatColor.RED + "Player not found.");
                return;
            }
        } else {
            targetPlayer = (Player) sender;
        }
        plugin.sendFormatter(targetPlayer);
        plugin.sendMessage(sender, "Refreshed preview.");
    }

}
