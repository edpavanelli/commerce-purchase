package net.mycompany.commerce.purchasemgmt.store.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import net.mycompany.commerce.purchasemgmt.application.purchase.dto.StorePurchaseRequestDto;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        StorePurchaseRequestDto req = StorePurchaseRequestDto.builder()
            .amount(new BigDecimal("123.45"))
            .description("Valid description")
            .purchaseDate(LocalDate.of(2025, 9, 19))
            .build();
        Set<ConstraintViolation<StorePurchaseRequestDto>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullAmount() {
        StorePurchaseRequestDto req = StorePurchaseRequestDto.builder()
            .amount(null)
            .description("desc")
            .purchaseDate(LocalDate.of(2025, 9, 19))
            .build();
        Set<ConstraintViolation<StorePurchaseRequestDto>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testNegativeAmount() {
        StorePurchaseRequestDto req = StorePurchaseRequestDto.builder()
            .amount(new BigDecimal("-10.00"))
            .description("desc")
            .purchaseDate(LocalDate.of(2025, 9, 19))
            .build();
        Set<ConstraintViolation<StorePurchaseRequestDto>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testAmountTooManyDigits() {
        StorePurchaseRequestDto req = StorePurchaseRequestDto.builder()
            .amount(new BigDecimal("12345678901234.56")) // 14 digits
            .description("desc")
            .purchaseDate(LocalDate.of(2025, 9, 19))
            .build();
        Set<ConstraintViolation<StorePurchaseRequestDto>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
    }

    @Test
    void testDescriptionTooLong() {
        StorePurchaseRequestDto req = StorePurchaseRequestDto.builder()
            .amount(new BigDecimal("10.00"))
            .description("a".repeat(51))
            .purchaseDate(LocalDate.of(2025, 9, 19))
            .build();
        Set<ConstraintViolation<StorePurchaseRequestDto>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void testNullPurchaseDate() {
        StorePurchaseRequestDto req = StorePurchaseRequestDto.builder()
            .amount(new BigDecimal("10.00"))
            .description("desc")
            .purchaseDate(null)
            .build();
        Set<ConstraintViolation<StorePurchaseRequestDto>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("purchaseDate")));
    }
}