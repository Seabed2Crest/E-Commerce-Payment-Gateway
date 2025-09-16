package com.ecommerce.app.config;

import com.ecommerce.app.filter.AuthTokenFilter;
import com.ecommerce.app.filter.TenantFilter;
import com.ecommerce.app.validations.accessdenied.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import static com.ecommerce.app.util.SecurityConstantsUrl.getTenantCustomerListUrls;
import static com.ecommerce.app.util.SecurityConstantsUrl.getWhiteListUrls;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;

    private final AuthTokenFilter authTokenFilter;

    private final TenantFilter tenantFilter;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(getWhiteListUrls()).permitAll()
                                .requestMatchers(getTenantCustomerListUrls()).authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception-> exception.accessDeniedHandler(customAccessDeniedHandler));

        http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(tenantFilter, AuthTokenFilter.class);

        return http.build();
    }
}
