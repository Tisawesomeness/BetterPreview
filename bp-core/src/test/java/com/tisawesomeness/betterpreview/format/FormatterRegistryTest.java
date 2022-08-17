package com.tisawesomeness.betterpreview.format;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormatterRegistryTest {

    @Test
    public void testWriteEmpty() {
        var buf = Unpooled.buffer();
        FormatterRegistry.write(buf, null);
        assertThat(buf.readByte()).isEqualTo((byte) 0);
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
        assertThat(buf.readByte()).isEqualTo((byte) 1);
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testRead() {
        var buf = Unpooled.buffer();
        FormatterRegistry.write(buf, new NopFormatter());
        assertThat(FormatterRegistry.read(buf)).containsInstanceOf(NopFormatter.class);
    }

}
