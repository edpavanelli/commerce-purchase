package net.mycompany.commerce.purchase.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = USDateTimeFormatValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface USDateTimeFormat {
    String message() default "invalid date pattern";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
