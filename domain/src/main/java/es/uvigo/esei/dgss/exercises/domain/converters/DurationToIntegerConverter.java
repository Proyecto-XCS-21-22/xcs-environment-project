package es.uvigo.esei.dgss.exercises.domain.converters;

import java.time.Duration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DurationToIntegerConverter implements AttributeConverter<Duration, Long> {
	@Override
	public Long convertToDatabaseColumn(Duration attribute) {
		return attribute.toNanos();
	}

	@Override
	public Duration convertToEntityAttribute(Long dbData) {
		return Duration.ofNanos(dbData);
	}
}
