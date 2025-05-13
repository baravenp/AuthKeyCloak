package com.bithaus.AuthBackend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JwtAuthenticationConverter
     implements Converter<Jwt, JwtAuthenticationToken> {

    // Claim que usaremos como principal (sub, email…)
    @Value("${jwt.principal-claim:sub}")
    private String principalClaim;

    // Client ID configurado en Keycloak
    @Value("${jwt.resource-id}")
    private String resourceId;

    // Converter por defecto para scope y realm_roles
    private final JwtGrantedAuthoritiesConverter defaultConverter =
        new JwtGrantedAuthoritiesConverter();

    @Override
    public JwtAuthenticationToken convert(Jwt token) {
        // 1) Authorities “estándar”
        Collection<GrantedAuthority> authorities =
            new HashSet<>(defaultConverter.convert(token));

        // 2) Añadir roles de resource_access[resourceId]
        Map<String, Object> resourceAccess =
            token.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey(resourceId)) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>)
              ((Map<String, ?>)resourceAccess.get(resourceId)).get("roles");
            for (String r : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
            }
        }

        // 3) Principal: extraigo el claim (sub, email…)
        String principal = token.getClaim(principalClaim);

        // 4) Construyo el Authentication
        return new JwtAuthenticationToken(token, authorities, principal);
    }
}