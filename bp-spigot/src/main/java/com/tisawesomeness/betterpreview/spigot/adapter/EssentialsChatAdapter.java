package com.tisawesomeness.betterpreview.spigot.adapter;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.ClassicFormat;
import com.tisawesomeness.betterpreview.format.ClassicFormatter;
import com.tisawesomeness.betterpreview.spigot.Util;

import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public class EssentialsChatAdapter implements FormatAdapter {

    private static final String BASE_PERMISSION = "essentials.chat";
    private static final Set<ClassicFormat> FORMAT_DECORATIONS = EnumSet.of(
            ClassicFormat.BOLD,
            ClassicFormat.ITALIC,
            ClassicFormat.STRIKETHROUGH,
            ClassicFormat.UNDERLINE
    );

    @Override
    public Optional<ChatFormatter> buildChatFormatter(Player player) {
        var allowedFormatting = EnumSet.noneOf(ClassicFormat.class);

        if (Util.hasPermission(player, BASE_PERMISSION + ".color")) {
            allowedFormatting.addAll(ClassicFormat.ALL_COLOR_FORMATS);
        }
        for (var color : ClassicFormat.ALL_COLOR_FORMATS) {
            String perm = BASE_PERMISSION + "." + color;
            if (player.isPermissionSet(perm)) {
                if (Util.hasPermission(player, perm)) {
                    allowedFormatting.add(color);
                } else {
                    allowedFormatting.remove(color);
                }
            }
        }

        if (Util.hasPermission(player, BASE_PERMISSION + ".format")) {
            allowedFormatting.addAll(FORMAT_DECORATIONS);
            allowedFormatting.add(ClassicFormat.RESET);
        }
        if (Util.hasPermission(player, BASE_PERMISSION + ".magic")) {
            allowedFormatting.add(ClassicFormat.MAGIC);
        }

        boolean rgb = Util.hasPermission(player, BASE_PERMISSION + ".rgb");

        if (allowedFormatting.isEmpty() && !rgb) {
            return Optional.empty();
        }
        return Optional.of(new ClassicFormatter('&', allowedFormatting, rgb));
    }

}
