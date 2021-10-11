package es.uvigo.esei.dgss.exercises.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import es.uvigo.esei.dgss.exercises.domain.constraints.DifferentSenderAndReceiver;

@Entity
@IdClass(Friendship.CompositeKey.class)
@DifferentSenderAndReceiver
public class Friendship implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@ManyToOne(optional = false)
	@NotNull
	private User sender;

	@Id
	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	@NotNull
	private User receiver;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	@NotNull
	private Date date;

	@Column(nullable = false)
	private boolean accepted;

	protected Friendship() {}

	public Friendship(User sender, User receiver) {
		this(sender, receiver, Date.from(Instant.now()), false);
	}

	public Friendship(User sender, User receiver, Date date, boolean accepted) {
		this.sender = Objects.requireNonNull(sender);
		this.receiver = Objects.requireNonNull(receiver);
		this.date = Objects.requireNonNull(date);
		this.accepted = accepted;
	}

	public User getSender() {
		return sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public Date getDate() {
		return date;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = obj instanceof Friendship;

		if (equal && this != obj) {
			Friendship other = (Friendship) obj;
			equal = Objects.equals(sender, other.sender) &&
				Objects.equals(receiver, other.receiver);
		}

		return equal;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sender, receiver);
	}

	public static class CompositeKey implements Serializable {
		private static final long serialVersionUID = 1L;

		private String sender;
		private String receiver;

		@Override
		public boolean equals(Object obj) {
			boolean equal = obj instanceof CompositeKey;

			if (equal && this != obj) {
				CompositeKey other = (CompositeKey) obj;
				equal = Objects.equals(sender, other.sender) &&
					Objects.equals(receiver, other.receiver);
			}

			return equal;
		}

		@Override
		public int hashCode() {
			return Objects.hash(receiver, sender);
		}
	}
}
