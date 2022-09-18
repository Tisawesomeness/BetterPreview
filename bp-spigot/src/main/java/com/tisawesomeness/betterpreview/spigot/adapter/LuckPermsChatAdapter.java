package com.tisawesomeness.betterpreview.spigot.adapter;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.ClassicFormat;
import com.tisawesomeness.betterpreview.format.ClassicFormatter;
import com.tisawesomeness.betterpreview.spigot.Util;

import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.Optional;

public class LuckPermsChatAdapter implements FormatAdapter {

    private static final String BASE_PERMISSION = "lpc";

    @Override
    public Optional<ChatFormatter> buildChatFormatter(Player player) {
        var allowedFormatting = Util.hasPermission(player, BASE_PERMISSION + ".colorcodes")
                ? EnumSet.allOf(ClassicFormat.class)
                : EnumSet.noneOf(ClassicFormat.class);
        boolean rgb = Util.hasPermission(player, BASE_PERMISSION + ".rgbcodes");
        if (allowedFormatting.isEmpty() && !rgb) {
            return Optional.empty();
        }
        return Optional.of(new ClassicFormatter('&', allowedFormatting, rgb));
    }

}
