package es.uvigo.esei.dgss.exercises.rest.dto;

import static es.uvigo.esei.dgss.exercises.rest.dto.Constants.DATE_FORMATTER;

import javax.ws.rs.core.UriBuilder;

import es.uvigo.esei.dgss.exercises.domain.Comment;

public class CommentDTO {
	private long id;
	private String date;
	private String text;
	private String author;

	public static CommentDTO of(Comment c, UriBuilder baseUri) {
		final CommentDTO commentDto = new CommentDTO();

		commentDto.setId(c.getId());
		commentDto.setDate(DATE_FORMATTER.apply(c.getDate()));
		commentDto.setText(c.getComment());
		commentDto.setAuthor(
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
