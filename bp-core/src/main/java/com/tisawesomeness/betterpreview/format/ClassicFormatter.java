package com.tisawesomeness.betterpreview.format;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;

/**
 * Formats with the classic "&" color codes
 */
public class ClassicFormatter implements ChatFormatter {

    public static final Set<NamedTextColor> ALL_NAMED_COLORS = Set.of(
            NamedTextColor.BLACK,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.DARK_GREEN,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_RED,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.GOLD,
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY,
            NamedTextColor.BLUE,
            NamedTextColor.GREEN,
            NamedTextColor.AQUA,
            NamedTextColor.RED,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.YELLOW,
            NamedTextColor.WHITE
    );

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
     * @param allowedColors set of allowed named colors
     * @param allowedDecorations set of allowed decorations, such as bold and obfuscated
     * @param resetAllowed whether the reset code "&r" is allowed
     * @throws IllegalArgumentException if one of the text decorations is unsupported
     *                                  (in case one is added in a future version)
     */
    public ClassicFormatter(char colorSymbol, Set<NamedTextColor> allowedColors, Set<TextDecoration> allowedDecorations, boolean resetAllowed) {
        this.colorSymbol = colorSymbol;
        this.allowedFormatting = EnumSet.noneOf(ClassicFormat.class);
        for (var format : ClassicFormat.values()) {
            var textFormat = format.textFormat;
            if (textFormat instanceof NamedTextColor color) {
                if (allowedColors.contains(color)) {
                    allowedFormatting.add(format);
                }
            } else if (textFormat instanceof TextDecoration decoration) {
                if (allowedDecorations.contains(decoration)) {
                    allowedFormatting.add(format);
                }
            } else if (textFormat == null) {
                if (resetAllowed) {
                    allowedFormatting.add(format);
                }
            } else {
                throw new IllegalArgumentException("Unsupported text format: " + textFormat);
            }
        }
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
                    i++; // only consume second char if it's a valid formatting code, to ensure "&&a" still works
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

    @AllArgsConstructor
    private enum ClassicFormat {
        BLACK(NamedTextColor.BLACK, '0'),
        DARK_BLUE(NamedTextColor.DARK_BLUE, '1'),
        DARK_GREEN(NamedTextColor.DARK_GREEN, '2'),
        DARK_AQUA(NamedTextColor.DARK_AQUA, '3'),
        DARK_RED(NamedTextColor.DARK_RED, '4'),
        DARK_PURPLE(NamedTextColor.DARK_PURPLE, '5'),
        GOLD(NamedTextColor.GOLD, '6'),
        GRAY(NamedTextColor.GRAY, '7'),
        DARK_GRAY(NamedTextColor.DARK_GRAY, '8'),
        BLUE(NamedTextColor.BLUE, '9'),
        GREEN(NamedTextColor.GREEN, 'a'),
        AQUA(NamedTextColor.AQUA, 'b'),
        RED(NamedTextColor.RED, 'c'),
        LIGHT_PURPLE(NamedTextColor.LIGHT_PURPLE, 'd'),
        YELLOW(NamedTextColor.YELLOW, 'e'),
        WHITE(NamedTextColor.WHITE, 'f'),
        MAGIC(TextDecoration.OBFUSCATED, 'k'),
        BOLD(TextDecoration.BOLD, 'l'),
        STRIKETHROUGH(TextDecoration.STRIKETHROUGH, 'm'),
        UNDERLINE(TextDecoration.UNDERLINED, 'n'),
        ITALIC(TextDecoration.ITALIC, 'o'),
        RESET('r');

        // Null means reset formatting
        private final @Nullable TextFormat textFormat;
        private final char formattingCode;

        ClassicFormat(char formattingCode) {
            this(null, formattingCode);
        }

        public Style apply(Style style) {
            if (textFormat instanceof NamedTextColor color) {
                return style.color(color);
            } else if (textFormat instanceof TextDecoration decoration) {
                return style.decorate(decoration);
            } else {
                return Style.empty();
            }
        }

        public static @Nullable ClassicFormat byCode(char code) {
            for (var format : ClassicFormat.values()) {
                if (format.formattingCode == code) {
                    return format;
                }
            }
            return null;
        }
    }

}
