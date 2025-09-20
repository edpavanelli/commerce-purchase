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
        Currency currency = new Currency("USD", "US Dollar", "United States");
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCodeTooShort() {
        Currency currency = new Currency("US", "US Dollar", "United States");
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
    }

    @Test
    void testCodeBlank() {
        Currency currency = new Currency("", "US Dollar", "United States");
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
    }

    @Test
    void testNameTooLong() {
        Currency currency = new Currency("USD", "a".repeat(51), "United States");
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testCountryBlank() {
        Currency currency = new Currency("USD", "US Dollar", "");
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("country")));
    }

    @Test
    void testCountryTooLong() {
        Currency currency = new Currency("USD", "US Dollar", "a".repeat(51));
        Set<ConstraintViolation<Currency>> violations = validator.validate(currency);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("country")));
    }
}
