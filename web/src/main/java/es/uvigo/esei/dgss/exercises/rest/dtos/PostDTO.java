package es.uvigo.esei.dgss.exercises.rest.dtos;

import static es.uvigo.esei.dgss.exercises.rest.dtos.Constants.DATE_FORMATTER;
import static es.uvigo.esei.dgss.exercises.rest.dtos.Constants.USER_RESOURCE;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import es.uvigo.esei.dgss.exercises.domain.Comment;
import es.uvigo.esei.dgss.exercises.domain.Link;
import es.uvigo.esei.dgss.exercises.domain.Photo;
import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.domain.Video;

public abstract class PostDTO {
	private long id;
	private String date;
	private String authorResource;
	private List<CommentDTO> comments;
	private int likes;

	public static PostDTO of(Post p, UriBuilder baseUri) {
		final PostDTO dto;

		if (p instanceof Link) {
			final Link link = (Link) p;
			final LinkDTO linkDto = new LinkDTO();

			initializeBaseDTOFields(linkDto, link, baseUri);
			linkDto.setUrl(link.getUrl().toString());

			dto = linkDto;
		} else if (p instanceof Photo) {
			final Photo photo = (Photo) p;
			final PhotoDTO photoDto = new PhotoDTO();

			initializeBaseDTOFields(photoDto, photo, baseUri);
			photoDto.setContent(photo.getContent());

			dto = photoDto;
		} else if (p instanceof Video) {
			final Video video = (Video) p;
			final VideoDTO videoDto = new VideoDTO();

			initializeBaseDTOFields(videoDto, video, baseUri);
			videoDto.setDuration(video.getDuration().toNanos());

			dto = videoDto;
		} else {
			throw new AssertionError("Missing post subclass mapping");
		}

		return dto;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAuthorResource() {
		return authorResource;
	}

	public void setAuthorResource(String authorResource) {
		this.authorResource = authorResource;
	}

	public List<CommentDTO> getComments() {
		return comments;
	}

	public void setComments(List<CommentDTO> comments) {
		this.comments = comments;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	private static void initializeBaseDTOFields(PostDTO dto, Post post, UriBuilder baseUsersUri) {
		dto.setId(post.getId());
		dto.setDate(DATE_FORMATTER.apply(post.getDate()));
		dto.setAuthorResource(USER_RESOURCE.apply(post.getAuthor(), baseUsersUri));
		dto.setComments(post.getComments().stream().map(
			(Comment c) -> CommentDTO.of(c, baseUsersUri)
		).collect(Collectors.toList()));
		dto.setLikes(post.getLikes().size());
	}
}
