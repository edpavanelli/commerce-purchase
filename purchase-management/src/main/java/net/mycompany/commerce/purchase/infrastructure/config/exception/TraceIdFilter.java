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

        String traceId = idGenerator.nextId(); // Gera um ID único
        MDC.put("traceId", traceId); // Adiciona no MDC para logs SLF4J

        try {
            response.setHeader("X-Trace-Id", traceId); // opcional: retorna no header tb
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId"); // limpa no fim
        }
    }
}
