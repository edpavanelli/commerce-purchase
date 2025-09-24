package net.mycompany.commerce.purchasemgmt.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import net.mycompany.commerce.purchasemgmt.domain.model.Currency;
import net.mycompany.commerce.purchasemgmt.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchasemgmt.domain.valueobject.TransactionId;

import java.time.LocalDate;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseTransactionValidationTest {
    private Validator validator;
    private Currency validCurrency;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validCurrency = Currency.builder()
            .code("USD")
            .name("US Dollar")
            .country("United States")
            .build();
    }

    @Test
    void testValidTransaction() {
        PurchaseTransaction tx = new PurchaseTransaction();
        tx.setTransactionId(new TransactionId("tx-1"));
        tx.setAmount(new BigDecimal("100.00"));
        tx.setCurrency(validCurrency);
        tx.setPurchaseDate(LocalDate.now());
        tx.setDescription("desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullTransactionId() {
        PurchaseTransaction tx = new PurchaseTransaction();
        tx.setTransactionId(null);
        tx.setAmount(new BigDecimal("100.00"));
        tx.setCurrency(validCurrency);
        tx.setPurchaseDate(LocalDate.now());
        tx.setDescription("desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("transactionId")));
    }

    @Test
    void testNullAmount() {
        PurchaseTransaction tx = new PurchaseTransaction();
        tx.setTransactionId(new TransactionId("tx-1"));
        tx.setAmount(null);
        tx.setCurrency(validCurrency);
        tx.setPurchaseDate(LocalDate.now());
        tx.setDescription("desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testNegativeAmount() {
        PurchaseTransaction tx = new PurchaseTransaction();
        tx.setTransactionId(new TransactionId("tx-1"));
        tx.setAmount(new BigDecimal("-10.00"));
        tx.setCurrency(validCurrency);
        tx.setPurchaseDate(LocalDate.now());
        tx.setDescription("desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testAmountTooManyDigits() {
        PurchaseTransaction tx = new PurchaseTransaction();
        tx.setTransactionId(new TransactionId("tx-1"));
        tx.setAmount(new BigDecimal("12345678901234.56"));
        tx.setCurrency(validCurrency);
        tx.setPurchaseDate(LocalDate.now());
        tx.setDescription("desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testNullCurrency() {
        PurchaseTransaction tx = new PurchaseTransaction();
        tx.setTransactionId(new TransactionId("tx-1"));
        tx.setAmount(new BigDecimal("100.00"));
        tx.setCurrency(null);
        tx.setPurchaseDate(LocalDate.now());
        tx.setDescription("desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currency")));
    }

    @Test
    void testNullPurchaseDate() {
        PurchaseTransaction tx = new PurchaseTransaction();
        tx.setTransactionId(new TransactionId("tx-1"));
        tx.setAmount(new BigDecimal("100.00"));
        tx.setCurrency(validCurrency);
        tx.setPurchaseDate(null);
        tx.setDescription("desc");
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("purchaseDate")));
    }

    @Test
    void testDescriptionTooLong() {
        PurchaseTransaction tx = new PurchaseTransaction();
        tx.setTransactionId(new TransactionId("tx-1"));
        tx.setAmount(new BigDecimal("100.00"));
        tx.setCurrency(validCurrency);
        tx.setPurchaseDate(LocalDate.now());
        tx.setDescription("a".repeat(81));
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }
}