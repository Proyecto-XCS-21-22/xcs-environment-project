package es.uvigo.esei.dgss.exercises.jsf.dto;

import java.time.Duration;

public class VideoDTO extends PostDTO {
	private Duration duration;

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}
}
