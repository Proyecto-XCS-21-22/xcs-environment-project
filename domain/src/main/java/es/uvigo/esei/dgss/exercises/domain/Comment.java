package es.uvigo.esei.dgss.exercises.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	@NotNull @Size(min = 1)
	private String comment;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	@NotNull @Past
	private Date date;

	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	@NotNull
	private Post post;

	protected Comment() {}

	public Comment(String comment, Post post) {
		this(comment, Date.from(Instant.now()), post);
	}

	public Comment(String comment, Date date, Post post) {
		this.comment = Objects.requireNonNull(comment);
		this.date = Objects.requireNonNull(date);
		this.post = Objects.requireNonNull(post);
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
