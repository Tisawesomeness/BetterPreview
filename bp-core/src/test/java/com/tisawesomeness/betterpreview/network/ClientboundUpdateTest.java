package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.format.ClassicFormatter;
import com.tisawesomeness.betterpreview.format.FormatterStatus;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientboundUpdateTest {

    @Test
    public void test() {
        var hello = new ClientboundUpdate(FormatterUpdate.disabled(FormatterStatus.NO_PERMISSION));
        var buf = Unpooled.buffer();
        hello.write(buf);
        assertThat(new ClientboundUpdate(buf).getUpdate().getFormatter()).isEmpty();
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testFormatter() {
        var hello = new ClientboundUpdate(FormatterUpdate.enabled(new ClassicFormatter('&')));
        var buf = Unpooled.buffer();
        hello.write(buf);
        assertThat(new ClientboundUpdate(buf).getUpdate().getFormatter()).containsInstanceOf(ClassicFormatter.class);
        assertThat(buf.isReadable()).isFalse();
    }

}
