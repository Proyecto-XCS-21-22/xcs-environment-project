package es.uvigo.esei.dgss.exercises.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

@Entity
public class Comment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(insertable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	@NotNull @Size(min = 1, max = 1024)
	private String comment;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull @Past
	private Date date;

	@ManyToOne(optional = false)
	@NotNull
	private User author;

	@ManyToOne(optional = false)
	@NotNull
	private Post post;

	protected Comment() {}

	public Comment(String comment, User author, Post post) {
		this(comment, Date.from(Instant.now()), author, post);
	}

	public Comment(String comment, Date date, User author, Post post) {
		this.comment = Objects.requireNonNull(comment);
		this.date = Objects.requireNonNull(date);
		this.author = Objects.requireNonNull(author);
		this.post = Objects.requireNonNull(post);
	}

	public long getId() {
		return id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getAuthor() {
		return author;
	}

	public Post getPost() {
		return post;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equal = obj instanceof Comment;

		if (equal && this != obj) {
			Comment other = (Comment) obj;
			equal = id == other.id;
		}

		return equal;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}
}
