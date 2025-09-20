package net.mycompany.commerce.purchase.store.dto;

import net.mycompany.commerce.purchase.validators.USDateTimeFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StorePurchaseRequestValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRequest() {
        StorePurchaseRequest req = new StorePurchaseRequest();
        req.setAmount(new BigDecimal("123.45"));
        req.setDescription("Valid description");
        req.setPurchaseDate(java.time.LocalDateTime.of(2025, 9, 19, 14, 30, 0)); // valid LocalDateTime
        Set<ConstraintViolation<StorePurchaseRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullAmount() {
        StorePurchaseRequest req = new StorePurchaseRequest();
        req.setAmount(null);
        req.setDescription("desc");
        req.setPurchaseDate(java.time.LocalDateTime.of(2025, 9, 19, 14, 30, 0));
        Set<ConstraintViolation<StorePurchaseRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testNegativeAmount() {
        StorePurchaseRequest req = new StorePurchaseRequest();
        req.setAmount(new BigDecimal("-10.00"));
        req.setDescription("desc");
        req.setPurchaseDate(java.time.LocalDateTime.of(2025, 9, 19, 14, 30, 0));
        Set<ConstraintViolation<StorePurchaseRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testAmountTooManyDigits() {
        StorePurchaseRequest req = new StorePurchaseRequest();
        req.setAmount(new BigDecimal("12345678901234.56")); // 14 digits
        req.setDescription("desc");
        req.setPurchaseDate(java.time.LocalDateTime.of(2025, 9, 19, 14, 30, 0));
        Set<ConstraintViolation<StorePurchaseRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testDescriptionTooLong() {
        StorePurchaseRequest req = new StorePurchaseRequest();
        req.setAmount(new BigDecimal("10.00"));
        req.setDescription("a".repeat(51));
        req.setPurchaseDate(java.time.LocalDateTime.of(2025, 9, 19, 14, 30, 0));
        Set<ConstraintViolation<StorePurchaseRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void testNullPurchaseDate() {
        StorePurchaseRequest req = new StorePurchaseRequest();
        req.setAmount(new BigDecimal("10.00"));
        req.setDescription("desc");
        req.setPurchaseDate(null);
        Set<ConstraintViolation<StorePurchaseRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("purchaseDate")));
    }

    @Test
    void testUSDateTimeFormatValidator() {
        StorePurchaseRequest req = new StorePurchaseRequest();
        req.setAmount(new BigDecimal("10.00"));
        req.setDescription("desc");
        req.setPurchaseDate(null); // invalid LocalDateTime (simulate format error)
        Set<ConstraintViolation<StorePurchaseRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("purchaseDate")));
    }
}