package com.tisawesomeness.betterpreview.format;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FormatterUpdateTest {

    @Test
    public void testEnabled() {
        var update = FormatterUpdate.enabled(new NopFormatter());
        assertThat(update.getFormatter()).containsInstanceOf(NopFormatter.class);
        assertThat(update.getStatus()).isEqualTo(FormatterStatus.OK);
    }
    @Test
    public void testDisabled() {
        var update = FormatterUpdate.disabled(FormatterStatus.NO_PERMISSION);
        assertThat(update.getFormatter()).isEmpty();
        assertThat(update.getStatus()).isEqualTo(FormatterStatus.NO_PERMISSION);
    }
    @Test
    public void testDisabledError() {
        assertThatThrownBy(() -> FormatterUpdate.disabled(FormatterStatus.OK))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testEnabledWrite() {
        var update = FormatterUpdate.enabled(new NopFormatter());
        var buf = Unpooled.buffer();
        update.write(buf);
        var result = new FormatterUpdate(buf);
        assertThat(result.getFormatter()).containsInstanceOf(NopFormatter.class);
        assertThat(result.getStatus()).isEqualTo(FormatterStatus.OK);
        assertThat(buf.isReadable()).isFalse();
    }
    @Test
    public void testDisabledWrite() {
        var update = FormatterUpdate.disabled(FormatterStatus.NO_PERMISSION);
        var buf = Unpooled.buffer();
        update.write(buf);
        var result = new FormatterUpdate(buf);
        assertThat(result.getFormatter()).isEmpty();
        assertThat(result.getStatus()).isEqualTo(FormatterStatus.NO_PERMISSION);
        assertThat(buf.isReadable()).isFalse();
    }

}
