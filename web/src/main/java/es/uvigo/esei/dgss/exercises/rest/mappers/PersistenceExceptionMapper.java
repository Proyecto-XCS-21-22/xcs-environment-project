package es.uvigo.esei.dgss.exercises.rest.mappers;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {
	@Override
	public Response toResponse(PersistenceException exception) {
		final Status status;

		if (exception instanceof EntityExistsException) {
			status = Status.BAD_REQUEST;
		} else {
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response
			.status(status)
			.entity("Database operation error")
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
