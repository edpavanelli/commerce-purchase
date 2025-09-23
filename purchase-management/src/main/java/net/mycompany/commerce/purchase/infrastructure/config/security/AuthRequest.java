package net.mycompany.commerce.purchase.infrastructure.config.security;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AuthRequest", description = "Request body for authentication containing client credentials.")
public record AuthRequest(
    @Schema(description = "Client ID for authentication.", example = "myclient")
    String clientId,
    @Schema(description = "Client secret for authentication.", example = "changeit")
    String secret
) {}