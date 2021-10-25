package es.uvigo.esei.dgss.exercises.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import es.uvigo.esei.dgss.exercises.domain.Link;
import es.uvigo.esei.dgss.exercises.domain.Photo;
import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.domain.Video;
import es.uvigo.esei.dgss.exercises.rest.dto.LinkManipulationDTO;
import es.uvigo.esei.dgss.exercises.rest.dto.PhotoManipulationDTO;
import es.uvigo.esei.dgss.exercises.rest.dto.PostManipulationDTO;
import es.uvigo.esei.dgss.exercises.rest.dto.VideoManipulationDTO;
import es.uvigo.esei.dgss.exercises.service.PostEJB;
import es.uvigo.esei.dgss.exercises.service.UserEJB;

@Path("/posts")
@Stateless
public class PostsResource {
	@Context
	private UriInfo uriInfo;

	@Inject
	private PostEJB posts;

	@Inject
	private UserEJB users;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(PostManipulationDTO postDto) {
		final Post addedPost;

		if (postDto == null) {
			throw new IllegalArgumentException("No data provided");
		}

		if (postDto instanceof LinkManipulationDTO) {
			try {
				addedPost = posts.addLink(
					new URL(((LinkManipulationDTO) postDto).getUrl())
				);
			} catch (final MalformedURLException exc) {
				throw new IllegalArgumentException(exc);
			}
		} else if (postDto instanceof PhotoManipulationDTO) {
			addedPost = posts.addPhoto(
				((PhotoManipulationDTO) postDto).getContent()
			);
		} else if (postDto instanceof VideoManipulationDTO) {
			addedPost = posts.addVideo(
				Duration.ofNanos(((VideoManipulationDTO) postDto).getDuration())
			);
		} else {
			throw new AssertionError("Missing post subclass mapping");
		}

		return Response.created(
			uriInfo.getAbsolutePathBuilder()
				.path(Long.toString(addedPost.getId()))
				.build()
		).build();
	}

	@POST
	@Path("/{id}/like")
	public Response like(@PathParam("id") long postId) {
		final Post post = posts.get(postId, Post.class, false);

		users.likePost(post);

		return Response.noContent().build();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response edit(@PathParam("id") long postId, PostManipulationDTO postDto) {
		if (postDto == null) {
			throw new IllegalArgumentException("No data provided");
		}

		if (postDto instanceof LinkManipulationDTO) {
			final Link link = posts.get(postId, Link.class, true);

			try {
				link.setUrl(new URL(((LinkManipulationDTO) postDto).getUrl()));
			} catch (final MalformedURLException exc) {
				throw new IllegalArgumentException(exc);
			}
		} else if (postDto instanceof PhotoManipulationDTO) {
			final Photo photo = posts.get(postId, Photo.class, true);

			photo.setContent(((PhotoManipulationDTO) postDto).getContent());
		} else if (postDto instanceof VideoManipulationDTO) {
			final Video video = posts.get(postId, Video.class, true);

			video.setDuration(
				Duration.ofNanos(((VideoManipulationDTO) postDto).getDuration())
			);
		} else {
			throw new AssertionError("Missing post subclass mapping");
		}

		return Response.noContent().build();
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") long postId) {
		posts.delete(postId);

		return Response.noContent().build();
	}
}
