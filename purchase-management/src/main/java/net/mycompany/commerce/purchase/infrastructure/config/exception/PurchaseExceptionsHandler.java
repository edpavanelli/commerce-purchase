package net.mycompany.commerce.purchase.infrastructure.config.exception;

public class PurchaseExceptionsHandler extends RuntimeException {

	public final String code;
	
	
	public PurchaseExceptionsHandler(String code, String message) {
		super(message);
		this.code = code;
		
	}
	
	public String getCode() {
        return code;
    }
}
