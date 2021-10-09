package es.uvigo.esei.dgss.exercises.web;

import java.util.Collection;
import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.uvigo.esei.dgss.exercises.domain.Photo;
import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.domain.User;

@Dependent
public class Facade {
	@PersistenceContext
	private EntityManager em;

	public Collection<Post> getPostsCommentedByFriendsAfterDate(String login, Date date) {
		// TODO: test that this query yields the expected results
		return em.createQuery(
			"SELECT DISTINCT c.post "
			+ "FROM Comment c "
			+ "WHERE c.date > :date AND (c.author IN "
				+ "(SELECT f.receiver FROM Friendship f WHERE f.sender.login = :login)"
				+ " OR c.author IN "
				+ "(SELECT f.sender FROM Friendship f WHERE f.receiver.login = :login)"
			+ ")",
			Post.class
		)
		.setParameter("login", login)
		.setParameter("date", date)
		.getResultList();
	}

	public Collection<User> getFriendsWhoLikePost(String login, long postId) {
		// TODO: test that this query yields the expected results
		return em.createQuery(
			"SELECT DISTINCT u "
			+ "FROM Post p JOIN p.likes u "
			+ "WHERE p.id = :postId AND (u IN "
				+ "(SELECT f.receiver FROM Friendship f WHERE f.sender.login = :login)"
				+ " OR u IN "
				+ "(SELECT f.sender FROM Friendship f WHERE f.receiver.login = :login)"
			+ ")",
			User.class
		)
		.setParameter("login", login)
		.setParameter("postId", postId)
		.getResultList();
	}

	public Collection<Photo> getLikedPictures(String login) {
		// TODO: test that this query yields the expected results
		return em.createQuery(
			"SELECT p "
			+ "FROM User u JOIN u.likedPosts p "
			+ "WHERE u.login = :login AND TYPE(p) = Photo",
			Photo.class
		)
		.setParameter("login", login)
		.getResultList();
	}
}
