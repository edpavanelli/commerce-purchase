package net.mycompany.commerce.purchase.infrastructure.config.exception;

public class PurchaseDomainException extends PurchaseExceptionsHandler{


    public PurchaseDomainException(String code, String message) {
        super(code, message);
    }

}
