package es.uvigo.esei.dgss.exercises.domain.constraints;

import java.lang.reflect.Field;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public final class DifferentSenderAndReceiverValidator implements ConstraintValidator<DifferentSenderAndReceiver, Object> {
	@Override
	public void initialize(DifferentSenderAndReceiver constraintAnnotation) {}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		boolean valid = value == null;

		if (!valid) {
			try {
				Field senderField = value.getClass().getDeclaredField("sender");
				Field receiverField = value.getClass().getDeclaredField("receiver");

				senderField.setAccessible(true);
				receiverField.setAccessible(true);

				valid = !Objects.equals(senderField.get(value), receiverField.get(value));
			} catch (NoSuchFieldException exc) {
				// There is not a sender or receiver field
				valid = true;
			} catch (IllegalAccessException exc) {
				throw new AssertionError(
					"The JVM enforced field access control even though the target fields were made accessible",
					exc
				);
			}
		}

		return valid;
	}
}
