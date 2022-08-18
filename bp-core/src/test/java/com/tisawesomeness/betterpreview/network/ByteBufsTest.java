package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.format.FormatterStatus;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ByteBufsTest {

    @Test
    public void testArray() {
        byte[] arr = {1, 2, 3, 4, 5};
        var buf = ByteBufs.fromArray(arr);
        assertThat(ByteBufs.asArray(buf)).isEqualTo(arr);
    }
    @Test
    public void testArrayEmpty() {
        byte[] arr = new byte[0];
        var buf = ByteBufs.fromArray(arr);
        assertThat(ByteBufs.asArray(buf)).isEqualTo(arr);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 7, 127, 128, Integer.MAX_VALUE, Integer.MIN_VALUE, -128, -1})
    public void testVarInt(int value) {
        var buf = Unpooled.buffer();
        ByteBufs.writeVarInt(buf, value);
        assertThat(ByteBufs.readVarInt(buf)).isEqualTo(value);
        assertThat(buf.isReadable()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "abc", "abcdefghijklmnopqrstuvwxyz", "田"})
    @MethodSource("largeStringProvider")
    public void testString(String str) {
        var buf = Unpooled.buffer();
        ByteBufs.writeString(buf, str);
        assertThat(ByteBufs.readString(buf)).isEqualTo(str);
        assertThat(buf.isReadable()).isFalse();
    }
    private static Stream<String> largeStringProvider() {
        return Stream.of("*".repeat(256));
    }

    @Test
    public void testNullableStringAsEmpty() {
        var buf = Unpooled.buffer();
        ByteBufs.writeNullableStringAsEmpty(buf, null);
        assertThat(ByteBufs.readNullableStringAsEmpty(buf)).isNull();
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testNullableStringAsEmpty2() {
        var buf = Unpooled.buffer();
        ByteBufs.writeNullableStringAsEmpty(buf, "abc");
        assertThat(ByteBufs.readNullableStringAsEmpty(buf)).isEqualTo("abc");
        assertThat(buf.isReadable()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = FormatterStatus.class, names = {"OK", "UNKNOWN", "NO_PERMISSION"})
    public void testEnum(FormatterStatus status) {
        var buf = Unpooled.buffer();
        ByteBufs.writeEnum(buf, status);
        assertThat(ByteBufs.readEnum(buf, FormatterStatus.class)).isEqualTo(status);
        assertThat(buf.isReadable()).isFalse();
    }

}
