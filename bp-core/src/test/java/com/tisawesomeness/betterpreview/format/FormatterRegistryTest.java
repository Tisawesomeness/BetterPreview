package com.tisawesomeness.betterpreview.format;

import com.tisawesomeness.betterpreview.network.ByteBufs;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FormatterRegistryTest {

    @Test
    public void testWriteEmpty() {
        var buf = Unpooled.buffer();
        FormatterRegistry.write(buf, null);
        assertThat(ByteBufs.readVarInt(buf)).isEqualTo(0);
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testReadEmpty() {
        var buf = Unpooled.buffer();
        FormatterRegistry.write(buf, null);
        assertThat(FormatterRegistry.read(buf)).isEmpty();
    }

    @Test
    public void testWrite() {
        var buf = Unpooled.buffer();
        FormatterRegistry.write(buf, new NopFormatter());
        assertThat(ByteBufs.readVarInt(buf)).isEqualTo(1);
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testRead() {
        var buf = Unpooled.buffer();
        FormatterRegistry.write(buf, new NopFormatter());
        assertThat(FormatterRegistry.read(buf)).containsInstanceOf(NopFormatter.class);
    }

    @Test
    public void testReadInvalid() {
        var buf = Unpooled.buffer();
        ByteBufs.writeVarInt(buf, -1);
        assertThatThrownBy(() -> FormatterRegistry.read(buf)).isInstanceOf(IllegalArgumentException.class);
    }

}
