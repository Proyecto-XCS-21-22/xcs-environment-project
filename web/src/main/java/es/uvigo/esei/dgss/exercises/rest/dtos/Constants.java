package es.uvigo.esei.dgss.exercises.rest.dtos;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.ws.rs.core.UriBuilder;

import es.uvigo.esei.dgss.exercises.domain.User;

class Constants {
	static final Function<Date, String> DATE_FORMATTER = (Date d) ->
		DateTimeFormatter.ISO_INSTANT.format(d.toInstant());

	static final BiFunction<User, UriBuilder, String> USER_RESOURCE = (User u, UriBuilder baseUri) ->
		baseUri.clone().path(u.getLogin()).build().toASCIIString();

	private Constants() {}
}
