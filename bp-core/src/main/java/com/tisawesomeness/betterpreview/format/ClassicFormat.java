package com.tisawesomeness.betterpreview.format;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextFormat;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * An enumeration of all legacy formatting codes, such as "&a" for green and "&l" for bold.
 */
@AllArgsConstructor
public enum ClassicFormat {
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

    public static final EnumSet<ClassicFormat> ALL_COLOR_FORMATS = EnumSet.range(BLACK, WHITE);

    // Null means reset formatting
    private final @Nullable TextFormat textFormat;
    @Getter private final char formattingCode;

    ClassicFormat(char formattingCode) {
        this(null, formattingCode);
    }

    /**
     * Applies this formatting to the given style.
     * @param style the style to apply this formatting to
     * @return the new style
     */
    public Style apply(Style style) {
        if (textFormat instanceof NamedTextColor color) {
            return style.color(color);
        } else if (textFormat instanceof TextDecoration decoration) {
            return style.decorate(decoration);
        } else {
            return Style.empty();
        }
    }

    /**
     * Gets the format by its formatting code.
     * @param code the formatting code
     * @return the format, or null if not found
     */
    public static @Nullable ClassicFormat byCode(char code) {
        for (var format : ClassicFormat.values()) {
            if (format.formattingCode == code) {
                return format;
            }
        }
        return null;
    }

}
