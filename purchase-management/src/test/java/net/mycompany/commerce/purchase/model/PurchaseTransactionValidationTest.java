package net.mycompany.commerce.purchase.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseTransactionValidationTest {
    private Validator validator;
    private Currency validCurrency;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validCurrency = new Currency("USD", "US Dollar", "United States");
    }

    @Test
    void testValidTransaction() {
        PurchaseTransaction tx = new PurchaseTransaction("tx-1", new BigDecimal("100.00"), validCurrency, LocalDateTime.now(), "desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullTransactionId() {
        PurchaseTransaction tx = new PurchaseTransaction(null, new BigDecimal("100.00"), validCurrency, LocalDateTime.now(), "desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("transactionId")));
    }

    @Test
    void testNullAmount() {
        PurchaseTransaction tx = new PurchaseTransaction("tx-1", null, validCurrency, LocalDateTime.now(), "desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testNegativeAmount() {
        PurchaseTransaction tx = new PurchaseTransaction("tx-1", new BigDecimal("-10.00"), validCurrency, LocalDateTime.now(), "desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testAmountTooManyDigits() {
        PurchaseTransaction tx = new PurchaseTransaction("tx-1", new BigDecimal("12345678901234.56"), validCurrency, LocalDateTime.now(), "desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testNullCurrency() {
        PurchaseTransaction tx = new PurchaseTransaction("tx-1", new BigDecimal("100.00"), null, LocalDateTime.now(), "desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currency")));
    }

    @Test
    void testNullPurchaseDate() {
        PurchaseTransaction tx = new PurchaseTransaction("tx-1", new BigDecimal("100.00"), validCurrency, null, "desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("purchaseDate")));
    }

    @Test
    void testDescriptionTooLong() {
        PurchaseTransaction tx = new PurchaseTransaction("tx-1", new BigDecimal("100.00"), validCurrency, LocalDateTime.now(), "a".repeat(81));
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }
}
