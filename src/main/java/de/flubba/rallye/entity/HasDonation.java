package de.flubba.rallye.entity;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DonationValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasDonation {
    String message() default ""; //this message is never actually shown

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
