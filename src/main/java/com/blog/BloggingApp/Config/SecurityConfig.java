package com.blog.BloggingApp.Config;

import com.blog.BloggingApp.Jwt.JwtAuthenticationFilter;
import com.blog.BloggingApp.Jwt.JwtConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtConfig jwtConfig;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RedisRateLimitingFilter redisRateLimitingFilter;

    public SecurityConfig(JwtConfig jwtConfig, JwtAuthenticationFilter jwtAuthenticationFilter, RedisRateLimitingFilter redisRateLimitingFilter) {
        this.jwtConfig = jwtConfig;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.redisRateLimitingFilter = redisRateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF (replace with your CSRF configuration if needed)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users/getAll", "/api/v1/comments/{commentId}",
                                "/api/v1/posts/{postId}")
                        .permitAll()
                        .requestMatchers("/api/v1/posts/all", "/api/v1/posts/comments/{postId}", "/auth/signup",
                                "/auth/login")
                        .permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // Ensure logout URL is correctly set
                        .logoutSuccessUrl("/auth/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .authenticationProvider(jwtConfig.daoAuthenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Ensure JWT
                                                                                                      // authentication
                                                                                                      // filter is
                                                                                                      // before
                                                                                                      // UsernamePasswordAuthenticationFilter
                .addFilterBefore(redisRateLimitingFilter, JwtAuthenticationFilter.class); // Ensure rate limiting filter
                                                                                          // is before JWT
                                                                                          // authentication filter

        return http.build();
    }
}
