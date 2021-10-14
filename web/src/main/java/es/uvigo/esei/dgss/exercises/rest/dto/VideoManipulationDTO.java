package es.uvigo.esei.dgss.exercises.rest.dto;

public class VideoManipulationDTO extends PostManipulationDTO {
	private long duration;

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
}
