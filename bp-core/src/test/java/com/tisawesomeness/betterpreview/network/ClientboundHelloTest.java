package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.format.FormatterStatus;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;
import com.tisawesomeness.betterpreview.format.NopFormatter;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientboundHelloTest {

    @Test
    public void test() {
        var hello = new ClientboundHello("1.2.3", FormatterUpdate.disabled(FormatterStatus.NO_PERMISSION));
        var buf = Unpooled.buffer();
        hello.write(buf);
        var result = new ClientboundHello(buf);
        assertThat(result.getServerVersion()).isEqualTo("1.2.3");
        var update = result.getUpdate();
        assertThat(update.getFormatter()).isEmpty();
        assertThat(update.getStatus()).isEqualTo(FormatterStatus.NO_PERMISSION);
        assertThat(buf.isReadable()).isFalse();
    }

    @Test
    public void testFormatter() {
        var hello = new ClientboundHello("0.1.0", FormatterUpdate.enabled(new NopFormatter()));
        var buf = Unpooled.buffer();
        hello.write(buf);
        var result = new ClientboundHello(buf);
        assertThat(result.getServerVersion()).isEqualTo("0.1.0");
        var update = result.getUpdate();
        assertThat(update.getFormatter()).containsInstanceOf(NopFormatter.class);
        assertThat(update.getStatus()).isEqualTo(FormatterStatus.OK);
        assertThat(buf.isReadable()).isFalse();
    }

}