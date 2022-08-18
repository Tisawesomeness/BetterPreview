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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @Test
    public void testVarIntInvalid() {
        var buf = Unpooled.buffer();
        // Normally stops at 5 bytes, add 1 more to confuse it :troll:
        buf.writeBytes(b(0xff, 0xff, 0xff, 0xff, 0xff, 0x01));
        System.out.println(ByteBufs.debug(buf));
        assertThatThrownBy(() -> ByteBufs.readVarInt(buf)).isInstanceOf(IllegalArgumentException.class);
    }
    private static byte[] b(int... values) {
        byte[] arr = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            arr[i] = (byte) values[i];
        }
        return arr;
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "abc", "abcdefghijklmnopqrstuvwxyz", "田", "飨", "\uD83D\uDF01"})
    @MethodSource("largeStringProvider")
    public void testString(String str) {
        var buf = Unpooled.buffer();
        ByteBufs.writeString(buf, str);
        assertThat(ByteBufs.readString(buf)).isEqualTo(str);
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testStringLength() {
        var buf = Unpooled.buffer();
        ByteBufs.writeString(buf, "飨", 1);
        assertThat(ByteBufs.readString(buf, 1)).isEqualTo("飨");
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testWriteStringTooLong() {
        var buf = Unpooled.buffer();
        assertThatThrownBy(() -> ByteBufs.writeString(buf, "abc", 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void testReadStringTooLong() {
        var buf = Unpooled.buffer();
        ByteBufs.writeString(buf, "abc");
        assertThatThrownBy(() -> ByteBufs.readString(buf, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void testReadStringEncodedTooLong() {
        var buf = Unpooled.buffer();
        ByteBufs.writeString(buf, "abcdefghijklmnopqrstuvwxyz");
        assertThatThrownBy(() -> ByteBufs.readString(buf, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    public void testReadStringNegative() {
        var buf = Unpooled.buffer();
        ByteBufs.writeVarInt(buf, -1);
        assertThatThrownBy(() -> ByteBufs.readString(buf))
                .isInstanceOf(IllegalArgumentException.class);
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
    @Test
    public void testEnumInvalid() {
        var buf = Unpooled.buffer();
        ByteBufs.writeVarInt(buf, FormatterStatus.values().length);
        assertThatThrownBy(() -> ByteBufs.readEnum(buf, FormatterStatus.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
