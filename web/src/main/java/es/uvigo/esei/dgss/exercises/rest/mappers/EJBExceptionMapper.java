package es.uvigo.esei.dgss.exercises.rest.mappers;

import javax.ejb.EJBException;
import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {
	@Override
	public Response toResponse(EJBException exception) {
		final Status status;
		final Throwable cause = exception.getCause();

		if (
			cause instanceof ValidationException ||
			cause instanceof IllegalArgumentException ||
			cause instanceof EntityExistsException
		) {
			status = Status.BAD_REQUEST;
		} else if (cause instanceof NoResultException) {
			status = Status.NOT_FOUND;
		} else {
			status = Status.INTERNAL_SERVER_ERROR;
		}

		return Response
			.status(status)
			.entity(exception.getMessage())
			.type(MediaType.TEXT_PLAIN)
			.build();
	}
}
