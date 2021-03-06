package es.uvigo.esei.dgss.exercises.service;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import es.uvigo.esei.dgss.exercises.domain.Friendship;
import es.uvigo.esei.dgss.exercises.domain.Post;
import es.uvigo.esei.dgss.exercises.domain.User;

@Stateless
public class UserEJB {
	@PersistenceContext
	private EntityManager em;

	@Resource
	private SessionContext ctx;

	@Inject
	private StatisticsEJB statistics;

	@Inject
	private EmailService email;

	public User add(String login, String name, InternetAddress email, String password, byte[] picture) {
		final User user = new User(login, name, email, password);

		user.setPicture(picture);

		em.persist(user);
		em.flush();

		statistics.incrementUserCount();

		return user;
	}

	public User get(String login) {
		final User user = em.find(User.class, login);

		if (user == null) {
			throw new NoResultException("Unable to find user");
		}

		return user;
	}

	public User getCurrent() {
		return get(getCurrentUserLogin());
	}

	public Collection<User> search(String query) {
		// LIKE patterns interpret wildcard characters that might be
		// present in the query string, and would need to be escaped.
		// To avoid escaping strings and keep the code straightforward,
		// use the LOCATE SQL function instead, which interprets strings
		// literally, without giving any special treatment to any char
		return em.createQuery(
			"SELECT u FROM User u WHERE " +
			"LOCATE(:query, u.login) > 0 OR LOCATE(:query, u.name) > 0 OR LOCATE(:query, u.email) > 0 " +
			"ORDER BY CONCAT(u.login, u.name, u.email) ASC",
			User.class
		).setParameter("query", query).getResultList();
	}

	public boolean delete(String login) {
		// See PostEJB#delete on why this apparently unneeded complexity
		// with DELETE FROM is actually necessary

		em.createQuery(
			"DELETE FROM Comment c WHERE c.author.login = :login OR " +
			"c.post IN (SELECT p FROM Post p WHERE p.author.login = :login)"
		).setParameter("login", login).executeUpdate();

		em.createQuery(
			"DELETE FROM Friendship f WHERE " +
			"f.sender.login = :login OR f.receiver.login = :login"
		).setParameter("login", login).executeUpdate();

		final int deletedPosts = em.createQuery(
			"DELETE FROM Post p WHERE p.author.login = :login"
		).setParameter("login", login).executeUpdate();

		final int deletedUsers = em.createQuery(
			"DELETE FROM User u WHERE u.login = :login"
		).setParameter("login", login).executeUpdate();

		statistics.decrementPostCountBy(deletedPosts);
		statistics.decrementUserCountBy(deletedUsers);

		return deletedUsers > 0;
	}

	public Friendship addFriendship(String receiverLogin) {
		return addFriendship(getCurrent(), get(receiverLogin));
	}

	public Friendship addFriendship(User sender, User receiver) {
		final Friendship friendship = new Friendship(sender, receiver);

		em.persist(friendship);

		return friendship;
	}

	public void setRequestedFriendshipAcceptedStatus(String senderLogin, boolean accepted) {
		final String receiverLogin = getCurrentUserLogin();

		final int modifiedFriendships = em.createQuery(
				"UPDATE Friendship f SET f.accepted = :accepted WHERE " +
				"f.sender.login = :senderLogin AND f.receiver.login = :receiverLogin"
			)
			.setParameter("accepted", accepted)
			.setParameter("senderLogin", senderLogin)
			.setParameter("receiverLogin", receiverLogin)
			.executeUpdate();

		if (modifiedFriendships < 1) {
			throw new NoResultException("Unable to find friendship request");
		}
	}

	public void likePost(Post post) {
		likePost(getCurrent(), post);
	}

	public void likePost(User user, Post post) {
		if (user.likePost(post)) {
			em.flush();

			email.sendEmail(
				post.getAuthor(),
				"Someone liked your post!",
				user.getName() + " liked the post " + post.getId() + " that you made at " + post.getDate()
			);
		}
	}

	public Collection<Post> getAuthoredPosts(String login) {
		return em.createQuery(
				"SELECT p FROM Post p WHERE p.author.login = :login",
				Post.class
			)
			.setParameter("login", login)
			.getResultList();
	}

	public Collection<Post> getWallPosts() {
		final String login = getCurrentUserLogin();

		return em.createQuery(
				"SELECT p FROM Post p WHERE " +
				// The post was made by a friend
				"p.author IN (SELECT f.receiver FROM Friendship f WHERE f.sender.login = :login AND f.accepted = true) OR " +
				"p.author IN (SELECT f.sender FROM Friendship f WHERE f.receiver.login = :login AND f.accepted = true) OR " +
				// The post was made by ourselves
				"p.author.login = :login " +
				// Recent posts first
				"ORDER BY p.date DESC",
				Post.class
			)
			.setParameter("login", login)
			.getResultList();
	}

	private String getCurrentUserLogin() {
		return ctx.getCallerPrincipal().getName();
	}
}
