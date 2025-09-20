package net.mycompany.commerce.purchase.errorhandler;

public class DataBaseNotFoundException extends RuntimeException {
    private final String code;

    public DataBaseNotFoundException() {
        //super(message);
        this.code = "CURRENCY_NOT_FOUND";
    }

    public String getCode() {
        return code;
    }
}