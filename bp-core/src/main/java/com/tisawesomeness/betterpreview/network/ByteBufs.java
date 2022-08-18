package com.tisawesomeness.betterpreview.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

/**
 * Helper methods for working with netty {@link ByteBuf}.
 */
public class ByteBufs {

    private static final int MAX_LENGTH = 32767;

    public static ByteBuf create() {
        return Unpooled.buffer();
    }

    /**
     * Gets the ByteBuf as an array without modifying the readerIndex. Uses the backing array if possible.
     * @param buf the ByteBuf
     * @return a byte array
     */
    public static byte[] asArray(ByteBuf buf) {
        if (buf.hasArray() && buf.arrayOffset() == 0 && buf.readableBytes() == buf.array().length) {
            return buf.array();
        } else {
            byte[] arr = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), arr);
            return arr;
        }
    }
    public static ByteBuf fromArray(byte[] arr) {
        return Unpooled.wrappedBuffer(arr);
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        while ((value & -128) != 0) {
            buf.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        buf.writeByte(value);
    }
    public static int readVarInt(ByteBuf buf) {
        int value = 0;
        int i = 0;

        byte b;
        do {
            b = buf.readByte();
            value |= (b & 127) << i * 7;
            i++;
            if (i > 5) {
                throw new IllegalArgumentException("var int was too big");
            }
        } while ((b & 128) == 128);

        return value;
    }

    public static void writeString(ByteBuf buf, String str) {
        writeString(buf, str, MAX_LENGTH);
    }
    public static void writeString(ByteBuf buf, String str, @Nonnegative int maxLength) {
        if (str.length() > maxLength) {
            throw new IllegalArgumentException("String too long: " + str.length() + " > " + maxLength);
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }
    public static String readString(ByteBuf buf) {
        return readString(buf, MAX_LENGTH);
    }
    public static String readString(ByteBuf buf, @Nonnegative int maxLength) {
        int length = readVarInt(buf);
        // A java character can be encoded as up to 3 bytes.
        // 4-byte characters exist in UTF-8 but java sees them as two characters
        int maxEncodedLength = maxLength * 3;
        if (length > maxEncodedLength) {
            throw new IllegalArgumentException("Encoded string too long: " + length + " > " + maxEncodedLength);
        }
        if (length < 0) {
            throw new IllegalArgumentException("String length cannot be negative: " + length);
        }
        String str = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
        if (str.length() > maxLength) {
            throw new IllegalArgumentException("String too long: " + str.length() + " > " + maxLength);
        }
        buf.readerIndex(buf.readerIndex() + length);
        return str;
    }

    public static void writeNullableStringAsEmpty(ByteBuf buf, @Nullable String str) {
        writeNullableStringAsEmpty(buf, str, MAX_LENGTH);
    }
    public static void writeNullableStringAsEmpty(ByteBuf buf, @Nullable String str, @Nonnegative int maxLength) {
        writeString(buf, str == null ? "" : str, maxLength);
    }
    public static @Nullable String readNullableStringAsEmpty(ByteBuf buf) {
        return readNullableStringAsEmpty(buf, MAX_LENGTH);
    }
    public static @Nullable String readNullableStringAsEmpty(ByteBuf buf, @Nonnegative int maxLength) {
        String str = readString(buf, maxLength);
        return str.isEmpty() ? null : str;
    }

    public static void writeEnum(ByteBuf buf, Enum<?> value) {
        writeVarInt(buf, value.ordinal());
    }
    public static <T extends Enum<T>> T readEnum(ByteBuf buf, Class<T> enumClass) {
        int ordinal = readVarInt(buf);
        T[] values = enumClass.getEnumConstants();
        if (ordinal < 0 || values.length <= ordinal) {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        }
        return values[ordinal];
    }

    public static String debug(ByteBuf buf) {
        return "ByteBuf[readerIndex=" + buf.readerIndex() + ", writerIndex=" + buf.writerIndex() +
                ", capacity=" + buf.capacity() + ", data=" + asHex(buf) + "]";
    }
    public static String asHex(ByteBuf buf) {
        var sb = new StringBuilder();
        for (byte b : asArray(buf)) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
