package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.format.ChatFormatter;
import com.tisawesomeness.betterpreview.format.ClassicFormatter;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientboundUpdateTest {

    @Test
    public void test() {
        var hello = new ClientboundUpdate((ChatFormatter) null);
        var buf = Unpooled.buffer();
        hello.write(buf);
        assertThat(new ClientboundUpdate(buf).getFormatter()).isEmpty();
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testFormatter() {
        var hello = new ClientboundUpdate(new ClassicFormatter('&'));
        var buf = Unpooled.buffer();
        hello.write(buf);
        assertThat(new ClientboundUpdate(buf).getFormatter()).containsInstanceOf(ClassicFormatter.class);
        assertThat(buf.isReadable()).isFalse();
    }

}
