package net.mycompany.commerce.purchasemgmt.infrastructure.config.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "api.service.treasury")
public class TreasuryApiProperties extends BaseApiProperties {
    
}
