package com.grepp.diary.infra.config;

import static org.springframework.http.HttpMethod.GET;

import com.grepp.diary.app.model.auth.token.RefreshTokenService;
import com.grepp.diary.app.model.auth.token.UserBlackListRepository;
import com.grepp.diary.infra.auth.token.JwtAuthenticationEntryPoint;
import com.grepp.diary.infra.auth.token.JwtProvider;
import com.grepp.diary.infra.auth.token.filter.AuthExceptionFilter;
import com.grepp.diary.infra.auth.token.filter.JwtAuthenticationFilter;
import com.grepp.diary.infra.auth.token.filter.LogoutFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthExceptionFilter jwtExceptionFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;
    private final LogoutFilter logoutFilter;

    public SecurityConfig(
        PasswordEncoder passwordEncoder,
        @Qualifier("authService") UserDetailsService userDetailsService,
        AuthExceptionFilter jwtExceptionFilter,
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
        JwtProvider jwtProvider,
        RefreshTokenService refreshTokenService,
        UserBlackListRepository userBlackListRepository,
        LogoutFilter logoutFilter
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtExceptionFilter = jwtExceptionFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtProvider = jwtProvider;
        this.refreshTokenService = refreshTokenService;
        this.userBlackListRepository = userBlackListRepository;
        this.logoutFilter = logoutFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, refreshTokenService, userBlackListRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().none()
            )
            .exceptionHandling(eh -> eh.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/csrf-token").permitAll()
                .requestMatchers(GET, "/", "/css/**", "/js/**", "/images/**", "/assets/**", "/uploads/**").permitAll()
                .requestMatchers(
                    "/auth/**",
                    "/member/leave", "/member/leave-success",
                    "/custom/**"
                ).permitAll()
                .requestMatchers("/mail/verify").permitAll()
                .requestMatchers("/api/ai/list/img").permitAll()
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(logoutFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtExceptionFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
