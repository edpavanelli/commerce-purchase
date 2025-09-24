package net.mycompany.commerce.purchasemgmt.infrastructure.config.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseApiProperties {

	
	private String baseUrl;
    private String path;
}
