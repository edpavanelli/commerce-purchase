package net.mycompany.commerce.purchase.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;

public class USDateTimeFormatValidator implements ConstraintValidator<USDateTimeFormat, LocalDateTime> {
    private static final DateTimeFormatter US_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            // Try to format and parse to ensure strict ISO format
            String formatted = value.format(US_FORMATTER);
            LocalDateTime.parse(formatted, US_FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
