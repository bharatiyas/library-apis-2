package com.skb.course.apis.libraryapis.security;

public class SecurityConstants {

    public static final String SIGNING_SECRET = "s0m3R@nd0mStr1n9";
    public static final long EXPIRATION_TIME = 864_000_000;
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER_STRING = "Authorization";
    public static final String  NEW_USER_REGISTERATION_URL = "/users/register";
    public static String NEW_USER_DEFAULT_PASSWORD = "Password123";

}
