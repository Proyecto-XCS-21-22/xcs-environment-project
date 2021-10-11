package es.uvigo.esei.dgss.exercises.rest.dtos;

import static es.uvigo.esei.dgss.exercises.rest.dtos.Constants.DATE_FORMATTER;

import javax.ws.rs.core.UriBuilder;

import es.uvigo.esei.dgss.exercises.domain.Comment;

public class CommentDTO {
	private long id;
	private String date;
	private String text;
	private String authorResource;

	public static CommentDTO of(Comment c, UriBuilder baseUri) {
		final CommentDTO commentDto = new CommentDTO();

		commentDto.setId(c.getId());
		commentDto.setDate(DATE_FORMATTER.apply(c.getDate()));
		commentDto.setText(c.getComment());
		commentDto.setAuthorResource(
			baseUri.clone().path(c.getAuthor().getLogin())
				.build().toASCIIString()
		);

		return commentDto;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthorResource() {
		return authorResource;
	}

	public void setAuthorResource(String authorResource) {
		this.authorResource = authorResource;
	}
}
