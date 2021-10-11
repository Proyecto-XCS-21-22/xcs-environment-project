package es.uvigo.esei.dgss.exercises.rest.dtos;

public class VideoDTO extends PostDTO {
	private long duration;

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
}
