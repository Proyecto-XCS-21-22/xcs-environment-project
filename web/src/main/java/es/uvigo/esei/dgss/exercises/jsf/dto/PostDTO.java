package es.uvigo.esei.dgss.exercises.jsf.dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import es.uvigo.esei.dgss.exercises.domain.Link;
import es.uvigo.esei.dgss.exercises.domain.Photo;
import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.domain.User;
import es.uvigo.esei.dgss.exercises.domain.Video;

public abstract class PostDTO {
	private long id;
	private Date date;
	private int commentCount;
	private List<String> likeLogins;

	public static PostDTO of(Post p) {
		final PostDTO dto;

		if (p instanceof Link) {
			final Link link = (Link) p;
			final LinkDTO linkDto = new LinkDTO();

			initializeBaseDTOFields(linkDto, link);
			linkDto.setUrl(link.getUrl().toString());

			dto = linkDto;
		} else if (p instanceof Photo) {
			final Photo photo = (Photo) p;
			final PhotoDTO photoDto = new PhotoDTO();

			initializeBaseDTOFields(photoDto, photo);
			photoDto.setContent(photo.getContent());

			dto = photoDto;
		} else if (p instanceof Video) {
			final Video video = (Video) p;
			final VideoDTO videoDto = new VideoDTO();

			initializeBaseDTOFields(videoDto, video);
			videoDto.setDuration(video.getDuration());

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public List<String> getLikeLogins() {
		return likeLogins;
	}

	public void setLikeLogins(List<String> likeLogins) {
		this.likeLogins = likeLogins;
	}

	private static void initializeBaseDTOFields(PostDTO dto, Post post) {
		dto.setId(post.getId());
		dto.setDate(post.getDate());
		dto.setCommentCount(post.getComments().size());
		dto.setLikeLogins(post.getLikes().stream().map(
			(User u) -> u.getLogin()
		).collect(Collectors.toList()));
	}
}
