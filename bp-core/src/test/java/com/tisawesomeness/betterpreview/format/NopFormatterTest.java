package com.tisawesomeness.betterpreview.format;

import org.junit.jupiter.api.Test;

import static com.tisawesomeness.betterpreview.ComponentAssert.assertThat;

class NopFormatterTest {

    @Test
    public void test() {
        var formatter = new NopFormatter();
        assertThat(formatter.format("test")).isSimilarTo("test");
    }

}
