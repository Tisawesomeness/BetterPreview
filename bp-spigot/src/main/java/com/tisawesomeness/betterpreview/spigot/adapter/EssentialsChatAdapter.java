package com.tisawesomeness.betterpreview.spigot.adapter;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.ClassicFormatter;
import com.tisawesomeness.betterpreview.spigot.Util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class EssentialsChatAdapter implements FormatAdapter {

    private static final String BASE_PERMISSION = "essentials.chat";
    private static final Set<TextDecoration> FORMAT_DECORATIONS = EnumSet.of(
            TextDecoration.BOLD,
            TextDecoration.ITALIC,
            TextDecoration.STRIKETHROUGH,
            TextDecoration.UNDERLINED
    );

    @Override
    public ChatFormatter buildChatFormatter(Player player) {
        var colors = new HashSet<NamedTextColor>();
        if (Util.hasPermission(player, BASE_PERMISSION + ".color")) {
            colors.addAll(ClassicFormatter.ALL_NAMED_COLORS);
        }
        for (var color : ClassicFormatter.ALL_NAMED_COLORS) {
            String perm = BASE_PERMISSION + "." + color.toString();
            if (player.isPermissionSet(perm)) {
                if (Util.hasPermission(player, perm)) {
                    colors.add(color);
                } else {
                    colors.remove(color);
                }
            }
        }

        var decorations = EnumSet.noneOf(TextDecoration.class);
        boolean resetAllowed = false;
        if (Util.hasPermission(player, BASE_PERMISSION + ".format")) {
            decorations.addAll(FORMAT_DECORATIONS);
            resetAllowed = true;
        }
        if (Util.hasPermission(player, BASE_PERMISSION + ".magic")) {
            decorations.add(TextDecoration.OBFUSCATED);
        }

        return new ClassicFormatter('&', colors, decorations, resetAllowed);
    }

}
