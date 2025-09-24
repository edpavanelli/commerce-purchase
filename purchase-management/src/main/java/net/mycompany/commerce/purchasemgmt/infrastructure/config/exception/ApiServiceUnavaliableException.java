package net.mycompany.commerce.purchasemgmt.infrastructure.config.exception;

public class ApiServiceUnavaliableException extends PurchaseExceptionsHandler{

	public ApiServiceUnavaliableException(String message) {
		super("SERVICE_UNAVAILABLE", message);
		
	}

}
