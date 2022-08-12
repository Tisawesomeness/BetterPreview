package com.tisawesomeness.betterpreview.format;

import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.stream.Stream;

import static com.tisawesomeness.betterpreview.ComponentAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ClassicFormatterTest {

    private static final ChatFormatter NORMAL = new ClassicFormatter('&');
    private static final ChatFormatter NONE = new ClassicFormatter('&', EnumSet.noneOf(ClassicFormat.class));
    private static final ChatFormatter COLORS_ONLY = new ClassicFormatter('&', ClassicFormat.ALL_COLOR_FORMATS);
    private static final ChatFormatter SYMBOL_CHAR = new ClassicFormatter(ClassicFormatter.SYMBOL_CHAR);

    private static Stream<ChatFormatter> formatterProvider() {
        return Stream.of(NORMAL, NONE, COLORS_ONLY, SYMBOL_CHAR);
    }

    @Test
    public void testNoFormatting() {
        assertThat(NORMAL.format("test")).isSimilarTo("test");
    }
    @Test
    public void testNoFormatting2() {
        assertThat(NORMAL.format("&")).isSimilarTo("&");
    }
    @Test
    public void testNoFormatting3() {
        assertThat(NORMAL.format("&&")).isSimilarTo("&&");
    }
    @Test
    public void testNoFormatting4() {
        assertThat(NORMAL.format("&&&")).isSimilarTo("&&&");
    }

    @ParameterizedTest
    @EnumSource(ClassicFormat.class)
    public void testStyle(ClassicFormat format) {
        String input = String.format("&%ctest", format.getFormattingCode());
        var expected = Component.text("test").style(format.apply(Style.empty()));
        assertThat(NORMAL.format(input)).isSimilarTo(expected);
    }
    @ParameterizedTest
    @EnumSource(ClassicFormat.class)
    public void testStyle2(ClassicFormat format) {
        String input = String.format("plain&%cstyled", format.getFormattingCode());
        var expected = Component.text("plain").append(
                Component.text("styled").style(format.apply(Style.empty())));
        assertThat(NORMAL.format(input)).isSimilarTo(expected);
    }
    @Test
    public void testStyle3() {
        String input = "&a&atest";
        var expected = Component.text("test").color(NamedTextColor.GREEN);
        assertThat(NORMAL.format(input)).isSimilarTo(expected);
    }
    @Test
    public void testStackedStyles() {
        String input = "&a&l&ktest";
        var expected = Component.text("test").style(
                Style.style(NamedTextColor.GREEN, TextDecoration.BOLD, TextDecoration.OBFUSCATED)
        );
        assertThat(NORMAL.format(input)).isSimilarTo(expected);
    }
    @Test
    public void testOverrideStyles() {
        String input = "&agreen&baqua&cred&lboldred";
        var expected = Component.join(JoinConfiguration.noSeparators(),
                Component.text("green").color(NamedTextColor.GREEN),
                Component.text("aqua").color(NamedTextColor.AQUA),
                Component.text("red").color(NamedTextColor.RED),
                Component.text("boldred").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
        );
        assertThat(NORMAL.format(input)).isSimilarTo(expected);
    }
    @Test
    public void testReset() {
        String input = "&a&rtest";
        assertThat(NORMAL.format(input)).isSimilarTo(Component.text("test"));
    }

    @Test
    public void testEscape() {
        String input = "&&atest";
        assertThat(NORMAL.format(input)).isSimilarTo("&atest");
    }
    @Test
    public void testEscape2() {
        String input = "&&&atest";
        assertThat(NORMAL.format(input)).isSimilarTo("&&atest");
    }
    @Test
    public void testEscape3() {
        String input = "&&&&atest";
        assertThat(NORMAL.format(input)).isSimilarTo("&&&atest");
    }
    @Test
    public void testEscape4() {
        String input = "&&a&&btest";
        assertThat(NORMAL.format(input)).isSimilarTo("&a&btest");
    }
    @Test
    public void testEscape5() {
        String input = "&a&&btest";
        var expected = Component.text("&btest").color(NamedTextColor.GREEN);
        assertThat(NORMAL.format(input)).isSimilarTo(expected);
    }

    @Test
    public void testNoneAllowed() {
        String input = "&a&ktest";
        assertThat(NONE.format(input)).isSimilarTo("&a&ktest");
    }
    @Test
    public void testOnlyColors() {
        String input = "&a&ktest";
        var expected = Component.text("&ktest").color(NamedTextColor.GREEN);
        assertThat(COLORS_ONLY.format(input)).isSimilarTo(expected);
    }

    @Test
    public void testSymbolChar() {
        String input = ClassicFormatter.SYMBOL_CHAR + "6&3test";
        var expected = Component.text("&3test").color(NamedTextColor.GOLD);
        assertThat(SYMBOL_CHAR.format(input)).isSimilarTo(expected);
    }

    @ParameterizedTest
    @MethodSource("formatterProvider")
    public void testReadWrite(ChatFormatter formatter) {
        var buf = Unpooled.buffer();
        formatter.write(buf);
        assertThat(new ClassicFormatter(buf)).usingRecursiveComparison().isEqualTo(formatter);
    }

}
