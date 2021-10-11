package es.uvigo.esei.dgss.exercises.rest.mappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
	@Override
	public Response toResponse(IllegalArgumentException exception) {
		return Response
			.status(Status.BAD_REQUEST)
			.entity(exception.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
