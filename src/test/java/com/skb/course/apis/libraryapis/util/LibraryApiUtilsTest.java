package com.skb.course.apis.libraryapis.util;

import com.auth0.jwt.JWT;
import com.skb.course.apis.libraryapis.testutils.LibraryApiTestUtil;
import com.skb.course.apis.libraryapis.testutils.TestConstants;
import com.skb.course.apis.libraryapis.user.LibraryUser;
import com.skb.course.apis.libraryapis.user.Role;
import com.skb.course.apis.libraryapis.security.SecurityConstants;
import org.junit.Test;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.junit.Assert.*;

public class LibraryApiUtilsTest {

    @Test
    public void doesStringValueExist() {

        assertTrue(LibraryApiUtils.doesStringValueExist("ValueExist"));
        assertFalse(LibraryApiUtils.doesStringValueExist(""));
        assertFalse(LibraryApiUtils.doesStringValueExist(null));

    }

    @Test
    public void getUserIdFromClaim() {
        String jwtToken = createJwtToken(1234, Role.USER);
        assertEquals(1234, LibraryApiUtils.getUserIdFromClaim(jwtToken));
    }

    @Test
    public void getRoleFromClaim_user() {
        String jwtToken = createJwtToken(1234, Role.USER);
        assertTrue(LibraryApiUtils.getRoleFromClaim(jwtToken).equals("USER"));
    }

    @Test
    public void getRoleFromClaim_admin() {
        String jwtToken = createJwtToken(1234, Role.ADMIN);
        assertTrue(LibraryApiUtils.getRoleFromClaim(jwtToken).equals("ADMIN"));
    }

    @Test
    public void isUserAdmin_user() {
        String jwtToken = createJwtToken(1234, Role.USER);
        assertFalse(LibraryApiUtils.isUserAdmin(jwtToken));
    }

    @Test
    public void isUserAdmin_admin() {
        String jwtToken = createJwtToken(1234, Role.ADMIN);
        assertTrue(LibraryApiUtils.isUserAdmin(jwtToken));
    }

    private String createJwtToken(int userId, Role role) {
        LibraryUser principal = LibraryApiTestUtil.createUser(TestConstants.TEST_USER_USERNAME);
        principal.setUserId(userId);
        principal.setRole(role);
        String token = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .withClaim("userId", principal.getUserId())
                .withClaim("role", principal.getRole().toString())
                .sign(HMAC512(SecurityConstants.SIGNING_SECRET.getBytes()));

        return token;
    }
}