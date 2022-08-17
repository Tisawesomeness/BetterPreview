package com.tisawesomeness.betterpreview.format;

import io.netty.buffer.ByteBuf;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;

import java.util.EnumSet;

/**
 * Formats with the classic "&" color codes
 */
public class ClassicFormatter implements ChatFormatter {

    public static final char SYMBOL_CHAR = '\u00A7';

    private final char colorSymbol;
    // stored as an integer bit field in the packet
    private final EnumSet<ClassicFormat> allowedFormatting;
    private final boolean rgbAllowed;

    /**
     * Creates a new formatter that allows all formatting codes.
     * @param colorSymbol the character used in formatting codes, such as "&" or "§"
     */
    public ClassicFormatter(char colorSymbol) {
        this(colorSymbol, EnumSet.allOf(ClassicFormat.class), true);
    }

    /**
     * Creates a new formatter. Disallowed formatting codes will be left in the message without replacement.
     * @param colorSymbol the character used in formatting codes, such as "&" or "§"
     * @param allowedFormats set of allowed formats
     * @param rgbAllowed whether to allow RGB codes
     */
    public ClassicFormatter(char colorSymbol, EnumSet<ClassicFormat> allowedFormats, boolean rgbAllowed) {
        this.colorSymbol = colorSymbol;
        this.allowedFormatting = allowedFormats;
        this.rgbAllowed = rgbAllowed;
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
        rgbAllowed = buf.readBoolean();
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
                if (rgbAllowed && c2 == '#' && i + 7 < rawInput.length()) {
                    boolean isEscaped = i > 0 && rawInput.charAt(i - 1) == colorSymbol;
                    if (isEscaped) {
                        continue; // Start processing next character without including last "&" for escaping
                    }
                    String hex = rawInput.substring(i + 2, i + 8);
                    try {
                        int rgb = Integer.parseInt(hex, 16);
                        i += 7;
                        textBuilder.append(Component.text(currentRun.toString()).style(style));
                        style = style.color(TextColor.color(rgb));
                        currentRun.setLength(0);
                        continue;
                    } catch (NumberFormatException ignored) {
                        // hex code not valid, keep processing as normal
                    }
                } else {
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
                        continue;
                    }
                }

            }
            currentRun.append(c);
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
        buf.writeBoolean(rgbAllowed);
    }

}
