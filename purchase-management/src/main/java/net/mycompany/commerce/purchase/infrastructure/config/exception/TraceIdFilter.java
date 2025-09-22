package net.mycompany.commerce.purchase.infrastructure.config.exception;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.mycompany.commerce.purchase.domain.port.TransactionIdGeneratorPort;

@Component
public class TraceIdFilter extends OncePerRequestFilter {
	private final TransactionIdGeneratorPort idGenerator;
	public TraceIdFilter(TransactionIdGeneratorPort idGenerator) {
		this.idGenerator = idGenerator;
	}

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = idGenerator.nextId(); // Generates a unique ID
        MDC.put("traceId", traceId); // Adds to MDC for SLF4J logs
        try {
            response.setHeader("X-Trace-Id", traceId); // optional: also returns in the header
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId"); // cleans up at the end
        }
    }
}