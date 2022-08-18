package com.tisawesomeness.betterpreview.format;

public enum FormatterStatus {
    /** BetterPreview is enabled */
    OK,
    /** BetterPreview is disabled for an unknown reason */
    UNKNOWN,
    /** Disabled for the user */
    USER_DISABLED,
    /** Disabled for the server */
    SERVER_DISABLED,
    /** Player cannot chat, so does not need preview */
    MUTED,
    /** Player does not have permission to receive previews */
    NO_PERMISSION
}
