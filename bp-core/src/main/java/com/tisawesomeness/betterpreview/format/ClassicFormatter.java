package com.tisawesomeness.betterpreview.format;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

/**
 * Formats with the classic "&" color codes
 */
public class ClassicFormatter implements ChatFormatter {

    public static final char SYMBOL_CHAR = '\u00A7';

    private final char colorSymbol;
    // stored as an integer bit field in the packet
    private final EnumSet<@NotNull ClassicFormat> allowedFormatting;

    /**
     * Creates a new formatter that allows all formatting codes.
     * @param colorSymbol the character used in formatting codes, such as "&" or "§"
     */
    public ClassicFormatter(char colorSymbol) {
        this.colorSymbol = colorSymbol;
        this.allowedFormatting = EnumSet.allOf(ClassicFormat.class);
    }

    /**
     * Creates a new formatter. Disallowed formatting codes will be left in the message without replacement.
     * @param colorSymbol the character used in formatting codes, such as "&" or "§"
     * @param allowedFormats set of allowed formats
     */
    public ClassicFormatter(char colorSymbol, EnumSet<ClassicFormat> allowedFormats) {
        this.colorSymbol = colorSymbol;
        this.allowedFormatting = allowedFormats;
    }

    public ClassicFormatter(ByteBuf buf) {
        colorSymbol = buf.readChar();
        int bitSet = buf.readInt();
        allowedFormatting = EnumSet.noneOf(ClassicFormat.class);
        for (var format : ClassicFormat.values()) {
            if ((bitSet & (1 << format.ordinal())) != 0) {
                allowedFormatting.add(format);
            }
        }
    }

    // Adventure legacy serializer doesn't support enabling/disabling individual color codes
    // doing it manually here
    @Override
    public Component format(String rawInput) {
        var textBuilder = Component.text();
        var currentRun = new StringBuilder();
        var style = Style.empty();

        for (int i = 0; i < rawInput.length(); i++) {
            char c = rawInput.charAt(i);
            if (c == colorSymbol && i + 1 < rawInput.length()) {

                char c2 = rawInput.charAt(i + 1);
                var format = ClassicFormat.byCode(c2);
                if (allowedFormatting.contains(format)) {
                    boolean isEscaped = i > 0 && rawInput.charAt(i - 1) == colorSymbol;
                    if (isEscaped) {
                        continue; // Start processing next character without including last "&" for escaping
                    }
                    i++; // only consume second char if the formatting code is actually used
                    textBuilder.append(Component.text(currentRun.toString()).style(style));
                    assert format != null; // allowedFormatting cannot contain null
                    style = format.apply(style);
                    currentRun.setLength(0);
                } else {
                    currentRun.append(c);
                }

            } else {
                currentRun.append(c);
            }
        }

        textBuilder.append(Component.text(currentRun.toString()).style(style));
        return textBuilder.build();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeChar(colorSymbol);
        int bitSet = 0;
        for (var format : allowedFormatting) {
            bitSet |= 1 << format.ordinal();
        }
        buf.writeInt(bitSet);
    }

}
