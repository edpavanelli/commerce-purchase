package net.mycompany.commerce.purchasemgmt.infrastructure.config.rest;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientFactory {

	public WebClient createJsonWebClient(BaseApiProperties props) {
        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(WebClientLoggingFilters.logRequest())
                .filter(WebClientLoggingFilters.logResponse())
                .build();
    }
    
}
