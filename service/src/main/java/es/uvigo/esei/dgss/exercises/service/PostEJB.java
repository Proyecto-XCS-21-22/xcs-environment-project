package es.uvigo.esei.dgss.exercises.service;

import java.net.URL;
import java.time.Duration;
import java.util.function.BiFunction;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

	@Resource
	private SessionContext ctx;

	@Inject
	private StatisticsEJB statistics;

	@Inject
	private UserEJB users;

	public Link addLink(URL url) {
		return addLink(users.getCurrent(), url);
	}

	public Link addLink(User author, URL url) {
		final Link link = new Link(author, url);

		em.persist(link);
		em.flush();

		statistics.incrementPostCount();

		return link;
	}

	public Video addVideo(Duration duration) {
		return addVideo(users.getCurrent(), duration);
	}

	public Video addVideo(User author, Duration duration) {
		final Video video = new Video(author, duration);

		em.persist(video);
		em.flush();

		statistics.incrementPostCount();

		return video;
	}

	public Photo addPhoto(byte[] content) {
		return addPhoto(users.getCurrent(), content);
	}

	public Photo addPhoto(User author, byte[] content) {
		final Photo photo = new Photo(author, content);

		em.persist(photo);
		em.flush();

		statistics.incrementPostCount();

		return photo;
	}

	// Type erasure makes Class<T> behave like Class<Post>, so JPA queries
	// can't really return instances of subclasses and we need to do the
	// unchecked cast. The cast is safe at runtime because we filter posts
	// by the class name of T
	@SuppressWarnings("unchecked")
	public <T extends Post> T get(long id, Class<T> type, boolean forEdition) {
		final boolean canEditAnyPost = isCurrentUserPrivileged();
		final Post post;

		// ":type = Post" is used in the queries below to disable filtering
		// by type if the type is the abstract base class, as there can not
		// be instances of an abstract type
		if (forEdition && !canEditAnyPost) {
			post = em.createQuery(
					"SELECT p FROM Post p WHERE (:type = Post OR TYPE(p) = :type) AND p.id = :id AND p.author.login = :login",
					Post.class
				)
				.setMaxResults(1)
				.setParameter("type", type)
				.setParameter("id", id)
				.setParameter("login", getCurrentUserLogin())
				.getSingleResult();
		} else {
			post = em.createQuery(
					"SELECT p FROM Post p WHERE (:type = Post OR TYPE(p) = :type) AND p.id = :id",
					Post.class
				)
				.setMaxResults(1)
				.setParameter("type", type)
				.setParameter("id", id)
				.getSingleResult();
		}

		return (T) post;
	}

	public boolean delete(long id) {
		final BiFunction<String, String, Query> updateQuerySupplier = (String dml, String filtertedDml) -> {
			final boolean canDeleteAnyPost = isCurrentUserPrivileged();
			final String actualDml;

			if (!canDeleteAnyPost) {
				actualDml = filtertedDml;
			} else {
				actualDml = dml;
			}

			final Query query = em.createQuery(actualDml);
			query.setParameter("id", id);

			if (!canDeleteAnyPost) {
				query.setParameter("login", getCurrentUserLogin());
			}

			return query;
		};

		// Comments are deleted first, because although our entity mapping
		// should be correct, Hibernate is not smart enough to cascade the
		// deletion to entities it hasn't loaded. For them to be loaded, we
		// have to retrieve the posts as objects first, which is terrible
		// from a performance standpoint.
		//
		// Therefore, do things the manual way, which is not elegant,
		// but it's free from these shenanigans and should not throw
		// performance out of the window that much.
		//
		// See: https://stackoverflow.com/questions/29733873/jpa-cascadetype-remove-not-working
		//
		// Also, we can't use straightforward syntax like
		// DELETE FROM Comment c WHERE c.post.author.login = :login because
		// that leads Hibernate to generate invalid SQL syntax for MySQL,
		// due to MySQL not supporting joins in DELETE FROM. See this bug
		// report: https://hibernate.atlassian.net/browse/HHH-9711
		//
		// Let the leaky abstraction party begin!

		updateQuerySupplier.apply(
			"DELETE FROM Comment c WHERE c.post.id = :id",
			"DELETE FROM Comment c WHERE c.post IN (SELECT p FROM Post p WHERE p.id = :id AND p.author.login = :login)"
		).executeUpdate();

		final int deletedPosts = updateQuerySupplier.apply(
			"DELETE FROM Post p WHERE p.id = :id",
			"DELETE FROM Post p WHERE p.id = :id AND p.author.login = :login"
		).executeUpdate();

		statistics.decrementPostCountBy(deletedPosts);

		return deletedPosts > 0;
	}

	public Comment addComment(Post post, User author, String text) {
		final Comment comment = new Comment(text, author, post);

		em.persist(comment);

		return comment;
	}

	private String getCurrentUserLogin() {
		return ctx.getCallerPrincipal().getName();
	}

	private boolean isCurrentUserPrivileged() {
		return ctx.isCallerInRole("admin");
	}
}
