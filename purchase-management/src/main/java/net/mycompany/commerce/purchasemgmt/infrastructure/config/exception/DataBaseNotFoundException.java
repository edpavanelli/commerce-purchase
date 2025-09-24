package net.mycompany.commerce.purchasemgmt.infrastructure.config.exception;

public class DataBaseNotFoundException extends PurchaseExceptionsHandler {
    

    public DataBaseNotFoundException(String message) {
        super("NOT_FOUND", message);
        
    }

   
}