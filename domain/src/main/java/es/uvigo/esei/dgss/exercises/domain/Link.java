package es.uvigo.esei.dgss.exercises.domain;

import java.net.URL;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class Link extends Post {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	@NotNull
	private URL url;

	protected Link() {}

	public Link(User author, URL url) {
		super(author);

		this.url = Objects.requireNonNull(url);
	}

	public Link(Date date, User author, URL url) {
		super(date, author);

		this.url = Objects.requireNonNull(url);
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
}
