package es.uvigo.esei.dgss.exercises.rest;

import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import es.uvigo.esei.dgss.exercises.domain.Friendship;
import es.uvigo.esei.dgss.exercises.rest.dto.FriendshipDTO;
import es.uvigo.esei.dgss.exercises.rest.dto.ListDTO;
import es.uvigo.esei.dgss.exercises.service.UserEJB;

@Path("/friendships")
@Stateless
public class FriendshipsResource {
	@Context
	private UriInfo uriInfo;

	@Inject
	private UserEJB users;

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response add(@FormParam("targetLogin") String targetLogin) {
		users.addFriendship(targetLogin);

		return Response.noContent().build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get() {
		return Response.ok(
			new ListDTO<>(users.getCurrent()
				.getReceivedFriendships().stream()
				.map((Friendship f) -> FriendshipDTO.of(
					f, uriInfo.getBaseUriBuilder().path(UsersResource.class)
				))
				.collect(Collectors.toList()))
		).build();
	}

	@PUT
	@Path("/{senderLogin}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response changeAcceptedStatus(
		@PathParam("senderLogin") String senderLogin,
		@FormParam("accepted") boolean accepted
	) {
		users.setRequestedFriendshipAcceptedStatus(senderLogin, accepted);

		return Response.noContent().build();
	}
}
