package es.uvigo.esei.dgss.exercises.service;

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

	public void delete(User user) {
		em.remove(user);
		em.flush();

		// When a user is deleted, so they are the posts they made
		int deletedPosts = user.getPosts().size();
		statistics.decrementPostCountBy(deletedPosts);

		statistics.decrementUserCount();
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

	public Friendship addFriendship(User sender, User receiver) {
		Friendship friendship = new Friendship(sender, receiver);

		em.persist(friendship);

		return friendship;
	}

	public void likePost(User user, Post post) {
		user.like(post);
		em.flush();

		email.sendEmail(
			post.getAuthor(),
			"Someone liked your post!",
			user.getName() + " liked the post " + post.getId() + " that you made at " + post.getDate()
		);
	}
}
