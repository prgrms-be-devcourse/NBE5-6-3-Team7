package com.grepp.diary.infra.config;

import static org.springframework.http.HttpMethod.GET;

import com.grepp.diary.app.model.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${remember-me.key}")
    private String rememberMeKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(@Lazy AuthService authService) {
        return authService;
    }

    @Bean
    public RememberMeServices rememberMeServices(@Lazy UserDetailsService userDetailsService) {
        TokenBasedRememberMeServices services = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService);
        services.setAlwaysRemember(false); // 체크박스 체크시에만 remember-me 작동
        services.setCookieName("remember-me");
        services.setTokenValiditySeconds(60 * 60 * 24 * 7); // 7일
        return services;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
//            .csrf(csrf -> csrf
//                .ignoringRequestMatchers("/api/**", "/admin/**", "/member/**", "/diary/**", "/app/**", "/ai/**", "/auth/**")
//            )
            .formLogin(login -> login
                .loginPage("/")
                .loginProcessingUrl("/none")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID", "remember-me")
                .invalidateHttpSession(true)
                .permitAll()
            )
            .rememberMe(rememberMe -> rememberMe
                .key(rememberMeKey)
                .rememberMeParameter("remember-me")
                .rememberMeServices(rememberMeServices(userDetailsService))
            )
            .authorizeHttpRequests(auth -> auth
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
