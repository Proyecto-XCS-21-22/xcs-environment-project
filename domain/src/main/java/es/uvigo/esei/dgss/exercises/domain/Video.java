package es.uvigo.esei.dgss.exercises.domain;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class Video extends Post {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	@NotNull
	private Duration duration;

	protected Video() {}

	public Video(User author, Duration duration) {
		super(author);

		this.duration = Objects.requireNonNull(duration);
	}

	public Video(Date date, User author, Duration duration) {
		super(date, author);

		this.duration = Objects.requireNonNull(duration);
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = Objects.requireNonNull(duration);
	}
}
