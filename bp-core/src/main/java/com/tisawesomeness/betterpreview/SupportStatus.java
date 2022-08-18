package com.tisawesomeness.betterpreview;

public enum SupportStatus {
    FULL_SUPPORT,
    LIMITED_SUPPORT,
    NO_SUPPORT,
    OUTDATED;

    public boolean supportsPreviews() {
        return this == FULL_SUPPORT || this == LIMITED_SUPPORT;
    }
}
