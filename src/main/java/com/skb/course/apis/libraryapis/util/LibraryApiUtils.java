package com.skb.course.apis.libraryapis.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.skb.course.apis.libraryapis.security.SecurityConstants;

public class LibraryApiUtils {

    public static boolean doesStringValueExist(String str) {
        if(str != null && str.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int getUserIdFromClaim(String jwtString) {
        return JWT.require(Algorithm.HMAC512(SecurityConstants.getSigningSecret()))
                .build()
                .verify(jwtString.replace(SecurityConstants.getBearerTokenPrefix(), ""))
                .getClaim("userId").asInt();
    }

    public static String getRoleFromClaim(String jwtString) {
        return JWT.require(Algorithm.HMAC512(SecurityConstants.getSigningSecret()))
                .build()
                .verify(jwtString.replace(SecurityConstants.getBearerTokenPrefix(), ""))
                .getClaim("role").asString();
    }

    public static boolean isUserAdmin(String jwtString) {
        String role = JWT.require(Algorithm.HMAC512(SecurityConstants.getSigningSecret()))
                .build()
                .verify(jwtString.replace(SecurityConstants.getBearerTokenPrefix(), ""))
                .getClaim("role").asString();

        return role.equals("ADMIN");
    }
}
