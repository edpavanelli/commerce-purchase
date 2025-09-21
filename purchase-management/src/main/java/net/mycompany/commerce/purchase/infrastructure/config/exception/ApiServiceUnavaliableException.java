package net.mycompany.commerce.purchase.infrastructure.config.exception;

public class ApiServiceUnavaliableException extends PurchaseExceptionsHandler{

	public ApiServiceUnavaliableException(String message) {
		super("SERVICE_UNAVAILABLE", message);
		
	}

}
