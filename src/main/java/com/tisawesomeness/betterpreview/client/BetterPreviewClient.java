package com.tisawesomeness.betterpreview.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BetterPreviewClient implements ClientModInitializer {

    private static @NotNull String rawPreviewText = "";

    @Override
    public void onInitializeClient() {
    }

    public static void pushPreview(@NotNull String rawText) {
        rawPreviewText = rawText;
    }
    public static Text getPreview() {
        return format(rawPreviewText);
    }

    private static Text format(@NotNull String rawText) {
        MutableText text = Text.empty();
        StringBuilder currentRun = new StringBuilder();
        Style style = Style.EMPTY;
        for (int i = 0; i < rawText.length(); i++) {
            char c = rawText.charAt(i);
            if (c == '&' && i + 1 < rawText.length()) {
                char c2 = rawText.charAt(i + 1);
                Formatting format = Formatting.byCode(c2);
                if (format != null) {
                    i++; // only consume second char if it's a valid formatting code, to ensure "&&a" still works
                    text.append(Text.literal(currentRun.toString()).setStyle(style));
                    style = style.withFormatting(format);
                    currentRun.setLength(0);
                } else {
                    currentRun.append(c);
                }
            } else {
                currentRun.append(c);
            }
        }
        return text.append(Text.literal(currentRun.toString()).setStyle(style));
    }

}
