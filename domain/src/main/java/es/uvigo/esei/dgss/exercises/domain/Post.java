package es.uvigo.esei.dgss.exercises.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Post implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(insertable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	@NotNull @Past
	private Date date;

	@ManyToOne(optional = false)
	@NotNull
	private User author;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
	private Set<Comment> comments;

	@ManyToMany(cascade = CascadeType.MERGE, mappedBy = "likedPosts")
	private Set<User> likes;

	protected Post() {}

	protected Post(User author) {
		this(Date.from(Instant.now()), author);
	}

	protected Post(Date date, User author) {
		this.date = Objects.requireNonNull(date);
		this.author = Objects.requireNonNull(author);
	}

	public long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public User getAuthor() {
		return author;
	}

	public Collection<Comment> getComments() {
		return Collections.unmodifiableSet(comments);
	}

	public Collection<User> getLikes() {
		return Collections.unmodifiableSet(likes);
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = obj instanceof Post;

		if (equal && this != obj) {
			Post other = (Post) obj;
			equal = id == other.id;
		}

		return equal;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
}
