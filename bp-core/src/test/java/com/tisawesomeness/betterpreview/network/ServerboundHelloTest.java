package com.tisawesomeness.betterpreview.network;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServerboundHelloTest {

    @Test
    public void test() {
        var hello = new ServerboundHello("1.2.3");
        var buf = Unpooled.buffer();
        hello.write(buf);
        assertThat(new ServerboundHello(buf).getClientVersion()).isEqualTo("1.2.3");
        assertThat(buf.isReadable()).isFalse();
    }

}
