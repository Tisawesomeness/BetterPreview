package com.tisawesomeness.betterpreview.format;

import lombok.AllArgsConstructor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.EnumSet;

@AllArgsConstructor
public class ChatFormatter {

    /** The character used in formatting codes, such as "&" or "§" */
    private final char colorSymbol;
    // stored as an integer bit field in the packet
    /**
     * Set of allowed formatting styles,
     * disallowed formatting codes will be left in the message without replacement
     */
    private final EnumSet<Formatting> allowedFormatting;

    public ChatFormatter(PacketByteBuf buf) {
        colorSymbol = buf.readChar();
        int bitSet = buf.readInt();
        allowedFormatting = EnumSet.noneOf(Formatting.class);
        for (Formatting format : Formatting.values()) {
            if ((bitSet & (1 << format.ordinal())) != 0) {
                allowedFormatting.add(format);
            }
        }
    }

    public Text format(String rawInput) {
        var text = Text.empty();
        var currentRun = new StringBuilder();
        var style = Style.EMPTY;
        for (int i = 0; i < rawInput.length(); i++) {
            char c = rawInput.charAt(i);
            if (c == colorSymbol && i + 1 < rawInput.length()) {
                char c2 = rawInput.charAt(i + 1);
                var format = Formatting.byCode(c2);
                if (allowedFormatting.contains(format)) {
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

    public void write(PacketByteBuf buf) {
        buf.writeChar(colorSymbol);
        int bitSet = 0;
        for (Formatting format : allowedFormatting) {
            bitSet |= 1 << format.ordinal();
        }
        buf.writeInt(bitSet);
    }

}
