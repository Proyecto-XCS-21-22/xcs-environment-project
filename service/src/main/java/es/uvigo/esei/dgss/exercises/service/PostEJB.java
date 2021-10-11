package es.uvigo.esei.dgss.exercises.service;

import java.net.URL;
import java.time.Duration;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.uvigo.esei.dgss.exercises.domain.Comment;
import es.uvigo.esei.dgss.exercises.domain.Link;
import es.uvigo.esei.dgss.exercises.domain.Photo;
import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.domain.User;
import es.uvigo.esei.dgss.exercises.domain.Video;

@Stateless
public class PostEJB {
	@PersistenceContext
	private EntityManager em;

	@Inject
	private StatisticsEJB statistics;

	public Link addLink(User author, URL url) {
		final Link link = new Link(author, url);

		em.persist(link);
		em.flush();

		statistics.incrementPostCount();

		return link;
	}

	public Video addVideo(User author, Duration duration) {
		final Video video = new Video(author, duration);

		em.persist(video);
		em.flush();

		statistics.incrementPostCount();

		return video;
	}

	public Photo addPhoto(User author, byte[] content) {
		final Photo photo = new Photo(author, content);

		em.persist(photo);
		em.flush();

		statistics.incrementPostCount();

		return photo;
	}

	public <T extends Post> T get(long id, Class<T> type) {
		return em.find(type, id);
	}

	public void delete(Post post) {
		delete(post.getId());
	}

	public boolean delete(long id) {
		int deletedPosts = em.createQuery(
			"DELETE FROM Post p WHERE p.id = :id"
		).setParameter("id", id).executeUpdate();

		statistics.decrementPostCountBy(deletedPosts);

		return deletedPosts > 0;
	}

	public Comment addComment(Post post, User author, String text) {
		final Comment comment = new Comment(text, author, post);

		em.persist(comment);

		return comment;
	}
}
