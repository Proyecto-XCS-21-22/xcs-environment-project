package es.uvigo.esei.dgss.exercises.domain.constraints;

import java.time.Duration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public final class PositiveDurationValidator implements ConstraintValidator<PositiveDuration, Duration> {
	@Override
	public void initialize(PositiveDuration constraintAnnotation) {}

	@Override
	public boolean isValid(Duration value, ConstraintValidatorContext context) {
		return value == null || !value.isNegative();
	}
}
