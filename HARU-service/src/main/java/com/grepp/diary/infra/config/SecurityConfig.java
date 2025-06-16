package com.grepp.diary.infra.config;

import static org.springframework.http.HttpMethod.GET;

import com.grepp.diary.app.model.auth.token.RefreshTokenService;
import com.grepp.diary.app.model.auth.token.UserBlackListRepository;
import com.grepp.diary.infra.auth.token.JwtAuthenticationEntryPoint;
import com.grepp.diary.infra.auth.token.JwtProvider;
import com.grepp.diary.infra.auth.token.filter.AuthExceptionFilter;
import com.grepp.diary.infra.auth.token.filter.JwtAuthenticationFilter;
import com.grepp.diary.infra.auth.token.filter.LogoutFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthExceptionFilter jwtExceptionFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;
    private final LogoutFilter logoutFilter;
    private final UserDetailsService userDetailsService;


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, refreshTokenService, userBlackListRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
//        .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().none()
            )
            .exceptionHandling(eh -> eh.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
<<<<<<< Updated upstream
                    .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                    .requestMatchers(GET, "/", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/auth/login", "/auth/logout", "/auth/find_id", "/auth/find_pw", "/auth/regist/**", "/auth/regist-mail","/auth/auth-id","/auth/auth-pw").permitAll()
                .requestMatchers("/auth/change-pw", "/auth/find-idpw","/member/leave", "/member/leave-success").permitAll()
                .requestMatchers("/custom/**").permitAll()
//                .anyRequest().permitAll() // 개발 중 전체 열기
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
=======
                .requestMatchers("/api/auth/csrf-token").permitAll()
                .requestMatchers(GET, "/", "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                .requestMatchers(
                    "/auth/**",
                    "/member/leave", "/member/leave-success",
                    "/custom/**"
                ).permitAll()
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            // ✅ 필터 순서 중요!
            .addFilterBefore(logoutFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtExceptionFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
>>>>>>> Stashed changes
