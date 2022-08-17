package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.format.NopFormatter;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientboundHelloTest {

    @Test
    public void test() {
        var hello = new ClientboundHello("1.2.3", null);
        var buf = Unpooled.buffer();
        hello.write(buf);
        var result = new ClientboundHello(buf);
        assertThat(result.getServerVersion()).isEqualTo("1.2.3");
        assertThat(result.getFormatter()).isEmpty();
        assertThat(buf.isReadable()).isFalse();
    }

    @Test
    public void testFormatter() {
        var hello = new ClientboundHello("0.1.0", new NopFormatter());
        var buf = Unpooled.buffer();
        hello.write(buf);
        var result = new ClientboundHello(buf);
        assertThat(result.getServerVersion()).isEqualTo("0.1.0");
        assertThat(result.getFormatter()).containsInstanceOf(NopFormatter.class);
        assertThat(buf.isReadable()).isFalse();
    }

}