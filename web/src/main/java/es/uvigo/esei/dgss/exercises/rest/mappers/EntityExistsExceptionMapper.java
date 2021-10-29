package es.uvigo.esei.dgss.exercises.rest.mappers;

import javax.persistence.EntityExistsException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

public class EntityExistsExceptionMapper implements ExceptionMapper<EntityExistsException> {
	@Override
	public Response toResponse(EntityExistsException exception) {
		return Response
			.status(Status.CONFLICT)
			.entity(exception.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
