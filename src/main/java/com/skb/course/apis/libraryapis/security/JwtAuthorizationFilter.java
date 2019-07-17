package com.skb.course.apis.libraryapis.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        // Validate if the Authorization header is present in HTTP header
        String authorizationHeader = request.getHeader(SecurityConstants.getAuthorizationHeaderString());

        if(authorizationHeader == null || !authorizationHeader.startsWith(SecurityConstants.getBearerTokenPrefix())){
            chain.doFilter(request, response);
            return;
        }

        // If Authorization header is present then proceed further
        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(authorizationHeader);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }


    private UsernamePasswordAuthenticationToken getAuthentication(String authorizationHeader) {

        // Verify the token and fetch the user information and set it in SecurityContext to access later
        if(authorizationHeader != null) {
            String userNameFromJwt = JWT.require(Algorithm.HMAC512(SecurityConstants.getSigningSecret()))
                        .build()
                        .verify(authorizationHeader.replace(SecurityConstants.getBearerTokenPrefix(), ""))
                        .getSubject();
            if(userNameFromJwt != null) {
                return new UsernamePasswordAuthenticationToken(userNameFromJwt, null, new ArrayList<>());
            }
        }
        return null;
    }
}
