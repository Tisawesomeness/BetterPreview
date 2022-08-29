package com.tisawesomeness.betterpreview.network;

import com.tisawesomeness.betterpreview.SupportInfo;
import com.tisawesomeness.betterpreview.SupportStatus;
import com.tisawesomeness.betterpreview.format.FormatterStatus;
import com.tisawesomeness.betterpreview.format.FormatterUpdate;
import com.tisawesomeness.betterpreview.format.NopFormatter;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClientboundHelloTest {

    @Test
    public void test() {
        var si = SupportInfo.supported("1.0.0");
        var update = FormatterUpdate.disabled(FormatterStatus.NO_PERMISSION);
        var hello = ClientboundHello.withUpdate("1.2.3", si, update);

        var buf = Unpooled.buffer();
        hello.write(buf);
        var result = new ClientboundHello(buf);

        assertThat(result.getServerVersion()).isEqualTo("1.2.3");

        var resultSi = result.getSupportInfo();
        assertThat(resultSi.getStatus()).isEqualTo(SupportStatus.FULL_SUPPORT);
        assertThat(resultSi.getRequestedVersion()).isEqualTo("1.0.0");

        var resultUpdate = result.getUpdate();
        assertThat(resultUpdate.getFormatter()).isEmpty();
        assertThat(resultUpdate.getStatus()).isEqualTo(FormatterStatus.NO_PERMISSION);

        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testOutdated() {
        var si = SupportInfo.unsupported(SupportStatus.OUTDATED, "1.0.0", null);
        var hello = ClientboundHello.withoutUpdate("1.2.3", si);

        var buf = Unpooled.buffer();
        hello.write(buf);
        var result = new ClientboundHello(buf);

        assertThat(result.getServerVersion()).isEqualTo("1.2.3");

        var resultSi = result.getSupportInfo();
        assertThat(resultSi.getStatus()).isEqualTo(SupportStatus.OUTDATED);
        assertThat(resultSi.getRequestedVersion()).isEqualTo("1.0.0");
        assertThat(resultSi.getMessage()).isEmpty();

        assertThatThrownBy(result::getUpdate).isInstanceOf(IllegalStateException.class);

        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testLimited() {
        var si = SupportInfo.unsupported(SupportStatus.LIMITED_SUPPORT, "1.0.0", null);
        var update = FormatterUpdate.enabled(new NopFormatter());
        var hello = ClientboundHello.withUpdate("1.2.3", si, update);

        var buf = Unpooled.buffer();
        hello.write(buf);
        var result = new ClientboundHello(buf);

        assertThat(result.getServerVersion()).isEqualTo("1.2.3");

        var resultSi = result.getSupportInfo();
        assertThat(resultSi.getStatus()).isEqualTo(SupportStatus.LIMITED_SUPPORT);
        assertThat(resultSi.getRequestedVersion()).isEqualTo("1.0.0");
        assertThat(resultSi.getMessage()).isEmpty();

        var resultUpdate = result.getUpdate();
        assertThat(resultUpdate.getFormatter()).containsInstanceOf(NopFormatter.class);
        assertThat(resultUpdate.getStatus()).isEqualTo(FormatterStatus.OK);

        assertThat(buf.isReadable()).isFalse();
    }

    @Test
    public void testError() {
        var si = SupportInfo.unsupported(SupportStatus.OUTDATED, "1.0.0", "msg");
        var update = FormatterUpdate.disabled(FormatterStatus.NO_PERMISSION);
        assertThatThrownBy(() -> ClientboundHello.withUpdate("1.2.3", si, update))
                .isInstanceOf(IllegalStateException.class);
    }
    @Test
    public void testOutdatedError() {
        var si = SupportInfo.supported("1.0.0");
        assertThatThrownBy(() -> ClientboundHello.withoutUpdate("1.2.3", si))
                .isInstanceOf(IllegalStateException.class);
    }

}
