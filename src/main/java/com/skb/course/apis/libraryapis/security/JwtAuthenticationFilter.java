package com.skb.course.apis.libraryapis.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skb.course.apis.libraryapis.user.LibraryUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

// JwtAuthenticationFilter is responsible for authentiating a user
// Extending UsernamePasswordAuthenticationFilter will add a new filter to Spring Security.
// When we add a new filter in Spring Security, then either we can explicitly define where in the filter chain we want
// that filter, or we can let the Spring Security framework to figure it out by itself.
// By extending the filter (UsernamePasswordAuthenticationFilter) provided within the security framework,
// Spring can automatically identify the best place to put it in the security chain.
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        LibraryUser libraryUser = null;
        try {
            libraryUser = new ObjectMapper().readValue(request.getInputStream(), LibraryUser.class);


            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(libraryUser.getUsername(),
                    libraryUser.getPassword(), new ArrayList<>()));
        } catch (BadCredentialsException e) {
            String errMsg = "Authentication failed for Username: " + libraryUser.getUsername();
            logger.error(errMsg, e.getMessage());
            throw e;
        } catch (Exception e) {
            //logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    // This method is called when a user successfully logs in. We use this method to generate a JWT for the
    // authenticated user.
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        LibraryUser principal = (LibraryUser) auth.getPrincipal();
        String token = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .withClaim("userId", principal.getUserId())
                .withClaim("role", principal.getRole().toString())
                .sign(HMAC512(SecurityConstants.SIGNING_SECRET.getBytes()));

        response.addHeader(SecurityConstants.AUTHORIZATION_HEADER_STRING,  SecurityConstants.BEARER_TOKEN_PREFIX + token);
    }
}
