package net.mycompany.commerce.purchase.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import net.mycompany.commerce.purchase.domain.model.Currency;
import net.mycompany.commerce.purchase.domain.model.PurchaseTransaction;
import net.mycompany.commerce.purchase.domain.valueobject.TransactionId;
import java.time.LocalDate;
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
        validCurrency = Currency.builder()
            .code("USD")
            .name("US Dollar")
            .country("United States")
            .build();
    }

    @Test
    void testValidTransaction() {
        PurchaseTransaction tx = PurchaseTransaction.builder()
            .transactionId(new TransactionId("tx-1"))
            .amount(new BigDecimal("100.00"))
            .currency(validCurrency)
            .purchaseDate(LocalDate.now())
            .description("desc")
            .build();
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullTransactionId() {
        PurchaseTransaction tx = PurchaseTransaction.builder()
            .transactionId(null)
            .amount(new BigDecimal("100.00"))
            .currency(validCurrency)
            .purchaseDate(LocalDate.now())
            .description("desc")
            .build();
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("transactionId")));
    }

    @Test
    void testNullAmount() {
        PurchaseTransaction tx = PurchaseTransaction.builder()
            .transactionId(new TransactionId("tx-1"))
            .amount(null)
            .currency(validCurrency)
            .purchaseDate(LocalDate.now())
            .description("desc")
            .build();
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testNegativeAmount() {
        PurchaseTransaction tx = PurchaseTransaction.builder()
            .transactionId(new TransactionId("tx-1"))
            .amount(new BigDecimal("-10.00"))
            .currency(validCurrency)
            .purchaseDate(LocalDate.now())
            .description("desc")
            .build();
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testAmountTooManyDigits() {
        PurchaseTransaction tx = PurchaseTransaction.builder()
            .transactionId(new TransactionId("tx-1"))
            .amount(new BigDecimal("12345678901234.56"))
            .currency(validCurrency)
            .purchaseDate(LocalDate.now())
            .description("desc")
            .build();
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testNullCurrency() {
        PurchaseTransaction tx = PurchaseTransaction.builder()
            .transactionId(new TransactionId("tx-1"))
            .amount(new BigDecimal("100.00"))
            .currency(null)
            .purchaseDate(LocalDate.now())
            .description("desc")
            .build();
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("currency")));
    }

    @Test
    void testNullPurchaseDate() {
        PurchaseTransaction tx = PurchaseTransaction.builder()
            .transactionId(new TransactionId("tx-1"))
            .amount(new BigDecimal("100.00"))
            .currency(validCurrency)
            .purchaseDate(null)
            .description("desc")
            .build();
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("purchaseDate")));
    }

    @Test
    void testDescriptionTooLong() {
        PurchaseTransaction tx = PurchaseTransaction.builder()
            .transactionId(new TransactionId("tx-1"))
            .amount(new BigDecimal("100.00"))
            .currency(validCurrency)
            .purchaseDate(LocalDate.now())
            .description("a".repeat(81))
            .build();
        Set<ConstraintViolation<PurchaseTransaction>> violations = validator.validate(tx);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }
}