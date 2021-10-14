package es.uvigo.esei.dgss.exercises.rest.dto;

public class PhotoDTO extends PostDTO {
	private byte[] content;

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}
