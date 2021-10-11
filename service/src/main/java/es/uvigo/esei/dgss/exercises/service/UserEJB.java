package es.uvigo.esei.dgss.exercises.service;

import java.util.Collection;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
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
		return em.find(User.class, login);
	}

	public User getCurrent() {
		final User currentUser = get(getCurrentLogin());

		if (currentUser == null) {
			throw new IllegalArgumentException("Unable to find current user");
		}

		return currentUser;
	}

	public void delete(User user) {
		delete(user.getLogin());
	}

	public boolean delete(String login) {
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
		final User sender = getCurrent();
		final User receiver = get(receiverLogin);

		if (receiver == null) {
			throw new IllegalArgumentException("Unable to find receiver user");
		}

		return addFriendship(sender, receiver);
	}

	public Friendship addFriendship(User sender, User receiver) {
		Friendship friendship = new Friendship(sender, receiver);

		if (sender.getLogin().equals(receiver.getLogin())) {
			throw new IllegalArgumentException(
				"The receiver login must be not equal to the sender login"
			);
		}

		em.persist(friendship);

		return friendship;
	}

	public void setRequestedFriendshipAcceptedStatus(String senderLogin, boolean accepted) {
		final String receiverLogin = getCurrentLogin();

		final int modifiedFriendships = em.createQuery(
				"UPDATE Friendship f SET f.accepted = :accepted WHERE " +
				"f.sender.login = :senderLogin AND f.receiver.login = :receiverLogin"
			)
			.setParameter("accepted", accepted)
			.setParameter("senderLogin", senderLogin)
			.setParameter("receiverLogin", receiverLogin)
			.executeUpdate();

		if (modifiedFriendships < 1) {
			throw new IllegalArgumentException("Unable to find friendship request");
		}
	}

	public void likePost(User user, Post post) {
		user.likePost(post);
		em.flush();

		email.sendEmail(
			post.getAuthor(),
			"Someone liked your post!",
			user.getName() + " liked the post " + post.getId() + " that you made at " + post.getDate()
		);
	}

	public Collection<Post> getAuthoredPosts(String login) {
		if (ctx.isCallerInRole("admin") || login.equals(getCurrentLogin())) {
			return em.createQuery(
				"SELECT p FROM Post p WHERE p.author.login = :login",
				Post.class
			)
			.setParameter("login", login)
			.getResultList();
		} else {
			throw new IllegalArgumentException(
				"Not enough permission to see posts other people posts"
			);
		}
	}

	public Collection<Post> getWallPosts() {
		final String login = getCurrentLogin();

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

	private String getCurrentLogin() {
		return ctx.getCallerPrincipal().getName();
	}
}
