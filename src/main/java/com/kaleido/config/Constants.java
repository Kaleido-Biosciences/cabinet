package com.kaleido.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";
    public static final String SYSTEM_ACCOUNT = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String PLATEMAP_DRAFT_STATUS = "DRAFT";
    public static final String PLATEMAP_BUCKET_SYSTEM_KEY = "platemap_export_bucket";

    private Constants() {
    }
}
