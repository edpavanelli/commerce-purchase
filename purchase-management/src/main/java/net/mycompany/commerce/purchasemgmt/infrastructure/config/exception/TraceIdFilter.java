package net.mycompany.commerce.purchasemgmt.infrastructure.config.exception;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.mycompany.commerce.purchasemgmt.domain.port.TransactionIdGeneratorPort;

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
        String traceId = idGenerator.nextId(); 
        MDC.put("traceId", traceId); 
        try {
            response.setHeader("X-Trace-Id", traceId); 
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId");
        }
    }
}