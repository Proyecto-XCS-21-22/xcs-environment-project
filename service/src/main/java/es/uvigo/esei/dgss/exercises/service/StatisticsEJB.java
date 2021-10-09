package es.uvigo.esei.dgss.exercises.service;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
public class StatisticsEJB {
	private final AtomicLong userCount = new AtomicLong(0);
	private final AtomicLong postCount = new AtomicLong(0);

	@PersistenceContext
	private EntityManager em;

	@PostConstruct
	private void initStats() {
		userCount.set(em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult());
		postCount.set(em.createQuery("SELECT COUNT(p) FROM Post p", Long.class).getSingleResult());
	}

	public long getUserCount() {
		return userCount.get();
	}

	public long getPostCount() {
		return postCount.get();
	}

	public void incrementUserCount() {
		userCount.getAndIncrement();
	}

	public void decrementUserCount() {
		userCount.getAndDecrement();
	}

	public void decrementUserCountBy(int amount) {
		userCount.getAndAdd(-((long) amount));
	}

	public void incrementPostCount() {
		postCount.getAndIncrement();
	}

	public void decrementPostCount() {
		postCount.getAndDecrement();
	}

	public void decrementPostCountBy(int amount) {
		postCount.getAndAdd(-((long) amount));
	}
}
