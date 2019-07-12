package com.skb.course.apis.libraryapis.security;

public class SecurityConstants {

    private static final String SIGNING_SECRET = "s0m3R@nd0mStr1n9";
    private static final long EXPIRATION_TIME = 864_000_000;
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER_STRING = "Authorization";
    private static final String  NEW_USER_REGISTERATION_URL = "/users/register";
    private static String NEW_USER_DEFAULT_PASSWORD = "Password123";

    public static String getSigningSecret() {
        return SIGNING_SECRET;
    }

    public static long getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public static String getBearerTokenPrefix() {
        return BEARER_TOKEN_PREFIX;
    }

    public static String getAuthorizationHeaderString() {
        return AUTHORIZATION_HEADER_STRING;
    }

    public static String getNewUserRegisterationUrl() {
        return NEW_USER_REGISTERATION_URL;
    }

    public static String getNewUserDefaultPassword() {
        return NEW_USER_DEFAULT_PASSWORD;
    }
}
