package es.uvigo.esei.dgss.exercises.rest;

import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.rest.dtos.ListDTO;
import es.uvigo.esei.dgss.exercises.rest.dtos.PostDTO;
import es.uvigo.esei.dgss.exercises.service.UserEJB;

@Path("/wall")
@Stateless
public class WallResource {
	@Context
	private UriInfo uriInfo;

	@Inject
	private UserEJB users;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response show() {
		return Response.ok(
			new ListDTO<>(users.getWallPosts().stream()
				.map((Post p) -> PostDTO.of(
					p,
					uriInfo.getBaseUriBuilder().path(UsersResource.class)
				))
				.collect(Collectors.toList()))
		).build();
	}
}
