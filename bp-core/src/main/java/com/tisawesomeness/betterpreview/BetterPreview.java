package com.tisawesomeness.betterpreview;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.NopFormatter;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nullable;

public class BetterPreview {

    private static final String NAMESPACE = "betterpreview";
    /** Used for the version exchange handshake on join */
    public static final Key HELLO_CHANNEL = Key.key(NAMESPACE, "hello");
    /** Used to update formatter on change */
    public static final Key UPDATE_CHANNEL = Key.key(NAMESPACE, "update");

    public static final Component MESSAGE_PREFIX = Component.join(JoinConfiguration.noSeparators(),
            Component.text("[").color(NamedTextColor.GRAY),
            Component.text("BP").color(NamedTextColor.GREEN),
            Component.text("]").color(NamedTextColor.GRAY),
            Component.text(" ")
    );
    public static final String MESSAGE_PREFIX_RAW = "§7[§aBP§7]§r ";

    private static final ChatFormatter backupFormatter = new NopFormatter();

    @Setter private static @Nullable ChatFormatter chatFormatter;
    @Getter private static Component preview = Component.empty();

    /**
     * Updates the raw input used to generate the preview.
     * @param rawInput the raw string that the player typed in chat
     */
    public static void updateChatInput(String rawInput) {
        var formatter = chatFormatter == null ? backupFormatter : chatFormatter;
        preview = formatter.format(rawInput).compact();
    }

    /**
     * Determines whether the preview should display.
     * @return whether to display preview
     */
    public static boolean shouldDisplayPreview() {
        return chatFormatter != null;
    }

}
