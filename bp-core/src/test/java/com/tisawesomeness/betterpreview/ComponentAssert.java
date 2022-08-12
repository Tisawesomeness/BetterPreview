package com.tisawesomeness.betterpreview;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

@SuppressWarnings("UnusedReturnValue")
public class ComponentAssert extends AbstractAssert<ComponentAssert, Component> {

    private static final LegacyComponentSerializer DESERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    public ComponentAssert(Component actual) {
        super(actual, ComponentAssert.class);
    }
    public static ComponentAssert assertThat(Component actual) {
        return new ComponentAssert(actual);
    }

    /**
     * Checks if two components are similar by serializing them both using
     * {@link LegacyComponentSerializer#legacyAmpersand()}.
     * @param expected the expected component
     * @return this assertion
     */
    public ComponentAssert isSimilarTo(Component expected) {
        isNotNull();
        Assertions.assertThat(expected).isNotNull();
        String actualStr = DESERIALIZER.serialize(actual);
        String expectedStr = DESERIALIZER.serialize(expected);
        Assertions.assertThat(actualStr)
                .withFailMessage(() ->String.format("expected: \"%s\" from %s\n but was: \"%s\" from %s",
                        expectedStr, expected, actualStr, actual))
                .isEqualTo(expectedStr);
        return this;
    }
    /**
     * Checks if a component is similar to a string by serializing it using
     * {@link LegacyComponentSerializer#legacyAmpersand()}.
     * @param expected the expected string
     * @return this assertion
     */
    public ComponentAssert isSimilarTo(String expected) {
        isNotNull();
        Assertions.assertThat(expected).isNotNull();
        String actualStr = DESERIALIZER.serialize(actual);
        Assertions.assertThat(actualStr)
                .withFailMessage(() ->String.format("expected: \"%s\"\n but was: \"%s\" from %s",
                        expected, actualStr, actual))
                .isEqualTo(expected);
        return this;
    }

}
