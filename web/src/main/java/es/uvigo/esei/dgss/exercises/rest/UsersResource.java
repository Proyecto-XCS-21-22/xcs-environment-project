package es.uvigo.esei.dgss.exercises.rest;

import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.rest.dto.ListDTO;
import es.uvigo.esei.dgss.exercises.rest.dto.PostDTO;
import es.uvigo.esei.dgss.exercises.rest.dto.UserDTO;
import es.uvigo.esei.dgss.exercises.rest.dto.UserRegistrationDTO;
import es.uvigo.esei.dgss.exercises.service.UserEJB;

@Path("/users")
@Stateless
public class UsersResource {
	@Context
	private UriInfo uriInfo;

	@Inject
	private UserEJB users;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(UserRegistrationDTO registrationDto) {
		if (registrationDto == null) {
			throw new IllegalArgumentException("No data provided");
		}

		try {
			users.add(
				registrationDto.getLogin(), registrationDto.getName(),
				new InternetAddress(registrationDto.getEmail()),
				registrationDto.getPassword(), registrationDto.getPicture()
			);
		} catch (final AddressException exc) {
			throw new IllegalArgumentException(exc);
		}

		return Response.created(
			uriInfo.getAbsolutePathBuilder()
				.path(registrationDto.getLogin())
				.build()
		).build();
	}

	@GET
	@Path("/{login}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("login") String login) {
		return Response.ok(
			UserDTO.of(users.get(login), uriInfo.getAbsolutePathBuilder())
		).build();
	}

	@GET
	@Path("/{login}/posts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPosts(@PathParam("login") String login) {
		return Response.ok(
			new ListDTO<>(users.getAuthoredPosts(login).stream()
				.map((Post p) -> PostDTO.of(
					p,
					uriInfo.getBaseUriBuilder().path(UsersResource.class)
				))
				.collect(Collectors.toList()))
		).build();
	}
}
