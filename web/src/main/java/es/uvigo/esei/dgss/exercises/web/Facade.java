package es.uvigo.esei.dgss.exercises.web;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

	public Collection<User> getFriends(String login) {
		final Collection<User> receivedFriends = em.createQuery(
			"SELECT f.receiver FROM Friendship f WHERE " +
			"f.receiver.login <> :login AND f.sender.login = :login AND f.accepted = true",
			User.class
		)
		.setParameter("login", login)
		.getResultList();

		final Collection<User> sentFriends = em.createQuery(
			"SELECT f.sender FROM Friendship f WHERE " +
			"f.sender.login <> :login AND f.receiver.login = :login AND f.accepted = true",
			User.class
		)
		.setParameter("login", login)
		.getResultList();

		// The queries above handled the case where sender and receiver are the same
		// (there are checks in the application code to avoid that, but the database does
		// not enforce that via triggers, so don't rely on that). Therefore, neither list
		// contains a user with our login.
		// Now handle a friendship being sent in both directions (i.e. from A to B and B
		// to A) and accepted, by using a set to remove duplicate users.
		// The database already enforces that each pair of users has a single row, at most
		final Set<User> friends = new HashSet<>(receivedFriends.size() + sentFriends.size());
		friends.addAll(receivedFriends);
		friends.addAll(sentFriends);

		return friends;
	}

	public Collection<Post> getPostsCommentedByFriendsAfterDate(String login, Date date) {
		return em.createQuery(
			"SELECT DISTINCT c.post "
			+ "FROM Comment c "
			+ "WHERE c.date > :date AND (c.author IN "
				+ "(SELECT f.receiver FROM Friendship f WHERE f.sender.login = :login AND f.accepted = true)"
				+ " OR c.author IN "
				+ "(SELECT f.sender FROM Friendship f WHERE f.receiver.login = :login AND f.accepted = true)"
			+ ")",
			Post.class
		)
		.setParameter("login", login)
		.setParameter("date", date)
		.getResultList();
	}

	public Collection<User> getFriendsWhoLikePost(String login, long postId) {
		return em.createQuery(
			"SELECT DISTINCT u "
			+ "FROM Post p JOIN p.likes u "
			+ "WHERE p.id = :postId AND (u IN "
				+ "(SELECT f.receiver FROM Friendship f WHERE f.sender.login = :login AND f.accepted = true)"
				+ " OR u IN "
				+ "(SELECT f.sender FROM Friendship f WHERE f.receiver.login = :login AND f.accepted = true)"
			+ ")",
			User.class
		)
		.setParameter("login", login)
		.setParameter("postId", postId)
		.getResultList();
	}

	public Collection<Photo> getLikedPictures(String login) {
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
