package com.tisawesomeness.betterpreview.spigot;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PreviewCommand implements CommandExecutor, TabCompleter {

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
        plugin.sendMessage(sender, String.format("/%s refresh * %s- Refresh all players' chat formatting rules",
                label,  ChatColor.GOLD));
    }

    private void refreshSubcommand(CommandSender sender, String[] args) {
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
            String playerArg = args[1];
            if (playerArg.equals("*")) {
                refreshAllSubcommand(sender);
                return;
            }
            var playerOpt = plugin.getPlayer(playerArg);
            if (playerOpt.isEmpty()) {
                plugin.sendMessage(sender, ChatColor.RED + "Player not found.");
                return;
            }
            targetPlayer = playerOpt.get();
        } else if (sender instanceof Player) {
            targetPlayer = (Player) sender;
        } else {
            plugin.sendMessage(sender, ChatColor.RED + "Please specify a player.");
            return;
        }
        plugin.updateFormatter(targetPlayer);
        plugin.sendMessage(sender, "Refreshed preview.");
    }
    private void refreshAllSubcommand(CommandSender sender) {
        if (!Util.hasPermission(sender, "betterpreview.refresh.all")) {
            plugin.sendMessage(sender, ChatColor.RED + "You don't have permission to refresh preview for all players.");
            return;
        }
        for (var player : plugin.getServer().getOnlinePlayers()) {
            plugin.updateFormatter(player);
        }
        plugin.sendMessage(sender, "Refreshed all previews.");
    }

    @Override
    public @Nullable List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        var commands = new ArrayList<String>();
        if (args.length == 1) {
            if (Util.hasPermission(sender, "betterpreview.refresh")) {
                commands.add("refresh");
            }
            return StringUtil.copyPartialMatches(args[0], commands, new ArrayList<>());
        }
        if (args.length == 2 && args[0].equals("refresh") && Util.hasPermission(sender, "betterpreview.refresh")) {
            if (Util.hasPermission(sender, "betterpreview.refresh.others")) {
                for (var player : plugin.getServer().getOnlinePlayers()) {
                    commands.add(player.getName());
                }
            }
            if (Util.hasPermission(sender, "betterpreview.refresh.all")) {
                commands.add("*");
            }
            return StringUtil.copyPartialMatches(args[1], commands, new ArrayList<>());
        }
        return null;
    }

}
