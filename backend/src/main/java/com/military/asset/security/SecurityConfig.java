package com.military.asset.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler))
            .authorizeHttpRequests(auth -> auth
                // Unauthenticated public endpoints
                .requestMatchers("/api/auth/**", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                
                // Reference endpoints (Require Authentication)
                .requestMatchers("/api/bases/**", "/api/equipment-types/**").authenticated()
                
                // Admin only endpoints
                .requestMatchers("/api/audit-logs/**").hasAuthority("ROLE_ADMIN")
                
                // Assignments & Expenditures: Forbidden for Logistics Officer (Only ADMIN and BASE_COMMANDER)
                .requestMatchers("/api/assignments/**", "/api/expenditures/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_BASE_COMMANDER")
                
                // Purchases & Transfers: Accessible by ADMIN, BASE_COMMANDER, and LOGISTICS_OFFICER
                .requestMatchers("/api/purchases/**", "/api/transfers/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_BASE_COMMANDER", "ROLE_LOGISTICS_OFFICER")
                
                // Dashboard telemetry
                .requestMatchers("/api/dashboard/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_BASE_COMMANDER", "ROLE_LOGISTICS_OFFICER")
                
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        List<String> allowedOrigins = new ArrayList<>(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:5174"
        ));

        String envOrigins = System.getenv("ALLOWED_ORIGINS");
        if (envOrigins != null && !envOrigins.isBlank()) {
            for (String origin : envOrigins.split(",")) {
                if (!origin.isBlank()) {
                    allowedOrigins.add(origin.trim());
                }
            }
        }

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
