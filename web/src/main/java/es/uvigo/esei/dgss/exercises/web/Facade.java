package es.uvigo.esei.dgss.exercises.web;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.uvigo.esei.dgss.exercises.domain.Friendship;
import es.uvigo.esei.dgss.exercises.domain.User;

@Dependent
public class Facade {
	@PersistenceContext
	private EntityManager em;

	public User addUser(String login, String name, String password, byte[] picture) {
		User user = new User(login, password);

		user.setName(name);
		user.setPicture(picture);

		em.persist(user);

		return user;
	}

	public Friendship addFriendship(User sender, User receiver) {
		Friendship friendship = new Friendship(sender, receiver);

		em.persist(friendship);

		return friendship;
	}
}
