package es.uvigo.esei.dgss.exercises.domain.converters;

import java.math.BigInteger;
import java.time.Duration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DurationToIntegerConverter implements AttributeConverter<Duration, BigInteger> {
	private static final BigInteger NS_IN_SECOND = BigInteger.valueOf(1000000000);

	@Override
	public BigInteger convertToDatabaseColumn(Duration attribute) {
		return BigInteger.valueOf(attribute.getSeconds()).multiply(NS_IN_SECOND)
				.add(BigInteger.valueOf(attribute.getNano()));
	}

	@Override
	public Duration convertToEntityAttribute(BigInteger dbData) {
		BigInteger[] durationFields = dbData.divideAndRemainder(NS_IN_SECOND);
		long seconds = durationFields[0].longValueExact();
		long nanoseconds_in_second = durationFields[1].longValueExact();

		return Duration.ofSeconds(seconds).plusNanos(nanoseconds_in_second);
	}
}
