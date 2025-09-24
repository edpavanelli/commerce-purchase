package net.mycompany.commerce.purchasemgmt.infrastructure.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Purchase Management API",
        version = "1.0.0",
        description = "API for managing purchases, currency exchange, and store transactions.",
        contact = @Contact(name = "Support Team", email = "support@mycompany.net"),
        license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")
    )
)
public class OpenApiConfig {
}
