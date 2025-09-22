package net.mycompany.commerce.purchase.infrastructure.config.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class USDateTimeFormatValidator implements ConstraintValidator<USDateTimeFormat, LocalDate> {
    private static final DateTimeFormatter US_FORMATTER = DateTimeFormatter.ISO_DATE;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            // Try to format and parse to ensure strict ISO format
            String formatted = value.format(US_FORMATTER);
            LocalDate.parse(formatted, US_FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
