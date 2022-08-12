package com.tisawesomeness.betterpreview.format;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassicFormatTest {

    @ParameterizedTest
    @EnumSource(ClassicFormat.class)
    public void testByCode(ClassicFormat format) {
        assertThat(ClassicFormat.byCode(format.getFormattingCode())).isEqualTo(format);
    }
    @Test
    public void testByCodeInvalid() {
        assertThat(ClassicFormat.byCode('z')).isNull();
    }

}