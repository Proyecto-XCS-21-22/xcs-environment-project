package es.uvigo.esei.dgss.exercises.domain.constraints;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public final class ValidAddressValidator implements ConstraintValidator<ValidAddress, InternetAddress> {
	@Override
	public void initialize(ValidAddress constraintAnnotation) {}

	@Override
	public boolean isValid(InternetAddress value, ConstraintValidatorContext context) {
		try {
			value.validate();

			return !value.isGroup() && value.getPersonal() == null;
		} catch (final NullPointerException | AddressException exc) {
			return false;
		}
	}
}
