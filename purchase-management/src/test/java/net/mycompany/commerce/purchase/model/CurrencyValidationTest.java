package net.mycompany.commerce.purchase.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyValidationTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidCurrency() {
        Currency currency = Currency.builder()
            .code("USD")
            .name("US Dollar")
            .country("United States")
            .build();
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCodeTooShort() {
        Currency currency = Currency.builder()
            .code("US")
            .name("US Dollar")
            .country("United States")
            .build();
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
    }

    @Test
    void testCodeBlank() {
        Currency currency = Currency.builder()
            .code("")
            .name("US Dollar")
            .country("United States")
            .build();
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
    }

    @Test
    void testNameTooLong() {
        Currency currency = Currency.builder()
            .code("USD")
            .name("a".repeat(51))
            .country("United States")
            .build();
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testCountryBlank() {
        Currency currency = Currency.builder()
            .code("USD")
            .name("US Dollar")
            .country("")
            .build();
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("country")));
    }

    @Test
    void testCountryTooLong() {
        Currency currency = Currency.builder()
            .code("USD")
            .name("US Dollar")
            .country("a".repeat(51))
            .build();
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("country")));
    }
}