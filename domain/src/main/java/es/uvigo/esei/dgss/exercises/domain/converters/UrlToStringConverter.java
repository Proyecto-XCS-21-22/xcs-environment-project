package es.uvigo.esei.dgss.exercises.domain.converters;

import java.net.MalformedURLException;
import java.net.URL;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class UrlToStringConverter implements AttributeConverter<URL, String> {
	@Override
	public String convertToDatabaseColumn(URL attribute) {
		return attribute.toString();
	}

	@Override
	public URL convertToEntityAttribute(String dbData) {
		try {
			return new URL(dbData);
		} catch (MalformedURLException exc) {
			// Built-in conversors specific to the Hibernate JPA implementation
			// also throw an unchecked exception under the rug:
			// https://github.com/hibernate/hibernate-orm/blob/ec841c0d6c97c03b9f0877fc924e462e3b9ec8d2/hibernate-core/src/main/java/org/hibernate/type/descriptor/java/UrlTypeDescriptor.java#L31-L37
			//
			// The JPA converter API does not specify how to handle conversion
			// errors, so this should do as a better design than just returning
			// null and blowing up with a NPE later
			throw new UncheckedMalformedURLException(exc);
		}
	}

	private static class UncheckedMalformedURLException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private UncheckedMalformedURLException(Throwable cause) {
			super(cause);
		}
	}
}
