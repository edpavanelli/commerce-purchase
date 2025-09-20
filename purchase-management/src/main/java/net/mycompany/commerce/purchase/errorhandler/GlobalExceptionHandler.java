package net.mycompany.commerce.purchase.errorhandler;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataBaseNotFoundException.class)
    public ResponseEntity<ApiError> handleDataBaseNotFound(DataBaseNotFoundException ex) {
        String traceId = MDC.get("traceId");
        ApiError error = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                ex.getCode(),
                "We are facing troubles, please contact the support team.",
                traceId
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        String traceId = MDC.get("traceId");
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "We are facing troubles, please contact the support team.",
                traceId
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
