package com.tisawesomeness.betterpreview;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.NopFormatter;

import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class BetterPreview {

    public static final Key CHANNEL = Key.key("betterpreview", "formatter");
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
    public static Component getPreview() {
        // Use backup no-op formatter just in case formatter was disabled
        // between shouldRender check and getPreview call
        var formatter = chatFormatter == null ? backupFormatter : chatFormatter;
        return formatter.format(rawPreviewInput).compact();
    }
    public static boolean shouldDisplayPreview() {
        return chatFormatter != null;
    }

}
