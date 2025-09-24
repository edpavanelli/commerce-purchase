package net.mycompany.commerce.purchasemgmt.infrastructure.config.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.mycompany.commerce.purchasemgmt.infrastructure.config.exception.UnauthorizedException;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for client authentication and JWT token generation.")
public class AuthController {

    private final JwtService jwtService;

    @Value("${auth.client-id}")
    private String expectedClientId;

    @Value("${auth.client-secret}")
    private String expectedClientSecret;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Operation(
        summary = "Generate JWT token",
        description = "Authenticates client credentials and returns a JWT token if valid.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Token generated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid client credentials")
        }
    )
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