package de.flubba.rallye.entity;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DonationValidator implements ConstraintValidator<HasDonation, Sponsor> {
    @Override
    public boolean isValid(Sponsor value, ConstraintValidatorContext context) {
        if (value.getOneTimeDonation() == null && value.getPerLapDonation() == null) {
            context.buildConstraintViolationWithTemplate("foo")
                    .addPropertyNode("oneTimeDonation")
                    .addConstraintViolation();
            context.buildConstraintViolationWithTemplate("bar")
                    .addPropertyNode("perLapDonation")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
