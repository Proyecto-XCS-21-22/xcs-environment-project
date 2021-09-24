package es.uvigo.esei.dgss.exercises.domain;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class Photo extends Post {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	@NotNull
	private byte[] content;

	protected Photo() {}

	public Photo(User author, byte[] content) {
		super(author);

		this.content = Objects.requireNonNull(content);
	}

	public Photo(Date date, User author, byte[] content) {
		super(date, author);

		this.content = Objects.requireNonNull(content);
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}
