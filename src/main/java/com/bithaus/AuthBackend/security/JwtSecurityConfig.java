package com.bithaus.AuthBackend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

// Importa tu converter custom, no el de Spring
import com.bithaus.AuthBackend.security.JwtAuthenticationConverter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class JwtSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${security.jwt.enabled:true}") boolean securityEnabled,
            JwtAuthenticationConverter jwtAuthConverter  // tu converter marcado @Component
    ) throws Exception {

        if (!securityEnabled) {
            return http
              .authorizeHttpRequests(a -> a.anyRequest().permitAll())
              .csrf(cs -> cs.disable())
              .build();
        }

        http
          .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .csrf(cs -> cs.disable())
          .cors(Customizer.withDefaults())
          .oauth2ResourceServer(oauth ->
              oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
          )
          .authorizeHttpRequests(auth ->
              auth.requestMatchers("/public/**", "/health").permitAll()
                  .anyRequest().authenticated()
          );

        return http.build();
    }
}
