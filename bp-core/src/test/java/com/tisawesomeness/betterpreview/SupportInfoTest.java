package com.tisawesomeness.betterpreview;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SupportInfoTest {

    @Test
    public void testSupported() {
        var si = SupportInfo.supported("1.0.0");
        assertThat(si.getStatus()).isEqualTo(SupportStatus.FULL_SUPPORT);
        assertThat(si.getRequestedVersion()).isEqualTo("1.0.0");
        assertThatThrownBy(si::getMessage).isInstanceOf(IllegalStateException.class);
    }
    @Test
    public void testUnsupported() {
        var si = SupportInfo.unsupported(SupportStatus.OUTDATED, "0.0.1", null);
        assertThat(si.getStatus()).isEqualTo(SupportStatus.OUTDATED);
        assertThat(si.getRequestedVersion()).isEqualTo("0.0.1");
        assertThat(si.getMessage()).isEmpty();
    }
    @Test
    public void testLimited() {
        var si = SupportInfo.unsupported(SupportStatus.LIMITED_SUPPORT, "2.8.4-pre2", "msg");
        assertThat(si.getStatus()).isEqualTo(SupportStatus.LIMITED_SUPPORT);
        assertThat(si.getRequestedVersion()).isEqualTo("2.8.4-pre2");
        assertThat(si.getMessage()).contains("msg");
    }

    @Test
    public void testSupportedWrite() {
        var si = SupportInfo.supported("1.0.0");
        var buf = Unpooled.buffer();
        si.write(buf);
        var result = new SupportInfo(buf);
        assertThat(result.getStatus()).isEqualTo(SupportStatus.FULL_SUPPORT);
        assertThat(result.getRequestedVersion()).isEqualTo("1.0.0");
        assertThatThrownBy(result::getMessage).isInstanceOf(IllegalStateException.class);
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testUnsupportedWrite() {
        var si = SupportInfo.unsupported(SupportStatus.OUTDATED, "0.0.1", null);
        var buf = Unpooled.buffer();
        si.write(buf);
        var result = new SupportInfo(buf);
        assertThat(result.getStatus()).isEqualTo(SupportStatus.OUTDATED);
        assertThat(result.getRequestedVersion()).isEqualTo("0.0.1");
        assertThat(result.getMessage()).isEmpty();
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testLimitedWrite() {
        var si = SupportInfo.unsupported(SupportStatus.LIMITED_SUPPORT, "2.8.4-pre2", "msg");
        var buf = Unpooled.buffer();
        si.write(buf);
        var result = new SupportInfo(buf);
        assertThat(result.getStatus()).isEqualTo(SupportStatus.LIMITED_SUPPORT);
        assertThat(result.getRequestedVersion()).isEqualTo("2.8.4-pre2");
        assertThat(result.getMessage()).contains("msg");
        assertThat(buf.isReadable()).isFalse();
    }

    @Test
    public void testUnsupportedError() {
        assertThatThrownBy(() -> SupportInfo.unsupported(SupportStatus.FULL_SUPPORT, "3.2.1", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
