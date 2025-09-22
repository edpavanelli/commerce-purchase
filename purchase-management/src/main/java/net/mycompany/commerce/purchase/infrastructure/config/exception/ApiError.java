package net.mycompany.commerce.purchase.infrastructure.config.exception;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private int status;
    private String code;
    private String message;
    private Instant timestamp;
    private String traceId;

    public ApiError(int status, String code, String message, String traceId) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.timestamp = Instant.now();
        this.traceId = traceId;
    }

	
}
