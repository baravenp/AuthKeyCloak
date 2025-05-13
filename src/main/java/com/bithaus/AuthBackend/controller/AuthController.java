package com.bithaus.AuthBackend.controller;


import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
public class AuthController {

    /**
     * Refleja el JWT decodificado y validado.
     * Si el token no es válido, Spring responderá 401 automáticamente.
     */
    @GetMapping("/whoami")
    public Jwt whoAmI(@AuthenticationPrincipal Jwt jwt) {
        return jwt;
    }
}
