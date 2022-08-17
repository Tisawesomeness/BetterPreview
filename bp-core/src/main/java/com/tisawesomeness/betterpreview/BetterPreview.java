package com.tisawesomeness.betterpreview;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.NopFormatter;

import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class BetterPreview {

    private static final String NAMESPACE = "betterpreview";
    /** Used for the version exchange handshake on join */
    public static final Key HELLO_CHANNEL = Key.key(NAMESPACE, "hello");
    /** Used to update formatter on change */
    public static final Key UPDATE_CHANNEL = Key.key(NAMESPACE, "update");

    private static final ChatFormatter backupFormatter = new NopFormatter();

    @Setter private static @Nullable ChatFormatter chatFormatter;
    private static String rawPreviewInput = "";

    /**
     * Updates the raw input used to generate the preview.
     * @param rawInput the raw string that the player typed in chat
     */
    public static void updateChatInput(String rawInput) {
        rawPreviewInput = rawInput;
    }

    /**
     * Formats the current raw input into a preview.
     * @return the formatted preview
     */
    public static Component getPreview() {
        var formatter = chatFormatter == null ? backupFormatter : chatFormatter;
        return formatter.format(rawPreviewInput).compact();
    }

    /**
     * Determines whether the preview should display.
     * @return whether to display preview
     */
    public static boolean shouldDisplayPreview() {
        return chatFormatter != null;
    }

}
