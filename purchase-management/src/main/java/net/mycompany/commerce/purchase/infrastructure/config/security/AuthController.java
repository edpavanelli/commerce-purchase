package net.mycompany.commerce.purchase.infrastructure.config.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.mycompany.commerce.purchase.infrastructure.config.exception.UnauthorizedException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    @Value("${auth.client-id}")
    private String expectedClientId;

    @Value("${auth.client-secret}")
    private String expectedClientSecret;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(@RequestBody AuthRequest request) {


        if (!expectedClientId.equals(request.clientId()) ||
            !expectedClientSecret.equals(request.secret())) {
            throw new UnauthorizedException("Invalid client credentials");
        }

        String token = jwtService.generateToken(request.clientId());
        return ResponseEntity.ok(Map.of("token", token));
    }
}