package net.mycompany.commerce.purchase.errorhandler;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.mycompany.commerce.purchase.Utils;

@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String traceId = Utils.getNanoId(); // Gera um ID único
        MDC.put("traceId", traceId); // Adiciona no MDC para logs SLF4J

        try {
            response.setHeader("X-Trace-Id", traceId); // opcional: retorna no header tb
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("traceId"); // limpa no fim
        }
    }
}
