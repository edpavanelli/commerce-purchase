package net.mycompany.commerce.purchasemgmt.infrastructure.config.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class WebClientLoggingFilters {

    private static final Logger log = LoggerFactory.getLogger(WebClientLoggingFilters.class);

    public static ExchangeFilterFunction logRequest() {
        return (request, next) -> {
            if (log.isDebugEnabled()) { 
                log.debug("Request: {} {}", request.method(), request.url());
                request.headers()
                        .forEach((name, values) -> values.forEach(value ->
                                log.debug("{}={}", name, value)));
            }
            return next.exchange(request);
        };
    }

    public static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (log.isDebugEnabled()) {
                log.debug("Response Status: {}", response.statusCode());
            }
            return Mono.just(response);
        });
    }
}