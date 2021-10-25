package es.uvigo.esei.dgss.exercises.jsf.converters;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("duration")
public final class DurationConverter implements Converter {
	private static final Pattern DURATION_PATTERN = Pattern
		.compile("(?<hours>[0-9]+):(?<minutes>[0-9]{2}):(?<seconds>[0-9]{2})");

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		final Object result;

		// Javadoc says null should stay null
		// (they say something less obvious than "returns the converted object", yay!)
		if (value == null) {
			return null;
		}

		final Matcher matcher = DURATION_PATTERN.matcher(value);
		if (matcher.matches()) {
			long seconds = Integer.parseInt(matcher.group("seconds"));
			seconds += Integer.parseInt(matcher.group("minutes")) * 60;
			seconds += Integer.parseInt(matcher.group("hours")) * 3600;

			result = Duration.ofSeconds(seconds);
		} else {
			throw new ConverterException("Unrecognized duration format");
		}

		return result;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		final String result;

		if (value instanceof Duration) {
			final long seconds = ((Duration) value).getSeconds();

			result = String.format(
				"%d:%02d:%02d",
				seconds / 3600, (seconds % 3600) / 60, seconds % 60
			);
		} else {
			result = value.toString();
		}

		return result;
	}
}
