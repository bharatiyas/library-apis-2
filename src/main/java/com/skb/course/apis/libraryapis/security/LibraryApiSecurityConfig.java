package com.skb.course.apis.libraryapis.security;


import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class LibraryApiSecurityConfig extends WebSecurityConfigurerAdapter {

    private LibraryUserDetailsServiceImpl libraryUserDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public LibraryApiSecurityConfig(LibraryUserDetailsServiceImpl libraryUserDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.libraryUserDetailsService = libraryUserDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    protected void configure(HttpSecurity httpSecurity) {
        try {
            httpSecurity.cors().and().csrf().disable().authorizeRequests()
                    .antMatchers(HttpMethod.POST, SecurityConstants.NEW_USER_REGISTERATION_URL).permitAll()
                    .antMatchers(HttpMethod.GET, "/users/search").permitAll()
                    .antMatchers(HttpMethod.GET, "/books/search").permitAll()
                    .antMatchers(HttpMethod.GET, "/authors/search").permitAll()
                    .antMatchers(HttpMethod.GET, "/publishers/search").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                    .addFilter(new JwtAuthorizationFilter(authenticationManager()))
                    // this disables session creation on Spring Security
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(libraryUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
}
