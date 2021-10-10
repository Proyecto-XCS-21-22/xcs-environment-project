package es.uvigo.esei.dgss.exercises.domain.converters;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class MailAddressToStringConverter implements AttributeConverter<InternetAddress, String> {
	@Override
	public String convertToDatabaseColumn(InternetAddress attribute) {
		return attribute.getAddress();
	}

	@Override
	public InternetAddress convertToEntityAttribute(String dbData) {
		try {
			return new InternetAddress(dbData, true);
		} catch (AddressException exc) {
			// The JPA converter API does not specify how to handle conversion
			// errors, so this should do as a better design than just returning
			// null and blowing up with a NPE later
			throw new UncheckedAddressException(exc);
		}
	}

	private static class UncheckedAddressException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private UncheckedAddressException(Throwable cause) {
			super(cause);
		}
	}
}
